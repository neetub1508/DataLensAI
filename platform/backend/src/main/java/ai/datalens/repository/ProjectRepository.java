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

    List<Project> findByOwnerAndStatus(User owner, String status);

    List<Project> findByOwnerOrderByLastAccessedAtDesc(User owner);

    @Query("SELECT p FROM Project p WHERE p.owner = :user OR :user MEMBER OF p.members")
    List<Project> findByUserAccess(@Param("user") User user);

    @Query("SELECT p FROM Project p WHERE (p.owner = :user OR :user MEMBER OF p.members) AND p.status = :status")
    List<Project> findByUserAccessAndStatus(@Param("user") User user, @Param("status") String status);

    @Query("SELECT p FROM Project p WHERE (p.owner = :user OR :user MEMBER OF p.members) ORDER BY p.lastAccessedAt DESC")
    List<Project> findByUserAccessOrderByLastAccessedAtDesc(@Param("user") User user);

    @Query("SELECT p FROM Project p WHERE (p.owner = :user OR :user MEMBER OF p.members) AND p.status = 'ACTIVE' ORDER BY p.lastAccessedAt DESC")
    List<Project> findActiveProjectsByUserOrderByLastAccessedAtDesc(@Param("user") User user);

    Optional<Project> findByIdAndOwner(UUID id, User owner);

    @Query("SELECT p FROM Project p WHERE p.id = :id AND (p.owner = :user OR :user MEMBER OF p.members)")
    Optional<Project> findByIdAndUserAccess(@Param("id") UUID id, @Param("user") User user);

    boolean existsByNameAndOwner(String name, User owner);

    Page<Project> findByOwner(User owner, Pageable pageable);

    @Query("SELECT COUNT(p) FROM Project p WHERE p.owner = :user AND p.status = 'ACTIVE'")
    long countActiveProjectsByOwner(@Param("user") User user);

    @Query("SELECT p FROM Project p WHERE p.name LIKE %:searchTerm% AND (p.owner = :user OR :user MEMBER OF p.members)")
    List<Project> searchProjectsByNameAndUserAccess(@Param("searchTerm") String searchTerm, @Param("user") User user);
}