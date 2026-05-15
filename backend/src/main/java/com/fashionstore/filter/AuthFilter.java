package com.fashionstore.filter;

import com.fashionstore.model.User;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Set;

/**
 * Restricts authenticated-only areas while keeping catalog, auth, password reset,
 * search, payment callbacks, and static assets reachable without a session.
 */
public class AuthFilter implements Filter {

    /** Paths that are public without trailing-slash normalization issues (exact match after normalize). */
    private static final Set<String> PUBLIC_EXACT_PATHS = Set.of(
            "/",
            "/home",
            "/products",
            "/product",
            "/login",
            "/register",
            "/logout",
            "/404",
            "/error",
            "/forgot-password",
            "/reset-password",
            "/search",
            "/success",
            "/payment",
            "/index.jsp",
            "/api/metrics"
    );

    private static final Set<String> PUBLIC_PATH_PREFIXES = Set.of(
            "/assets/",
            "/api/admin/login",
            "/api/admin/register"
    );

    /** Admin JSON API endpoints. */
    private static final String ADMIN_API_PREFIX = "/api/admin/";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        if (req.getDispatcherType() == DispatcherType.ERROR) {
            chain.doFilter(request, response);
            return;
        }

        String contextPath = req.getContextPath();
        String relativePath = normalizePath(req.getRequestURI().substring(contextPath.length()));

        if (isPublicPath(relativePath)) {
            if (shouldRedirectAuthenticatedAwayFromAuthForms(req, relativePath)) {
                resp.sendRedirect(contextPath + "/home");
                return;
            }
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = req.getSession(false);
        User customerUser = (session != null) ? (User) session.getAttribute("customerAuth") : null;
        User adminUser = (session != null) ? (User) session.getAttribute("adminAuth") : null;

        // Handle remember-me for customer users only
        if (customerUser == null) {
            jakarta.servlet.http.Cookie[] cookies = req.getCookies();
            if (cookies != null) {
                for (jakarta.servlet.http.Cookie cookie : cookies) {
                    if ("remember_me".equals(cookie.getName())) {
                        String cookieValue = cookie.getValue();
                        User rememberedUser = validateRememberMeToken(cookieValue);
                        if (rememberedUser != null && !rememberedUser.isAdmin()) {
                            session = req.getSession(true);
                            session.setAttribute("userId", rememberedUser.getUserId());
                            session.setAttribute("customerId", rememberedUser.getUserId());
                            session.setAttribute("customerAuth", rememberedUser);
                            com.fashionstore.security.CSRFProtection.generateToken(req);
                            
                            // Sync cart
                            com.fashionstore.service.CartService cartService = new com.fashionstore.serviceimpl.CartServiceImpl();
                            session.setAttribute("cartItems", cartService.getCartItems(rememberedUser.getUserId()));
                            
                            customerUser = rememberedUser;
                            com.fashionstore.util.AuditLogger.log("LOGIN_REMEMBER_ME", "User auto-logged in via Remember Me: " + rememberedUser.getEmail(), String.valueOf(rememberedUser.getUserId()), req);
                            break;
                        }
                    }
                }
            }
        }

        // Check for any authentication (customer or admin)
        boolean isLoggedIn = (customerUser != null) || (adminUser != null);
        boolean isAdmin = (adminUser != null);

        if (!isLoggedIn) {
            if (relativePath.startsWith(ADMIN_API_PREFIX)) {
                resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
            boolean isAjax = "XMLHttpRequest".equals(req.getHeader("X-Requested-With"));
            if (isAjax) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.setContentType("application/json");
                resp.setCharacterEncoding("UTF-8");
                resp.getWriter().write("{\"success\":false,\"message\":\"Please login to continue.\",\"redirect\":\""
                        + contextPath + "/login\"}");
            } else {
                resp.sendRedirect(contextPath + "/login");
            }
            return;
        }

        boolean isAdminPath = "/admin".equals(relativePath) || relativePath.startsWith("/admin/");
        if (isAdminPath && !isAdmin) {
            resp.sendRedirect(contextPath + "/home");
            return;
        }

        if (relativePath.startsWith(ADMIN_API_PREFIX) && !isAdmin) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        chain.doFilter(request, response);
    }

    private static boolean isPublicPath(String relativePath) {
        for (String prefix : PUBLIC_PATH_PREFIXES) {
            if (relativePath.startsWith(prefix)) {
                return true;
            }
        }
        return PUBLIC_EXACT_PATHS.contains(relativePath)
                || relativePath.startsWith("/search/");
    }

    /**
     * Covers {@code /search/suggestions} and any future {@code /search/...} endpoints.
     */
    private static String normalizePath(String path) {
        if (path == null || path.isEmpty()) {
            return "/";
        }
        int semi = path.indexOf(';');
        if (semi >= 0) {
            path = path.substring(0, semi);
        }
        if (path.length() > 1 && path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }

    private static boolean shouldRedirectAuthenticatedAwayFromAuthForms(HttpServletRequest req, String relativePath) {
        if (!"GET".equalsIgnoreCase(req.getMethod())) {
            return false;
        }
        if (!"/login".equals(relativePath) && !"/register".equals(relativePath)) {
            return false;
        }
        HttpSession session = req.getSession(false);
        if (session == null) {
            return false;
        }
        // Redirect if either customer or admin is authenticated
        User customerUser = (User) session.getAttribute("customerAuth");
        User adminUser = (User) session.getAttribute("adminAuth");
        return (customerUser != null) || (adminUser != null);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("AuthFilter initialized");
    }

    @Override
    public void destroy() {}

    private User validateRememberMeToken(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            byte[] decodedBytes = java.util.Base64.getUrlDecoder().decode(value);
            String tokenStr = new String(decodedBytes, java.nio.charset.StandardCharsets.UTF_8);
            String[] parts = tokenStr.split(":", 3);
            if (parts.length != 3) {
                return null;
            }
            String email = parts[0];
            long expiry = Long.parseLong(parts[1]);
            String signature = parts[2];

            if (System.currentTimeMillis() > expiry) {
                return null;
            }

            com.fashionstore.service.UserService userService = new com.fashionstore.service.UserService();
            User user = userService.getUserByEmail(email);
            if (user == null) {
                return null;
            }

            String signatureSource = email + ":" + expiry + ":" + user.getPassword() + ":" + "FashionStoreSecretSalt2026";
            String expectedSignature = sha256(signatureSource);
            if (expectedSignature.equals(signature)) {
                return user;
            }
        } catch (Exception e) {
            // Ignore decoding/parsing errors
        }
        return null;
    }

    private static String sha256(String data) {
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
