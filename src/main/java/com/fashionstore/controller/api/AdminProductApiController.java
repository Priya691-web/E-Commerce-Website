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
 * Modular API controller for product management in admin dashboard
 * Handles: GET /api/admin/products, POST /api/admin/products, PUT /api/admin/products/{id}, DELETE /api/admin/products/{id}
 */
@WebServlet("/api/admin/products/*")
public class AdminProductApiController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(AdminProductApiController.class);

    private ProductDAO productDAO;
    private ProductSizeDAO productSizeDAO;
    private CategoryDAO categoryDAO;
    private Set<String> allowedOrigins;

    @Override
    public void init() {
        productDAO = new ProductDAOImpl();
        productSizeDAO = new ProductSizeDAOImpl();
        categoryDAO = new CategoryDAOImpl();
        
        // Initialize allowed origins from environment variable
        String allowedOriginsEnv = System.getenv("CORS_ALLOWED_ORIGINS");
        if (allowedOriginsEnv != null && !allowedOriginsEnv.isBlank()) {
            allowedOrigins = new HashSet<>(Arrays.asList(allowedOriginsEnv.split(",")));
            logger.info("AdminProductApiController initialized with allowed origins from env: {}", allowedOrigins);
        } else {
            // Fallback to localhost for local development only
            allowedOrigins = new HashSet<>(Arrays.asList(
                "http://localhost:5173",
                "http://127.0.0.1:5173",
                "http://localhost:3000",
                "http://127.0.0.1:3000"
            ));
            logger.info("AdminProductApiController initialized with default localhost origins for development");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        applyCors(request, response);
        if (!ensureAdmin(request, response)) return;
        
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            // GET /api/admin/products - List all products
            List<Product> products = productDAO.getAllProducts();
            writeJson(response, 200, Map.of("success", true, "products", products.stream().map(this::publicProduct).toList(), "count", products.size()));
            return;
        }
        
        // GET /api/admin/products/{id} - Get single product
        String[] segments = pathInfo.split("/");
        if (segments.length == 2) {
            try {
                int productId = Integer.parseInt(segments[1]);
                Product product = productDAO.getProductById(productId);
                if (product == null) {
                    writeJson(response, 404, Map.of("success", false, "message", "Product not found"));
                    return;
                }
                writeJson(response, 200, Map.of("success", true, "product", publicProduct(product)));
            } catch (NumberFormatException e) {
                writeJson(response, 400, Map.of("success", false, "message", "Invalid product ID"));
            }
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
        
        // POST /api/admin/products - Create new product
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            Map<String, Object> body = readJsonBody(request);
            Product product = bodyToProduct(body, true);
            int newId = productDAO.addProduct(product);
            if (newId > 0) {
                saveProductSizes(newId, body);
                writeJson(response, 201, Map.of("success", true, "productId", newId));
            } else {
                writeJson(response, 400, Map.of("success", false, "message", "Failed to create product"));
            }
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
                    int productId = Integer.parseInt(segments[1]);
                    Product existing = productDAO.getProductById(productId);
                    if (existing == null) {
                        writeJson(response, 404, Map.of("success", false, "message", "Product not found"));
                        return;
                    }
                    
                    Map<String, Object> body = readJsonBody(request);
                    Product product = bodyToProduct(body, false);
                    product.setProductId(productId);
                    boolean success = productDAO.updateProduct(product);
                    if (success) {
                        saveProductSizes(productId, body);
                        writeJson(response, 200, Map.of("success", true));
                    } else {
                        writeJson(response, 400, Map.of("success", false, "message", "Failed to update product"));
                    }
                } catch (NumberFormatException e) {
                    writeJson(response, 400, Map.of("success", false, "message", "Invalid product ID"));
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
                    int productId = Integer.parseInt(segments[1]);
                    boolean success = productDAO.deleteProduct(productId);
                    if (success) {
                        writeJson(response, 200, Map.of("success", true));
                    } else {
                        writeJson(response, 400, Map.of("success", false, "message", "Failed to delete product"));
                    }
                } catch (NumberFormatException e) {
                    writeJson(response, 400, Map.of("success", false, "message", "Invalid product ID"));
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

    private void saveProductSizes(int productId, Map<String, Object> body) {
        Object sizesObj = body.get("sizes");
        if (sizesObj instanceof List<?> sizesList) {
            for (Object s : sizesList) {
                if (s == null) continue;
                String label = String.valueOf(s).trim();
                if (label.isEmpty()) continue;
                ProductSize ps = new ProductSize();
                ps.setProductId(productId);
                ps.setSizeLabel(label);
                ps.setStockQuantity(0);
                ps.setAvailable(true);
                productSizeDAO.addOrUpdateSize(ps);
            }
        }
    }

    private Map<String, Object> publicProduct(Product p) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", p.getProductId());
        m.put("name", p.getProductName());
        m.put("description", p.getDescription());
        m.put("price", p.getPrice());
        m.put("discount", p.getDiscountPercent());
        m.put("stock", p.getStockQuantity());
        m.put("imageUrl", p.getImageUrl());
        m.put("category", p.getCategoryName());
        m.put("categoryId", p.getCategoryId());
        m.put("status", p.isActive() ? "active" : "inactive");
        m.put("brand", p.getBrand());
        m.put("isNew", p.isNew());
        m.put("isSale", p.isSale());
        m.put("isTrending", p.isTrending());
        List<String> sizeLabels = new ArrayList<>();
        if (p.getSizes() != null) {
            for (ProductSize ps : p.getSizes()) sizeLabels.add(ps.getSizeLabel());
        }
        m.put("sizes", sizeLabels);
        return m;
    }

    private Product bodyToProduct(Map<String, Object> body, boolean isNew) {
        Product p = new Product();
        p.setProductName(strParam(body, "name"));
        p.setDescription(strParam(body, "description"));
        p.setPrice(parseDouble(body.get("price"), 0.0));
        p.setDiscountPercent(parseDouble(body.get("discount"), 0.0));
        p.setImageUrl(strParam(body, "imageUrl"));
        p.setStockQuantity(parseIntFromObject(body.get("stock"), 0));
        p.setBrand(strParam(body, "brand"));

        String status = strParam(body, "status");
        p.setActive("active".equalsIgnoreCase(status));

        // Category: try int first, else lookup by name
        Object catObj = body.get("category");
        int categoryId = 0;
        if (catObj != null) {
            try {
                categoryId = Integer.parseInt(String.valueOf(catObj).trim());
            } catch (NumberFormatException e) {
                String catName = String.valueOf(catObj).trim();
                for (Category c : categoryDAO.getAllCategories()) {
                    if (c.getCategoryName() != null && c.getCategoryName().equalsIgnoreCase(catName)) {
                        categoryId = c.getCategoryId();
                        break;
                    }
                }
            }
        }
        p.setCategoryId(categoryId);

        p.setNew(parseBoolean(body.get("isNew"), false));
        p.setSale(parseBoolean(body.get("isSale"), false));
        p.setTrending(parseBoolean(body.get("isTrending"), false));
        return p;
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

    private boolean parseBoolean(Object v, boolean defaultVal) {
        if (v == null) return defaultVal;
        return Boolean.parseBoolean(String.valueOf(v));
    }

    private int parseIntFromObject(Object v, int defaultVal) {
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
