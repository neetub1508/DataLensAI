package ai.datalens.repository;

import ai.datalens.entity.Project;
import ai.datalens.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProjectRepository extends JpaRepository<Project, UUID> {

    /**
     * Find all projects for a specific user
     */
    List<Project> findByUser(User user);

    /**
     * Find all projects for a specific user with pagination
     */
    Page<Project> findByUser(User user, Pageable pageable);

    /**
     * Find all projects for a specific user ID
     */
    List<Project> findByUserId(UUID userId);

    /**
     * Find all projects for a specific user ID with pagination
     */
    Page<Project> findByUserId(UUID userId, Pageable pageable);

    /**
     * Find projects by name containing (case-insensitive) for a specific user
     */
    List<Project> findByUserAndNameContainingIgnoreCase(User user, String name);

    /**
     * Find projects by name containing (case-insensitive) for a specific user with pagination
     */
    Page<Project> findByUserAndNameContainingIgnoreCase(User user, String name, Pageable pageable);

    /**
     * Find project by user and project ID
     */
    Optional<Project> findByUserAndId(User user, UUID projectId);

    /**
     * Find project by user ID and project ID
     */
    Optional<Project> findByUserIdAndId(UUID userId, UUID projectId);

    /**
     * Check if a project exists for a user with the given name
     */
    boolean existsByUserAndNameIgnoreCase(User user, String name);

    /**
     * Count projects for a specific user
     */
    long countByUser(User user);

    /**
     * Count projects for a specific user ID
     */
    long countByUserId(UUID userId);

    /**
     * Find projects ordered by creation date (most recent first) for a specific user
     */
    @Query("SELECT p FROM Project p WHERE p.user = :user ORDER BY p.createdAt DESC")
    List<Project> findByUserOrderByCreatedAtDesc(@Param("user") User user);

    /**
     * Find projects ordered by creation date (most recent first) for a specific user with pagination
     */
    @Query("SELECT p FROM Project p WHERE p.user = :user ORDER BY p.createdAt DESC")
    Page<Project> findByUserOrderByCreatedAtDesc(@Param("user") User user, Pageable pageable);

    /**
     * Find recent projects for a user (limit by pageable)
     */
    @Query("SELECT p FROM Project p WHERE p.user.id = :userId ORDER BY p.createdAt DESC")
    Page<Project> findRecentProjectsByUserId(@Param("userId") UUID userId, Pageable pageable);

    /**
     * Find active projects for a user
     */
    List<Project> findByUserAndIsActiveTrue(User user);

    /**
     * Find active projects for a user with pagination
     */
    Page<Project> findByUserAndIsActiveTrue(User user, Pageable pageable);

    /**
     * Find projects by active status for a user
     */
    List<Project> findByUserAndIsActive(User user, Boolean isActive);

    /**
     * Find projects by active status for a user with pagination
     */
    Page<Project> findByUserAndIsActive(User user, Boolean isActive, Pageable pageable);

    /**
     * Count active projects for a user
     */
    long countByUserAndIsActiveTrue(User user);

    /**
     * Count projects by active status for a user
     */
    long countByUserAndIsActive(User user, Boolean isActive);

    /**
     * Find projects updated by a specific user
     */
    List<Project> findByUpdateBy(UUID updateBy);

    /**
     * Find projects updated after a specific date for a user
     */
    @Query("SELECT p FROM Project p WHERE p.user = :user AND p.updateDate >= :updateDate ORDER BY p.updateDate DESC")
    List<Project> findByUserAndUpdateDateAfter(@Param("user") User user, @Param("updateDate") java.time.LocalDateTime updateDate);

    /**
     * Find active projects ordered by update date (most recent first) for a specific user
     */
    @Query("SELECT p FROM Project p WHERE p.user = :user AND p.isActive = true ORDER BY p.updateDate DESC")
    List<Project> findActiveProjectsByUserOrderByUpdateDateDesc(@Param("user") User user);

    /**
     * Find active projects ordered by update date (most recent first) for a specific user with pagination
     */
    @Query("SELECT p FROM Project p WHERE p.user = :user AND p.isActive = true ORDER BY p.updateDate DESC")
    Page<Project> findActiveProjectsByUserOrderByUpdateDateDesc(@Param("user") User user, Pageable pageable);

    /**
     * Find all projects with user eagerly loaded (for admin)
     */
    @Query("SELECT p FROM Project p LEFT JOIN FETCH p.user ORDER BY p.updateDate DESC")
    List<Project> findAllWithUser();
}