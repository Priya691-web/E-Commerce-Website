package com.fashionstore.controller;

import com.fashionstore.dao.implementation.OrderDAOImpl;
import com.fashionstore.dao.implementation.OrderItemDAOImpl;
import com.fashionstore.dao.implementation.CartDAOImpl;
import com.fashionstore.dao.interfaces.OrderDAO;
import com.fashionstore.dao.interfaces.OrderItemDAO;
import com.fashionstore.dao.interfaces.CartDAO;
import com.fashionstore.model.Order;
import com.fashionstore.model.OrderItem;
import com.fashionstore.model.Cart;
import com.fashionstore.model.CartItem;
import com.fashionstore.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/order")
public class OrderServlet extends HttpServlet {
    private OrderDAO orderDAO;
    private OrderItemDAO orderItemDAO;
    private CartDAO cartDAO;

    @Override
    public void init() throws ServletException {
        orderDAO = new OrderDAOImpl();
        orderItemDAO = new OrderItemDAOImpl();
        cartDAO = new CartDAOImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String action = request.getParameter("action");
        if ("details".equals(action)) {
            int orderId = Integer.parseInt(request.getParameter("id"));
            Order order = orderDAO.getOrderById(orderId);
            if(order != null && order.getUserId() == user.getId()) {
                request.setAttribute("order", order);
                request.setAttribute("orderItems", orderItemDAO.getItemsByOrderId(orderId));
                request.getRequestDispatcher("/WEB-INF/view/order/order-details.jsp").forward(request, response);
            } else {
                response.sendRedirect(request.getContextPath() + "/order?action=my-orders");
            }
        } else {
            request.setAttribute("orders", orderDAO.getOrdersByUserId(user.getId()));
            request.getRequestDispatcher("/WEB-INF/view/order/my-orders.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String shippingAddress = request.getParameter("shippingAddress");
        Cart cart = cartDAO.getCartByUserId(user.getId());
        
        if (cart != null) {
            List<CartItem> cartItems = cartDAO.getCartItems(cart.getId());
            if (!cartItems.isEmpty()) {
                // Simplification: We calculate total in actual scenario with Product price
                // For this mock, assume a fixed total or fetch product prices in a real impl
                BigDecimal totalAmount = new BigDecimal("100.00"); // MOCK
                
                Order order = new Order();
                order.setUserId(user.getId());
                order.setTotalAmount(totalAmount);
                order.setShippingAddress(shippingAddress);
                
                int orderId = orderDAO.createOrder(order);
                if (orderId > 0) {
                    List<OrderItem> orderItems = new ArrayList<>();
                    for (CartItem ci : cartItems) {
                        OrderItem oi = new OrderItem();
                        oi.setOrderId(orderId);
                        oi.setProductVariantId(ci.getProductVariantId());
                        oi.setQuantity(ci.getQuantity());
                        oi.setUnitPrice(new BigDecimal("50.00")); // MOCK
                        orderItems.add(oi);
                    }
                    orderItemDAO.addOrderItems(orderItems);
                    cartDAO.clearCart(cart.getId());
                    
                    response.sendRedirect(request.getContextPath() + "/order?action=details&id=" + orderId);
                    return;
                }
            }
        }
        response.sendRedirect(request.getContextPath() + "/cart?error=true");
    }
}
