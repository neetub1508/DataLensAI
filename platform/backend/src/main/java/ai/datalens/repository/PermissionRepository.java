package ai.datalens.repository;

import ai.datalens.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, UUID> {

    /**
     * Find permission by name (case-insensitive)
     */
    Optional<Permission> findByNameIgnoreCase(String name);

    /**
     * Check if permission exists by name
     */
    boolean existsByNameIgnoreCase(String name);

    /**
     * Find permissions by name containing (case-insensitive)
     */
    List<Permission> findByNameContainingIgnoreCase(String name);

    /**
     * Find permissions assigned to roles
     */
    @Query("SELECT DISTINCT p FROM Permission p WHERE SIZE(p.roles) > 0")
    List<Permission> findPermissionsWithRoles();

    /**
     * Find permissions by role name
     */
    @Query("SELECT DISTINCT p FROM Permission p JOIN p.roles r WHERE r.name = :roleName")
    List<Permission> findByRoleName(@Param("roleName") String roleName);

    /**
     * Count permissions assigned to roles
     */
    @Query("SELECT COUNT(DISTINCT p) FROM Permission p WHERE SIZE(p.roles) > 0")
    long countPermissionsWithRoles();
}