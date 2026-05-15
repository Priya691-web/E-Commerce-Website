package com.fashionstore.exception;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Exception handler for controllers
 * Provides standardized exception handling for all controller methods
 */
public class ControllerExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(ControllerExceptionHandler.class);

    /**
     * Handle exceptions in controller methods
     * @param request HTTP request
     * @param response HTTP response
     * @param exception Exception to handle
     * @param controllerName Name of the controller
     * @param actionName Name of the action/method
     */
    public static void handleControllerException(HttpServletRequest request,
                                                HttpServletResponse response,
                                                Exception exception,
                                                String controllerName,
                                                String actionName) {
        
        ExceptionContext context = ExceptionContext.builder()
                .path(request.getRequestURI())
                .method(request.getMethod())
                .controller(controllerName)
                .action(actionName)
                .sessionId(request.getSession(false) != null ? request.getSession(false).getId() : null)
                .build();

        GlobalExceptionHandler.handleException(request, response, exception, context);
    }

    /**
     * Handle exceptions in controller methods with user context
     * @param request HTTP request
     * @param response HTTP response
     * @param exception Exception to handle
     * @param controllerName Name of the controller
     * @param actionName Name of the action/method
     * @param userId User ID if available
     */
    public static void handleControllerException(HttpServletRequest request,
                                                HttpServletResponse response,
                                                Exception exception,
                                                String controllerName,
                                                String actionName,
                                                Long userId) {
        
        ExceptionContext context = ExceptionContext.builder()
                .path(request.getRequestURI())
                .method(request.getMethod())
                .controller(controllerName)
                .action(actionName)
                .userId(userId)
                .sessionId(request.getSession(false) != null ? request.getSession(false).getId() : null)
                .build();

        GlobalExceptionHandler.handleException(request, response, exception, context);
    }

    /**
     * Handle exceptions in controller methods with additional context
     * @param request HTTP request
     * @param response HTTP response
     * @param exception Exception to handle
     * @param controllerName Name of the controller
     * @param actionName Name of the action/method
     * @param context Additional context
     */
    public static void handleControllerException(HttpServletRequest request,
                                                HttpServletResponse response,
                                                Exception exception,
                                                String controllerName,
                                                String actionName,
                                                ExceptionContext context) {
        
        if (context == null) {
            context = ExceptionContext.builder()
                    .path(request.getRequestURI())
                    .method(request.getMethod())
                    .controller(controllerName)
                    .action(actionName)
                    .sessionId(request.getSession(false) != null ? request.getSession(false).getId() : null)
                    .build();
        }

        GlobalExceptionHandler.handleException(request, response, exception, context);
    }

    /**
     * Wrap controller method execution with exception handling
     * @param request HTTP request
     * @param response HTTP response
     * @param controllerName Name of the controller
     * @param actionName Name of the action/method
     * @param runnable Code to execute
     */
    public static void executeWithExceptionHandling(HttpServletRequest request,
                                                   HttpServletResponse response,
                                                   String controllerName,
                                                   String actionName,
                                                   ControllerRunnable runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            handleControllerException(request, response, e, controllerName, actionName);
        }
    }

    /**
     * Wrap controller method execution with exception handling and user context
     * @param request HTTP request
     * @param response HTTP response
     * @param controllerName Name of the controller
     * @param actionName Name of the action/method
     * @param userId User ID
     * @param runnable Code to execute
     */
    public static void executeWithExceptionHandling(HttpServletRequest request,
                                                   HttpServletResponse response,
                                                   String controllerName,
                                                   String actionName,
                                                   Long userId,
                                                   ControllerRunnable runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            handleControllerException(request, response, e, controllerName, actionName, userId);
        }
    }

    /**
     * Functional interface for controller methods
     */
    @FunctionalInterface
    public interface ControllerRunnable {
        void run() throws ServletException, IOException;
    }
}
