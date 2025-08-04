package ai.datalens.repository;

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
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Find user by email
     */
    Optional<User> findByEmail(String email);

    /**
     * Find user by email (case-insensitive)
     */
    Optional<User> findByEmailIgnoreCase(String email);

    /**
     * Check if user exists by email
     */
    boolean existsByEmail(String email);

    /**
     * Check if user exists by email (case-insensitive)
     */
    boolean existsByEmailIgnoreCase(String email);

    /**
     * Find users by status
     */
    List<User> findByStatus(String status);

    /**
     * Find users by status with pagination
     */
    Page<User> findByStatus(String status, Pageable pageable);

    /**
     * Find verified users
     */
    List<User> findByIsVerifiedTrue();

    /**
     * Find users by role name
     */
    @Query("SELECT DISTINCT u FROM User u JOIN u.roles r WHERE r.name = :roleName")
    List<User> findByRoleName(@Param("roleName") String roleName);

    /**
     * Find users with specific permission
     */
    @Query("SELECT DISTINCT u FROM User u " +
           "JOIN u.roles r " +
           "JOIN r.permissions p " +
           "WHERE p.name = :permissionName")
    List<User> findByPermissionName(@Param("permissionName") String permissionName);

    /**
     * Count users by status
     */
    long countByStatus(String status);

    /**
     * Count verified users
     */
    long countByIsVerifiedTrue();

    /**
     * Find users with roles eagerly loaded
     */
    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.roles WHERE u.id = :id")
    Optional<User> findByIdWithRoles(@Param("id") UUID id);

    /**
     * Find users with roles and permissions eagerly loaded
     */
    @Query("SELECT DISTINCT u FROM User u " +
           "LEFT JOIN FETCH u.roles r " +
           "LEFT JOIN FETCH r.permissions " +
           "WHERE u.id = :id")
    Optional<User> findByIdWithRolesAndPermissions(@Param("id") UUID id);

    /**
     * Find user by email with roles eagerly loaded
     */
    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.roles r LEFT JOIN FETCH r.permissions WHERE u.email = :email")
    Optional<User> findByEmailWithRolesAndPermissions(@Param("email") String email);

    /**
     * Search users by email containing (case-insensitive)
     */
    Page<User> findByEmailContainingIgnoreCase(String email, Pageable pageable);
}