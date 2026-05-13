package com.fashionstore.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SecurityHeadersFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(SecurityHeadersFilter.class);
    private Set<String> allowedFrameAncestors;

    @Override
    public void init(FilterConfig filterConfig) {
        // Load allowed frame ancestors from environment variable
        String allowedFrameAncestorsEnv = System.getenv("CSP_ALLOWED_FRAME_ANCESTORS");
        
        if (allowedFrameAncestorsEnv != null && !allowedFrameAncestorsEnv.isBlank()) {
            allowedFrameAncestors = new HashSet<>(Arrays.asList(allowedFrameAncestorsEnv.split(",")));
            logger.info("SecurityHeadersFilter initialized with allowed frame ancestors from env: {}", allowedFrameAncestors);
        } else {
            // Fallback to localhost for local development only
            allowedFrameAncestors = new HashSet<>(Arrays.asList(
                "http://localhost:5173",
                "http://127.0.0.1:5173",
                "http://localhost:3000",
                "http://127.0.0.1:3000"
            ));
            logger.info("SecurityHeadersFilter initialized with default localhost frame ancestors for development");
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Content Security Policy
        // NOTE: style-src must include https://fonts.googleapis.com because Google
        // Fonts serves the @font-face stylesheet from that domain. style-src-elem
        // falls back to style-src when not set explicitly.
        String origin = httpRequest.getHeader("Origin");
        String cspFrameAncestors = "frame-ancestors 'none'";
        
        if (origin != null && allowedFrameAncestors.contains(origin)) {
            cspFrameAncestors = "frame-ancestors 'self' " + String.join(" ", allowedFrameAncestors);
        }
        
        // Content Security Policy
        // SECURITY FIX: Remove 'unsafe-inline' and 'unsafe-eval' to prevent XSS attacks
        // Use nonce or hash-based CSP for inline scripts/styles instead
        // For now, keeping minimal 'unsafe-inline' for style-src only as Google Fonts requires it
        httpResponse.setHeader("Content-Security-Policy",
            "default-src 'self'; " +
            "script-src 'self' https://cdn.jsdelivr.net; " +
            "style-src 'self' 'unsafe-inline' https://fonts.googleapis.com; " +
            "style-src-elem 'self' 'unsafe-inline' https://fonts.googleapis.com; " +
            "img-src 'self' data: https:; " +
            "font-src 'self' data: https://fonts.gstatic.com https://fonts.googleapis.com; " +
            "connect-src 'self'; " +
            cspFrameAncestors + "; " +
            "form-action 'self'; " +
            "base-uri 'self'");

        // X-Content-Type-Options
        httpResponse.setHeader("X-Content-Type-Options", "nosniff");

        // X-Frame-Options
        // Allow framing from configured origins
        if (origin != null && allowedFrameAncestors.contains(origin)) {
            httpResponse.setHeader("X-Frame-Options", "SAMEORIGIN");
        } else {
            httpResponse.setHeader("X-Frame-Options", "DENY");
        }

        // X-XSS-Protection
        httpResponse.setHeader("X-XSS-Protection", "1; mode=block");

        // Referrer-Policy
        httpResponse.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");

        // Permissions-Policy (formerly Feature-Policy)
        httpResponse.setHeader("Permissions-Policy", 
            "geolocation=(), " +
            "microphone=(), " +
            "camera=(), " +
            "payment=(), " +
            "usb=()");

        // Strict-Transport-Security (HSTS) - only for HTTPS
        if (httpRequest.isSecure()) {
            httpResponse.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains; preload");
        }

        // Cache-Control for sensitive pages
        String path = httpRequest.getRequestURI();
        if (path.contains("/login") || path.contains("/register") ||
            path.contains("/forgot-password") || path.contains("/reset-password") ||
            path.contains("/checkout") || path.contains("/payment")) {
            httpResponse.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
            httpResponse.setHeader("Pragma", "no-cache");
            httpResponse.setHeader("Expires", "0");
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        logger.info("SecurityHeadersFilter destroyed");
    }
}
