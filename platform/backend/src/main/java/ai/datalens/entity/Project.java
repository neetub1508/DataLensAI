package ai.datalens.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "projects", indexes = {
    @Index(name = "idx_project_name", columnList = "name"),
    @Index(name = "idx_project_user", columnList = "user_id"),
    @Index(name = "idx_project_active", columnList = "is_active"),
    @Index(name = "idx_project_update_date", columnList = "update_date")
})
public class Project extends BaseEntity {

    @NotBlank
    @Size(max = 255)
    @Column(name = "name", nullable = false)
    private String name;

    @Size(max = 1000)
    @Column(name = "description")
    private String description;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "update_date", nullable = false)
    private LocalDateTime updateDate;

    @Column(name = "update_by", nullable = false)
    private UUID updateBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Constructors
    public Project() {}

    public Project(String name, String description, User user, UUID updateBy) {
        this.name = name;
        this.description = description;
        this.user = user;
        this.isActive = true;
        this.updateDate = LocalDateTime.now();
        this.updateBy = updateBy;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public LocalDateTime getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(LocalDateTime updateDate) {
        this.updateDate = updateDate;
    }

    public UUID getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(UUID updateBy) {
        this.updateBy = updateBy;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Project{" +
            "id=" + getId() +
            ", name='" + name + '\'' +
            ", description='" + description + '\'' +
            ", isActive=" + isActive +
            ", updateDate=" + updateDate +
            ", updateBy=" + updateBy +
            ", user=" + (user != null ? user.getEmail() : "null") +
            '}';
    }
}