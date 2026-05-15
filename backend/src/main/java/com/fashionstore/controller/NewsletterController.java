package com.fashionstore.controller;

import com.fashionstore.dto.NewsletterDTO;
import com.fashionstore.model.User;
import com.fashionstore.security.CSRFProtection;
import com.fashionstore.util.JsonUtil;
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

/**
 * Controller for handling newsletter subscriptions and email marketing
 * Provides newsletter signup, management, and campaign tracking
 */
@WebServlet("/api/newsletter/*")
public class NewsletterController extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(NewsletterController.class);
    private ObjectMapper objectMapper;

    @Override
    public void init() {
        objectMapper = new ObjectMapper();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();

        try {
            if ("/subscribe".equals(pathInfo)) {
                showSubscribePage(request, response);
            } else if ("/unsubscribe".equals(pathInfo)) {
                showUnsubscribePage(request, response);
            } else if ("/preferences".equals(pathInfo)) {
                showPreferencesPage(request, response);
            } else if ("/status".equals(pathInfo)) {
                getSubscriptionStatus(request, response);
            } else if ("/campaigns".equals(pathInfo)) {
                getCampaigns(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Error in NewsletterController doGet: {}", e.getMessage(), e);
            sendErrorResponse(response, "Internal server error", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();

        // CSRF validation for POST requests
        if (!CSRFProtection.validateRequest(request)) {
            sendErrorResponse(response, "Invalid CSRF token", HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        try {
            if ("/subscribe".equals(pathInfo)) {
                handleSubscription(request, response);
            } else if ("/unsubscribe".equals(pathInfo)) {
                handleUnsubscription(request, response);
            } else if ("/update-preferences".equals(pathInfo)) {
                updatePreferences(request, response);
            } else if ("/track-open".equals(pathInfo)) {
                trackEmailOpen(request, response);
            } else if ("/track-click".equals(pathInfo)) {
                trackEmailClick(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Error in NewsletterController doPost: {}", e.getMessage(), e);
            sendErrorResponse(response, "Internal server error", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void showSubscribePage(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setAttribute("_pageTitle", "Newsletter Subscription");
        request.setAttribute("_pageDescription", "Subscribe to FashionStore newsletter for exclusive offers and fashion updates.");
        request.getRequestDispatcher("/WEB-INF/views/newsletter/subscribe.jsp").forward(request, response);
    }

    private void showUnsubscribePage(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String token = request.getParameter("token");
        String email = request.getParameter("email");
        
        request.setAttribute("_pageTitle", "Unsubscribe Newsletter");
        request.setAttribute("_pageDescription", "Unsubscribe from FashionStore newsletter.");
        request.setAttribute("token", token);
        request.setAttribute("email", email);
        
        request.getRequestDispatcher("/WEB-INF/views/newsletter/unsubscribe.jsp").forward(request, response);
    }

    private void showPreferencesPage(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String token = request.getParameter("token");
        String email = request.getParameter("email");
        
        request.setAttribute("_pageTitle", "Newsletter Preferences");
        request.setAttribute("_pageDescription", "Manage your FashionStore newsletter preferences.");
        request.setAttribute("token", token);
        request.setAttribute("email", email);
        
        request.getRequestDispatcher("/WEB-INF/views/newsletter/preferences.jsp").forward(request, response);
    }

    private void getSubscriptionStatus(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        String email = request.getParameter("email");
        
        if (email == null || email.trim().isEmpty()) {
            sendErrorResponse(response, "Email is required", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // Placeholder implementation - newsletter service not available
        NewsletterDTO subscription = null;
        
        Map<String, Object> data = new HashMap<>();
        if (subscription != null) {
            data.put("success", true);
            data.put("subscribed", true);
            data.put("subscription", subscription);
        } else {
            data.put("success", true);
            data.put("subscribed", false);
        }
        
        sendJsonResponse(response, data);
    }

    private void getCampaigns(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        int limit = parseIntParameter(request.getParameter("limit"), 10);
        // Placeholder implementation - newsletter service not available
        List<Map<String, Object>> campaigns = new ArrayList<>();
        
        Map<String, Object> data = new HashMap<>();
        data.put("success", true);
        data.put("campaigns", campaigns);
        data.put("count", campaigns.size());
        
        sendJsonResponse(response, data);
    }

    private void handleSubscription(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        String email = request.getParameter("email");
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String preferences = request.getParameter("preferences");
        
        if (email == null || email.trim().isEmpty()) {
            sendErrorResponse(response, "Email is required", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        NewsletterDTO subscription = new NewsletterDTO();
        subscription.setEmail(email.trim());
        subscription.setFirstName(firstName);
        subscription.setLastName(lastName);
        subscription.setPreferences(preferences);
        
        // Placeholder implementation - newsletter service not available
        boolean success = false;
        
        Map<String, Object> data = new HashMap<>();
        data.put("success", success);
        data.put("message", success ? "Successfully subscribed to newsletter" : "Failed to subscribe");
        data.put("email", email);
        
        sendJsonResponse(response, data);
    }

    private void handleUnsubscription(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        String email = request.getParameter("email");
        String token = request.getParameter("token");
        
        if (email == null || email.trim().isEmpty()) {
            sendErrorResponse(response, "Email is required", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // Placeholder implementation - newsletter service not available
        boolean success = false;
        
        Map<String, Object> data = new HashMap<>();
        data.put("success", success);
        data.put("message", success ? "Successfully unsubscribed from newsletter" : "Failed to unsubscribe");
        
        sendJsonResponse(response, data);
    }

    private void updatePreferences(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        String email = request.getParameter("email");
        String token = request.getParameter("token");
        String preferences = request.getParameter("preferences");
        
        if (email == null || email.trim().isEmpty()) {
            sendErrorResponse(response, "Email is required", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // Placeholder implementation - newsletter service not available
        boolean success = false;
        
        Map<String, Object> data = new HashMap<>();
        data.put("success", success);
        data.put("message", success ? "Preferences updated successfully" : "Failed to update preferences");
        
        sendJsonResponse(response, data);
    }

    private void trackEmailOpen(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        String campaignId = request.getParameter("campaignId");
        String subscriberId = request.getParameter("subscriberId");
        
        if (campaignId == null || subscriberId == null) {
            sendErrorResponse(response, "Campaign ID and Subscriber ID are required", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // Placeholder implementation - newsletter service not available
        boolean success = false;
        
        // Return 1x1 transparent pixel for email tracking
        if (success) {
            response.setContentType("image/gif");
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Expires", "0");
            
            // 1x1 transparent GIF
            byte[] pixel = {
                0x47, 0x49, 0x46, 0x38, 0x39, 0x61, 0x01, 0x00, 0x01, 0x00, (byte)0x80, 0x00, 0x00, (byte)0xff, (byte)0xff, (byte)0xff,
                0x00, 0x00, 0x00, 0x21, (byte)0xf9, 0x04, 0x01, 0x00, 0x00, 0x00, 0x2c, 0x00, 0x00, 0x00, 0x00,
                0x01, 0x00, 0x01, 0x00, 0x00, 0x02, 0x02, 0x04, 0x01, 0x00, 0x3b
            };
            response.getOutputStream().write(pixel);
        } else {
            sendErrorResponse(response, "Tracking failed", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void trackEmailClick(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        String campaignId = request.getParameter("campaignId");
        String subscriberId = request.getParameter("subscriberId");
        String linkUrl = request.getParameter("url");
        
        if (campaignId == null || subscriberId == null || linkUrl == null) {
            sendErrorResponse(response, "Campaign ID, Subscriber ID, and URL are required", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // Placeholder implementation - newsletter service not available
        boolean success = false;
        
        if (success) {
            // Redirect to the original URL
            response.sendRedirect(linkUrl);
        } else {
            sendErrorResponse(response, "Tracking failed", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
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

    private int parseIntParameter(String value, int defaultValue) {
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
