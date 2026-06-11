package com.fashionstore.controller;

import com.fashionstore.dao.implementation.CategoryDAOImpl;
import com.fashionstore.dao.implementation.ProductDAOImpl;
import com.fashionstore.dao.interfaces.CategoryDAO;
import com.fashionstore.dao.interfaces.ProductDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/home")
public class HomeServlet extends HttpServlet {
    private ProductDAO productDAO;
    private CategoryDAO categoryDAO;

    @Override
    public void init() throws ServletException {
        productDAO = new ProductDAOImpl();
        categoryDAO = new CategoryDAOImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("categories", categoryDAO.getAllCategories());
        request.setAttribute("featuredProducts", productDAO.getAllProducts()); // Optionally limit to top N
        request.getRequestDispatcher("/WEB-INF/view/home.jsp").forward(request, response);
    }
}
