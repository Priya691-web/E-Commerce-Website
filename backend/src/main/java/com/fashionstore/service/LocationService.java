package com.fashionstore.service;

import com.fashionstore.dto.DeliveryLocationDTO;
import java.util.List;

/**
 * Service interface for location-based operations
 */
public interface LocationService {
    
    /**
     * Validate if a pincode is serviceable
     */
    DeliveryLocationDTO validatePincode(String pincode);
    
    /**
     * Get saved locations for a user
     */
    List<DeliveryLocationDTO> getSavedLocations(int userId);
    
    /**
     * Detect user location by IP address
     */
    DeliveryLocationDTO detectLocationByIP(String ipAddress);
    
    /**
     * Get delivery estimate for a pincode
     */
    DeliveryLocationDTO getDeliveryEstimate(String pincode);
    
    /**
     * Save a new location for user
     */
    boolean saveLocation(DeliveryLocationDTO location);
    
    /**
     * Update an existing location
     */
    boolean updateLocation(DeliveryLocationDTO location);
    
    /**
     * Delete a location
     */
    boolean deleteLocation(int locationId, int userId);
    
    /**
     * Set default location for user
     */
    boolean setDefaultLocation(int locationId, int userId);
    
    /**
     * Get default location for user
     */
    DeliveryLocationDTO getDefaultLocation(int userId);
}
