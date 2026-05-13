package com.fashionstore.dto;

import java.time.LocalDateTime;

/**
 * DTO for CMS pages
 * Used for managing static content pages like About Us, Privacy Policy, etc.
 */
public class CMSPageDTO {
    private int pageId;
    private String title;
    private String slug;
    private String content;
    private String metaTitle;
    private String metaDescription;
    private String metaKeywords;
    private String status; // published, draft, archived
    private String template; // default, about, policy, etc.
    private int authorId;
    private String authorName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime publishedAt;
    private boolean isActive;
    private int viewCount;
    private String featuredImage;
    private String excerpt;

    public CMSPageDTO() {}

    public CMSPageDTO(String title, String slug, String content) {
        this.title = title;
        this.slug = slug;
        this.content = content;
        this.status = "draft";
        this.template = "default";
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.isActive = true;
        this.viewCount = 0;
    }

    // Getters and Setters
    public int getPageId() { return pageId; }
    public void setPageId(int pageId) { this.pageId = pageId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getMetaTitle() { return metaTitle; }
    public void setMetaTitle(String metaTitle) { this.metaTitle = metaTitle; }

    public String getMetaDescription() { return metaDescription; }
    public void setMetaDescription(String metaDescription) { this.metaDescription = metaDescription; }

    public String getMetaKeywords() { return metaKeywords; }
    public void setMetaKeywords(String metaKeywords) { this.metaKeywords = metaKeywords; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getTemplate() { return template; }
    public void setTemplate(String template) { this.template = template; }

    public int getAuthorId() { return authorId; }
    public void setAuthorId(int authorId) { this.authorId = authorId; }

    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDateTime getPublishedAt() { return publishedAt; }
    public void setPublishedAt(LocalDateTime publishedAt) { this.publishedAt = publishedAt; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public int getViewCount() { return viewCount; }
    public void setViewCount(int viewCount) { this.viewCount = viewCount; }

    public String getFeaturedImage() { return featuredImage; }
    public void setFeaturedImage(String featuredImage) { this.featuredImage = featuredImage; }

    public String getExcerpt() { return excerpt; }
    public void setExcerpt(String excerpt) { this.excerpt = excerpt; }

    @Override
    public String toString() {
        return "CMSPageDTO{" +
                "pageId=" + pageId +
                ", title='" + title + '\'' +
                ", slug='" + slug + '\'' +
                ", status='" + status + '\'' +
                ", isActive=" + isActive +
                ", viewCount=" + viewCount +
                '}';
    }
}
