package ai.datalens.repository;

import ai.datalens.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {

    /**
     * Find role by name
     */
    Optional<Role> findByName(String name);

    /**
     * Find role by name (case-insensitive)
     */
    Optional<Role> findByNameIgnoreCase(String name);

    /**
     * Check if role exists by name
     */
    boolean existsByNameIgnoreCase(String name);

    /**
     * Find roles by name containing (case-insensitive)
     */
    List<Role> findByNameContainingIgnoreCase(String name);

    /**
     * Find role with permissions eagerly loaded
     */
    @Query("SELECT DISTINCT r FROM Role r LEFT JOIN FETCH r.permissions WHERE r.id = :id")
    Optional<Role> findByIdWithPermissions(@Param("id") UUID id);

    /**
     * Find role by name with permissions eagerly loaded
     */
    @Query("SELECT DISTINCT r FROM Role r LEFT JOIN FETCH r.permissions WHERE r.name = :name")
    Optional<Role> findByNameWithPermissions(@Param("name") String name);

    /**
     * Find roles with specific permission
     */
    @Query("SELECT DISTINCT r FROM Role r JOIN r.permissions p WHERE p.name = :permissionName")
    List<Role> findByPermissionName(@Param("permissionName") String permissionName);

    /**
     * Find roles assigned to users
     */
    @Query("SELECT DISTINCT r FROM Role r WHERE SIZE(r.users) > 0")
    List<Role> findRolesWithUsers();

    /**
     * Count roles with specific permission
     */
    @Query("SELECT COUNT(DISTINCT r) FROM Role r JOIN r.permissions p WHERE p.name = :permissionName")
    long countByPermissionName(@Param("permissionName") String permissionName);
}