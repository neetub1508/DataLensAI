package ai.datalens.repository;

import ai.datalens.entity.BlogCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BlogCategoryRepository extends JpaRepository<BlogCategory, UUID> {
    
    // Find by slug
    Optional<BlogCategory> findBySlug(String slug);
    
    // Find by name
    Optional<BlogCategory> findByName(String name);
    
    // Check if slug exists
    boolean existsBySlug(String slug);
    
    // Check if name exists
    boolean existsByName(String name);
    
    // Get all categories ordered by name
    List<BlogCategory> findAllByOrderByNameAsc();
    
    // Get categories that have published posts
    @Query("SELECT DISTINCT c FROM BlogCategory c JOIN c.posts p WHERE p.status = 'PUBLISHED'")
    List<BlogCategory> findCategoriesWithPublishedPosts();
}