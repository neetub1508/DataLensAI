package ai.datalens.repository;

import ai.datalens.entity.BlogPost;
import ai.datalens.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BlogPostRepository extends JpaRepository<BlogPost, UUID> {
    
    // Find published posts for public viewing
    Page<BlogPost> findByStatusOrderByPublishedAtDesc(BlogPost.BlogPostStatus status, Pageable pageable);
    
    // Find posts by author
    Page<BlogPost> findByAuthorOrderByCreatedAtDesc(User author, Pageable pageable);
    
    // Find posts by status for admin management
    Page<BlogPost> findByStatusOrderByCreatedAtDesc(BlogPost.BlogPostStatus status, Pageable pageable);
    
    // Find by slug for public viewing (only published)
    Optional<BlogPost> findBySlugAndStatus(String slug, BlogPost.BlogPostStatus status);
    
    // Find by slug for admin/author editing
    Optional<BlogPost> findBySlug(String slug);
    
    // Check if slug exists
    boolean existsBySlug(String slug);
    
    // Search posts by title or content (published only)
    @Query("SELECT p FROM BlogPost p WHERE p.status = :status AND (LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(p.content) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<BlogPost> searchPublishedPosts(@Param("query") String query, @Param("status") BlogPost.BlogPostStatus status, Pageable pageable);
    
    // Get posts by category (published only)
    @Query("SELECT p FROM BlogPost p JOIN p.categories c WHERE p.status = :status AND c.slug = :categorySlug ORDER BY p.publishedAt DESC")
    Page<BlogPost> findByCategorySlugAndStatus(@Param("categorySlug") String categorySlug, @Param("status") BlogPost.BlogPostStatus status, Pageable pageable);
    
    // Get posts by tag (published only)
    @Query("SELECT p FROM BlogPost p WHERE p.status = :status AND :tag MEMBER OF p.tags ORDER BY p.publishedAt DESC")
    Page<BlogPost> findByTagAndStatus(@Param("tag") String tag, @Param("status") BlogPost.BlogPostStatus status, Pageable pageable);
    
    // Count posts by status
    long countByStatus(BlogPost.BlogPostStatus status);
    
    // Count posts by author
    long countByAuthor(User author);
}