package com.fashionstore.dao;

import java.util.Map;

/**
 * SettingsDAO - Settings Data Access Object
 * 
 * Handles database operations for application settings
 */
public interface SettingsDAO {
    
    /**
     * Get setting value by key
     */
    String getSetting(String key);
    
    /**
     * Get all settings
     */
    Map<String, String> getAllSettings();
    
    /**
     * Set or update setting
     */
    boolean setSetting(String key, String value);
    
    /**
     * Delete setting
     */
    boolean deleteSetting(String key);
    
    /**
     * Check if setting exists
     */
    boolean settingExists(String key);
}
