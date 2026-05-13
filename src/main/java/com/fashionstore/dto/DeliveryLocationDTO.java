package com.fashionstore.dto;

import java.time.LocalDateTime;

/**
 * DTO for delivery location information
 * Used for location-based delivery estimates and address management
 */
public class DeliveryLocationDTO {
    private int locationId;
    private String pincode;
    private String city;
    private String state;
    private String country;
    private String area;
    private String landmark;
    private double latitude;
    private double longitude;
    private boolean isDefault;
    private boolean isServiceable;
    private int estimatedDeliveryDays;
    private String deliveryTimeSlot;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int userId;

    public DeliveryLocationDTO() {}

    public DeliveryLocationDTO(String pincode, String city, String state, String country) {
        this.pincode = pincode;
        this.city = city;
        this.state = state;
        this.country = country;
        this.isServiceable = true;
        this.estimatedDeliveryDays = 3;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public int getLocationId() { return locationId; }
    public void setLocationId(int locationId) { this.locationId = locationId; }

    public String getPincode() { return pincode; }
    public void setPincode(String pincode) { this.pincode = pincode; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getArea() { return area; }
    public void setArea(String area) { this.area = area; }

    public String getLandmark() { return landmark; }
    public void setLandmark(String landmark) { this.landmark = landmark; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public boolean isDefault() { return isDefault; }
    public void setDefault(boolean isDefault) { this.isDefault = isDefault; }

    public boolean isServiceable() { return isServiceable; }
    public void setServiceable(boolean isServiceable) { this.isServiceable = isServiceable; }

    public int getEstimatedDeliveryDays() { return estimatedDeliveryDays; }
    public void setEstimatedDeliveryDays(int estimatedDeliveryDays) { this.estimatedDeliveryDays = estimatedDeliveryDays; }

    public String getDeliveryTimeSlot() { return deliveryTimeSlot; }
    public void setDeliveryTimeSlot(String deliveryTimeSlot) { this.deliveryTimeSlot = deliveryTimeSlot; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    @Override
    public String toString() {
        return "DeliveryLocationDTO{" +
                "locationId=" + locationId +
                ", pincode='" + pincode + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", country='" + country + '\'' +
                ", isDefault=" + isDefault +
                ", isServiceable=" + isServiceable +
                ", estimatedDeliveryDays=" + estimatedDeliveryDays +
                '}';
    }
}
