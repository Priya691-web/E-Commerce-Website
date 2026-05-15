package com.fashionstore.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/logout")
public class LogoutController extends HttpServlet {

    private static final long serialVersionUID = 1L; // ✅ FIXES WARNING

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);

        if (session != null) {
            // Determine which user is logged in and clear only their session
            Object customerAuth = session.getAttribute("customerAuth");
            Object adminAuth = session.getAttribute("adminAuth");
            
            if (customerAuth != null) {
                // Customer logout - clear customer session attributes only
                session.removeAttribute("customerAuth");
                session.removeAttribute("customerId");
                session.removeAttribute("userId");
                session.removeAttribute("cartItems");
            } else if (adminAuth != null) {
                // Admin logout - clear admin session attributes only
                session.removeAttribute("adminAuth");
                session.removeAttribute("adminId");
            }
        }

        // Delete remember_me cookie (only applies to customer users)
        jakarta.servlet.http.Cookie cookie = new jakarta.servlet.http.Cookie("remember_me", "");
        cookie.setMaxAge(0);
        cookie.setPath(req.getContextPath() + "/");
        resp.addCookie(cookie);

        resp.sendRedirect(req.getContextPath() + "/login");
    }
}