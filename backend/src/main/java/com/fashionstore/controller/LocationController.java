package com.fashionstore.controller;

import com.fashionstore.dto.DeliveryLocationDTO;
import com.fashionstore.model.User;
import com.fashionstore.security.CSRFProtection;
import com.fashionstore.serviceimpl.LocationServiceImpl;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for handling location-based operations
 * Delivery location management, pincode validation, and delivery estimates
 */
@WebServlet("/api/location/*")
public class LocationController extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(LocationController.class);
    private LocationServiceImpl locationService;
    private ObjectMapper objectMapper;

    @Override
    public void init() {
        locationService = new LocationServiceImpl();
        objectMapper = new ObjectMapper();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("customerAuth") : null;

        try {
            if ("/validate-pincode".equals(pathInfo)) {
                validatePincode(request, response);
            } else if ("/saved-locations".equals(pathInfo)) {
                getSavedLocations(request, response, user);
            } else if ("/detect-location".equals(pathInfo)) {
                detectUserLocation(request, response);
            } else if ("/delivery-estimate".equals(pathInfo)) {
                getDeliveryEstimate(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Error in LocationController doGet: {}", e.getMessage(), e);
            sendErrorResponse(response, "Internal server error", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("customerAuth") : null;

        // CSRF validation for POST requests
        if (!CSRFProtection.validateRequest(request)) {
            sendErrorResponse(response, "Invalid CSRF token", HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        try {
            if ("/save-location".equals(pathInfo)) {
                saveLocation(request, response, user);
            } else if ("/update-location".equals(pathInfo)) {
                updateLocation(request, response, user);
            } else if ("/delete-location".equals(pathInfo)) {
                deleteLocation(request, response, user);
            } else if ("/set-default".equals(pathInfo)) {
                setDefaultLocation(request, response, user);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Error in LocationController doPost: {}", e.getMessage(), e);
            sendErrorResponse(response, "Internal server error", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void validatePincode(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        String pincode = request.getParameter("pincode");
        
        if (pincode == null || pincode.trim().isEmpty()) {
            sendErrorResponse(response, "Pincode is required", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        DeliveryLocationDTO location = locationService.validatePincode(pincode);
        
        Map<String, Object> data = new HashMap<>();
        if (location != null) {
            data.put("success", true);
            data.put("location", location);
            data.put("message", "Pincode is serviceable");
        } else {
            data.put("success", false);
            data.put("message", "Pincode not serviceable");
        }
        
        sendJsonResponse(response, data);
    }

    private void getSavedLocations(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        if (user == null) {
            sendErrorResponse(response, "Please login to view saved locations", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        List<DeliveryLocationDTO> locations = locationService.getSavedLocations(user.getUserId());
        
        Map<String, Object> data = new HashMap<>();
        data.put("success", true);
        data.put("locations", locations);
        data.put("count", locations.size());
        
        sendJsonResponse(response, data);
    }

    private void detectUserLocation(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        try {
            // Get client IP for geolocation
            String clientIP = getClientIP(request);
            DeliveryLocationDTO detectedLocation = locationService.detectLocationByIP(clientIP);
            
            Map<String, Object> data = new HashMap<>();
            if (detectedLocation != null) {
                data.put("success", true);
                data.put("location", detectedLocation);
                data.put("message", "Location detected successfully");
            } else {
                data.put("success", false);
                data.put("message", "Could not detect location");
            }
            
            sendJsonResponse(response, data);
        } catch (Exception e) {
            logger.error("Error detecting user location: {}", e.getMessage(), e);
            sendErrorResponse(response, "Could not detect location", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void getDeliveryEstimate(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        String pincode = request.getParameter("pincode");
        
        if (pincode == null || pincode.trim().isEmpty()) {
            sendErrorResponse(response, "Pincode is required", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        DeliveryLocationDTO location = locationService.getDeliveryEstimate(pincode);
        
        Map<String, Object> data = new HashMap<>();
        if (location != null) {
            data.put("success", true);
            data.put("estimate", location);
            data.put("message", "Delivery estimate calculated");
        } else {
            data.put("success", false);
            data.put("message", "Could not calculate delivery estimate");
        }
        
        sendJsonResponse(response, data);
    }

    private void saveLocation(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        if (user == null) {
            sendErrorResponse(response, "Please login to save locations", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        try {
            DeliveryLocationDTO location = objectMapper.readValue(request.getReader(), DeliveryLocationDTO.class);
            location.setUserId(user.getUserId());
            
            boolean success = locationService.saveLocation(location);
            
            Map<String, Object> data = new HashMap<>();
            data.put("success", success);
            data.put("message", success ? "Location saved successfully" : "Failed to save location");
            data.put("location", success ? location : null);
            
            sendJsonResponse(response, data);
        } catch (Exception e) {
            logger.error("Error saving location: {}", e.getMessage(), e);
            sendErrorResponse(response, "Invalid location data", HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private void updateLocation(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        if (user == null) {
            sendErrorResponse(response, "Please login to update locations", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        try {
            DeliveryLocationDTO location = objectMapper.readValue(request.getReader(), DeliveryLocationDTO.class);
            location.setUserId(user.getUserId());
            
            boolean success = locationService.updateLocation(location);
            
            Map<String, Object> data = new HashMap<>();
            data.put("success", success);
            data.put("message", success ? "Location updated successfully" : "Failed to update location");
            
            sendJsonResponse(response, data);
        } catch (Exception e) {
            logger.error("Error updating location: {}", e.getMessage(), e);
            sendErrorResponse(response, "Invalid location data", HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private void deleteLocation(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        if (user == null) {
            sendErrorResponse(response, "Please login to delete locations", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        try {
            int locationId = Integer.parseInt(request.getParameter("locationId"));
            boolean success = locationService.deleteLocation(locationId, user.getUserId());
            
            Map<String, Object> data = new HashMap<>();
            data.put("success", success);
            data.put("message", success ? "Location deleted successfully" : "Failed to delete location");
            
            sendJsonResponse(response, data);
        } catch (NumberFormatException e) {
            sendErrorResponse(response, "Invalid location ID", HttpServletResponse.SC_BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Error deleting location: {}", e.getMessage(), e);
            sendErrorResponse(response, "Failed to delete location", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void setDefaultLocation(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        if (user == null) {
            sendErrorResponse(response, "Please login to set default location", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        try {
            int locationId = Integer.parseInt(request.getParameter("locationId"));
            boolean success = locationService.setDefaultLocation(locationId, user.getUserId());
            
            Map<String, Object> data = new HashMap<>();
            data.put("success", success);
            data.put("message", success ? "Default location updated successfully" : "Failed to update default location");
            
            sendJsonResponse(response, data);
        } catch (NumberFormatException e) {
            sendErrorResponse(response, "Invalid location ID", HttpServletResponse.SC_BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Error setting default location: {}", e.getMessage(), e);
            sendErrorResponse(response, "Failed to update default location", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
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

    private String getClientIP(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIP = request.getHeader("X-Real-IP");
        if (xRealIP != null && !xRealIP.isEmpty()) {
            return xRealIP;
        }
        
        return request.getRemoteAddr();
    }
}
