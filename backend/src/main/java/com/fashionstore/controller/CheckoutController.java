package com.fashionstore.controller;

import com.fashionstore.dto.PaymentDTO;
import com.fashionstore.model.User;
import com.fashionstore.security.CSRFProtection;
import com.fashionstore.service.CartService;
import com.fashionstore.service.CheckoutService;
import com.fashionstore.service.OrderService;
import com.fashionstore.serviceimpl.CartServiceImpl;
import com.fashionstore.serviceimpl.CheckoutServiceImpl;
import com.fashionstore.serviceimpl.OrderServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Controller for enterprise-grade checkout flow
 * Handles multi-step checkout, payment processing, and order management
 */
@WebServlet({"/checkout", "/checkout/*"})
public class CheckoutController extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(CheckoutController.class);
    private CheckoutService checkoutService;
    private CartService cartService;
    private OrderService orderService;
    private ObjectMapper objectMapper;

    @Override
    public void init() {
        checkoutService = new CheckoutServiceImpl();
        cartService = new CartServiceImpl();
        orderService = new OrderServiceImpl();
        objectMapper = new ObjectMapper();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String path = normalizePath(request.getPathInfo());
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("customerAuth") : null;

        try {
            if ("/".equals(path)) {
                // Display checkout page
                displayCheckoutPage(request, response, user);
            } else if ("/init".equals(path)) {
                initCheckout(request, response, user);
            } else if ("/validate".equals(path)) {
                validateCheckout(request, response, user);
            } else if ("/delivery-slots".equals(path)) {
                getDeliverySlots(request, response, user);
            } else if ("/payment-methods".equals(path)) {
                getPaymentMethods(request, response, user);
            } else if ("/order-summary".equals(path)) {
                getOrderSummary(request, response, user);
            } else if ("/address".equals(path)) {
                getAddressSelection(request, response, user);
            } else if ("/review".equals(path)) {
                getReviewPage(request, response, user);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Error in CheckoutController doGet: {}", e.getMessage(), e);
            sendErrorResponse(response, "Internal server error", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String path = normalizePath(request.getPathInfo());
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("customerAuth") : null;

        // CSRF validation for POST requests
        if (!CSRFProtection.validateRequest(request)) {
            sendErrorResponse(response, "Invalid CSRF token", HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        try {
            if ("/submit".equals(path)) {
                submitOrder(request, response, user);
            } else if ("/validate-step".equals(path)) {
                validateCheckoutStep(request, response, user);
            } else if ("/save-address".equals(path)) {
                saveAddress(request, response, user);
            } else if ("/select-delivery".equals(path)) {
                selectDeliverySlot(request, response, user);
            } else if ("/apply-coupon".equals(path)) {
                applyCoupon(request, response, user);
            } else if ("/remove-coupon".equals(path)) {
                removeCoupon(request, response, user);
            } else if ("/process-payment".equals(path)) {
                processPayment(request, response, user);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Error in CheckoutController doPost: {}", e.getMessage(), e);
            sendErrorResponse(response, "Internal server error", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    /** Normalize servlet pathInfo: treat null/blank as root slash. */
    private String normalizePath(String pathInfo) {
        return (pathInfo == null || pathInfo.isBlank()) ? "/" : pathInfo;
    }

    private void displayCheckoutPage(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException {
        
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            // Ensure CSRF token is available
            CSRFProtection.addTokenToRequest(request);

            // Pass Stripe publishable key to JSP
            String stripePublishableKey = System.getenv("STRIPE_PUBLISHABLE_KEY");
            if (stripePublishableKey == null || stripePublishableKey.trim().isEmpty()) {
                stripePublishableKey = ""; // Default to empty if not configured
            }
            request.setAttribute("stripePublishableKey", stripePublishableKey);

            // Get cart items and totals for the checkout page
            int userId = user.getUserId();
            List<com.fashionstore.model.CartItem> cartItems = cartService.getCartItems(userId);
            if (cartItems == null) {
                cartItems = new ArrayList<>();
            }

            // Calculate totals
            Map<String, Double> totals = checkoutService.calculateCheckoutTotals(userId, null);
            if (totals == null) {
                totals = new HashMap<>();
                totals.put("subtotal", 0.0);
                totals.put("total", 0.0);
            }

            // Get addresses
            List<com.fashionstore.model.Address> addresses = checkoutService.getUserCheckoutAddresses(userId);
            com.fashionstore.model.Address defaultAddress = checkoutService.getDefaultShippingAddress(userId);

            // Set request attributes for JSP
            request.setAttribute("cartItems", cartItems);
            request.setAttribute("cartTotal", totals.get("total"));
            request.setAttribute("addresses", addresses);
            request.setAttribute("defaultShipping", defaultAddress);
            request.setAttribute("defaultBilling", defaultAddress);

            request.getRequestDispatcher("/WEB-INF/views/checkout.jsp")
                   .forward(request, response);
        } catch (Exception e) {
            logger.error("Error displaying checkout page for user {}: {}", user != null ? user.getUserId() : "null", e.getMessage(), e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error loading checkout page");
        }
    }

    private void initCheckout(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        
        if (user == null) {
            sendErrorResponse(response, "Please login to proceed with checkout", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        int userId = user.getUserId();

        try {
            // Validate cart
            List<com.fashionstore.model.CartItem> cartItems = cartService.getCartItems(userId);
            if (cartItems == null || cartItems.isEmpty()) {
                sendErrorResponse(response, "Your cart is empty", HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            // Validate cart for checkout
            if (!cartService.validateCartForCheckout(userId)) {
                sendErrorResponse(response, "Some items in your cart are not available", HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            // Generate unique checkout session ID with duplicate prevention
            String checkoutSessionId = UUID.randomUUID().toString();
            
            // Check for existing active checkout session to prevent duplicate checkout
            String existingSessionId = (String) request.getSession().getAttribute("checkoutSessionId");
            if (existingSessionId != null) {
                // Reuse existing session if still valid
                checkoutSessionId = existingSessionId;
            }
            
            request.getSession().setAttribute("checkoutSessionId", checkoutSessionId);
            request.getSession().setAttribute("checkoutStartTime", System.currentTimeMillis());

            // Calculate totals
            Map<String, Double> totals = checkoutService.calculateCheckoutTotals(userId, null);

            // Get user addresses
            List<com.fashionstore.model.Address> addresses = checkoutService.getUserCheckoutAddresses(userId);
            com.fashionstore.model.Address defaultAddress = checkoutService.getDefaultShippingAddress(userId);

            // Initialize checkout data
            Map<String, Object> checkout = new HashMap<>();
            checkout.put("checkoutSessionId", checkoutSessionId);
            checkout.put("cartItems", cartItems);
            checkout.put("totals", totals);
            checkout.put("addresses", addresses);
            checkout.put("defaultAddress", defaultAddress);

            Map<String, Object> cartValidation = new HashMap<>();
            cartValidation.put("valid", true);
            cartValidation.put("message", "Cart validated successfully");

            Map<String, Object> data = new HashMap<>();
            data.put("success", true);
            data.put("checkout", checkout);
            data.put("checkoutSessionId", checkoutSessionId);
            data.put("cartValidation", cartValidation);

            sendJsonResponse(response, data);
        } catch (Exception e) {
            logger.error("Error initializing checkout for user {}: {}", userId, e.getMessage(), e);
            sendErrorResponse(response, "Failed to initialize checkout", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void validateCheckout(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        
        String checkoutSessionId = (String) request.getSession().getAttribute("checkoutSessionId");
        if (checkoutSessionId == null) {
            sendErrorResponse(response, "Checkout session expired", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        int userId = user.getUserId();

        try {
            // Validate cart
            if (!cartService.validateCartForCheckout(userId)) {
                sendErrorResponse(response, "Some items in your cart are not available", HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            Map<String, Object> validation = new HashMap<>();
            validation.put("valid", true);
            validation.put("message", "Checkout data validated successfully");

            Map<String, Object> data = new HashMap<>();
            data.put("success", true);
            data.put("validation", validation);

            sendJsonResponse(response, data);
        } catch (Exception e) {
            logger.error("Error validating checkout for user {}: {}", userId, e.getMessage(), e);
            sendErrorResponse(response, "Failed to validate checkout", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void getDeliverySlots(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        
        String pincode = request.getParameter("pincode");
        if (pincode == null || pincode.trim().isEmpty()) {
            sendErrorResponse(response, "Pincode is required", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // Placeholder implementation - delivery service method not available
        List<Map<String, Object>> deliverySlots = new ArrayList<>();
        
        Map<String, Object> data = new HashMap<>();
        data.put("success", true);
        data.put("deliverySlots", deliverySlots);
        data.put("pincode", pincode);
        data.put("count", deliverySlots.size());
        
        sendJsonResponse(response, data);
    }

    private void getPaymentMethods(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        
        String checkoutSessionId = (String) request.getSession().getAttribute("checkoutSessionId");
        if (checkoutSessionId == null) {
            sendErrorResponse(response, "Checkout session expired", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // Placeholder implementation - checkout service method not available
        Map<String, Object> checkout = new HashMap<>();
        checkout.put("checkoutSessionId", checkoutSessionId);

        // Placeholder implementation - checkout service method not available
        List<Map<String, Object>> paymentMethods = new ArrayList<>();
        
        Map<String, Object> data = new HashMap<>();
        data.put("success", true);
        data.put("paymentMethods", paymentMethods);
        data.put("count", paymentMethods.size());
        
        sendJsonResponse(response, data);
    }

    private void getOrderSummary(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        
        String checkoutSessionId = (String) request.getSession().getAttribute("checkoutSessionId");
        if (checkoutSessionId == null) {
            sendErrorResponse(response, "Checkout session expired", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        int userId = user.getUserId();
        String couponCode = request.getParameter("couponCode");

        try {
            // Get cart items
            List<com.fashionstore.model.CartItem> cartItems = cartService.getCartItems(userId);
            if (cartItems == null || cartItems.isEmpty()) {
                sendErrorResponse(response, "Your cart is empty", HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            // Calculate totals with coupon
            Map<String, Double> totals = checkoutService.calculateCheckoutTotals(userId, couponCode);

            // Get addresses
            List<com.fashionstore.model.Address> addresses = checkoutService.getUserCheckoutAddresses(userId);
            com.fashionstore.model.Address defaultAddress = checkoutService.getDefaultShippingAddress(userId);

            Map<String, Object> orderSummary = new HashMap<>();
            orderSummary.put("cartItems", cartItems);
            orderSummary.put("totals", totals);
            orderSummary.put("addresses", addresses);
            orderSummary.put("defaultAddress", defaultAddress);
            orderSummary.put("couponCode", couponCode);
            orderSummary.put("checkoutSessionId", checkoutSessionId);

            Map<String, Object> data = new HashMap<>();
            data.put("success", true);
            data.put("orderSummary", orderSummary);

            sendJsonResponse(response, data);
        } catch (Exception e) {
            logger.error("Error getting order summary for user {}: {}", userId, e.getMessage(), e);
            sendErrorResponse(response, "Failed to get order summary", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void getAddressSelection(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        
        if (user == null) {
            sendErrorResponse(response, "Please login to proceed", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // Placeholder implementation - checkout service method not available
        List<Map<String, Object>> addresses = new ArrayList<>();
        
        Map<String, Object> data = new HashMap<>();
        data.put("success", true);
        data.put("addresses", addresses);
        data.put("count", addresses.size());
        
        sendJsonResponse(response, data);
    }

    private void getReviewPage(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        
        String checkoutSessionId = (String) request.getSession().getAttribute("checkoutSessionId");
        if (checkoutSessionId == null) {
            sendErrorResponse(response, "Checkout session expired", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // Placeholder implementation - checkout service method not available
        Map<String, Object> checkout = new HashMap<>();
        checkout.put("checkoutSessionId", checkoutSessionId);

        // Placeholder implementation - checkout service method not available
        Map<String, Object> reviewData = new HashMap<>();
        
        Map<String, Object> data = new HashMap<>();
        data.put("success", true);
        data.put("reviewData", reviewData);
        
        sendJsonResponse(response, data);
    }

    private void submitOrder(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        
        if (user == null) {
            sendErrorResponse(response, "Please login to submit order", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        int userId = user.getUserId();
        String checkoutSessionId = (String) request.getSession().getAttribute("checkoutSessionId");
        if (checkoutSessionId == null) {
            sendErrorResponse(response, "Checkout session expired", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // Idempotency protection - check for duplicate submission FIRST
        String idempotencyKey = request.getHeader("X-Idempotency-Key");
        if (idempotencyKey != null && !idempotencyKey.trim().isEmpty()) {
            try {
                String processedKey = (String) request.getSession().getAttribute("processed_" + idempotencyKey);
                if (processedKey != null) {
                    // Return the previously created order
                    Integer previousOrderId = (Integer) request.getSession().getAttribute("orderId_" + idempotencyKey);
                    if (previousOrderId != null) {
                        com.fashionstore.model.Order previousOrder = orderService.getOrderById(previousOrderId, userId);
                        if (previousOrder != null) {
                            logger.info("Duplicate order submission detected for user {} with idempotency key {}", userId, idempotencyKey);
                            Map<String, Object> data = new HashMap<>();
                            data.put("success", true);
                            data.put("orderId", previousOrder.getOrderId());
                            data.put("order", previousOrder);
                            data.put("message", "Order already processed (duplicate submission prevented)");
                            sendJsonResponse(response, data);
                            return;
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("Error checking idempotency for user {}: {}", userId, e.getMessage());
            }
        }

        try {
            // Parse checkout data
            Map<String, Object> checkoutData = objectMapper.readValue(request.getReader(), Map.class);
            if (checkoutData == null || checkoutData.isEmpty()) {
                sendErrorResponse(response, "Invalid checkout data", HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            
            // Process checkout order through service layer orchestration
            com.fashionstore.model.Order order = checkoutService.processCheckoutOrder(userId, checkoutData);

            // Clear cart after successful order creation
            try {
                com.fashionstore.dao.CartDAO cartDAO = new com.fashionstore.daoimpl.CartDAOImpl();
                cartDAO.clearCartByUserId(userId);
            } catch (Exception e) {
                logger.error("Failed to clear cart for user {}: {}", userId, e.getMessage());
                // Don't fail the order if cart clearing fails
            }

            // Clear checkout session
            try {
                request.getSession().removeAttribute("checkoutSessionId");
            } catch (Exception e) {
                logger.error("Failed to clear checkout session for user {}: {}", userId, e.getMessage());
            }

            // Store idempotency key to prevent duplicate submissions
            if (idempotencyKey != null && !idempotencyKey.trim().isEmpty()) {
                try {
                    request.getSession().setAttribute("processed_" + idempotencyKey, idempotencyKey);
                    request.getSession().setAttribute("orderId_" + idempotencyKey, order.getOrderId());
                } catch (Exception e) {
                    logger.error("Failed to store idempotency key for user {}: {}", userId, e.getMessage());
                }
            }

            logger.info("Order {} created successfully for user {}", order.getOrderId(), userId);
            Map<String, Object> data = new HashMap<>();
            data.put("success", true);
            data.put("orderId", order.getOrderId());
            data.put("order", order);
            data.put("message", "Order placed successfully");

            sendJsonResponse(response, data);
        } catch (IllegalArgumentException e) {
            sendErrorResponse(response, e.getMessage(), HttpServletResponse.SC_BAD_REQUEST);
        } catch (IllegalStateException e) {
            sendErrorResponse(response, e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error submitting order for user {}: {}", userId, e.getMessage(), e);
            sendErrorResponse(response, "Failed to submit order. Please try again.", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void validateCheckoutStep(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        
        String checkoutSessionId = (String) request.getSession().getAttribute("checkoutSessionId");
        if (checkoutSessionId == null) {
            sendErrorResponse(response, "Checkout session expired", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        String step = request.getParameter("step");
        if (step == null || step.trim().isEmpty()) {
            sendErrorResponse(response, "Step is required", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            Map<String, Object> checkout = objectMapper.readValue(request.getReader(), Map.class);
            checkout.put("checkoutSessionId", checkoutSessionId);
            
            // Placeholder implementation - checkout service method not available
            Map<String, Object> validation = new HashMap<>();
        validation.put("valid", false);
        validation.put("message", "Checkout step validation not implemented");
            
            Map<String, Object> data = new HashMap<>();
            data.put("success", true);
            data.put("validation", validation);
            data.put("step", step);
            
            sendJsonResponse(response, data);
        } catch (Exception e) {
            logger.error("Error parsing checkout data: {}", e.getMessage(), e);
            sendErrorResponse(response, "Invalid checkout data", HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private void saveAddress(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        
        if (user == null) {
            sendErrorResponse(response, "Please login to save address", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        try {
            // Placeholder implementation - checkout service method not available
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "Save address not implemented");
            
            Map<String, Object> data = new HashMap<>();
            data.put("success", result.get("success"));
            data.put("message", result.get("message"));
            if (result.containsKey("address")) {
                data.put("address", result.get("address"));
            }
            
            sendJsonResponse(response, data);
        } catch (Exception e) {
            logger.error("Error parsing address data: {}", e.getMessage(), e);
            sendErrorResponse(response, "Invalid address data", HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private void selectDeliverySlot(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        
        String checkoutSessionId = (String) request.getSession().getAttribute("checkoutSessionId");
        if (checkoutSessionId == null) {
            sendErrorResponse(response, "Checkout session expired", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        String slotId = request.getParameter("slotId");
        if (slotId == null || slotId.trim().isEmpty()) {
            sendErrorResponse(response, "Delivery slot ID is required", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            // Placeholder implementation - checkout service method not available
        Map<String, Object> checkout = new HashMap<>();
        checkout.put("checkoutSessionId", checkoutSessionId);

            // Placeholder implementation - checkout service method not available
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "Select delivery slot not implemented");
            
            Map<String, Object> data = new HashMap<>();
            data.put("success", result.get("success"));
            data.put("message", result.get("message"));
            if (result.containsKey("deliverySlot")) {
                data.put("deliverySlot", result.get("deliverySlot"));
            }
            
            sendJsonResponse(response, data);
        } catch (Exception e) {
            logger.error("Error selecting delivery slot: {}", e.getMessage(), e);
            sendErrorResponse(response, "Failed to select delivery slot", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void applyCoupon(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        
        if (user == null) {
            sendErrorResponse(response, "Please login to apply coupon", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        int userId = user.getUserId();
        String couponCode = request.getParameter("couponCode");
        if (couponCode == null || couponCode.trim().isEmpty()) {
            sendErrorResponse(response, "Coupon code is required", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            boolean applied = checkoutService.applyCouponToCheckout(userId, couponCode);
            if (!applied) {
                sendErrorResponse(response, "Invalid or expired coupon", HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            // Recalculate totals with coupon
            Map<String, Double> totals = checkoutService.calculateCheckoutTotals(userId, couponCode);

            Map<String, Object> data = new HashMap<>();
            data.put("success", true);
            data.put("couponCode", couponCode);
            data.put("totals", totals);
            data.put("message", "Coupon applied successfully");

            sendJsonResponse(response, data);
        } catch (Exception e) {
            logger.error("Error applying coupon for user {}: {}", userId, e.getMessage(), e);
            sendErrorResponse(response, "Failed to apply coupon", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void removeCoupon(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        
        if (user == null) {
            sendErrorResponse(response, "Please login to remove coupon", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        int userId = user.getUserId();

        try {
            checkoutService.removeCouponFromCheckout(userId);
            
            // Recalculate totals without coupon
            Map<String, Double> totals = checkoutService.calculateCheckoutTotals(userId, null);

            Map<String, Object> data = new HashMap<>();
            data.put("success", true);
            data.put("totals", totals);
            data.put("message", "Coupon removed successfully");

            sendJsonResponse(response, data);
        } catch (Exception e) {
            logger.error("Error removing coupon for user {}: {}", userId, e.getMessage(), e);
            sendErrorResponse(response, "Failed to remove coupon", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void processPayment(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        
        if (user == null) {
            sendErrorResponse(response, "Please login to process payment", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        int userId = user.getUserId();
        String checkoutSessionId = (String) request.getSession().getAttribute("checkoutSessionId");
        if (checkoutSessionId == null) {
            sendErrorResponse(response, "Checkout session expired", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            PaymentDTO payment = objectMapper.readValue(request.getReader(), PaymentDTO.class);
            
            // Validate payment method
            String paymentMethod = payment.getPaymentMethod();
            if (paymentMethod == null || paymentMethod.trim().isEmpty()) {
                sendErrorResponse(response, "Payment method is required", HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            // Get order ID if provided (for payment processing)
            Integer orderId = payment.getOrderId();
            if (orderId == null || orderId <= 0) {
                sendErrorResponse(response, "Order ID is required for payment", HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            // Verify order belongs to user
            com.fashionstore.model.Order order = orderService.getOrderById(orderId, userId);
            if (order == null) {
                sendErrorResponse(response, "Order not found or unauthorized", HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            // Prepare payment data based on payment method
            Map<String, Object> paymentData = new HashMap<>();
            paymentData.put("orderId", orderId);
            paymentData.put("paymentMethod", paymentMethod);
            paymentData.put("amount", order.getTotalAmount());
            paymentData.put("currency", "INR");

            // Add payment method specific data
            if ("STRIPE".equalsIgnoreCase(paymentMethod)) {
                String stripePublishableKey = System.getenv("STRIPE_PUBLISHABLE_KEY");
                paymentData.put("stripePublishableKey", stripePublishableKey);
            } else if ("RAZORPAY".equalsIgnoreCase(paymentMethod)) {
                String razorpayKeyId = System.getenv("RAZORPAY_KEY_ID");
                paymentData.put("razorpayKeyId", razorpayKeyId);
            } else if ("COD".equalsIgnoreCase(paymentMethod)) {
                paymentData.put("codMessage", "Cash on Delivery selected");
            }

            Map<String, Object> data = new HashMap<>();
            data.put("success", true);
            data.put("paymentData", paymentData);
            data.put("message", "Payment data prepared successfully");

            sendJsonResponse(response, data);
        } catch (Exception e) {
            logger.error("Error processing payment for user {}: {}", userId, e.getMessage(), e);
            sendErrorResponse(response, "Failed to process payment", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void sendJsonResponse(HttpServletResponse response, Map<String, Object> data) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(data));
    }

    private void sendErrorResponse(HttpServletResponse response, String message, int status) throws IOException {
        response.setContentType("application/json");
        response.setStatus(status);
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("message", message);
        response.getWriter().write(objectMapper.writeValueAsString(error));
    }
}
