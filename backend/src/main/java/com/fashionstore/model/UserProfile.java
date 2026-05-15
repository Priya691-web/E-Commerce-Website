package com.fashionstore.model;

import java.sql.Date;
import java.sql.Timestamp;

public class UserProfile {
    private int profileId;
    private int userId;
    private Date dateOfBirth;
    private String profileImageUrl;
    private String bio;
    private int preferredShippingAddressId;
    private int preferredBillingAddressId;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Constructors
    public UserProfile() {}

    public UserProfile(int userId) {
        this.userId = userId;
    }

    // Getters and Setters
    public int getProfileId() { return profileId; }
    public void setProfileId(int profileId) { this.profileId = profileId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public Date getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(Date dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public int getPreferredShippingAddressId() { return preferredShippingAddressId; }
    public void setPreferredShippingAddressId(int preferredShippingAddressId) { this.preferredShippingAddressId = preferredShippingAddressId; }

    public int getPreferredBillingAddressId() { return preferredBillingAddressId; }
    public void setPreferredBillingAddressId(int preferredBillingAddressId) { this.preferredBillingAddressId = preferredBillingAddressId; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "UserProfile{" +
                "profileId=" + profileId +
                ", userId=" + userId +
                ", dateOfBirth=" + dateOfBirth +
                ", profileImageUrl='" + profileImageUrl + '\'' +
                ", bio='" + bio + '\'' +
                ", preferredShippingAddressId=" + preferredShippingAddressId +
                ", preferredBillingAddressId=" + preferredBillingAddressId +
                '}';
    }
}
