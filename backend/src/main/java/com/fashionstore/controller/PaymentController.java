package com.fashionstore.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fashionstore.dao.CartDAO;
import com.fashionstore.dao.OrderDAO;
import com.fashionstore.daoimpl.CartDAOImpl;
import com.fashionstore.daoimpl.OrderDAOImpl;
import com.fashionstore.model.CartItem;
import com.fashionstore.model.Order;
import com.fashionstore.model.Payment;
import com.fashionstore.model.User;
import com.fashionstore.service.PaymentService;
import com.fashionstore.service.StripePaymentService;
import com.fashionstore.util.AuditLogger;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Payment controller for handling payment operations
 * Supports Razorpay, Stripe, and COD
 */
@WebServlet("/payment")
public class PaymentController extends HttpServlet {
    
    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);
    private PaymentService paymentService;
    private StripePaymentService stripePaymentService;
    private CartDAO cartDAO;
    private OrderDAO orderDAO;
    
    @Override
    public void init() throws ServletException {
        paymentService = new PaymentService();
        stripePaymentService = new StripePaymentService();
        cartDAO = new CartDAOImpl();
        orderDAO = new OrderDAOImpl();
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        
        if ("success".equals(action)) {
            handlePaymentSuccess(req, resp);
        } else if ("failure".equals(action)) {
            handlePaymentFailure(req, resp);
        } else if ("stripe-webhook".equals(action)) {
            handleStripeWebhook(req, resp);
        } else if ("webhook".equals(action)) {
            handleWebhook(req, resp);
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action");
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        
        if ("initiate".equals(action)) {
            initiatePayment(req, resp);
        } else if ("verify".equals(action)) {
            verifyPayment(req, resp);
        } else if ("stripe-webhook".equals(action)) {
            handleStripeWebhook(req, resp);
        } else if ("webhook".equals(action)) {
            handleWebhook(req, resp);
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action");
        }
    }
    
    /**
     * Initiate payment for an order
     */
    private void initiatePayment(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("customerAuth") : null;
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        // Read the userId from the User attribute (every other controller uses this pattern).
        // The previous "(int) session.getAttribute(\"userId\")" cast NPE'd on auto-unbox
        // when the legacy attribute was missing.
        int userId = user.getUserId();
        String paymentMethod = req.getParameter("paymentMethod");
        
        if (paymentMethod == null || paymentMethod.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Payment method is required");
            return;
        }
        
        try {
            // Validate user ID
            if (userId <= 0) {
                logger.error("Invalid user ID in payment initiation: {}", userId);
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid user session");
                return;
            }
            
            // Get cart items
            List<CartItem> cartItems = cartDAO.getCartItemsByUserId(userId);
            if (cartItems == null || cartItems.isEmpty()) {
                logger.warn("Empty cart for user {} during payment initiation", userId);
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Cart is empty");
                return;
            }
            
            // Validate cart items and calculate total
            double total = 0;
            for (CartItem item : cartItems) {
                if (item == null || item.getPrice() <= 0 || item.getQuantity() <= 0) {
                    logger.error("Invalid cart item found for user {}: {}", userId, item);
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid cart items");
                    return;
                }
                total += item.getPrice() * item.getQuantity();
            }
            
            // Validate total amount
            if (total <= 0 || total > 1000000) { // Reasonable upper limit
                logger.error("Invalid total amount for user {}: {}", userId, total);
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid total amount");
                return;
            }
            
            // Create order (pending payment)
            int orderId = createOrder(userId, total, paymentMethod, req);
            
            if (orderId == -1) {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to create order");
                return;
            }
            
            // Create payment record
            if ("COD".equals(paymentMethod)) {
                paymentService.processCODPayment(orderId, BigDecimal.valueOf(total), req);
                // For COD, redirect directly to success
                resp.sendRedirect(req.getContextPath() + "/payment?action=success&orderId=" + orderId);
            } else if ("RAZORPAY".equals(paymentMethod)) {
                String razorpayOrderId = "rzp_order_" + System.currentTimeMillis();
                paymentService.processRazorpayPayment(orderId, BigDecimal.valueOf(total), razorpayOrderId, req);
                // Return Razorpay order ID to frontend
                resp.setContentType("application/json");
                resp.getWriter().write("{\"razorpayOrderId\":\"" + razorpayOrderId + "\",\"amount\":" + (int)(total * 100) + ",\"orderId\":" + orderId + "}");
            } else if ("STRIPE".equals(paymentMethod)) {
                // Create real Stripe Payment Intent
                try {
                    if (!stripePaymentService.isConfigured()) {
                        resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Stripe is not configured");
                        return;
                    }
                    
                    // Get user email/name from the authenticated User (session attributes
                    // "email" / "fullName" are not set by LoginController, only "user").
                    String email = user.getEmail();
                    String name = user.getFullName();
                    
                    // Create metadata for the payment
                    Map<String, String> metadata = new HashMap<>();
                    metadata.put("order_id", String.valueOf(orderId));
                    metadata.put("user_id", String.valueOf(userId));
                    
                    // Create Stripe Payment Intent
                    PaymentIntent paymentIntent = stripePaymentService.createPaymentIntent(
                        BigDecimal.valueOf(total),
                        "INR",
                        email,
                        name,
                        "FashionStore Order #" + orderId,
                        metadata,
                        req
                    );
                    
                    // Create payment record with Stripe payment intent ID
                    paymentService.processStripePayment(orderId, BigDecimal.valueOf(total), paymentIntent.getId(), req);
                    
                    // Return Stripe client secret to frontend
                    resp.setContentType("application/json");
                    resp.getWriter().write("{\"clientSecret\":\"" + paymentIntent.getClientSecret() + "\",\"paymentIntentId\":\"" + paymentIntent.getId() + "\",\"amount\":" + (int)(total * 100) + ",\"orderId\":" + orderId + "}");
                    
                } catch (StripeException e) {
                    logger.error("Stripe error in PaymentController.initiatePayment: {}", e.getMessage(), e);
                    resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error creating Stripe payment intent");
                    return;
                }
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid payment method");
            }
            
        } catch (Exception e) {
            logger.error("Error in PaymentController.initiatePayment: {}", e.getMessage(), e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error initiating payment");
        }
    }
    
    /**
     * Verify payment after completion
     */
    private void verifyPayment(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String paymentMethod = req.getParameter("paymentMethod");
        Integer orderIdBoxed = parseIntOrNull(req.getParameter("orderId"));
        if (orderIdBoxed == null || orderIdBoxed <= 0) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid order id");
            return;
        }
        int orderId = orderIdBoxed;

        try {
            if ("RAZORPAY".equals(paymentMethod)) {
                String razorpayOrderId = req.getParameter("razorpayOrderId");
                String razorpayPaymentId = req.getParameter("razorpayPaymentId");
                String razorpaySignature = req.getParameter("razorpaySignature");
                
                // Verify signature to prevent fake confirmations
                boolean isValid = paymentService.verifyRazorpaySignature(razorpayOrderId, razorpayPaymentId, razorpaySignature, req);
                
                if (!isValid) {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid payment signature");
                    return;
                }
                
                // Update payment status
                Payment payment = paymentService.getPaymentByOrderId(orderId);
                if (payment != null) {
                    paymentService.handlePaymentSuccess(payment.getPaymentId(), razorpayPaymentId, req);
                    paymentService.markPaymentVerified(payment.getPaymentId(), razorpayPaymentId, req);
                }
                
                resp.sendRedirect(req.getContextPath() + "/payment?action=success&orderId=" + orderId);
                
            } else if ("STRIPE".equals(paymentMethod)) {
                String stripePaymentIntentId = req.getParameter("paymentIntentId");
                
                // Update payment status
                Payment payment = paymentService.getPaymentByOrderId(orderId);
                if (payment != null) {
                    paymentService.handlePaymentSuccess(payment.getPaymentId(), stripePaymentIntentId, req);
                }
                
                resp.sendRedirect(req.getContextPath() + "/payment?action=success&orderId=" + orderId);
            }
            
        } catch (Exception e) {
            resp.sendRedirect(req.getContextPath() + "/payment?action=failure&orderId=" + orderId);
        }
    }
    
    /**
     * Handle payment success page
     */
    private void handlePaymentSuccess(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        renderOrderResultPage(req, resp, "/WEB-INF/views/payment-success.jsp", "Error loading payment success page");
    }

    /**
     * Handle payment failure page
     */
    private void handlePaymentFailure(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        renderOrderResultPage(req, resp, "/WEB-INF/views/payment-failure.jsp", "Error loading payment failure page");
    }

    /**
     * Shared loader for the success/failure pages.
     * Validates orderId, looks up the order via the canonical OrderDAO, and verifies the order
     * belongs to the currently authenticated user before rendering it.
     */
    private void renderOrderResultPage(HttpServletRequest req, HttpServletResponse resp,
                                       String view, String genericError)
            throws ServletException, IOException {
        Integer orderIdBoxed = parseIntOrNull(req.getParameter("orderId"));
        if (orderIdBoxed == null || orderIdBoxed <= 0) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid order id");
            return;
        }

        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("customerAuth") : null;
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        try {
            Order order = orderDAO.getOrderById(orderIdBoxed);
            if (order == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Order not found");
                return;
            }
            // Ownership check: a payment result page must only be shown to the order's owner
            // (admins can view the order from /admin/orders).
            if (order.getUserId() != user.getUserId() && !user.isAdmin()) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            req.setAttribute("order", order);
            req.getRequestDispatcher(view).forward(req, resp);
        } catch (Exception e) {
            logger.error("Error rendering payment result page for order #{}: {}", orderIdBoxed, e.getMessage(), e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, genericError);
        }
    }
    
    /**
     * Handle webhook callbacks from payment gateways
     */
    private void handleWebhook(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Read webhook payload
        StringBuilder buffer = new StringBuilder();
        String line;
        try (BufferedReader reader = req.getReader()) {
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
        }
        
        String payload = buffer.toString();
        
        try {
            // Parse webhook payload (simplified - in production, use proper JSON parsing)
            String webhookId = "webhook_" + System.currentTimeMillis();
            
            // Extract payment details from webhook
            String paymentId = extractPaymentIdFromWebhook(payload);
            
            if (paymentId != null) {
                Payment payment = paymentService.getPaymentByTransactionId(paymentId);
                if (payment != null) {
                    paymentService.markPaymentVerified(payment.getPaymentId(), webhookId, req);
                }
            }
            
            resp.setStatus(HttpServletResponse.SC_OK);
            
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Handle Stripe webhook with signature verification
     */
    private void handleStripeWebhook(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Read webhook payload
        StringBuilder buffer = new StringBuilder();
        String line;
        try (BufferedReader reader = req.getReader()) {
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
        }
        
        String payload = buffer.toString();
        String signatureHeader = req.getHeader("Stripe-Signature");
        
        try {
            // Verify webhook signature
            boolean isValid = stripePaymentService.verifyWebhookSignature(payload, signatureHeader, req);
            
            if (!isValid) {
                logger.warn("Invalid Stripe webhook signature");
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            
            // Parse webhook event
            com.stripe.model.Event event = stripePaymentService.parseWebhookEvent(payload);
            
            // Handle different event types
            String eventType = event.getType();
            
            switch (eventType) {
                case "payment_intent.succeeded":
                    stripePaymentService.handlePaymentSucceeded(event, req);
                    break;
                case "payment_intent.payment_failed":
                    stripePaymentService.handlePaymentFailed(event, req);
                    break;
                case "charge.refunded":
                    // Handle refund if needed
                    logger.info("Stripe refund received: {}", event.getId());
                    break;
                default:
                    logger.info("Unhandled Stripe webhook event type: {}", eventType);
            }
            
            resp.setStatus(HttpServletResponse.SC_OK);
            
        } catch (Exception e) {
            logger.error("Error handling Stripe webhook: {}", e.getMessage(), e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Create order using the canonical OrderDAO so the row honours every NOT NULL column
     * declared in the schema (subtotal, full_name, address, city, ...). The previous hand-rolled
     * INSERT only populated 4 columns and broke the moment the DB enforced the schema.
     */
    private int createOrder(int userId, double total, String paymentMethod, HttpServletRequest req) {
        Order order = new Order();
        order.setUserId(userId);
        order.setTotalAmount(total);
        order.setPaymentMethod(paymentMethod);
        order.setStatus("Pending");
        // Shipping fields fall through to OrderDAO defaults (empty strings); callers that
        // route through CheckoutController already populate the proper Order shape.
        order.setFullName(req.getParameter("fullName"));
        order.setAddress(req.getParameter("address"));
        order.setCity(req.getParameter("city"));
        order.setState(req.getParameter("state"));
        order.setZip(req.getParameter("zip"));
        order.setPhone(req.getParameter("phone"));

        int orderId = orderDAO.createOrder(order);
        if (orderId > 0) {
            AuditLogger.log("ORDER_CREATED", "Order created: " + orderId + " for user: " + userId,
                    String.valueOf(userId), req);
            return orderId;
        }
        logger.error("PaymentController.createOrder failed for user #{}", userId);
        return -1;
    }

    /**
     * Extract payment ID from webhook payload (simplified)
     */
    private String extractPaymentIdFromWebhook(String payload) {
        // In production, parse JSON and extract relevant fields.
        if (payload == null) return null;
        if (payload.contains("razorpay_payment_id")) {
            int start = payload.indexOf("razorpay_payment_id") + 21;
            if (start <= 21 || start >= payload.length()) return null;
            int end = payload.indexOf("\"", start);
            if (end < 0) return null; // malformed payload
            return payload.substring(start, end);
        }
        return null;
    }

    private static Integer parseIntOrNull(String value) {
        if (value == null || value.isBlank()) return null;
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
