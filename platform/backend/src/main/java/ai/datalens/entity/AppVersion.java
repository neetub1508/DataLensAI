package ai.datalens.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
@Table(name = "app_version", indexes = {
    @Index(name = "idx_app_version_version", columnList = "version", unique = true)
})
public class AppVersion extends BaseEntity {

    @NotBlank
    @Size(max = 20)
    @Column(name = "version", nullable = false, unique = true)
    private String version;

    @Size(max = 255)
    @Column(name = "description")
    private String description;

    @Column(name = "applied_at", nullable = false)
    private LocalDateTime appliedAt;

    // Constructors
    public AppVersion() {
        this.appliedAt = LocalDateTime.now();
    }

    public AppVersion(String version, String description) {
        this();
        this.version = version;
        this.description = description;
    }

    // Getters and Setters
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getAppliedAt() {
        return appliedAt;
    }

    public void setAppliedAt(LocalDateTime appliedAt) {
        this.appliedAt = appliedAt;
    }

    @Override
    public String toString() {
        return "AppVersion{" +
            "id=" + getId() +
            ", version='" + version + '\'' +
            ", description='" + description + '\'' +
            ", appliedAt=" + appliedAt +
            '}';
    }
}