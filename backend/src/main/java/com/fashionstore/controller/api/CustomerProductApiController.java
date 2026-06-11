package com.fashionstore.controller.api;

import com.fashionstore.controller.ApiResponse;
import com.fashionstore.controller.BaseController;
import com.fashionstore.model.Product;
import com.fashionstore.model.ProductQuery;
import com.fashionstore.model.ProductSize;
import com.fashionstore.registry.ServiceRegistry;
import com.fashionstore.service.ProductService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CustomerProductApiController - Customer-Facing Product API
 * 
 * Provides product browsing and discovery for customers:
 * - GET /api/products - List products with filtering, search, pagination
 * - GET /api/products/{id} - Get single product details
 * - GET /api/products/{id}/sizes - Get available sizes for a product
 * - GET /api/products/featured - Get featured/new/trending products
 */
@WebServlet("/api/products/*")
public class CustomerProductApiController extends BaseController {

    private static final long serialVersionUID = 1L;
    private static final int PAGE_SIZE = 12;

    private ProductService productService;

    @Override
    public void init() {
        productService = ServiceRegistry.getInstance().getProductService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        applyCors(request, response);
        
        try {
            String pathInfo = request.getPathInfo();
            
            if (pathInfo == null || pathInfo.equals("/")) {
                // GET /api/products - List products
                listProducts(request, response);
                return;
            }
            
            if (pathInfo.equals("/featured")) {
                // GET /api/products/featured - Get featured products
                listFeaturedProducts(request, response);
                return;
            }
            
            // Handle /api/products/{id} patterns
            String[] pathParts = pathInfo.split("/");
            if (pathParts.length == 2) {
                try {
                    int productId = Integer.parseInt(pathParts[1]);
                    getProductById(productId, response);
                    return;
                } catch (NumberFormatException e) {
                    writeApiResponse(response, 400, ApiResponse.error("Invalid product ID"));
                    return;
                }
            }
            
            if (pathParts.length == 3 && "sizes".equals(pathParts[2])) {
                try {
                    int productId = Integer.parseInt(pathParts[1]);
                    getProductSizes(productId, response);
                    return;
                } catch (NumberFormatException e) {
                    writeApiResponse(response, 400, ApiResponse.error("Invalid product ID"));
                    return;
                }
            }
            
            writeApiResponse(response, 404, ApiResponse.error("Not found"));
        } catch (Exception e) {
            handleException(response, e);
        }
    }

    private void listProducts(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Extract query parameters
        String search = request.getParameter("search");
        String categoryIdStr = request.getParameter("categoryId");
        String sortBy = request.getParameter("sortBy");
        String pageStr = request.getParameter("page");
        String minPriceStr = request.getParameter("minPrice");
        String maxPriceStr = request.getParameter("maxPrice");
        String isNewStr = request.getParameter("isNew");
        String isSaleStr = request.getParameter("isSale");
        String isTrendingStr = request.getParameter("isTrending");

        // Parse pagination
        int page = parseIntOrNull(pageStr) != null ? parseIntOrNull(pageStr) : 1;
        page = Math.max(1, page);
        int offset = (page - 1) * PAGE_SIZE;

        // Build query
        ProductQuery query = new ProductQuery();
        query.setSearch(search);
        
        if (categoryIdStr != null && !categoryIdStr.isBlank()) {
            try {
                query.setCategoryId(Integer.parseInt(categoryIdStr));
            } catch (NumberFormatException ignored) {
            }
        }
        
        query.setSortBy(sortBy != null ? sortBy : "newest");
        query.setOffset(offset);
        query.setLimit(PAGE_SIZE);
        query.setActiveOnly(true);
        
        if (minPriceStr != null && !minPriceStr.isBlank()) {
            try {
                query.setMinPrice((int) Double.parseDouble(minPriceStr));
            } catch (NumberFormatException ignored) {
            }
        }
        
        if (maxPriceStr != null && !maxPriceStr.isBlank()) {
            try {
                query.setMaxPrice((int) Double.parseDouble(maxPriceStr));
            } catch (NumberFormatException ignored) {
            }
        }
        
        // Note: ProductQuery doesn't have isNew/isSale/isTrending filters
        // These will need to be filtered after retrieval

        // Get products and count
        List<Product> products = productService.getProducts(query);
        
        // Apply additional filters that ProductQuery doesn't support
        if (isNewStr != null && Boolean.parseBoolean(isNewStr)) {
            products = products.stream().filter(Product::isNew).toList();
        }
        if (isSaleStr != null && Boolean.parseBoolean(isSaleStr)) {
            products = products.stream().filter(Product::isSale).toList();
        }
        if (isTrendingStr != null && Boolean.parseBoolean(isTrendingStr)) {
            products = products.stream().filter(Product::isTrending).toList();
        }
        
        int totalCount = productService.countProducts(query);
        int totalPages = (totalCount + PAGE_SIZE - 1) / PAGE_SIZE;

        // Build response
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("products", products.stream().map(this::publicProduct).toList());
        responseData.put("pagination", Map.of(
            "currentPage", page,
            "totalPages", totalPages,
            "totalItems", totalCount,
            "pageSize", PAGE_SIZE
        ));
        
        writeApiResponse(response, 200, ApiResponse.success("Products retrieved successfully", responseData));
    }

