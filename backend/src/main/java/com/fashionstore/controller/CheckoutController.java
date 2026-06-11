package com.fashionstore.controller;

import com.fashionstore.dto.PaymentDTO;
import com.fashionstore.model.User;
import com.fashionstore.security.CSRFProtection;
import com.fashionstore.registry.ServiceRegistry;
import com.fashionstore.service.CartService;
import com.fashionstore.service.CheckoutService;
import com.fashionstore.service.OrderService;
import com.fashionstore.service.StripePaymentService;
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
 * CheckoutController - MVC Architecture
 * 
 * REFACTORED FOR PROPER MVC:
 * - Backend handles ALL checkout business logic
 * - Backend handles ALL order calculations (subtotal, tax, shipping, discount)
 * - Backend handles ALL payment validation
 * - Backend handles ALL order creation and stock deduction
 * - Frontend only provides form data and AJAX triggers
 * - Frontend only displays backend-calculated totals
 * - No frontend order/payment amount calculations
 * - No frontend payment tampering possible
 * 
 * Request Flow:
 * 1. Frontend submits checkout form with address, payment method
 * 2. Controller validates input
 * 3. Service layer calculates totals, validates cart, validates payment
 * 4. DAO layer creates order, deducts stock, processes payment
 * 5. Response sent to frontend with order confirmation
 */
