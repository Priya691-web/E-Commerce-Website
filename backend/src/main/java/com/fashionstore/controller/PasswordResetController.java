package com.fashionstore.controller;

import com.fashionstore.dao.PasswordResetTokenDAO;
import com.fashionstore.daoimpl.PasswordResetTokenDAOImpl;
import com.fashionstore.model.PasswordResetToken;
import com.fashionstore.model.User;
import com.fashionstore.service.EmailService;
import com.fashionstore.service.UserService;
import com.fashionstore.security.CSRFProtection;
import com.fashionstore.security.RateLimiter;
import com.fashionstore.util.AuditLogger;
import com.fashionstore.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@WebServlet({"/forgot-password", "/reset-password"})
public class PasswordResetController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(PasswordResetController.class);
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final int TOKEN_LENGTH = 32;
    private static final int TOKEN_EXPIRY_HOURS = 1;

    private UserService userService;
    private PasswordResetTokenDAO tokenDAO;

    @Override
    public void init() {
        userService = new UserService();
        tokenDAO = new PasswordResetTokenDAOImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath();

        if ("/forgot-password".equals(path)) {
            attachCsrfForView(request);
            request.getRequestDispatcher("/WEB-INF/views/forgot-password.jsp").forward(request, response);
        } else if ("/reset-password".equals(path)) {
            String token = request.getParameter("token");
            
            if (token == null || token.isBlank()) {
                request.setAttribute("error", "Invalid reset link. Please request a new password reset.");
                attachCsrfForView(request);
                request.getRequestDispatcher("/WEB-INF/views/forgot-password.jsp").forward(request, response);
                return;
            }

            PasswordResetToken resetToken = tokenDAO.getTokenByToken(token);
            
            if (resetToken == null || !resetToken.isValid()) {
                request.setAttribute("error", "Invalid or expired reset link. Please request a new password reset.");
                attachCsrfForView(request);
                request.getRequestDispatcher("/WEB-INF/views/forgot-password.jsp").forward(request, response);
                return;
            }

            request.setAttribute("token", token);
            attachCsrfForView(request);
            request.getRequestDispatcher("/WEB-INF/views/reset-password.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath();

        if ("/forgot-password".equals(path)) {
            handleForgotPassword(request, response);
        } else if ("/reset-password".equals(path)) {
            handleResetPassword(request, response);
        }
    }

    private void handleForgotPassword(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!RateLimiter.checkRateLimit(request, "/forgot-password")) {
            request.setAttribute("error", "Too many reset attempts. Please try again shortly.");
            attachCsrfForView(request);
            request.getRequestDispatcher("/WEB-INF/views/forgot-password.jsp").forward(request, response);
            return;
        }

        String email = request.getParameter("email");

        if (email == null || email.isBlank()) {
            request.setAttribute("error", "Email address is required");
            attachCsrfForView(request);
            request.getRequestDispatcher("/WEB-INF/views/forgot-password.jsp").forward(request, response);
            return;
        }

        // Generic success message regardless of whether the account exists,
        // so attackers can't enumerate registered emails via this endpoint.
        String genericSuccess = "If an account exists for that email, we just sent a password reset link. The link expires in 1 hour.";

        User user = userService.getUserByEmail(email);

        if (user == null) {
            // Don't disclose enumeration; do log so legitimate ops can audit.
            logger.info("Password reset requested for unknown email (no account)");
            request.setAttribute("success", genericSuccess);
            attachCsrfForView(request);
            request.getRequestDispatcher("/WEB-INF/views/forgot-password.jsp").forward(request, response);
            return;
        }

        try {
            tokenDAO.deleteExpiredTokens();

            PasswordResetToken existingToken = tokenDAO.getTokenByUserId(user.getUserId());
            if (existingToken != null) {
                tokenDAO.invalidateToken(existingToken.getToken());
            }

            String tokenString = generateSecureToken();
            LocalDateTime expiresAt = LocalDateTime.now().plusHours(TOKEN_EXPIRY_HOURS);

            PasswordResetToken token = new PasswordResetToken(user.getUserId(), tokenString, expiresAt);
            int tokenId = tokenDAO.createToken(token);

            if (tokenId > 0) {
                String resetLink = request.getRequestURL().toString().replace("forgot-password", "reset-password?token=" + tokenString);
                
                logger.info("Password reset link generated for user {}: {}", user.getEmail(), resetLink);
                AuditLogger.log("PASSWORD_RESET_REQUESTED", "Password reset requested for user: " + user.getEmail(), 
                               String.valueOf(user.getUserId()), request);

                // Send email with reset link
                EmailService.getInstance().sendPasswordResetEmail(user.getEmail(), resetLink, user.getFullName());

                // Use the generic success message (no resetLink echo) so the response
                // is identical for known and unknown emails.
                request.setAttribute("success", genericSuccess);
            } else {
                request.setAttribute("error", "Failed to generate reset link. Please try again.");
            }

        } catch (Exception e) {
            logger.error("Error in forgot password: {}", e.getMessage(), e);
            request.setAttribute("error", "An error occurred. Please try again.");
        }

        attachCsrfForView(request);
        request.getRequestDispatcher("/WEB-INF/views/forgot-password.jsp").forward(request, response);
    }

    private void handleResetPassword(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String token = request.getParameter("token");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        if (token == null || token.isBlank()) {
            request.setAttribute("error", "Invalid reset link");
            attachCsrfForView(request);
            request.getRequestDispatcher("/WEB-INF/views/forgot-password.jsp").forward(request, response);
            return;
        }

        Validator passwordRules = Validator.create()
                .validatePassword(password, "Password")
                .validateMatch(password, confirmPassword, "Passwords");
        if (passwordRules.hasErrors()) {
            request.setAttribute("error", passwordRules.getFirstError());
            request.setAttribute("token", token);
            attachCsrfForView(request);
            request.getRequestDispatcher("/WEB-INF/views/reset-password.jsp").forward(request, response);
            return;
        }

        try {
            PasswordResetToken resetToken = tokenDAO.getTokenByToken(token);

            if (resetToken == null || !resetToken.isValid()) {
                request.setAttribute("error", "Invalid or expired reset link. Please request a new password reset.");
                attachCsrfForView(request);
                request.getRequestDispatcher("/WEB-INF/views/forgot-password.jsp").forward(request, response);
                return;
            }

            boolean passwordUpdated = userService.changePassword(resetToken.getUserId(), password);

            if (passwordUpdated) {
                tokenDAO.markTokenAsUsed(resetToken.getTokenId());
                
                logger.info("Password reset successful for user ID: {}", resetToken.getUserId());
                AuditLogger.log("PASSWORD_RESET_SUCCESS", "Password reset successful for user ID: " + resetToken.getUserId(), 
                               String.valueOf(resetToken.getUserId()), request);

                response.sendRedirect(request.getContextPath() + "/login?reset=success");
                return;
            } else {
                request.setAttribute("error", "Failed to update password. Please try again.");
                request.setAttribute("token", token);
                attachCsrfForView(request);
                request.getRequestDispatcher("/WEB-INF/views/reset-password.jsp").forward(request, response);
            }

        } catch (Exception e) {
            logger.error("Error in reset password: {}", e.getMessage(), e);
            request.setAttribute("error", "An error occurred. Please try again.");
            request.setAttribute("token", token);
            attachCsrfForView(request);
            request.getRequestDispatcher("/WEB-INF/views/reset-password.jsp").forward(request, response);
        }
    }

    private static void attachCsrfForView(HttpServletRequest request) {
        CSRFProtection.addTokenToRequest(request);
    }

    private String generateSecureToken() {
        byte[] randomBytes = new byte[TOKEN_LENGTH];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }
}
