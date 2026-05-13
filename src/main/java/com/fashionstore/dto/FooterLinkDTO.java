package com.fashionstore.dto;

import java.time.LocalDateTime;

/**
 * DTO for footer links
 * Used for managing footer navigation and links
 */
public class FooterLinkDTO {
    private int linkId;
    private String title;
    private String url;
    private String section; // get-to-know-us, connect-with-us, make-money, support, legal, membership
    private String target; // _self, _blank
    private int sortOrder;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int clickCount;

    public FooterLinkDTO() {}

    public FooterLinkDTO(String title, String url, String section) {
        this.title = title;
        this.url = url;
        this.section = section;
        this.target = "_self";
        this.sortOrder = 0;
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.clickCount = 0;
    }

    // Getters and Setters
    public int getLinkId() { return linkId; }
    public void setLinkId(int linkId) { this.linkId = linkId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }

    public String getTarget() { return target; }
    public void setTarget(String target) { this.target = target; }

    public int getSortOrder() { return sortOrder; }
    public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public int getClickCount() { return clickCount; }
    public void setClickCount(int clickCount) { this.clickCount = clickCount; }

    @Override
    public String toString() {
        return "FooterLinkDTO{" +
                "linkId=" + linkId +
                ", title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", section='" + section + '\'' +
                ", isActive=" + isActive +
                ", clickCount=" + clickCount +
                '}';
    }
}
