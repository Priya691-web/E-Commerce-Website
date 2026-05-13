package com.fashionstore.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.time.LocalDateTime;
import java.util.List;

/**
 * User Data Transfer Object
 * Provides safe user data representation without sensitive information
 */
@JsonPropertyOrder({"userId", "email", "fullName", "phone", "role", "active", "createdAt", "lastLogin"})
public class UserDTO {

    private Integer userId;
    private String email;
    private String fullName;
    private String phone;
    private String role;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;

    // Additional user profile information
    private UserProfileDTO profile;
    private List<AddressDTO> addresses;
    private UserSettingsDTO settings;

    // Internal fields (not exposed)
    @JsonIgnore
    private String password;

    @JsonIgnore
    private String resetToken;

    @JsonIgnore
    private LocalDateTime tokenExpiry;

    // Constructors
    public UserDTO() {}

    public UserDTO(Integer userId, String email, String fullName, String phone, String role, Boolean active) {
        this.userId = userId;
        this.email = email;
        this.fullName = fullName;
        this.phone = phone;
        this.role = role;
        this.active = active;
    }

    // Getters and Setters
    @JsonProperty("id")
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @JsonProperty("name")
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    @JsonProperty("created_at")
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @JsonProperty("last_login")
    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    @JsonProperty("profile")
    public UserProfileDTO getProfile() {
        return profile;
    }

    public void setProfile(UserProfileDTO profile) {
        this.profile = profile;
    }

    @JsonProperty("addresses")
    public List<AddressDTO> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<AddressDTO> addresses) {
        this.addresses = addresses;
    }

    @JsonProperty("settings")
    public UserSettingsDTO getSettings() {
        return settings;
    }

    public void setSettings(UserSettingsDTO settings) {
        this.settings = settings;
    }

    // Internal setters (not exposed)
    public void setPassword(String password) {
        this.password = password;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }

    public void setTokenExpiry(LocalDateTime tokenExpiry) {
        this.tokenExpiry = tokenExpiry;
    }

    // Utility methods
    public boolean isAdmin() {
        return "admin".equalsIgnoreCase(role);
    }

    public boolean isActiveUser() {
        return Boolean.TRUE.equals(active);
    }

    public boolean hasProfile() {
        return profile != null;
    }

    public boolean hasAddresses() {
        return addresses != null && !addresses.isEmpty();
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "userId=" + userId +
                ", email='" + email + '\'' +
                ", fullName='" + fullName + '\'' +
                ", role='" + role + '\'' +
                ", active=" + active +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserDTO userDTO = (UserDTO) o;

        if (userId != null ? !userId.equals(userDTO.userId) : userDTO.userId != null) return false;
        return email != null ? email.equals(userDTO.email) : userDTO.email == null;
    }

    @Override
    public int hashCode() {
        int result = userId != null ? userId.hashCode() : 0;
        result = 31 * result + (email != null ? email.hashCode() : 0);
        return result;
    }
}
