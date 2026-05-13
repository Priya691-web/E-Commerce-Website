package com.fashionstore.dto;

import java.time.LocalDateTime;

/**
 * DTO for newsletter subscriptions
 * Used for managing email marketing and newsletter campaigns
 */
public class NewsletterDTO {
    private int subscriptionId;
    private String email;
    private String firstName;
    private String lastName;
    private String preferences; // JSON string of preference categories
    private String status; // active, unsubscribed, bounced
    private String subscriptionToken;
    private LocalDateTime subscribedAt;
    private LocalDateTime unsubscribedAt;
    private LocalDateTime lastEmailSent;
    private int emailCount;
    private int openCount;
    private int clickCount;
    private boolean isActive;
    private String source; // footer, popup, checkout, etc.

    public NewsletterDTO() {}

    public NewsletterDTO(String email, String firstName, String lastName) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.status = "active";
        this.subscribedAt = LocalDateTime.now();
        this.emailCount = 0;
        this.openCount = 0;
        this.clickCount = 0;
        this.isActive = true;
        this.source = "footer";
    }

    // Getters and Setters
    public int getSubscriptionId() { return subscriptionId; }
    public void setSubscriptionId(int subscriptionId) { this.subscriptionId = subscriptionId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getPreferences() { return preferences; }
    public void setPreferences(String preferences) { this.preferences = preferences; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getSubscriptionToken() { return subscriptionToken; }
    public void setSubscriptionToken(String subscriptionToken) { this.subscriptionToken = subscriptionToken; }

    public LocalDateTime getSubscribedAt() { return subscribedAt; }
    public void setSubscribedAt(LocalDateTime subscribedAt) { this.subscribedAt = subscribedAt; }

    public LocalDateTime getUnsubscribedAt() { return unsubscribedAt; }
    public void setUnsubscribedAt(LocalDateTime unsubscribedAt) { this.unsubscribedAt = unsubscribedAt; }

    public LocalDateTime getLastEmailSent() { return lastEmailSent; }
    public void setLastEmailSent(LocalDateTime lastEmailSent) { this.lastEmailSent = lastEmailSent; }

    public int getEmailCount() { return emailCount; }
    public void setEmailCount(int emailCount) { this.emailCount = emailCount; }

    public int getOpenCount() { return openCount; }
    public void setOpenCount(int openCount) { this.openCount = openCount; }

    public int getClickCount() { return clickCount; }
    public void setClickCount(int clickCount) { this.clickCount = clickCount; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    @Override
    public String toString() {
        return "NewsletterDTO{" +
                "subscriptionId=" + subscriptionId +
                ", email='" + email + '\'' +
                ", status='" + status + '\'' +
                ", isActive=" + isActive +
                ", subscribedAt=" + subscribedAt +
                '}';
    }
}
