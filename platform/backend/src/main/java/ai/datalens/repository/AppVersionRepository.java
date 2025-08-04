package ai.datalens.repository;

import ai.datalens.entity.AppVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AppVersionRepository extends JpaRepository<AppVersion, UUID> {

    /**
     * Find app version by version string
     */
    Optional<AppVersion> findByVersion(String version);

    /**
     * Check if version exists
     */
    boolean existsByVersion(String version);

    /**
     * Find the latest app version
     */
    @Query("SELECT av FROM AppVersion av ORDER BY av.appliedAt DESC LIMIT 1")
    Optional<AppVersion> findLatestVersion();
}