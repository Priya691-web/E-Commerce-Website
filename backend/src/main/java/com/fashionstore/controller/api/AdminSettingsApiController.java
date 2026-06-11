package com.fashionstore.controller.api;

import com.fashionstore.controller.ApiResponse;
import com.fashionstore.registry.ServiceRegistry;
import com.fashionstore.service.SettingsService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

/**
 * AdminSettingsApiController - Admin Settings Management
 * 
 * Handles application settings CRUD operations:
 * - GET /api/admin/settings - Get all settings
 * - PUT /api/admin/settings - Update settings
 * 
 * Settings include:
 * - Store configuration
 * - Payment gateway settings
 * - Email notification settings
 * - Shipping settings
 * - Tax settings
 * - UI/UX preferences
 */
@WebServlet("/api/admin/settings/*")
public class AdminSettingsApiController extends AdminApiBaseController {

    private static final long serialVersionUID = 1L;

    private SettingsService settingsService;

    @Override
    public void init() {
        super.init();
        settingsService = ServiceRegistry.getInstance().getSettingsService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        applyCors(request, response);
        if (!ensureAdmin(request, response)) return;
        
        try {
            String pathInfo = request.getPathInfo();
            
            if (pathInfo == null || pathInfo.equals("/")) {
                // GET /api/admin/settings - Get all settings
                Map<String, String> settings = settingsService.getAllSettings();
                writeApiResponse(response, 200, ApiResponse.success("Settings retrieved successfully", settings));
                return;
            }
            
            writeApiResponse(response, 404, ApiResponse.error("Not found"));
        } catch (Exception e) {
            handleException(response, e);
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        applyCors(request, response);
        if (!isTrustedStateChangingRequest(request)) {
            writeApiResponse(response, 403, ApiResponse.error("Blocked by origin policy"));
            return;
        }
        if (!ensureAdmin(request, response)) return;
        
        try {
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                // PUT /api/admin/settings - Update settings
                Map<String, Object> body = readJsonBody(request);
                
                // Convert Object values to String for settings
                Map<String, String> stringSettings = new java.util.HashMap<>();
                for (Map.Entry<String, Object> entry : body.entrySet()) {
                    if (entry.getValue() != null) {
                        stringSettings.put(entry.getKey(), String.valueOf(entry.getValue()));
                    }
                }
                
                boolean success = settingsService.setSettings(stringSettings);
                if (success) {
                    writeApiResponse(response, 200, ApiResponse.success("Settings updated successfully", null));
                } else {
                    writeApiResponse(response, 400, ApiResponse.error("Failed to update settings"));
                }
                return;
            }
            
            writeApiResponse(response, 404, ApiResponse.error("Not found"));
        } catch (Exception e) {
            handleException(response, e);
        }
    }

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) {
        applyCors(request, response);
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }
}
