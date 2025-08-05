package ai.datalens.service;

import ai.datalens.dto.request.BlogPostRequest;
import ai.datalens.dto.response.BlogCategoryResponse;
import ai.datalens.dto.response.BlogPostResponse;
import ai.datalens.entity.BlogCategory;
import ai.datalens.entity.BlogPost;
import ai.datalens.entity.User;
import ai.datalens.repository.BlogCategoryRepository;
import ai.datalens.repository.BlogPostRepository;
import ai.datalens.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class BlogService {
    
    @Autowired
    private BlogPostRepository blogPostRepository;
    
    @Autowired
    private BlogCategoryRepository blogCategoryRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    // Public methods for viewing published blogs
    public Page<BlogPostResponse> getPublishedPosts(Pageable pageable) {
        Page<BlogPost> posts = blogPostRepository.findByStatusOrderByPublishedAtDesc(
            BlogPost.BlogPostStatus.PUBLISHED, pageable);
        return posts.map(this::convertToResponseWithCategories);
    }
    
    public Optional<BlogPostResponse> getPublishedPostBySlug(String slug) {
        Optional<BlogPost> post = blogPostRepository.findBySlugAndStatus(slug, BlogPost.BlogPostStatus.PUBLISHED);
        return post.map(this::convertToResponseWithCategories);
    }
    
    public Page<BlogPostResponse> searchPublishedPosts(String query, Pageable pageable) {
        Page<BlogPost> posts = blogPostRepository.searchPublishedPosts(query, BlogPost.BlogPostStatus.PUBLISHED, pageable);
        return posts.map(this::convertToResponseWithCategories);
    }
    
    public Page<BlogPostResponse> getPublishedPostsByCategory(String categorySlug, Pageable pageable) {
        Page<BlogPost> posts = blogPostRepository.findByCategorySlugAndStatus(categorySlug, BlogPost.BlogPostStatus.PUBLISHED, pageable);
        return posts.map(this::convertToResponseWithCategories);
    }
    
    public Page<BlogPostResponse> getPublishedPostsByTag(String tag, Pageable pageable) {
        Page<BlogPost> posts = blogPostRepository.findByTagAndStatus(tag, BlogPost.BlogPostStatus.PUBLISHED, pageable);
        return posts.map(this::convertToResponseWithCategories);
    }
    
    // Methods for authenticated users to manage their posts
    public BlogPostResponse createPost(BlogPostRequest request, UUID authorId) {
        User author = userRepository.findById(authorId)
            .orElseThrow(() -> new RuntimeException("Author not found"));
        
        BlogPost post = new BlogPost();
        post.setTitle(request.getTitle());
        post.setSlug(generateSlug(request.getTitle()));
        post.setContent(request.getContent());
        post.setExcerpt(request.getExcerpt());
        post.setAuthor(author);
        post.setFeaturedImageUrl(request.getFeaturedImageUrl());
        post.setTags(request.getTags());
        post.setStatus(BlogPost.BlogPostStatus.DRAFT);
        
        // Set categories
        if (request.getCategoryIds() != null && !request.getCategoryIds().isEmpty()) {
            Set<BlogCategory> categories = new HashSet<>();
            for (String categoryIdStr : request.getCategoryIds()) {
                UUID categoryId = UUID.fromString(categoryIdStr);
                blogCategoryRepository.findById(categoryId).ifPresent(categories::add);
            }
            post.setCategories(categories);
        }
        
        BlogPost savedPost = blogPostRepository.save(post);
        return convertToResponseWithCategories(savedPost);
    }
    
    public BlogPostResponse updatePost(UUID postId, BlogPostRequest request, UUID userId) {
        BlogPost post = blogPostRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("Post not found"));
        
        // Check if user is the author or admin
        if (!post.getAuthor().getId().equals(userId) && !isAdmin(userId)) {
            throw new RuntimeException("Not authorized to update this post");
        }
        
        post.setTitle(request.getTitle());
        post.setSlug(generateSlug(request.getTitle()));
        post.setContent(request.getContent());
        post.setExcerpt(request.getExcerpt());
        post.setFeaturedImageUrl(request.getFeaturedImageUrl());
        post.setTags(request.getTags());
        
        // Update categories
        if (request.getCategoryIds() != null) {
            Set<BlogCategory> categories = new HashSet<>();
            for (String categoryIdStr : request.getCategoryIds()) {
                UUID categoryId = UUID.fromString(categoryIdStr);
                blogCategoryRepository.findById(categoryId).ifPresent(categories::add);
            }
            post.setCategories(categories);
        }
        
        BlogPost savedPost = blogPostRepository.save(post);
        return convertToResponseWithCategories(savedPost);
    }
    
    public void deletePost(UUID postId, UUID userId) {
        BlogPost post = blogPostRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("Post not found"));
        
        // Check if user is the author or admin
        if (!post.getAuthor().getId().equals(userId) && !isAdmin(userId)) {
            throw new RuntimeException("Not authorized to delete this post");
        }
        
        blogPostRepository.delete(post);
    }
    
    public Page<BlogPostResponse> getUserPosts(UUID userId, Pageable pageable) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Page<BlogPost> posts = blogPostRepository.findByAuthorOrderByCreatedAtDesc(user, pageable);
        return posts.map(this::convertToResponseWithCategories);
    }
    
    public BlogPostResponse submitForApproval(UUID postId, UUID userId) {
        BlogPost post = blogPostRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("Post not found"));
        
        if (!post.getAuthor().getId().equals(userId)) {
            throw new RuntimeException("Not authorized to submit this post");
        }
        
        if (post.getStatus() != BlogPost.BlogPostStatus.DRAFT) {
            throw new RuntimeException("Only draft posts can be submitted for approval");
        }
        
        post.setStatus(BlogPost.BlogPostStatus.PENDING_APPROVAL);
        BlogPost savedPost = blogPostRepository.save(post);
        return convertToResponseWithCategories(savedPost);
    }
    
    // Admin methods
    public Page<BlogPostResponse> getPendingPosts(Pageable pageable) {
        Page<BlogPost> posts = blogPostRepository.findByStatusOrderByCreatedAtDesc(
            BlogPost.BlogPostStatus.PENDING_APPROVAL, pageable);
        return posts.map(this::convertToResponseWithCategories);
    }
    
    public BlogPostResponse approvePost(UUID postId, UUID adminId) {
        if (!isAdmin(adminId)) {
            throw new RuntimeException("Not authorized to approve posts");
        }
        
        BlogPost post = blogPostRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("Post not found"));
        
        User admin = userRepository.findById(adminId)
            .orElseThrow(() -> new RuntimeException("Admin not found"));
        
        post.setStatus(BlogPost.BlogPostStatus.PUBLISHED);
        post.setApprovedBy(admin);
        post.setApprovedAt(LocalDateTime.now());
        post.setPublishedAt(LocalDateTime.now());
        
        BlogPost savedPost = blogPostRepository.save(post);
        return convertToResponseWithCategories(savedPost);
    }
    
    public BlogPostResponse rejectPost(UUID postId, UUID adminId) {
        if (!isAdmin(adminId)) {
            throw new RuntimeException("Not authorized to reject posts");
        }
        
        BlogPost post = blogPostRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("Post not found"));
        
        post.setStatus(BlogPost.BlogPostStatus.REJECTED);
        
        BlogPost savedPost = blogPostRepository.save(post);
        return convertToResponseWithCategories(savedPost);
    }
    
    // Category methods
    public List<BlogCategoryResponse> getAllCategories() {
        return blogCategoryRepository.findAllByOrderByNameAsc()
            .stream()
            .map(BlogCategoryResponse::new)
            .collect(Collectors.toList());
    }
    
    public List<BlogCategoryResponse> getCategoriesWithPublishedPosts() {
        return blogCategoryRepository.findCategoriesWithPublishedPosts()
            .stream()
            .map(BlogCategoryResponse::new)
            .collect(Collectors.toList());
    }
    
    // Helper methods
    private String generateSlug(String title) {
        String baseSlug = title.toLowerCase()
            .replaceAll("[^a-z0-9\\s-]", "")
            .replaceAll("\\s+", "-")
            .replaceAll("-+", "-")
            .replaceAll("^-|-$", "");
        
        String slug = baseSlug;
        int counter = 1;
        while (blogPostRepository.existsBySlug(slug)) {
            slug = baseSlug + "-" + counter;
            counter++;
        }
        
        return slug;
    }
    
    private boolean isAdmin(UUID userId) {
        Optional<User> user = userRepository.findById(userId);
        return user.isPresent() && user.get().getRoles().stream()
            .anyMatch(role -> "admin".equals(role.getName()));
    }
    
    private BlogPostResponse convertToResponseWithCategories(BlogPost post) {
        BlogPostResponse response = new BlogPostResponse(post);
        if (post.getCategories() != null) {
            Set<BlogCategoryResponse> categoryResponses = post.getCategories().stream()
                .map(BlogCategoryResponse::new)
                .collect(Collectors.toSet());
            response.setCategories(categoryResponses);
        }
        return response;
    }
}