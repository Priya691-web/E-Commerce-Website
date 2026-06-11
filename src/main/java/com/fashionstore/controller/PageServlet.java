package com.fashionstore.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/page")
public class PageServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = request.getParameter("id");
        if (id == null) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        String viewPath = "/WEB-INF/view/pages/" + id + ".jsp";
        
        // Basic security check to prevent directory traversal
        if (id.matches("^[a-zA-Z0-9-]+$")) {
            request.getRequestDispatcher(viewPath).forward(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
