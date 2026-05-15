package com.fashionstore.dto;

import java.time.LocalDateTime;

/**
 * DTO for user profile information
 * Used for comprehensive user profile management
 */
public class UserProfileDTO {
    private int userId;
    private String fullName;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String gender;
    private String dateOfBirth;
    private String profileImage;
    private String bio;
    private String preferences; // JSON string for user preferences
    private String membershipTier; // basic, premium, elite
    private LocalDateTime membershipExpiry;
    private int loyaltyPoints;
    private String referralCode;
    private int referredCount;
    private boolean isEmailVerified;
    private boolean isPhoneVerified;
    private boolean isProfileComplete;
    private LocalDateTime lastLoginAt;
    private String lastLoginIP;
    private String lastLoginDevice;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public UserProfileDTO() {}

    public UserProfileDTO(int userId, String fullName, String email) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.membershipTier = "basic";
        this.loyaltyPoints = 0;
        this.isEmailVerified = false;
        this.isPhoneVerified = false;
        this.isProfileComplete = false;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getProfileImage() { return profileImage; }
    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getPreferences() { return preferences; }
    public void setPreferences(String preferences) { this.preferences = preferences; }

    public String getMembershipTier() { return membershipTier; }
    public void setMembershipTier(String membershipTier) { this.membershipTier = membershipTier; }

    public LocalDateTime getMembershipExpiry() { return membershipExpiry; }
    public void setMembershipExpiry(LocalDateTime membershipExpiry) { this.membershipExpiry = membershipExpiry; }

    public int getLoyaltyPoints() { return loyaltyPoints; }
    public void setLoyaltyPoints(int loyaltyPoints) { this.loyaltyPoints = loyaltyPoints; }

    public String getReferralCode() { return referralCode; }
    public void setReferralCode(String referralCode) { this.referralCode = referralCode; }

    public int getReferredCount() { return referredCount; }
    public void setReferredCount(int referredCount) { this.referredCount = referredCount; }

    public boolean isEmailVerified() { return isEmailVerified; }
    public void setEmailVerified(boolean emailVerified) { isEmailVerified = emailVerified; }

    public boolean isPhoneVerified() { return isPhoneVerified; }
    public void setPhoneVerified(boolean phoneVerified) { isPhoneVerified = phoneVerified; }

    public boolean isProfileComplete() { return isProfileComplete; }
    public void setProfileComplete(boolean profileComplete) { isProfileComplete = profileComplete; }

    public LocalDateTime getLastLoginAt() { return lastLoginAt; }
    public void setLastLoginAt(LocalDateTime lastLoginAt) { this.lastLoginAt = lastLoginAt; }

    public String getLastLoginIP() { return lastLoginIP; }
    public void setLastLoginIP(String lastLoginIP) { this.lastLoginIP = lastLoginIP; }

    public String getLastLoginDevice() { return lastLoginDevice; }
    public void setLastLoginDevice(String lastLoginDevice) { this.lastLoginDevice = lastLoginDevice; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "UserProfileDTO{" +
                "userId=" + userId +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", membershipTier='" + membershipTier + '\'' +
                ", loyaltyPoints=" + loyaltyPoints +
                ", isEmailVerified=" + isEmailVerified +
                ", isPhoneVerified=" + isPhoneVerified +
                '}';
    }
}
