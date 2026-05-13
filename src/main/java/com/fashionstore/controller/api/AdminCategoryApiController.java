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
import java.util.*;

/**
 * Modular API controller for category management in admin dashboard
 * Handles: GET /api/admin/categories, POST /api/admin/categories, PUT /api/admin/categories/{id}, DELETE /api/admin/categories/{id}
 */
@WebServlet("/api/admin/categories/*")
public class AdminCategoryApiController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(AdminCategoryApiController.class);

    private CategoryDAO categoryDAO;
    private Set<String> allowedOrigins;

    @Override
    public void init() {
        categoryDAO = new CategoryDAOImpl();
        
        // Initialize allowed origins from environment variable
        String allowedOriginsEnv = System.getenv("CORS_ALLOWED_ORIGINS");
        if (allowedOriginsEnv != null && !allowedOriginsEnv.isBlank()) {
            allowedOrigins = new HashSet<>(Arrays.asList(allowedOriginsEnv.split(",")));
            logger.info("AdminCategoryApiController initialized with allowed origins from env: {}", allowedOrigins);
        } else {
            // Fallback to localhost for local development only
            allowedOrigins = new HashSet<>(Arrays.asList(
                "http://localhost:5173",
                "http://127.0.0.1:5173",
                "http://localhost:3000",
                "http://127.0.0.1:3000"
            ));
            logger.info("AdminCategoryApiController initialized with default localhost origins for development");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        applyCors(request, response);
        if (!ensureAdmin(request, response)) return;
        
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            // GET /api/admin/categories - List all categories
            List<Category> categories = categoryDAO.getAllCategories();
            writeJson(response, 200, Map.of("success", true, "categories", categories));
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
        
        // POST /api/admin/categories - Create new category
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            Map<String, Object> body = readJsonBody(request);
            Category category = new Category();
            category.setCategoryName(strParam(body, "name"));
            category.setDescription(strParam(body, "description"));
            category.setActive(true);
            int newId = categoryDAO.addCategory(category);
            writeJson(response, newId > 0 ? 201 : 400, Map.of("success", newId > 0, "categoryId", newId));
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
                    int categoryId = Integer.parseInt(segments[1]);
                    Category existing = categoryDAO.getCategoryById(categoryId);
                    if (existing == null) {
                        writeJson(response, 404, Map.of("success", false, "message", "Category not found"));
                        return;
                    }
                    
                    Map<String, Object> body = readJsonBody(request);
                    existing.setCategoryName(strParam(body, "name"));
                    existing.setDescription(strParam(body, "description"));
                    boolean success = categoryDAO.updateCategory(existing);
                    writeJson(response, success ? 200 : 400, Map.of("success", success));
                } catch (NumberFormatException e) {
                    writeJson(response, 400, Map.of("success", false, "message", "Invalid category ID"));
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
                    int categoryId = Integer.parseInt(segments[1]);
                    boolean success = categoryDAO.deleteCategory(categoryId);
                    writeJson(response, success ? 200 : 400, Map.of("success", success));
                } catch (NumberFormatException e) {
                    writeJson(response, 400, Map.of("success", false, "message", "Invalid category ID"));
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
