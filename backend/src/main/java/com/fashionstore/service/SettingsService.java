package com.fashionstore.service;

import java.util.Map;

/**
 * SettingsService - Application Settings Management
 * 
 * Handles CRUD operations for application settings including:
 * - Store configuration
 * - Payment gateway settings
 * - Email notification settings
 * - Shipping settings
 * - Tax settings
 * - UI/UX preferences
 */
public interface SettingsService {
    
    /**
     * Get setting value by key
     */
    String getSetting(String key);
    
    /**
     * Get setting value with default
     */
    String getSetting(String key, String defaultValue);
    
    /**
     * Get all settings as map
     */
    Map<String, String> getAllSettings();
    
    /**
     * Get settings by category/prefix
     */
    Map<String, String> getSettingsByPrefix(String prefix);
    
    /**
     * Update or create setting
     */
    boolean setSetting(String key, String value);
    
    /**
     * Update multiple settings
     */
    boolean setSettings(Map<String, String> settings);
    
    /**
     * Delete setting
     */
    boolean deleteSetting(String key);
    
    /**
     * Check if setting exists
     */
    boolean settingExists(String key);
}
