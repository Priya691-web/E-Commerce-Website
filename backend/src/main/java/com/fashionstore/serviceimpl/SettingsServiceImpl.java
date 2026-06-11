package com.fashionstore.serviceimpl;

import com.fashionstore.dao.SettingsDAO;
import com.fashionstore.service.SettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * SettingsServiceImpl - Application Settings Management Implementation
 */
public class SettingsServiceImpl implements SettingsService {

    private static final Logger logger = LoggerFactory.getLogger(SettingsServiceImpl.class);
    private SettingsDAO settingsDAO;

    public SettingsServiceImpl() {
        this.settingsDAO = null;
    }

    public SettingsServiceImpl(SettingsDAO settingsDAO) {
        this.settingsDAO = settingsDAO;
    }

    public void setSettingsDAO(SettingsDAO settingsDAO) {
        if (this.settingsDAO == null) {
            try {
                java.lang.reflect.Field field = SettingsServiceImpl.class.getDeclaredField("settingsDAO");
                field.setAccessible(true);
                field.set(this, settingsDAO);
            } catch (Exception e) {
                logger.error("Failed to set settingsDAO", e);
            }
        }
    }

    @Override
    public String getSetting(String key) {
        if (settingsDAO == null) {
            throw new IllegalStateException("SettingsDAO not initialized");
        }
        return settingsDAO.getSetting(key);
    }

    @Override
    public String getSetting(String key, String defaultValue) {
        String value = getSetting(key);
        return value != null ? value : defaultValue;
    }

    @Override
    public Map<String, String> getAllSettings() {
        if (settingsDAO == null) {
            throw new IllegalStateException("SettingsDAO not initialized");
        }
        return settingsDAO.getAllSettings();
    }

    @Override
    public Map<String, String> getSettingsByPrefix(String prefix) {
        Map<String, String> allSettings = getAllSettings();
        Map<String, String> filtered = new HashMap<>();
        
        for (Map.Entry<String, String> entry : allSettings.entrySet()) {
            if (entry.getKey().startsWith(prefix)) {
                filtered.put(entry.getKey(), entry.getValue());
            }
        }
        
        return filtered;
    }

    @Override
    public boolean setSetting(String key, String value) {
        if (settingsDAO == null) {
            throw new IllegalStateException("SettingsDAO not initialized");
        }
        return settingsDAO.setSetting(key, value);
    }

    @Override
    public boolean setSettings(Map<String, String> settings) {
        if (settings == null || settings.isEmpty()) {
            return false;
        }
        
        boolean allSuccess = true;
        for (Map.Entry<String, String> entry : settings.entrySet()) {
            if (!setSetting(entry.getKey(), entry.getValue())) {
                allSuccess = false;
                logger.warn("Failed to set setting: {}", entry.getKey());
            }
        }
        
        return allSuccess;
    }

    @Override
    public boolean deleteSetting(String key) {
        if (settingsDAO == null) {
            throw new IllegalStateException("SettingsDAO not initialized");
        }
        return settingsDAO.deleteSetting(key);
    }

    @Override
    public boolean settingExists(String key) {
        return getSetting(key) != null;
    }
}
