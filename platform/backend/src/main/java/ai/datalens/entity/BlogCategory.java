package ai.datalens.entity;

import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "blog_categories")
public class BlogCategory extends BaseEntity {
    
    @Column(nullable = false, unique = true, length = 100)
    private String name;
    
    @Column(length = 500)
    private String description;
    
    @Column(nullable = false, unique = true, length = 100)
    private String slug;
    
    @ManyToMany(mappedBy = "categories")
    private Set<BlogPost> posts;
    
    // Constructors
    public BlogCategory() {}
    
    public BlogCategory(String name, String description, String slug) {
        this.name = name;
        this.description = description;
        this.slug = slug;
    }
    
    // Getters and Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getSlug() {
        return slug;
    }
    
    public void setSlug(String slug) {
        this.slug = slug;
    }
    
    public Set<BlogPost> getPosts() {
        return posts;
    }
    
    public void setPosts(Set<BlogPost> posts) {
        this.posts = posts;
    }
}