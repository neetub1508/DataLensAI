package ai.datalens.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Set;

public class BlogPostRequest {
    
    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;
    
    @NotBlank(message = "Content is required")
    private String content;
    
    @Size(max = 500, message = "Excerpt must not exceed 500 characters")
    private String excerpt;
    
    @Size(max = 500, message = "Featured image URL must not exceed 500 characters")
    private String featuredImageUrl;
    
    private Set<String> tags;
    
    private Set<String> categoryIds;
    
    // Constructors
    public BlogPostRequest() {}
    
    // Getters and Setters
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
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
    
    public Set<String> getCategoryIds() {
        return categoryIds;
    }
    
    public void setCategoryIds(Set<String> categoryIds) {
        this.categoryIds = categoryIds;
    }
}