package com.fashionstore.controller;

import com.fashionstore.model.User;
import com.fashionstore.security.CSRFProtection;
import com.fashionstore.service.AddressService;
import com.fashionstore.service.UserService;
import com.fashionstore.util.DBConnection;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet("/account/profile/*")
public class ProfileController extends HttpServlet {
    private final UserService userService;
    private final AddressService addressService;

    public ProfileController() {
        this.userService = new UserService();
        this.addressService = new AddressService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        CSRFProtection.addTokenToRequest(request);

        if (pathInfo == null || pathInfo.equals("/")) {
            showProfile(request, response, user);
        } else if (pathInfo.equals("/edit")) {
            showEditProfileForm(request, response, user);
        } else if (pathInfo.equals("/settings")) {
            showAccountSettings(request, response, user);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // CSRF validation
        if (!CSRFProtection.validateRequest(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid CSRF token");
            return;
        }

        String action = request.getParameter("action");

        if ("updateProfile".equals(action)) {
            updateProfile(request, response, user);
        } else if ("updateSettings".equals(action)) {
            updateSettings(request, response, user);
        } else if ("changePassword".equals(action)) {
            changePassword(request, response, user);
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action");
        }
    }

    private void showProfile(HttpServletRequest request, HttpServletResponse response, User user) 
            throws ServletException, IOException {
        // Get user's addresses
        var addresses = addressService.getAddressesByUserId(user.getUserId());
        var defaultShipping = addressService.getDefaultAddress(user.getUserId(), "shipping");
        var defaultBilling = addressService.getDefaultAddress(user.getUserId(), "billing");

        request.setAttribute("addresses", addresses);
        request.setAttribute("addressCount", addresses.size());
        request.setAttribute("defaultShipping", defaultShipping);
        request.setAttribute("defaultBilling", defaultBilling);
        
        request.getRequestDispatcher("/WEB-INF/views/account/profile.jsp").forward(request, response);
    }

    private void showEditProfileForm(HttpServletRequest request, HttpServletResponse response, User user) 
            throws ServletException, IOException {
        request.setAttribute("user", user);
        request.getRequestDispatcher("/WEB-INF/views/account/edit-profile.jsp").forward(request, response);
    }

    private void showAccountSettings(HttpServletRequest request, HttpServletResponse response, User user) 
            throws ServletException, IOException {
        loadUserSettings(request, user.getUserId());
        request.getRequestDispatcher("/WEB-INF/views/account/account-settings.jsp").forward(request, response);
    }

    private void updateProfile(HttpServletRequest request, HttpServletResponse response, User user) 
            throws ServletException, IOException {
        String fullName = trim(request.getParameter("fullName"));
        String phone = trim(request.getParameter("phone"));
        String gender = trim(request.getParameter("gender"));
        String address = trim(request.getParameter("address"));

        // Server-side validation
        java.util.Map<String, String> fieldErrors = new java.util.LinkedHashMap<>();
        if (fullName == null || fullName.length() < 2 || fullName.length() > 100) {
            fieldErrors.put("fullName", "Full name must be between 2 and 100 characters");
        } else if (!fullName.matches("^[\\p{L} .'-]{2,100}$")) {
            fieldErrors.put("fullName", "Full name contains invalid characters");
        }
        if (phone != null && !phone.isEmpty() && !phone.matches("^[6-9]\\d{9}$")) {
            fieldErrors.put("phone", "Enter a valid 10-digit Indian mobile number");
        }
        if (gender != null && !gender.isEmpty()
                && !java.util.Set.of("male", "female", "other", "prefer_not_to_say").contains(gender)) {
            fieldErrors.put("gender", "Invalid gender value");
        }
        if (address != null && address.length() > 500) {
            fieldErrors.put("address", "Address must be 500 characters or less");
        }

        if (!fieldErrors.isEmpty()) {
            request.setAttribute("error", fieldErrors.values().iterator().next());
            request.setAttribute("fieldErrors", fieldErrors);
            user.setFullName(fullName);
            user.setPhone(phone);
            user.setGender(gender);
            user.setAddress(address);
            request.setAttribute("user", user);
            showEditProfileForm(request, response, user);
            return;
        }

        // Update user
        user.setFullName(fullName);
        user.setPhone(phone);
        user.setGender(gender);
        user.setAddress(address);

        boolean success = userService.updateUser(user);

        if (success) {
            // Update session
            HttpSession session = request.getSession();
            session.setAttribute("user", user);
            
            request.setAttribute("success", "Profile updated successfully");
            showProfile(request, response, user);
        } else {
            request.setAttribute("error", "Failed to update profile");
            request.setAttribute("user", user);
            showEditProfileForm(request, response, user);
        }
    }

    private void updateSettings(HttpServletRequest request, HttpServletResponse response, User user) 
            throws ServletException, IOException {
        boolean emailNotifications = "on".equals(request.getParameter("emailNotifications"));
        boolean smsNotifications = "on".equals(request.getParameter("smsNotifications"));
        boolean orderUpdates = "on".equals(request.getParameter("orderUpdates"));
        boolean promotionalEmails = "on".equals(request.getParameter("promotionalEmails"));
        boolean newsletterSubscription = "on".equals(request.getParameter("newsletterSubscription"));

        String language = normalizeToAllowed(request.getParameter("language"), java.util.Set.of("en", "hi", "es"), "en");
        String currency = normalizeToAllowed(request.getParameter("currency"), java.util.Set.of("INR", "USD", "EUR", "GBP"), "INR");
        String theme = normalizeToAllowed(request.getParameter("themePreference"), java.util.Set.of("auto", "light", "dark"), "auto");

        String sql = "INSERT INTO user_settings " +
                "(user_id, email_notifications, sms_notifications, order_updates, promotional_emails, newsletter_subscription, language, currency, theme_preference) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE " +
                "email_notifications = VALUES(email_notifications), " +
                "sms_notifications = VALUES(sms_notifications), " +
                "order_updates = VALUES(order_updates), " +
                "promotional_emails = VALUES(promotional_emails), " +
                "newsletter_subscription = VALUES(newsletter_subscription), " +
                "language = VALUES(language), " +
                "currency = VALUES(currency), " +
                "theme_preference = VALUES(theme_preference)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, user.getUserId());
            ps.setBoolean(2, emailNotifications);
            ps.setBoolean(3, smsNotifications);
            ps.setBoolean(4, orderUpdates);
            ps.setBoolean(5, promotionalEmails);
            ps.setBoolean(6, newsletterSubscription);
            ps.setString(7, language);
            ps.setString(8, currency);
            ps.setString(9, theme);
            ps.executeUpdate();
            request.setAttribute("success", "Settings updated successfully");
        } catch (Exception ex) {
            request.setAttribute("error", "Unable to save settings right now. Please try again.");
        }

        loadUserSettings(request, user.getUserId());
        showAccountSettings(request, response, user);
    }

    private void changePassword(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException {
        String currentPassword = request.getParameter("currentPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        // Validate
        if (currentPassword == null || currentPassword.isBlank()
                || newPassword == null || newPassword.isBlank()
                || confirmPassword == null || confirmPassword.isBlank()) {
            request.setAttribute("error", "All password fields are required");
            showAccountSettings(request, response, user);
            return;
        }
        if (newPassword.length() < 8 || newPassword.length() > 128) {
            request.setAttribute("error", "New password must be between 8 and 128 characters");
            showAccountSettings(request, response, user);
            return;
        }
        if (!newPassword.equals(confirmPassword)) {
            request.setAttribute("error", "New passwords do not match");
            showAccountSettings(request, response, user);
            return;
        }

        // Verify current password
        User verifiedUser = userService.loginUser(user.getEmail(), currentPassword);
        if (verifiedUser == null) {
            request.setAttribute("error", "Current password is incorrect");
            showAccountSettings(request, response, user);
            return;
        }

        boolean success = userService.changePassword(user.getUserId(), newPassword);

        if (success) {
            request.setAttribute("success", "Password changed successfully");
            showAccountSettings(request, response, user);
        } else {
            request.setAttribute("error", "Failed to change password");
            showAccountSettings(request, response, user);
        }
    }

    private static String trim(String s) {
        return s == null ? null : s.trim();
    }

    private void loadUserSettings(HttpServletRequest request, int userId) {
        String sql = "SELECT email_notifications, sms_notifications, order_updates, promotional_emails, " +
                "newsletter_subscription, language, currency, theme_preference FROM user_settings WHERE user_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    request.setAttribute("emailNotifications", rs.getBoolean("email_notifications"));
                    request.setAttribute("smsNotifications", rs.getBoolean("sms_notifications"));
                    request.setAttribute("orderUpdates", rs.getBoolean("order_updates"));
                    request.setAttribute("promotionalEmails", rs.getBoolean("promotional_emails"));
                    request.setAttribute("newsletterSubscription", rs.getBoolean("newsletter_subscription"));
                    request.setAttribute("language", rs.getString("language"));
                    request.setAttribute("currency", rs.getString("currency"));
                    request.setAttribute("themePreference", rs.getString("theme_preference"));
                    return;
                }
            }
        } catch (Exception ignored) {
        }

        request.setAttribute("emailNotifications", true);
        request.setAttribute("smsNotifications", false);
        request.setAttribute("orderUpdates", true);
        request.setAttribute("promotionalEmails", false);
        request.setAttribute("newsletterSubscription", false);
        request.setAttribute("language", "en");
        request.setAttribute("currency", "INR");
        request.setAttribute("themePreference", "auto");
    }

    private static String normalizeToAllowed(String value, java.util.Set<String> allowed, String fallback) {
        if (value == null) {
            return fallback;
        }
        String normalized = value.trim();
        return allowed.contains(normalized) ? normalized : fallback;
    }
}
