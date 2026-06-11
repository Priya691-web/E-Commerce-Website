package com.fashionstore.controller;

import com.fashionstore.dao.implementation.CartDAOImpl;
import com.fashionstore.dao.interfaces.CartDAO;
import com.fashionstore.model.Cart;
import com.fashionstore.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/cart")
public class CartServlet extends HttpServlet {
    private CartDAO cartDAO;

    @Override
    public void init() throws ServletException {
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
        
        Cart cart = cartDAO.getCartByUserId(user.getId());
        if (cart == null) {
            cartDAO.createCart(user.getId());
            cart = cartDAO.getCartByUserId(user.getId());
        }
        
        request.setAttribute("cartItems", cartDAO.getCartItems(cart.getId()));
        request.getRequestDispatcher("/WEB-INF/view/cart/cart.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String action = request.getParameter("action");
        Cart cart = cartDAO.getCartByUserId(user.getId());
        if(cart == null) {
            cartDAO.createCart(user.getId());
            cart = cartDAO.getCartByUserId(user.getId());
        }

        if ("add".equals(action)) {
            int variantId = Integer.parseInt(request.getParameter("variantId"));
            int quantity = Integer.parseInt(request.getParameter("quantity"));
            cartDAO.addItemToCart(cart.getId(), variantId, quantity);
        } else if ("remove".equals(action)) {
            int cartItemId = Integer.parseInt(request.getParameter("cartItemId"));
            cartDAO.removeItemFromCart(cartItemId);
        }

        response.sendRedirect(request.getContextPath() + "/cart");
    }
}
