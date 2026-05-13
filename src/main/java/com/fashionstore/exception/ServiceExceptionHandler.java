package com.fashionstore.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Exception handler for service layer
 * Provides standardized exception handling for service methods
 */
public class ServiceExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(ServiceExceptionHandler.class);

    /**
     * Handle exceptions in service methods
     * @param exception Exception to handle
     * @param serviceName Name of the service
     * @param methodName Name of the method
     * @param context Additional context information
     */
    public static void handleServiceException(Exception exception, 
                                              String serviceName, 
                                              String methodName, 
                                              String context) {
        
        ExceptionContext exceptionContext = ExceptionContext.builder()
                .controller(serviceName)
                .action(methodName)
                .addData("context", context)
                .build();

        logServiceException(exception, serviceName, methodName, exceptionContext);
    }

    /**
     * Handle exceptions in service methods with user context
     * @param exception Exception to handle
     * @param serviceName Name of the service
     * @param methodName Name of the method
     * @param userId User ID if available
     * @param context Additional context information
     */
    public static void handleServiceException(Exception exception, 
                                              String serviceName, 
                                              String methodName, 
                                              Long userId, 
                                              String context) {
        
        ExceptionContext exceptionContext = ExceptionContext.builder()
                .controller(serviceName)
                .action(methodName)
                .userId(userId)
                .addData("context", context)
                .build();

        logServiceException(exception, serviceName, methodName, exceptionContext);
    }

    /**
     * Wrap service method execution with exception handling
     * @param serviceName Name of the service
     * @param methodName Name of the method
     * @param runnable Code to execute
     * @return Result of the operation or null if exception occurred
     */
    public static <T> T executeWithExceptionHandling(String serviceName,
                                                      String methodName,
                                                      ServiceSupplier<T> runnable) {
        try {
            return runnable.get();
        } catch (Exception e) {
            handleServiceException(e, serviceName, methodName, null);
            return null;
        }
    }

    /**
     * Wrap service method execution with exception handling and context
     * @param serviceName Name of the service
     * @param methodName Name of the method
     * @param context Additional context
     * @param runnable Code to execute
     * @return Result of the operation or null if exception occurred
     */
    public static <T> T executeWithExceptionHandling(String serviceName,
                                                      String methodName,
                                                      String context,
                                                      ServiceSupplier<T> runnable) {
        try {
            return runnable.get();
        } catch (Exception e) {
            handleServiceException(e, serviceName, methodName, context);
            return null;
        }
    }

    /**
     * Wrap service method execution with exception handling and user context
     * @param serviceName Name of the service
     * @param methodName Name of the method
     * @param userId User ID
     * @param runnable Code to execute
     * @return Result of the operation or null if exception occurred
     */
    public static <T> T executeWithExceptionHandling(String serviceName,
                                                      String methodName,
                                                      Long userId,
                                                      ServiceSupplier<T> runnable) {
        try {
            return runnable.get();
        } catch (Exception e) {
            handleServiceException(e, serviceName, methodName, userId, null);
            return null;
        }
    }

    /**
     * Wrap void service method execution with exception handling
     * @param serviceName Name of the service
     * @param methodName Name of the method
     * @param runnable Code to execute
     * @return true if successful, false if exception occurred
     */
    public static boolean executeVoidWithExceptionHandling(String serviceName,
                                                           String methodName,
                                                           ServiceRunnable runnable) {
        try {
            runnable.run();
            return true;
        } catch (Exception e) {
            handleServiceException(e, serviceName, methodName, null);
            return false;
        }
    }

    /**
     * Wrap void service method execution with exception handling and context
     * @param serviceName Name of the service
     * @param methodName Name of the method
     * @param context Additional context
     * @param runnable Code to execute
     * @return true if successful, false if exception occurred
     */
    public static boolean executeVoidWithExceptionHandling(String serviceName,
                                                           String methodName,
                                                           String context,
                                                           ServiceRunnable runnable) {
        try {
            runnable.run();
            return true;
        } catch (Exception e) {
            handleServiceException(e, serviceName, methodName, context);
            return false;
        }
    }

    /**
     * Log service exception with structured information
     */
    private static void logServiceException(Exception exception, 
                                             String serviceName, 
                                             String methodName, 
                                             ExceptionContext context) {
        
        // Build structured log data
        java.util.Map<String, Object> logData = new java.util.HashMap<>();
        logData.put("serviceName", serviceName);
        logData.put("methodName", methodName);
        logData.put("exceptionType", exception.getClass().getSimpleName());
        logData.put("message", exception.getMessage());
        
        if (context != null) {
            logData.putAll(context.toMap());
        }
        
        // Log at appropriate level based on exception type
        if (exception instanceof FashionStoreException) {
            FashionStoreException fsException = (FashionStoreException) exception;
            switch (fsException.getType()) {
                case SYSTEM_ERROR, DATABASE_ERROR:
                    logger.error("Service error in {}.{}: {}", serviceName, methodName, logData, exception);
                    break;
                case PAYMENT_ERROR:
                    logger.warn("Payment error in {}.{}: {}", serviceName, methodName, logData, exception);
                    break;
                case AUTHORIZATION_ERROR:
                    logger.warn("Authorization error in {}.{}: {}", serviceName, methodName, logData, exception);
                    break;
                default:
                    logger.info("Service error in {}.{}: {}", serviceName, methodName, logData, exception);
                    break;
            }
        } else {
            logger.error("Unexpected service error in {}.{}: {}", serviceName, methodName, logData, exception);
        }
    }

    /**
     * Functional interface for service methods returning values
     */
    @FunctionalInterface
    public interface ServiceSupplier<T> {
        T get() throws Exception;
    }

    /**
     * Functional interface for void service methods
     */
    @FunctionalInterface
    public interface ServiceRunnable {
        void run() throws Exception;
    }
}
