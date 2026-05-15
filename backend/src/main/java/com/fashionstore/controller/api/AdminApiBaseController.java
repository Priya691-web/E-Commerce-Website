package com.fashionstore.controller.api;

import com.fashionstore.controller.BaseController;
import com.fashionstore.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Base class for admin API controllers, extending BaseController to leverage enterprise-wide standard behaviors
 */
public abstract class AdminApiBaseController extends BaseController {
    private static final long serialVersionUID = 1L;

    @Override
    public void init() {
        super.init();
        initializeAllowedOrigins();
    }

    protected void initializeAllowedOrigins() {
        // Handled in BaseController, but kept for legacy calls if any
    }

    protected Map<String, Object> publicUser(User u) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", u.getUserId());
        m.put("fullName", u.getFullName());
        m.put("email", u.getEmail());
        m.put("role", u.getRole());
        m.put("phone", u.getPhone());
        m.put("blocked", "disabled".equalsIgnoreCase(u.getRole()));
        m.put("orderCount", 0);
        return m;
    }
}
