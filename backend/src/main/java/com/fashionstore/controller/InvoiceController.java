package com.fashionstore.controller;

import com.fashionstore.model.User;
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
 * Controller for invoice generation and management
 * Handles invoice creation, download, and management
 */
@WebServlet("/invoice/*")
public class InvoiceController extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(InvoiceController.class);
    private ObjectMapper objectMapper;

    @Override
    public void init() {
        objectMapper = new ObjectMapper();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("customerAuth") : null;

        try {
            if ("/generate".equals(pathInfo)) {
                generateInvoice(request, response, user);
            } else if ("/download".equals(pathInfo)) {
                downloadInvoice(request, response, user);
            } else if ("/view".equals(pathInfo)) {
                viewInvoice(request, response, user);
            } else if ("/list".equals(pathInfo)) {
                listInvoices(request, response, user);
            } else if ("/details".equals(pathInfo)) {
                getInvoiceDetails(request, response, user);
            } else if ("/send-email".equals(pathInfo)) {
                sendInvoiceEmail(request, response, user);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Error in InvoiceController doGet: {}", e.getMessage(), e);
            sendErrorResponse(response, "Internal server error", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("customerAuth") : null;

        try {
            if ("/regenerate".equals(pathInfo)) {
                regenerateInvoice(request, response, user);
            } else if ("/mark-paid".equals(pathInfo)) {
                markInvoiceAsPaid(request, response, user);
            } else if ("/update-details".equals(pathInfo)) {
                updateInvoiceDetails(request, response, user);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Error in InvoiceController doPost: {}", e.getMessage(), e);
            sendErrorResponse(response, "Internal server error", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void generateInvoice(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        
        String orderId = request.getParameter("orderId");
        if (orderId == null || orderId.trim().isEmpty()) {
            sendErrorResponse(response, "Order ID is required", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // Placeholder implementation - invoice service not available
        Map<String, Object> invoiceResult = new HashMap<>();
        invoiceResult.put("success", false);
        invoiceResult.put("message", "Invoice generation not implemented");
        
        Map<String, Object> data = new HashMap<>();
        data.put("success", invoiceResult.get("success"));
        data.put("message", invoiceResult.get("message"));
        if (invoiceResult.containsKey("invoice")) {
            data.put("invoice", invoiceResult.get("invoice"));
        }
        if (invoiceResult.containsKey("invoiceId")) {
            data.put("invoiceId", invoiceResult.get("invoiceId"));
        }
        
        sendJsonResponse(response, data);
    }

    private void downloadInvoice(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        
        String invoiceId = request.getParameter("invoiceId");
        String orderId = request.getParameter("orderId");
        String format = request.getParameter("format"); // pdf, html
        
        if ((invoiceId == null || invoiceId.trim().isEmpty()) && 
            (orderId == null || orderId.trim().isEmpty())) {
            sendErrorResponse(response, "Invoice ID or Order ID is required", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        if (format == null || format.trim().isEmpty()) {
            format = "pdf";
        }

        // Generate invoice file
        // Placeholder implementation - invoice service not available
        byte[] invoiceData = "{}".getBytes();
        
        if (invoiceData == null) {
            sendErrorResponse(response, "Failed to generate invoice file", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }

        // Set response headers for file download
        String fileName = invoiceId != null ? 
            "invoice_" + invoiceId + "." + format : 
            "invoice_order_" + orderId + "." + format;
        
        response.setContentType(format.equals("pdf") ? "application/pdf" : "text/html");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        response.setContentLength(invoiceData.length);
        
        response.getOutputStream().write(invoiceData);
        response.getOutputStream().flush();
    }

    private void viewInvoice(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        
        String invoiceId = request.getParameter("invoiceId");
        String orderId = request.getParameter("orderId");
        
        if ((invoiceId == null || invoiceId.trim().isEmpty()) && 
            (orderId == null || orderId.trim().isEmpty())) {
            sendErrorResponse(response, "Invoice ID or Order ID is required", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // Placeholder implementation - invoice service not available
        Map<String, Object> invoiceData = new HashMap<>();
        invoiceData.put("invoiceId", invoiceId);
        invoiceData.put("orderId", orderId);
        
        Map<String, Object> data = new HashMap<>();
        data.put("success", true);
        data.put("invoiceData", invoiceData);
        
        sendJsonResponse(response, data);
    }

    private void listInvoices(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        
        if (user == null) {
            sendErrorResponse(response, "Please login to view invoices", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        int page = parseIntParameter(request.getParameter("page"), 1);
        int limit = parseIntParameter(request.getParameter("limit"), 10);
        String status = request.getParameter("status"); // paid, unpaid, overdue
        
        // Placeholder implementation - invoice service not available
        List<Map<String, Object>> invoices = new ArrayList<>();
        int totalCount = 0;
        
        Map<String, Object> data = new HashMap<>();
        data.put("success", true);
        data.put("invoices", invoices);
        data.put("page", page);
        data.put("limit", limit);
        data.put("totalCount", totalCount);
        data.put("totalPages", (int) Math.ceil((double) totalCount / limit));
        data.put("status", status);
        
        sendJsonResponse(response, data);
    }

    private void getInvoiceDetails(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        
        String invoiceId = request.getParameter("invoiceId");
        if (invoiceId == null || invoiceId.trim().isEmpty()) {
            sendErrorResponse(response, "Invoice ID is required", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // Placeholder implementation - invoice service not available
        Map<String, Object> invoiceDetails = new HashMap<>();
        invoiceDetails.put("invoiceId", invoiceId);
        
        Map<String, Object> data = new HashMap<>();
        data.put("success", true);
        data.put("invoiceDetails", invoiceDetails);
        
        sendJsonResponse(response, data);
    }

    private void sendInvoiceEmail(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        
        String invoiceId = request.getParameter("invoiceId");
        String email = request.getParameter("email");
        
        if (invoiceId == null || invoiceId.trim().isEmpty()) {
            sendErrorResponse(response, "Invoice ID is required", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // Placeholder implementation - invoice service not available
        boolean success = false;
        
        Map<String, Object> data = new HashMap<>();
        data.put("success", success);
        data.put("message", "Send invoice email not implemented");
        
        sendJsonResponse(response, data);
    }

    private void regenerateInvoice(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        
        String invoiceId = request.getParameter("invoiceId");
        if (invoiceId == null || invoiceId.trim().isEmpty()) {
            sendErrorResponse(response, "Invoice ID is required", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // Placeholder implementation - invoice service not available
        Map<String, Object> regenerateResult = new HashMap<>();
        regenerateResult.put("success", false);
        regenerateResult.put("message", "Regenerate invoice not implemented");
        
        Map<String, Object> data = new HashMap<>();
        data.put("success", regenerateResult.get("success"));
        data.put("message", regenerateResult.get("message"));
        if (regenerateResult.containsKey("invoice")) {
            data.put("invoice", regenerateResult.get("invoice"));
        }
        
        sendJsonResponse(response, data);
    }

    private void markInvoiceAsPaid(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        
        String invoiceId = request.getParameter("invoiceId");
        String paymentId = request.getParameter("paymentId");
        String paymentMethod = request.getParameter("paymentMethod");
        
        if (invoiceId == null || invoiceId.trim().isEmpty()) {
            sendErrorResponse(response, "Invoice ID is required", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // Placeholder implementation - invoice service not available
        boolean success = false;
        
        Map<String, Object> data = new HashMap<>();
        data.put("success", success);
        data.put("message", success ? "Invoice marked as paid" : "Failed to mark invoice as paid");
        
        sendJsonResponse(response, data);
    }

    private void updateInvoiceDetails(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        
        String invoiceId = request.getParameter("invoiceId");
        if (invoiceId == null || invoiceId.trim().isEmpty()) {
            sendErrorResponse(response, "Invoice ID is required", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // Parse invoice details from request body
        Map<String, Object> invoiceDetails = objectMapper.readValue(request.getReader(), Map.class);
        
        // Placeholder implementation - invoice service not available
        Map<String, Object> updateResult = new HashMap<>();
        updateResult.put("success", false);
        updateResult.put("message", "Update invoice details not implemented");
        
        Map<String, Object> data = new HashMap<>();
        data.put("success", updateResult.get("success"));
        data.put("message", updateResult.get("message"));
        if (updateResult.containsKey("invoice")) {
            data.put("invoice", updateResult.get("invoice"));
        }
        
        sendJsonResponse(response, data);
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
