package com.fashionstore.util;

import com.fashionstore.dto.response.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * Response Helper Utility
 * Centralized response writing for controllers
 * Eliminates duplicate response handling code across controllers
 */
public class ResponseHelper {
    
    private static final Logger logger = LoggerFactory.getLogger(ResponseHelper.class);
    
    private static final String CONTENT_TYPE_JSON = "application/json";
    private static final String ENCODING_UTF8 = "UTF-8";
    
    /**
     * Send JSON response
     * @param response HTTP response
     * @param data Data to send
     * @throws IOException If writing fails
     */
    public static void sendJsonResponse(HttpServletResponse response, Object data) throws IOException {
        response.setContentType(CONTENT_TYPE_JSON);
        response.setCharacterEncoding(ENCODING_UTF8);
        PrintWriter out = response.getWriter();
        out.write(JsonUtil.toJson(data));
        out.flush();
    }
    
    /**
     * Send JSON response with custom status
     * @param response HTTP response
     * @param status HTTP status code
     * @param data Data to send
     * @throws IOException If writing fails
     */
    public static void sendJsonResponse(HttpServletResponse response, int status, Object data) throws IOException {
        response.setStatus(status);
        sendJsonResponse(response, data);
    }
    
    /**
     * Send success response using ApiResponse wrapper
     * @param response HTTP response
     * @param data Data to send
     * @throws IOException If writing fails
     */
    public static <T> void sendSuccessResponse(HttpServletResponse response, T data) throws IOException {
        ApiResponse<T> apiResponse = ApiResponse.success(data);
        sendJsonResponse(response, apiResponse);
    }
    
    /**
     * Send success response with message
     * @param response HTTP response
     * @param data Data to send
     * @param message Success message
     * @throws IOException If writing fails
     */
    public static <T> void sendSuccessResponse(HttpServletResponse response, T data, String message) throws IOException {
        ApiResponse<T> apiResponse = ApiResponse.success(data, message);
        sendJsonResponse(response, apiResponse);
    }
    
    /**
     * Send error response
     * @param response HTTP response
     * @param message Error message
     * @throws IOException If writing fails
     */
    public static void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
        ApiResponse<Object> apiResponse = ApiResponse.error(message);
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        sendJsonResponse(response, apiResponse);
    }
    
    /**
     * Send error response with status
     * @param response HTTP response
     * @param status HTTP status code
     * @param message Error message
     * @throws IOException If writing fails
     */
    public static void sendErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        ApiResponse<Object> apiResponse = ApiResponse.error(message);
        response.setStatus(status);
        sendJsonResponse(response, apiResponse);
    }
    
    /**
     * Send error response with error code
     * @param response HTTP response
     * @param status HTTP status code
     * @param message Error message
     * @param errorCode Error code
     * @throws IOException If writing fails
     */
    public static void sendErrorResponse(HttpServletResponse response, int status, String message, String errorCode) throws IOException {
        ApiResponse<Object> apiResponse = ApiResponse.error(message, errorCode);
        response.setStatus(status);
        sendJsonResponse(response, apiResponse);
    }
    
    /**
     * Send not found response
     * @param response HTTP response
     * @param message Error message
     * @throws IOException If writing fails
     */
    public static void sendNotFoundResponse(HttpServletResponse response, String message) throws IOException {
        ApiResponse<Object> apiResponse = ApiResponse.notFound(message);
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        sendJsonResponse(response, apiResponse);
    }
    
    /**
     * Send validation error response
     * @param response HTTP response
     * @param message Error message
     * @throws IOException If writing fails
     */
    public static void sendValidationErrorResponse(HttpServletResponse response, String message) throws IOException {
        ApiResponse<Object> apiResponse = ApiResponse.validationError(message);
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        sendJsonResponse(response, apiResponse);
    }
    
    /**
     * Send unauthorized response
     * @param response HTTP response
     * @param message Error message
     * @throws IOException If writing fails
     */
    public static void sendUnauthorizedResponse(HttpServletResponse response, String message) throws IOException {
        ApiResponse<Object> apiResponse = ApiResponse.unauthorized(message);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        sendJsonResponse(response, apiResponse);
    }
    
    /**
     * Send forbidden response
     * @param response HTTP response
     * @param message Error message
     * @throws IOException If writing fails
     */
    public static void sendForbiddenResponse(HttpServletResponse response, String message) throws IOException {
        ApiResponse<Object> apiResponse = ApiResponse.forbidden(message);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        sendJsonResponse(response, apiResponse);
    }
    
    /**
     * Send server error response
     * @param response HTTP response
     * @param message Error message
     * @throws IOException If writing fails
     */
    public static void sendServerErrorResponse(HttpServletResponse response, String message) throws IOException {
        ApiResponse<Object> apiResponse = ApiResponse.serverError(message);
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        sendJsonResponse(response, apiResponse);
    }
    
    /**
     * Safe send response with error handling
     * @param response HTTP response
     * @param data Data to send
     */
    public static void safeSendJsonResponse(HttpServletResponse response, Object data) {
        try {
            sendJsonResponse(response, data);
        } catch (IOException e) {
            logger.error("Failed to send JSON response", e);
        }
    }
    
    /**
     * Safe send success response with error handling
     * @param response HTTP response
     * @param data Data to send
     */
    public static <T> void safeSendSuccessResponse(HttpServletResponse response, T data) {
        try {
            sendSuccessResponse(response, data);
        } catch (IOException e) {
            logger.error("Failed to send success response", e);
        }
    }
    
    /**
     * Safe send error response with error handling
     * @param response HTTP response
     * @param message Error message
     */
    public static void safeSendErrorResponse(HttpServletResponse response, String message) {
        try {
            sendErrorResponse(response, message);
        } catch (IOException e) {
            logger.error("Failed to send error response", e);
        }
    }
}
