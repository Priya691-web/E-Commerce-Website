package com.fashionstore.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * User Settings Data Transfer Object
 */
public class UserSettingsDTO {

    private Integer userSettingsId;
    private Integer userId;
    private String language;
    private String currency;
    private String timezone;
    private Boolean emailNotifications;
    private Boolean smsNotifications;
    private Boolean promotionalEmails;
    private String theme;

    public UserSettingsDTO() {}

    @JsonProperty("id")
    public Integer getUserSettingsId() {
        return userSettingsId;
    }

    public void setUserSettingsId(Integer userSettingsId) {
        this.userSettingsId = userSettingsId;
    }

    @JsonProperty("user_id")
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @JsonProperty("language")
    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    @JsonProperty("currency")
    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @JsonProperty("timezone")
    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    @JsonProperty("email_notifications")
    public Boolean getEmailNotifications() {
        return emailNotifications;
    }

    public void setEmailNotifications(Boolean emailNotifications) {
        this.emailNotifications = emailNotifications;
    }

    @JsonProperty("sms_notifications")
    public Boolean getSmsNotifications() {
        return smsNotifications;
    }

    public void setSmsNotifications(Boolean smsNotifications) {
        this.smsNotifications = smsNotifications;
    }

    @JsonProperty("promotional_emails")
    public Boolean getPromotionalEmails() {
        return promotionalEmails;
    }

    public void setPromotionalEmails(Boolean promotionalEmails) {
        this.promotionalEmails = promotionalEmails;
    }

    @JsonProperty("theme")
    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }
}