    private void listFeaturedProducts(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String type = request.getParameter("type"); // "new", "trending", "sale"
        String limitStr = request.getParameter("limit");
        int limit = parseIntOrNull(limitStr) != null ? parseIntOrNull(limitStr) : 8;
        limit = Math.min(limit, 20); // Max 20 products

        ProductQuery query = new ProductQuery();
        query.setActiveOnly(true);
        query.setOffset(0);
        query.setLimit(limit);
        query.setSortBy("newest");

        List<Product> products = productService.getProducts(query);
        
        // Apply type filter since ProductQuery doesn't support these
        if ("new".equals(type)) {
            products = products.stream().filter(Product::isNew).toList();
        } else if ("trending".equals(type)) {
            products = products.stream().filter(Product::isTrending).toList();
        } else if ("sale".equals(type)) {
            products = products.stream().filter(Product::isSale).toList();
        }
        
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("products", products.stream().map(this::publicProduct).toList());
        responseData.put("type", type != null ? type : "featured");
        
        writeApiResponse(response, 200, ApiResponse.success("Featured products retrieved successfully", responseData));
    }

    private void getProductById(int productId, HttpServletResponse response) throws IOException {
        Product product = productService.getProductById(productId);
        
        if (product == null) {
            writeApiResponse(response, 404, ApiResponse.error("Product not found"));
            return;
        }
        
        if (!product.isActive()) {
            writeApiResponse(response, 404, ApiResponse.error("Product not available"));
            return;
        }
        
        // Get product sizes
        List<ProductSize> sizes = productService.getProductSizes(productId);
        
        Map<String, Object> productData = publicProduct(product);
        productData.put("sizes", sizes.stream().map(this::publicProductSize).toList());
        
        writeApiResponse(response, 200, ApiResponse.success("Product retrieved successfully", productData));
    }

    private void getProductSizes(int productId, HttpServletResponse response) throws IOException {
        Product product = productService.getProductById(productId);
        
        if (product == null || !product.isActive()) {
            writeApiResponse(response, 404, ApiResponse.error("Product not found"));
            return;
        }
        
        List<ProductSize> sizes = productService.getProductSizes(productId);
        
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("productId", productId);
        responseData.put("sizes", sizes.stream().map(this::publicProductSize).toList());
        
        writeApiResponse(response, 200, ApiResponse.success("Product sizes retrieved successfully", responseData));
    }

    private Map<String, Object> publicProduct(Product p) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", p.getProductId());
        m.put("name", p.getProductName());
        m.put("description", p.getDescription());
        m.put("price", p.getPrice());
        m.put("discountPercent", p.getDiscountPercent());
        m.put("finalPrice", p.getPrice() * (1 - p.getDiscountPercent() / 100));
        m.put("imageUrl", p.getImageUrl());
        m.put("categoryId", p.getCategoryId());
        m.put("brand", p.getBrand());
        m.put("stockQuantity", p.getStockQuantity());
        m.put("isNew", p.isNew());
        m.put("isSale", p.isSale());
        m.put("isTrending", p.isTrending());
        m.put("createdAt", p.getCreatedAt() != null ? p.getCreatedAt().getTime() : null);
        return m;
    }

    private Map<String, Object> publicProductSize(ProductSize ps) {
        Map<String, Object> m = new HashMap<>();
        m.put("sizeLabel", ps.getSizeLabel());
        m.put("stockQuantity", ps.getStockQuantity());
        m.put("skuCode", ps.getSkuCode());
        m.put("isAvailable", ps.isAvailable());
        return m;
    }

    private Integer parseIntOrNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            int parsed = Integer.parseInt(value.trim());
            return parsed > 0 ? parsed : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public void doOptions(HttpServletRequest request, HttpServletResponse response) throws IOException {
        applyCors(request, response);
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }
}
