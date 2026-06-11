package com.fashionstore.controller;

import com.fashionstore.dao.implementation.UserDAOImpl;
import com.fashionstore.dao.interfaces.UserDAO;
import com.fashionstore.model.User;
import org.mindrot.jbcrypt.BCrypt;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        userDAO = new UserDAOImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/view/auth/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        
        User existing = userDAO.getUserByEmail(email);
        if(existing != null) {
            request.setAttribute("error", "Email is already registered");
            request.getRequestDispatcher("/WEB-INF/view/auth/register.jsp").forward(request, response);
            return;
        }

        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPasswordHash(BCrypt.hashpw(password, BCrypt.gensalt()));

        if (userDAO.createUser(user)) {
            response.sendRedirect(request.getContextPath() + "/login?registered=true");
        } else {
            request.setAttribute("error", "Registration failed");
            request.getRequestDispatcher("/WEB-INF/view/auth/register.jsp").forward(request, response);
        }
    }
}
