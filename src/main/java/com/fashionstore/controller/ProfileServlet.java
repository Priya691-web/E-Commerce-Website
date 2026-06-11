package com.fashionstore.controller;

import com.fashionstore.dao.implementation.UserDAOImpl;
import com.fashionstore.dao.interfaces.UserDAO;
import com.fashionstore.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {
    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        userDAO = new UserDAOImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        request.getRequestDispatcher("/WEB-INF/view/profile/profile.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        user.setFirstName(request.getParameter("firstName"));
        user.setLastName(request.getParameter("lastName"));
        user.setAddress(request.getParameter("address"));
        user.setPhone(request.getParameter("phone"));

        if (userDAO.updateUser(user)) {
            session.setAttribute("user", user);
            request.setAttribute("success", "Profile updated successfully");
        } else {
            request.setAttribute("error", "Failed to update profile");
        }

        request.getRequestDispatcher("/WEB-INF/view/profile/profile.jsp").forward(request, response);
    }
}
