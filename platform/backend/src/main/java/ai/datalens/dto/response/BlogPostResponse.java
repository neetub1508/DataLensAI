package ai.datalens.dto.response;

import ai.datalens.entity.BlogPost;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public class BlogPostResponse {
    
    private UUID id;
    private String title;
    private String slug;
    private String content;
    private String excerpt;
    private UserResponse author;
    private String status;
    private String featuredImageUrl;
    private Set<String> tags;
    private Set<BlogCategoryResponse> categories;
    private UserResponse approvedBy;
    private LocalDateTime approvedAt;
    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors
    public BlogPostResponse() {}
    
    public BlogPostResponse(BlogPost blogPost) {
        this.id = blogPost.getId();
        this.title = blogPost.getTitle();
        this.slug = blogPost.getSlug();
        this.content = blogPost.getContent();
        this.excerpt = blogPost.getExcerpt();
        this.author = blogPost.getAuthor() != null ? new UserResponse(blogPost.getAuthor()) : null;
        this.status = blogPost.getStatus() != null ? blogPost.getStatus().name() : null;
        this.featuredImageUrl = blogPost.getFeaturedImageUrl();
        this.tags = blogPost.getTags();
        this.approvedBy = blogPost.getApprovedBy() != null ? new UserResponse(blogPost.getApprovedBy()) : null;
        this.approvedAt = blogPost.getApprovedAt();
        this.publishedAt = blogPost.getPublishedAt();
        this.createdAt = blogPost.getCreatedAt();
        this.updatedAt = blogPost.getUpdatedAt();
    }
    
    // Getters and Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getSlug() {
        return slug;
    }
    
    public void setSlug(String slug) {
        this.slug = slug;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public String getExcerpt() {
        return excerpt;
    }
    
    public void setExcerpt(String excerpt) {
        this.excerpt = excerpt;
    }
    
    public UserResponse getAuthor() {
        return author;
    }
    
    public void setAuthor(UserResponse author) {
        this.author = author;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getFeaturedImageUrl() {
        return featuredImageUrl;
    }
    
    public void setFeaturedImageUrl(String featuredImageUrl) {
        this.featuredImageUrl = featuredImageUrl;
    }
    
    public Set<String> getTags() {
        return tags;
    }
    
    public void setTags(Set<String> tags) {
        this.tags = tags;
    }
    
    public Set<BlogCategoryResponse> getCategories() {
        return categories;
    }
    
    public void setCategories(Set<BlogCategoryResponse> categories) {
        this.categories = categories;
    }
    
    public UserResponse getApprovedBy() {
        return approvedBy;
    }
    
    public void setApprovedBy(UserResponse approvedBy) {
        this.approvedBy = approvedBy;
    }
    
    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }
    
    public void setApprovedAt(LocalDateTime approvedAt) {
        this.approvedAt = approvedAt;
    }
    
    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }
    
    public void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}