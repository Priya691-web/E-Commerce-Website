package com.fashionstore.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Controller for handling CSP violation reports
 * Logs detailed information about CSP violations for security monitoring
 */
@WebServlet("/csp-violation-report")
public class CSPViolationController extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(CSPViolationController.class);
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            // Read the violation report
            StringBuilder reportBuilder = new StringBuilder();
            String line;
            BufferedReader reader = request.getReader();
            
            while ((line = reader.readLine()) != null) {
                reportBuilder.append(line);
            }
            
            String violationReport = reportBuilder.toString();
            
            // Log detailed CSP violation information
            logger.warn("CSP Violation Report received: {}", violationReport);
            logger.warn("CSP Violation Details - IP: {}, User-Agent: {}, Referer: {}", 
                request.getRemoteAddr(),
                request.getHeader("User-Agent"),
                request.getHeader("Referer"));
            
            // Parse and log specific violation details if possible
            if (violationReport.contains("document-uri")) {
                logger.warn("CSP Violation on document: {}", extractJsonValue(violationReport, "document-uri"));
            }
            if (violationReport.contains("blocked-uri")) {
                logger.warn("CSP Blocked URI: {}", extractJsonValue(violationReport, "blocked-uri"));
            }
            if (violationReport.contains("violated-directive")) {
                logger.warn("CSP Violated Directive: {}", extractJsonValue(violationReport, "violated-directive"));
            }
            
            // Return success response
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");
            response.getWriter().write("{\"status\":\"ok\"}");
            
        } catch (Exception e) {
            logger.error("Error processing CSP violation report: {}", e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Extract JSON value from violation report
     */
    private String extractJsonValue(String report, String key) {
        try {
            int keyIndex = report.indexOf("\"" + key + "\"");
            if (keyIndex == -1) return "unknown";
            
            int colonIndex = report.indexOf(":", keyIndex);
            if (colonIndex == -1) return "unknown";
            
            int valueStart = report.indexOf("\"", colonIndex) + 1;
            int valueEnd = report.indexOf("\"", valueStart);
            
            if (valueStart > 0 && valueEnd > valueStart) {
                return report.substring(valueStart, valueEnd);
            }
        } catch (Exception e) {
            logger.debug("Error extracting JSON value for key {}: {}", key, e.getMessage());
        }
        return "unknown";
    }
}
