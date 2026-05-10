package com.fashionstore.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fashionstore.dao.CartDAO;
import com.fashionstore.daoimpl.CartDAOImpl;
import com.fashionstore.model.Address;
import com.fashionstore.model.CartItem;
import com.fashionstore.model.User;
import com.fashionstore.security.CSRFProtection;
import com.fashionstore.service.AddressService;
import com.fashionstore.util.DBConnection;
import com.fashionstore.util.JsonUtil;
import com.fashionstore.validation.AddressValidator;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/checkout")
public class CheckoutController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(CheckoutController.class);

    private CartDAO cartDAO;
    private AddressService addressService;

    @Override
    public void init() {
        cartDAO = new CartDAOImpl();
        addressService = new AddressService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // AJAX: ?action=addresses returns saved addresses as JSON
        if ("addresses".equals(request.getParameter("action"))) {
            handleAddressesAjax(request, response, user.getUserId());
            return;
        }

        int userId = user.getUserId();
        try {
            List<CartItem> cartItems = cartDAO.getCartItemsByUserId(userId);

            if (cartItems == null || cartItems.isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/cart");
                return;
            }

            double total = 0;
            for (CartItem item : cartItems) {
                total += item.getPrice() * item.getQuantity();
            }

            request.setAttribute("cartItems", cartItems);
            request.setAttribute("cartTotal", total);

            // Load user addresses for checkout
            List<Address> addresses = addressService.getAddressesByUserId(userId);
            Address defaultShipping = addressService.getDefaultAddress(userId, "shipping");
            Address defaultBilling = addressService.getDefaultAddress(userId, "billing");

            request.setAttribute("addresses", addresses);
            request.setAttribute("defaultShipping", defaultShipping);
            request.setAttribute("defaultBilling", defaultBilling);

            // Generate/retrieve CSRF token for the form
            CSRFProtection.addTokenToRequest(request);

            request.getRequestDispatcher("/WEB-INF/views/checkout.jsp")
                   .forward(request, response);
        } catch (Exception e) {
            logger.error("Error in CheckoutController.doGet: {}", e.getMessage(), e);
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            if (isAjax(request)) {
                writeJson(response, HttpServletResponse.SC_UNAUTHORIZED,
                        Map.of("success", false, "message", "Please log in to continue"));
            } else {
                response.sendRedirect(request.getContextPath() + "/login");
            }
            return;
        }

        // CSRF
        if (!CSRFProtection.validateRequest(request)) {
            if (isAjax(request)) {
                writeJson(response, HttpServletResponse.SC_FORBIDDEN,
                        Map.of("success", false, "message", "Invalid CSRF token"));
                return;
            }
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid CSRF token");
            return;
        }

        // AJAX: add a new address inline from checkout
        if ("addAddress".equals(request.getParameter("action"))) {
            handleAddAddressAjax(request, response, user.getUserId());
            return;
        }

        int userId = user.getUserId();
        List<CartItem> cartItems;
        try {
            cartItems = cartDAO.getCartItemsByUserId(userId);
        } catch (Exception e) {
            logger.error("Could not load cart for user #{} during checkout: {}", userId, e.getMessage(), e);
            session.setAttribute("error", "We could not load your cart. Please try again.");
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        if (cartItems == null || cartItems.isEmpty()) {
            session.setAttribute("error", "Your cart is empty.");
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        double total = 0;
        for (CartItem item : cartItems) {
            total += item.getPrice() * item.getQuantity();
        }

        // Handle address selection
        String shippingAddressId = request.getParameter("shippingAddressId");
        String billingAddressId = request.getParameter("billingAddressId");
        boolean useNewShipping = shippingAddressId == null || shippingAddressId.isBlank()
                || "new".equals(shippingAddressId) || "0".equals(shippingAddressId);
        boolean useNewBilling = "new".equals(billingAddressId) || "0".equals(billingAddressId);

        // Preserve form data for error cases
        request.setAttribute("selectedShippingAddressId", shippingAddressId);
        request.setAttribute("selectedBillingAddressId", billingAddressId);
        request.setAttribute("useNewShipping", useNewShipping);
        request.setAttribute("useNewBilling", useNewBilling);

        // Server-side validation when typing a NEW address
        if (useNewShipping) {
            Address typed = buildAddressFromCheckoutForm(request, userId);
            Map<String, String> errors = AddressValidator.validate(typed);
            if (!errors.isEmpty()) {
                request.setAttribute("error", errors.values().iterator().next());
                request.setAttribute("fieldErrors", errors);
                request.setAttribute("cartItems", cartItems);
                request.setAttribute("cartTotal", total);
                List<Address> addresses = addressService.getAddressesByUserId(userId);
                request.setAttribute("addresses", addresses);
                request.setAttribute("defaultShipping", addressService.getDefaultAddress(userId, "shipping"));
                request.setAttribute("defaultBilling", addressService.getDefaultAddress(userId, "billing"));
                CSRFProtection.addTokenToRequest(request);
                request.getRequestDispatcher("/WEB-INF/views/checkout.jsp").forward(request, response);
                return;
            }

            // Optionally persist the new address for future use
            boolean saveNewAddress = "true".equals(request.getParameter("saveAddress"))
                    || "on".equals(request.getParameter("saveAddress"));
            if (saveNewAddress) {
                if (addressService.getAddressCount(userId) == 0) {
                    typed.setDefault(true);
                }
                if (addressService.addAddress(typed) && typed.getAddressId() > 0) {
                    shippingAddressId = String.valueOf(typed.getAddressId());
                    useNewShipping = false;
                    logger.info("Saved new address #{} for user #{} during checkout", typed.getAddressId(), userId);
                }
            }
        }

        Connection con = null;
        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false);

            // Step 1: Atomically reduce stock for each cart item.
            for (CartItem c : cartItems) {
                boolean stockReduced = reduceStockInTransaction(con, c);
                if (!stockReduced) {
                    con.rollback();
                    request.setAttribute("error", "Insufficient stock for " + c.getProductName() + " (Size: " + c.getSizeLabel() + "). Please update your cart.");
                    request.setAttribute("cartItems", cartItems);
                    request.setAttribute("cartTotal", total);

                    // Load addresses for re-display
                    List<Address> addresses = addressService.getAddressesByUserId(userId);
                    request.setAttribute("addresses", addresses);
                    request.setAttribute("defaultShipping", addressService.getDefaultAddress(userId, "shipping"));
                    request.setAttribute("defaultBilling", addressService.getDefaultAddress(userId, "billing"));

                    CSRFProtection.addTokenToRequest(request);

                    request.getRequestDispatcher("/WEB-INF/views/checkout.jsp").forward(request, response);
                    return;
                }
            }

            // Step 2: Create order header.
            int orderId = createOrderInTransaction(con, userId, total, request, shippingAddressId, billingAddressId);
            if (orderId <= 0) {
                con.rollback();
                request.setAttribute("error", "Could not place order. Please check your shipping details.");
                request.setAttribute("cartItems", cartItems);
                request.setAttribute("cartTotal", total);

                // Load addresses for re-display
                List<Address> addresses = addressService.getAddressesByUserId(userId);
                request.setAttribute("addresses", addresses);
                request.setAttribute("defaultShipping", addressService.getDefaultAddress(userId, "shipping"));
                request.setAttribute("defaultBilling", addressService.getDefaultAddress(userId, "billing"));

                CSRFProtection.addTokenToRequest(request);

                request.getRequestDispatcher("/WEB-INF/views/checkout.jsp").forward(request, response);
                return;
            }

            // Step 3: Insert order items.
            for (CartItem c : cartItems) {
                addOrderItemInTransaction(con, orderId, c);
            }

            // Step 4: Clear cart.
            clearCartInTransaction(con, userId);

            con.commit();
            logger.info("Order #{} placed successfully for user #{}", orderId, userId);
            
            // Clean up session cart cache if it exists
            session.removeAttribute("cartItems");
            
            response.sendRedirect(request.getContextPath() + "/success");

        } catch (Exception txEx) {
            logger.error("Checkout process failed for user #{}: {}", userId, txEx.getMessage(), txEx);
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException ignored) {}
            }
            
            // Preserve checkout state, preserve cart items, show inline error
            request.setAttribute("error", "Could not place order: " + txEx.getMessage());
            request.setAttribute("cartItems", cartItems);
            request.setAttribute("cartTotal", total);

            // Load addresses for re-display
            List<Address> addresses = addressService.getAddressesByUserId(userId);
            request.setAttribute("addresses", addresses);
            request.setAttribute("defaultShipping", addressService.getDefaultAddress(userId, "shipping"));
            request.setAttribute("defaultBilling", addressService.getDefaultAddress(userId, "billing"));

            CSRFProtection.addTokenToRequest(request);

            request.getRequestDispatcher("/WEB-INF/views/checkout.jsp").forward(request, response);
        } finally {
            if (con != null) {
                try {
                    con.setAutoCommit(true);
                    con.close();
                } catch (SQLException ignored) {}
            }
        }
    }

    private boolean reduceStockInTransaction(Connection con, CartItem item) throws SQLException {
        String sql = "UPDATE product_sizes " +
                     "SET stock_quantity = stock_quantity - ?, " +
                     "    is_available = CASE WHEN (stock_quantity - ?) > 0 THEN 1 ELSE 0 END " +
                     "WHERE product_id = ? AND size_label = ? AND stock_quantity >= ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, item.getQuantity());
            ps.setInt(2, item.getQuantity());
            ps.setInt(3, item.getProductId());
            ps.setString(4, item.getSizeLabel());
            ps.setInt(5, item.getQuantity());
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
    }

    private int createOrderInTransaction(Connection con, int userId, double total, HttpServletRequest request, 
                                          String shippingAddressId, String billingAddressId) throws SQLException {
        // Check if using saved addresses
        int shippingAddrId = 0;
        int billingAddrId = 0;
        
        try {
            if (shippingAddressId != null && !shippingAddressId.equals("new")) {
                shippingAddrId = Integer.parseInt(shippingAddressId);
            }
            if (billingAddressId != null && !billingAddressId.equals("new")) {
                billingAddrId = Integer.parseInt(billingAddressId);
            }
        } catch (NumberFormatException e) {
            logger.warn("Invalid address ID format: {}", e.getMessage());
        }
        
        // If using saved addresses, fetch address details
        String fullName = null;
        String address = null;
        String city = null;
        String state = null;
        String zip = null;
        String phone = null;
        
        if (shippingAddrId > 0) {
            // Use saved shipping address
            Address shippingAddr = addressService.getAddressById(shippingAddrId, userId);
            if (shippingAddr != null) {
                fullName = shippingAddr.getFullName();
                address = shippingAddr.getAddressLine1() + 
                          (shippingAddr.getAddressLine2() != null ? ", " + shippingAddr.getAddressLine2() : "");
                city = shippingAddr.getCity();
                state = shippingAddr.getState();
                zip = shippingAddr.getPostalCode();
                phone = shippingAddr.getPhone();
            }
        } else {
            // Use form input (new address)
            fullName = request.getParameter("fullName");
            address = request.getParameter("address");
            city = request.getParameter("city");
            state = request.getParameter("state");
            zip = request.getParameter("zip");
            phone = request.getParameter("phone");
        }
        
        // Populate both subtotal and total_amount so the orders row matches the
        // financial truth shown on the checkout page. Shipping/tax/discount keep
        // their schema defaults of 0.00 until those features are wired in.
        String sql = "INSERT INTO orders " +
                     "(user_id, subtotal, total_amount, full_name, address, city, state, zip, phone, payment_method, status, payment_status, shipping_address_id, billing_address_id) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        String paymentMethod = request.getParameter("paymentMethod");
        if (paymentMethod == null || paymentMethod.isBlank()) {
            paymentMethod = "COD";
        }

        if (fullName == null || address == null || city == null || state == null || zip == null || phone == null) {
            logger.warn("Missing shipping details for user #{}", userId);
            return 0;
        }

        java.math.BigDecimal totalDec = java.math.BigDecimal.valueOf(total)
                .setScale(2, java.math.RoundingMode.HALF_UP);

        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, userId);
            ps.setBigDecimal(2, totalDec);
            ps.setBigDecimal(3, totalDec);
            ps.setString(4, fullName);
            ps.setString(5, address);
            ps.setString(6, city);
            ps.setString(7, state);
            ps.setString(8, zip);
            ps.setString(9, phone);
            ps.setString(10, paymentMethod);
            ps.setString(11, "Pending");
            ps.setString(12, "pending");
            if (shippingAddrId > 0) {
                ps.setInt(13, shippingAddrId);
            } else {
                ps.setNull(13, java.sql.Types.INTEGER);
            }
            if (billingAddrId > 0) {
                ps.setInt(14, billingAddrId);
            } else {
                ps.setNull(14, java.sql.Types.INTEGER);
            }
            
            int rows = ps.executeUpdate();
            if (rows == 0) return 0;
            
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }

    private void addOrderItemInTransaction(Connection con, int orderId, CartItem item) throws SQLException {
        // The order_items schema declares three NOT NULL financial columns:
        //   price        (legacy unit-price snapshot, kept for back-compat)
        //   unit_price   (unit-price snapshot at time of purchase)
        //   total_price  (unit_price * quantity, for fast reporting)
        // All three must be populated explicitly using BigDecimal to preserve
        // currency precision; otherwise MySQL rejects the row with
        // "Field 'unit_price' doesn't have a default value".
        String sql = "INSERT INTO order_items " +
                     "(order_id, product_id, size_label, quantity, price, unit_price, total_price) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        java.math.BigDecimal unitPrice = java.math.BigDecimal.valueOf(item.getPrice())
                .setScale(2, java.math.RoundingMode.HALF_UP);
        java.math.BigDecimal totalPrice = unitPrice.multiply(java.math.BigDecimal.valueOf(item.getQuantity()))
                .setScale(2, java.math.RoundingMode.HALF_UP);
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.setInt(2, item.getProductId());
            ps.setString(3, item.getSizeLabel());
            ps.setInt(4, item.getQuantity());
            ps.setBigDecimal(5, unitPrice);
            ps.setBigDecimal(6, unitPrice);
            ps.setBigDecimal(7, totalPrice);
            ps.executeUpdate();
        }
    }

    private void clearCartInTransaction(Connection con, int userId) throws SQLException {
        String sql = "DELETE FROM cart_items WHERE user_id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        }
    }

    // ============================================================
    // Helpers
    // ============================================================

    private boolean isAjax(HttpServletRequest request) {
        String accept = request.getHeader("Accept");
        String requestedWith = request.getHeader("X-Requested-With");
        return (accept != null && accept.contains("application/json"))
                || "XMLHttpRequest".equalsIgnoreCase(requestedWith);
    }

    private void writeJson(HttpServletResponse response, int status, Map<String, Object> data) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(status);
        response.getWriter().write(JsonUtil.toJson(data));
    }

    private Address buildAddressFromCheckoutForm(HttpServletRequest request, int userId) {
        Address a = new Address();
        a.setUserId(userId);
        // Sensible default for new checkout-typed address
        String type = request.getParameter("addressType");
        a.setAddressType((type == null || type.isBlank()) ? "shipping" : type);
        a.setFullName(request.getParameter("fullName"));
        a.setPhone(request.getParameter("phone"));
        a.setAddressLine1(request.getParameter("address"));
        a.setAddressLine2(request.getParameter("addressLine2"));
        a.setCity(request.getParameter("city"));
        a.setState(request.getParameter("state"));
        a.setPostalCode(request.getParameter("zip"));
        String country = request.getParameter("country");
        a.setCountry((country == null || country.isBlank()) ? "India" : country);
        a.setDefault("on".equals(request.getParameter("setAsDefault"))
                || "true".equals(request.getParameter("setAsDefault")));
        return a;
    }

    private void handleAddressesAjax(HttpServletRequest request, HttpServletResponse response, int userId) throws IOException {
        List<Address> addresses = addressService.getAddressesByUserId(userId);
        Address defaultShipping = addressService.getDefaultAddress(userId, "shipping");

        Map<String, Object> data = new HashMap<>();
        data.put("success", true);
        data.put("addresses", addresses);
        data.put("count", addresses.size());
        data.put("defaultShippingId", defaultShipping != null ? defaultShipping.getAddressId() : 0);
        writeJson(response, HttpServletResponse.SC_OK, data);
    }

    private void handleAddAddressAjax(HttpServletRequest request, HttpServletResponse response, int userId) throws IOException {
        Address a = new Address();
        a.setUserId(userId);
        a.setAddressType(orDefault(request.getParameter("addressType"), "shipping"));
        a.setFullName(request.getParameter("fullName"));
        a.setPhone(request.getParameter("phone"));
        a.setAddressLine1(request.getParameter("addressLine1"));
        a.setAddressLine2(request.getParameter("addressLine2"));
        a.setCity(request.getParameter("city"));
        a.setState(request.getParameter("state"));
        a.setPostalCode(request.getParameter("postalCode"));
        a.setCountry(orDefault(request.getParameter("country"), "India"));
        a.setDefault("on".equals(request.getParameter("isDefault"))
                || "true".equals(request.getParameter("isDefault")));

        Map<String, String> errors = AddressValidator.validate(a);
        if (!errors.isEmpty()) {
            Map<String, Object> data = new HashMap<>();
            data.put("success", false);
            data.put("message", "Please correct the highlighted fields");
            data.put("errors", errors);
            writeJson(response, HttpServletResponse.SC_BAD_REQUEST, data);
            return;
        }

        // First address ever -> auto default
        if (addressService.getAddressCount(userId) == 0) {
            a.setDefault(true);
        }

        boolean ok = addressService.addAddress(a);
        Map<String, Object> data = new HashMap<>();
        data.put("success", ok);
        data.put("message", ok ? "Address saved" : "Failed to save address");
        if (ok) {
            data.put("address", a);
            data.put("addressId", a.getAddressId());
        }
        writeJson(response, ok ? HttpServletResponse.SC_OK : HttpServletResponse.SC_INTERNAL_SERVER_ERROR, data);
    }

    private static String orDefault(String s, String def) {
        return (s == null || s.isBlank()) ? def : s;
    }
}
