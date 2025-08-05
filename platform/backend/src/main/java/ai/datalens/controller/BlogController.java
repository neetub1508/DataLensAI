package ai.datalens.controller;

import ai.datalens.dto.request.BlogPostRequest;
import ai.datalens.dto.response.BlogCategoryResponse;
import ai.datalens.dto.response.BlogPostResponse;
import ai.datalens.security.UserPrincipal;
import ai.datalens.service.BlogService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/blog")
@CrossOrigin(origins = {"http://localhost:3000"})
public class BlogController {
    
    @Autowired
    private BlogService blogService;
    
    // Public endpoints (no authentication required)
    
    @GetMapping("/posts")
    public ResponseEntity<Page<BlogPostResponse>> getPublishedPosts(
            @PageableDefault(size = 10) Pageable pageable) {
        Page<BlogPostResponse> posts = blogService.getPublishedPosts(pageable);
        return ResponseEntity.ok(posts);
    }
    
    @GetMapping("/posts/{slug}")
    public ResponseEntity<BlogPostResponse> getPostBySlug(@PathVariable String slug) {
        return blogService.getPublishedPostBySlug(slug)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/posts/search")
    public ResponseEntity<Page<BlogPostResponse>> searchPosts(
            @RequestParam String query,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<BlogPostResponse> posts = blogService.searchPublishedPosts(query, pageable);
        return ResponseEntity.ok(posts);
    }
    
    @GetMapping("/posts/category/{categorySlug}")
    public ResponseEntity<Page<BlogPostResponse>> getPostsByCategory(
            @PathVariable String categorySlug,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<BlogPostResponse> posts = blogService.getPublishedPostsByCategory(categorySlug, pageable);
        return ResponseEntity.ok(posts);
    }
    
    @GetMapping("/posts/tag/{tag}")
    public ResponseEntity<Page<BlogPostResponse>> getPostsByTag(
            @PathVariable String tag,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<BlogPostResponse> posts = blogService.getPublishedPostsByTag(tag, pageable);
        return ResponseEntity.ok(posts);
    }
    
    @GetMapping("/categories")
    public ResponseEntity<List<BlogCategoryResponse>> getCategories() {
        List<BlogCategoryResponse> categories = blogService.getCategoriesWithPublishedPosts();
        return ResponseEntity.ok(categories);
    }
    
    // Authenticated user endpoints
    
    @PostMapping("/posts")
    @PreAuthorize("hasAuthority('blog:write')")
    public ResponseEntity<BlogPostResponse> createPost(
            @Valid @RequestBody BlogPostRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        BlogPostResponse post = blogService.createPost(request, userPrincipal.getId());
        return ResponseEntity.ok(post);
    }
    
    @PutMapping("/posts/{postId}")
    @PreAuthorize("hasAuthority('blog:write')")
    public ResponseEntity<BlogPostResponse> updatePost(
            @PathVariable UUID postId,
            @Valid @RequestBody BlogPostRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        BlogPostResponse post = blogService.updatePost(postId, request, userPrincipal.getId());
        return ResponseEntity.ok(post);
    }
    
    @DeleteMapping("/posts/{postId}")
    @PreAuthorize("hasAuthority('blog:delete')")
    public ResponseEntity<Void> deletePost(
            @PathVariable UUID postId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        blogService.deletePost(postId, userPrincipal.getId());
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/my-posts")
    @PreAuthorize("hasAuthority('blog:write')")
    public ResponseEntity<Page<BlogPostResponse>> getUserPosts(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<BlogPostResponse> posts = blogService.getUserPosts(userPrincipal.getId(), pageable);
        return ResponseEntity.ok(posts);
    }
    
    @PostMapping("/posts/{postId}/submit")
    @PreAuthorize("hasAuthority('blog:write')")
    public ResponseEntity<BlogPostResponse> submitForApproval(
            @PathVariable UUID postId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        BlogPostResponse post = blogService.submitForApproval(postId, userPrincipal.getId());
        return ResponseEntity.ok(post);
    }
    
    // Admin endpoints
    
    @GetMapping("/admin/pending-posts")
    @PreAuthorize("hasAuthority('blog:approve')")
    public ResponseEntity<Page<BlogPostResponse>> getPendingPosts(
            @PageableDefault(size = 10) Pageable pageable) {
        Page<BlogPostResponse> posts = blogService.getPendingPosts(pageable);
        return ResponseEntity.ok(posts);
    }
    
    @PostMapping("/admin/posts/{postId}/approve")
    @PreAuthorize("hasAuthority('blog:approve')")
    public ResponseEntity<BlogPostResponse> approvePost(
            @PathVariable UUID postId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        BlogPostResponse post = blogService.approvePost(postId, userPrincipal.getId());
        return ResponseEntity.ok(post);
    }
    
    @PostMapping("/admin/posts/{postId}/reject")
    @PreAuthorize("hasAuthority('blog:approve')")
    public ResponseEntity<BlogPostResponse> rejectPost(
            @PathVariable UUID postId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        BlogPostResponse post = blogService.rejectPost(postId, userPrincipal.getId());
        return ResponseEntity.ok(post);
    }
    
    @GetMapping("/admin/categories")
    @PreAuthorize("hasAuthority('blog:manage_categories')")
    public ResponseEntity<List<BlogCategoryResponse>> getAllCategories() {
        List<BlogCategoryResponse> categories = blogService.getAllCategories();
        return ResponseEntity.ok(categories);
    }
}