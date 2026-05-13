package com.fashionstore.controller.api;

import com.fashionstore.dao.*;
import com.fashionstore.daoimpl.*;
import com.fashionstore.model.*;
import com.fashionstore.util.JsonUtil;
import com.fashionstore.util.SecurityUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;

/**
 * Modular API controller for coupon management in admin dashboard
 * Handles: GET /api/admin/coupons, POST /api/admin/coupons, PUT /api/admin/coupons/{id}, DELETE /api/admin/coupons/{id}
 */
@WebServlet("/api/admin/coupons/*")
public class AdminCouponApiController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(AdminCouponApiController.class);

    private CouponDAO couponDAO;
    private Set<String> allowedOrigins;

    @Override
    public void init() {
        couponDAO = new CouponDAOImpl();
        
        // Initialize allowed origins from environment variable
        String allowedOriginsEnv = System.getenv("CORS_ALLOWED_ORIGINS");
        if (allowedOriginsEnv != null && !allowedOriginsEnv.isBlank()) {
            allowedOrigins = new HashSet<>(Arrays.asList(allowedOriginsEnv.split(",")));
            logger.info("AdminCouponApiController initialized with allowed origins from env: {}", allowedOrigins);
        } else {
            // Fallback to localhost for local development only
            allowedOrigins = new HashSet<>(Arrays.asList(
                "http://localhost:5173",
                "http://127.0.0.1:5173",
                "http://localhost:3000",
                "http://127.0.0.1:3000"
            ));
            logger.info("AdminCouponApiController initialized with default localhost origins for development");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        applyCors(request, response);
        if (!ensureAdmin(request, response)) return;
        
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            // GET /api/admin/coupons - List all coupons
            List<Coupon> coupons = couponDAO.getAllCoupons();
            writeJson(response, 200, Map.of("success", true, "coupons", coupons));
            return;
        }
        
        writeJson(response, 404, Map.of("success", false, "message", "Not found"));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        applyCors(request, response);
        if (!isTrustedStateChangingRequest(request)) {
            writeJson(response, 403, Map.of("success", false, "message", "Blocked by origin policy"));
            return;
        }
        if (!ensureAdmin(request, response)) return;
        
        // POST /api/admin/coupons - Create new coupon
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            Map<String, Object> body = readJsonBody(request);
            Coupon coupon = bodyToCoupon(body);
            boolean success = couponDAO.addCoupon(coupon);
            writeJson(response, success ? 201 : 400, Map.of("success", success));
            return;
        }
        
        writeJson(response, 404, Map.of("success", false, "message", "Not found"));
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        applyCors(request, response);
        if (!isTrustedStateChangingRequest(request)) {
            writeJson(response, 403, Map.of("success", false, "message", "Blocked by origin policy"));
            return;
        }
        if (!ensureAdmin(request, response)) return;
        
        String pathInfo = request.getPathInfo();
        if (pathInfo != null && !pathInfo.equals("/")) {
            String[] segments = pathInfo.split("/");
            if (segments.length == 2) {
                try {
                    int couponId = Integer.parseInt(segments[1]);
                    Coupon existing = couponDAO.getCouponById(couponId);
                    if (existing == null) {
                        writeJson(response, 404, Map.of("success", false, "message", "Coupon not found"));
                        return;
                    }
                    
                    Map<String, Object> body = readJsonBody(request);
                    Coupon coupon = bodyToCoupon(body);
                    coupon.setCouponId(couponId);
                    boolean success = couponDAO.updateCoupon(coupon);
                    writeJson(response, success ? 200 : 400, Map.of("success", success));
                } catch (NumberFormatException e) {
                    writeJson(response, 400, Map.of("success", false, "message", "Invalid coupon ID"));
                }
                return;
            }
        }
        
        writeJson(response, 404, Map.of("success", false, "message", "Not found"));
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        applyCors(request, response);
        if (!isTrustedStateChangingRequest(request)) {
            writeJson(response, 403, Map.of("success", false, "message", "Blocked by origin policy"));
            return;
        }
        if (!ensureAdmin(request, response)) return;
        
        String pathInfo = request.getPathInfo();
        if (pathInfo != null && !pathInfo.equals("/")) {
            String[] segments = pathInfo.split("/");
            if (segments.length == 2) {
                try {
                    int couponId = Integer.parseInt(segments[1]);
                    boolean success = couponDAO.deleteCoupon(couponId);
                    writeJson(response, success ? 200 : 400, Map.of("success", success));
                } catch (NumberFormatException e) {
                    writeJson(response, 400, Map.of("success", false, "message", "Invalid coupon ID"));
                }
                return;
            }
        }
        
        writeJson(response, 404, Map.of("success", false, "message", "Not found"));
    }

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) {
        applyCors(request, response);
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    // Helper methods
    private boolean ensureAdmin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = SecurityUtil.getCurrentUser(request);
        if (user == null) {
            writeJson(response, 401, Map.of("success", false, "message", "Authentication required"));
            return false;
        }
        if (!user.isAdmin()) {
            writeJson(response, 403, Map.of("success", false, "message", "Admin access required"));
            return false;
        }
        return true;
    }

    private Coupon bodyToCoupon(Map<String, Object> body) {
        Coupon c = new Coupon();
        c.setCode(strParam(body, "code"));
        c.setDescription(strParam(body, "description"));
        String dt = strParam(body, "discountType");
        c.setDiscountType(dt.isEmpty() ? "percentage" : dt);
        c.setDiscountValue(parseDouble(body.get("discountValue"), 0.0));
        c.setMinimumOrderAmount(parseDouble(body.get("minOrder"), 0.0));
        c.setMaximumDiscountAmount(null);
        Object maxUses = body.get("maxUses");
        c.setUsageLimit(maxUses != null ? parseIntFromObject(maxUses, (Integer) null) : null);
        c.setUserUsageLimit(1);
        c.setUsageCount(0);
        c.setActive(true);

        try {
            String expires = strParam(body, "expiresAt");
            if (!expires.isEmpty()) {
                java.time.LocalDate ld = java.time.LocalDate.parse(expires);
                c.setValidUntil(Timestamp.valueOf(ld.atTime(23, 59, 59)));
            } else {
                c.setValidUntil(new Timestamp(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000));
            }
        } catch (Exception e) {
            c.setValidUntil(new Timestamp(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000));
        }
        c.setValidFrom(new Timestamp(System.currentTimeMillis()));
        return c;
    }

    private void applyCors(HttpServletRequest request, HttpServletResponse response) {
        String origin = request.getHeader("Origin");
        if (origin != null && allowedOrigins.contains(origin)) {
            response.setHeader("Access-Control-Allow-Origin", origin);
            response.setHeader("Vary", "Origin");
            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.setHeader("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
            response.setHeader("Access-Control-Allow-Headers", "Content-Type,X-Requested-With,X-CSRF-Token");
            response.setHeader("Access-Control-Max-Age", "3600");
        }
    }

    private void writeJson(HttpServletResponse response, int status, Object data) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(JsonUtil.toJson(data));
    }

    private Map<String, Object> readJsonBody(HttpServletRequest request) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = request.getReader()) {
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
        }
        String body = sb.toString().trim();
        if (body.isEmpty()) return new HashMap<>();
        try {
            Map<String, Object> parsed = JsonUtil.gson().fromJson(body,
                    new com.google.gson.reflect.TypeToken<Map<String, Object>>(){}.getType());
            return parsed != null ? parsed : new HashMap<>();
        } catch (Exception e) {
            Map<String, Object> form = new HashMap<>();
            for (String pair : body.split("&")) {
                int idx = pair.indexOf('=');
                if (idx > 0) {
                    form.put(java.net.URLDecoder.decode(pair.substring(0, idx), java.nio.charset.StandardCharsets.UTF_8),
                             java.net.URLDecoder.decode(pair.substring(idx + 1), java.nio.charset.StandardCharsets.UTF_8));
                }
            }
            return form;
        }
    }

    private String strParam(Map<String, Object> body, String key) {
        Object v = body.get(key);
        return v == null ? "" : String.valueOf(v).trim();
    }

    private double parseDouble(Object v, double defaultVal) {
        if (v == null) return defaultVal;
        try { return Double.parseDouble(String.valueOf(v).trim()); } catch (NumberFormatException e) { return defaultVal; }
    }

    private Integer parseIntFromObject(Object v, Integer defaultVal) {
        if (v == null) return defaultVal;
        if (v instanceof Number n) return n.intValue();
        try {
            return Integer.parseInt(String.valueOf(v).trim());
        } catch (NumberFormatException e) {
            try {
                return (int) Double.parseDouble(String.valueOf(v).trim());
            } catch (NumberFormatException ex) {
                return defaultVal;
            }
        }
    }

    private boolean isTrustedStateChangingRequest(HttpServletRequest request) {
        String origin = request.getHeader("Origin");
        String referer = request.getHeader("Referer");
        String local = request.getScheme() + "://" + request.getServerName()
                + ((request.getServerPort() == 80 || request.getServerPort() == 443) ? "" : ":" + request.getServerPort());

        if (origin != null && !origin.isBlank()) {
            return origin.equals(local) || allowedOrigins.contains(origin);
        }
        if (referer != null && !referer.isBlank()) {
            return referer.startsWith(local) || allowedOrigins.stream().anyMatch(referer::startsWith);
        }
        return false;
    }
}
