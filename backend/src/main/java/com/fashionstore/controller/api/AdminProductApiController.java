package com.fashionstore.controller.api;

import com.fashionstore.controller.ApiResponse;
import com.fashionstore.dao.*;
import com.fashionstore.daoimpl.*;
import com.fashionstore.model.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.*;

/**
 * Modular API controller for product management in admin dashboard.
 * Refactored to leverage AdminApiBaseController and BaseController.
 */
@WebServlet("/api/admin/products/*")
public class AdminProductApiController extends AdminApiBaseController {

    private static final long serialVersionUID = 1L;

    private ProductDAO productDAO;
    private ProductSizeDAO productSizeDAO;
    private CategoryDAO categoryDAO;

    @Override
    public void init() {
        super.init();
        productDAO = new ProductDAOImpl();
        productSizeDAO = new ProductSizeDAOImpl();
        categoryDAO = new CategoryDAOImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        applyCors(request, response);
        if (!ensureAdmin(request, response)) return;
        
        try {
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                // GET /api/admin/products - List all products
                List<Product> products = productDAO.getAllProducts();
                writeApiResponse(response, 200, ApiResponse.success("Products retrieved successfully", Map.of(
                    "products", products.stream().map(this::publicProduct).toList(),
                    "count", products.size()
                )));
                return;
            }
            
            // GET /api/admin/products/{id} - Get single product
            String[] segments = pathInfo.split("/");
            if (segments.length == 2) {
                try {
                    int productId = Integer.parseInt(segments[1]);
                    Product product = productDAO.getProductById(productId);
                    if (product == null) {
                        writeApiResponse(response, 404, ApiResponse.error("Product not found"));
                        return;
                    }
                    writeApiResponse(response, 200, ApiResponse.success("Product retrieved successfully", publicProduct(product)));
                } catch (NumberFormatException e) {
                    writeApiResponse(response, 400, ApiResponse.error("Invalid product ID"));
                }
                return;
            }
            
            writeApiResponse(response, 404, ApiResponse.error("Not found"));
        } catch (Exception e) {
            handleException(response, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        applyCors(request, response);
        if (!isTrustedStateChangingRequest(request)) {
            writeApiResponse(response, 403, ApiResponse.error("Blocked by origin policy"));
            return;
        }
        if (!ensureAdmin(request, response)) return;
        
        try {
            // POST /api/admin/products - Create new product
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                Map<String, Object> body = readJsonBody(request);
                if (!validateParams(response, body, "name", "price")) return;

                Product product = bodyToProduct(body, true);
                int newId = productDAO.addProduct(product);
                if (newId > 0) {
                    saveProductSizes(newId, body);
                    writeApiResponse(response, 201, ApiResponse.success("Product created successfully", Map.of("productId", newId)));
                } else {
                    writeApiResponse(response, 400, ApiResponse.error("Failed to create product"));
                }
                return;
            }
            
            writeApiResponse(response, 404, ApiResponse.error("Not found"));
        } catch (Exception e) {
            handleException(response, e);
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        applyCors(request, response);
        if (!isTrustedStateChangingRequest(request)) {
            writeApiResponse(response, 403, ApiResponse.error("Blocked by origin policy"));
            return;
        }
        if (!ensureAdmin(request, response)) return;
        
        try {
            String pathInfo = request.getPathInfo();
            if (pathInfo != null && !pathInfo.equals("/")) {
                String[] segments = pathInfo.split("/");
                if (segments.length == 2) {
                    try {
                        int productId = Integer.parseInt(segments[1]);
                        Product existing = productDAO.getProductById(productId);
                        if (existing == null) {
                            writeApiResponse(response, 404, ApiResponse.error("Product not found"));
                            return;
                        }
                        
                        Map<String, Object> body = readJsonBody(request);
                        if (!validateParams(response, body, "name", "price")) return;

                        Product product = bodyToProduct(body, false);
                        product.setProductId(productId);
                        boolean success = productDAO.updateProduct(product);
                        if (success) {
                            saveProductSizes(productId, body);
                            writeApiResponse(response, 200, ApiResponse.success("Product updated successfully", null));
                        } else {
                            writeApiResponse(response, 400, ApiResponse.error("Failed to update product"));
                        }
                    } catch (NumberFormatException e) {
                        writeApiResponse(response, 400, ApiResponse.error("Invalid product ID"));
                    }
                    return;
                }
            }
            
            writeApiResponse(response, 404, ApiResponse.error("Not found"));
        } catch (Exception e) {
            handleException(response, e);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        applyCors(request, response);
        if (!isTrustedStateChangingRequest(request)) {
            writeApiResponse(response, 403, ApiResponse.error("Blocked by origin policy"));
            return;
        }
        if (!ensureAdmin(request, response)) return;
        
        try {
            String pathInfo = request.getPathInfo();
            if (pathInfo != null && !pathInfo.equals("/")) {
                String[] segments = pathInfo.split("/");
                if (segments.length == 2) {
                    try {
                        int productId = Integer.parseInt(segments[1]);
                        boolean success = productDAO.deleteProduct(productId);
                        if (success) {
                            writeApiResponse(response, 200, ApiResponse.success("Product deleted successfully", null));
                        } else {
                            writeApiResponse(response, 400, ApiResponse.error("Failed to delete product"));
                        }
                    } catch (NumberFormatException e) {
                        writeApiResponse(response, 400, ApiResponse.error("Invalid product ID"));
                    }
                    return;
                }
            }
            
            writeApiResponse(response, 404, ApiResponse.error("Not found"));
        } catch (Exception e) {
            handleException(response, e);
        }
    }

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) {
        applyCors(request, response);
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    // Helper methods
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
}
