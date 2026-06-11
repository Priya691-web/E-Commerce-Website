package com.fashionstore.util;

import com.fashionstore.dao.implementation.UserDAOImpl;
import com.fashionstore.dao.interfaces.UserDAO;
import com.fashionstore.model.User;

public class DAOTest {
    public static void main(String[] args) {
        UserDAO userDAO = new UserDAOImpl();
        User user = new User();
        user.setFirstName("Test");
        user.setLastName("User");
        user.setEmail("test@example.com");
        user.setPasswordHash("hash");
        
        System.out.println("Testing DB Connection and UserDAO...");
        boolean result = userDAO.createUser(user);
        System.out.println("Create User Result: " + result);
    }
}