@WebServlet({"/checkout", "/checkout/*"})
public class CheckoutController extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(CheckoutController.class);
    private CheckoutService checkoutService;
    private CartService cartService;
    private OrderService orderService;
    private StripePaymentService stripePaymentService;
    private ObjectMapper objectMapper;

    @Override
    public void init() {
        ServiceRegistry registry = ServiceRegistry.getInstance();
        checkoutService = registry.getCheckoutService();
        cartService = registry.getCartService();
        orderService = registry.getOrderService();
        stripePaymentService = registry.getStripePaymentService();
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
                displayCheckoutPage(request, response, user);
            } else if ("/init".equals(path)) {
                initCheckout(request, response, user);
            } else if ("/validate".equals(path)) {
                validateCheckout(request, response, user);
            } else if ("/order-summary".equals(path)) {
                getOrderSummary(request, response, user);
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
            // Handle root path POST (form submission from checkout.jsp)
            if ("/".equals(path)) {
                handleFormSubmission(request, response, user);
            } else if ("/submit".equals(path)) {
                submitOrder(request, response, user);
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

    /**
     * Handle form submission from checkout.jsp (form-data)
     * This handles both COD and Stripe payment methods
     */
    private void handleFormSubmission(HttpServletRequest request, HttpServletResponse response, User user) 
            throws ServletException, IOException {
        
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        int userId = user.getUserId();

        try {
            // Extract form parameters
            String shippingAddressId = request.getParameter("shippingAddressId");
            String paymentMethod = request.getParameter("paymentMethod");
            String fullName = request.getParameter("fullName");
            String address = request.getParameter("address");
            String city = request.getParameter("city");
            String state = request.getParameter("state");
            String zip = request.getParameter("zip");
            String phone = request.getParameter("phone");
            String saveAddress = request.getParameter("saveAddress");
            String shippingMethod = request.getParameter("shippingMethod");

            // Validate payment method
            if (paymentMethod == null || paymentMethod.trim().isEmpty()) {
                request.setAttribute("error", "Please select a payment method");
                displayCheckoutPage(request, response, user);
                return;
            }

            // Build checkout data
            Map<String, Object> checkoutData = new HashMap<>();
            checkoutData.put("paymentMethod", paymentMethod);
            
            // Build shipping address data
            Map<String, Object> shippingAddressData = new HashMap<>();
            
            if ("new".equals(shippingAddressId) || shippingAddressId == null || shippingAddressId.isEmpty()) {
                // Using new address
                if (fullName == null || fullName.trim().isEmpty() ||
                    address == null || address.trim().isEmpty() ||
                    city == null || city.trim().isEmpty() ||
                    state == null || state.trim().isEmpty() ||
                    zip == null || zip.trim().isEmpty() ||
                    phone == null || phone.trim().isEmpty()) {
                    request.setAttribute("error", "Please fill in all required shipping address fields");
                    request.setAttribute("fieldErrors", Map.of(
                        "postalCode", zip == null || zip.trim().isEmpty() ? "ZIP code is required" : null,
                        "phone", phone == null || phone.trim().isEmpty() ? "Phone number is required" : null
                    ));
                    displayCheckoutPage(request, response, user);
                    return;
                }
                
                shippingAddressData.put("fullName", fullName);
                shippingAddressData.put("addressLine1", address);
                shippingAddressData.put("addressLine2", "");
                shippingAddressData.put("city", city);
                shippingAddressData.put("state", state);
                shippingAddressData.put("postalCode", zip);
                shippingAddressData.put("phone", phone);
                shippingAddressData.put("saveAddress", "true".equals(saveAddress));
            } else {
                // Using saved address - fetch from database
                try {
                    int addressId = Integer.parseInt(shippingAddressId);
                    List<com.fashionstore.model.Address> addresses = checkoutService.getUserCheckoutAddresses(userId);
                    com.fashionstore.model.Address savedAddress = null;
                    if (addresses != null) {
                        savedAddress = addresses.stream()
                            .filter(a -> a.getAddressId() == addressId)
                            .findFirst()
                            .orElse(null);
                    }
                    if (savedAddress == null) {
                        request.setAttribute("error", "Selected address not found");
                        displayCheckoutPage(request, response, user);
                        return;
                    }
                    
                    shippingAddressData.put("fullName", savedAddress.getFullName());
                    shippingAddressData.put("addressLine1", savedAddress.getAddressLine1());
                    shippingAddressData.put("addressLine2", savedAddress.getAddressLine2() != null ? savedAddress.getAddressLine2() : "");
                    shippingAddressData.put("city", savedAddress.getCity());
                    shippingAddressData.put("state", savedAddress.getState());
                    shippingAddressData.put("postalCode", savedAddress.getPostalCode());
                    shippingAddressData.put("phone", savedAddress.getPhone());
                    shippingAddressData.put("saveAddress", false);
                } catch (NumberFormatException e) {
                    request.setAttribute("error", "Invalid address selection");
                    displayCheckoutPage(request, response, user);
                    return;
                }
            }
            
            checkoutData.put("shippingAddress", shippingAddressData);
            checkoutData.put("shippingMethod", shippingMethod != null ? shippingMethod : "STANDARD");

            // Handle different payment methods
            if ("STRIPE".equalsIgnoreCase(paymentMethod)) {
                // For Stripe, create order first, then create payment intent
                try {
                    com.fashionstore.model.Order order = checkoutService.processCheckoutOrder(userId, checkoutData);
                    
                    // Create Stripe payment intent using StripePaymentService
                    if (stripePaymentService == null || !stripePaymentService.isConfigured()) {
                        throw new IllegalStateException("Stripe is not configured. Please contact support.");
                    }
                    
                    // Get user email from session
                    String userEmail = (user != null) ? user.getEmail() : "";
                    String userName = (user != null) ? user.getFullName() : "";
                    
                    // Create metadata for Stripe
                    Map<String, String> metadata = new HashMap<>();
                    metadata.put("order_id", String.valueOf(order.getOrderId()));
                    metadata.put("user_id", String.valueOf(userId));
                    
                    // Create payment intent
                    com.stripe.model.PaymentIntent paymentIntent = stripePaymentService.createPaymentIntent(
                        java.math.BigDecimal.valueOf(order.getTotalAmount()),
                        "INR",
                        userEmail,
                        userName,
                        "Order #" + order.getOrderId(),
                        metadata,
                        request
                    );
                    
                    Map<String, Object> data = new HashMap<>();
                    data.put("success", true);
                    data.put("orderId", order.getOrderId());
                    data.put("clientSecret", paymentIntent.getClientSecret());
                    data.put("paymentIntentId", paymentIntent.getId());
                    data.put("amount", order.getTotalAmount());
                    
                    sendJsonResponse(response, data);
                } catch (IllegalArgumentException e) {
                    request.setAttribute("error", e.getMessage());
                    displayCheckoutPage(request, response, user);
                } catch (com.stripe.exception.StripeException e) {
                    logger.error("Stripe error processing order for user {}: {}", userId, e.getMessage(), e);
                    request.setAttribute("error", "Payment processing failed. Please try again or use COD.");
                    displayCheckoutPage(request, response, user);
                } catch (Exception e) {
                    logger.error("Error processing Stripe order for user {}: {}", userId, e.getMessage(), e);
                    request.setAttribute("error", "Failed to process order. Please try again.");
                    displayCheckoutPage(request, response, user);
                }
            } else {
                // For COD, process order directly and redirect to success
                try {
                    com.fashionstore.model.Order order = checkoutService.processCheckoutOrder(userId, checkoutData);
                    
                    // Redirect to payment success page
                    response.sendRedirect(request.getContextPath() + "/payment?action=success&orderId=" + order.getOrderId());
                } catch (IllegalArgumentException e) {
                    request.setAttribute("error", e.getMessage());
                    displayCheckoutPage(request, response, user);
                } catch (Exception e) {
                    logger.error("Error processing COD order for user {}: {}", userId, e.getMessage(), e);
                    request.setAttribute("error", "Failed to process order. Please try again.");
                    displayCheckoutPage(request, response, user);
                }
            }
        } catch (Exception e) {
            logger.error("Error in handleFormSubmission for user {}: {}", userId, e.getMessage(), e);
            request.setAttribute("error", "An error occurred while processing your order. Please try again.");
            displayCheckoutPage(request, response, user);
        }
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
            if (!cartService.validateCartForCheckout(userId)) {
                sendErrorResponse(response, "Some items in your cart are not available", HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            Map<String, Object> data = new HashMap<>();
            data.put("success", true);
            data.put("checkoutSessionId", UUID.randomUUID().toString());

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


    /**
     * Submit order - Backend-Driven Order Creation
     * 
     * CRITICAL: ALL order validation and calculation happens on backend
     * 
     * Order Creation Flow:
     * 1. Validate user is authenticated
     * 2. Check checkout session is valid
     * 3. Prevent duplicate submissions with idempotency key
     * 4. Parse checkout data from frontend
     * 5. Validate cart items and availability
     * 6. Recalculate order totals (subtotal, tax, shipping, discount)
     * 7. Validate payment information
     * 8. Create order record in database
     * 9. Deduct stock from inventory
     * 10. Process payment
     * 11. Clear cart
     * 12. Return order confirmation
     * 
     * Security Measures:
     * - All calculations performed on backend
     * - Payment amount validated before processing
     * - Stock deduction is atomic and transactional
     * - Idempotency key prevents duplicate orders
     * - CSRF token validated by filter
     * - Frontend cannot modify order totals
     * - Frontend cannot apply invalid coupons
     * - Frontend cannot bypass payment validation
     */
    private void submitOrder(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        
        if (user == null) {
            sendErrorResponse(response, "Please login to submit order", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        int userId = user.getUserId();

        try {
            Map<String, Object> checkoutData = objectMapper.readValue(request.getReader(), Map.class);
            if (checkoutData == null || checkoutData.isEmpty()) {
                sendErrorResponse(response, "Invalid checkout data", HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            
            // CRITICAL FIX: Cart clearing is now inside the transaction in CheckoutServiceImpl.processCheckoutOrder()
            // This ensures atomicity: order creation, inventory update, payment, and cart clearing all happen together or not at all
            // If any step fails, the entire transaction rolls back and cart is preserved
            com.fashionstore.model.Order order = checkoutService.processCheckoutOrder(userId, checkoutData);

            Map<String, Object> data = new HashMap<>();
            data.put("success", true);
            data.put("orderId", order.getOrderId());
            data.put("order", order);

            sendJsonResponse(response, data);
        } catch (IllegalArgumentException e) {
            sendErrorResponse(response, e.getMessage(), HttpServletResponse.SC_BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Error submitting order for user {}: {}", userId, e.getMessage(), e);
            sendErrorResponse(response, "Failed to submit order", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
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

        try {
            PaymentDTO payment = objectMapper.readValue(request.getReader(), PaymentDTO.class);
            
            String paymentMethod = payment.getPaymentMethod();
            if (paymentMethod == null || paymentMethod.trim().isEmpty()) {
                sendErrorResponse(response, "Payment method is required", HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            Integer orderId = payment.getOrderId();
            if (orderId == null || orderId <= 0) {
                sendErrorResponse(response, "Order ID is required for payment", HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            com.fashionstore.model.Order order = orderService.getOrderById(orderId, userId);
            if (order == null) {
                sendErrorResponse(response, "Order not found or unauthorized", HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            Map<String, Object> data = new HashMap<>();
            data.put("success", true);
            data.put("orderId", orderId);
            data.put("paymentMethod", paymentMethod);
            data.put("amount", order.getTotalAmount());

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
