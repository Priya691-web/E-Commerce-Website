package com.fashionstore.controller;

import com.fashionstore.dao.implementation.CategoryDAOImpl;
import com.fashionstore.dao.implementation.ProductDAOImpl;
import com.fashionstore.dao.implementation.ProductVariantDAOImpl;
import com.fashionstore.dao.interfaces.CategoryDAO;
import com.fashionstore.dao.interfaces.ProductDAO;
import com.fashionstore.dao.interfaces.ProductVariantDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/product")
public class ProductServlet extends HttpServlet {
    private ProductDAO productDAO;
    private ProductVariantDAO productVariantDAO;
    private CategoryDAO categoryDAO;

    @Override
    public void init() throws ServletException {
        productDAO = new ProductDAOImpl();
        productVariantDAO = new ProductVariantDAOImpl();
        categoryDAO = new CategoryDAOImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if ("details".equals(action)) {
            int id = Integer.parseInt(request.getParameter("id"));
            request.setAttribute("product", productDAO.getProductById(id));
            request.setAttribute("variants", productVariantDAO.getVariantsByProductId(id));
            request.getRequestDispatcher("/WEB-INF/view/product/product-details.jsp").forward(request, response);
        } else {
            String categoryIdStr = request.getParameter("categoryId");
            if (categoryIdStr != null && !categoryIdStr.isEmpty()) {
                int categoryId = Integer.parseInt(categoryIdStr);
                request.setAttribute("products", productDAO.getProductsByCategory(categoryId));
            } else {
                request.setAttribute("products", productDAO.getAllProducts());
            }
            request.setAttribute("categories", categoryDAO.getAllCategories());
            request.getRequestDispatcher("/WEB-INF/view/product/products.jsp").forward(request, response);
        }
    }
}
