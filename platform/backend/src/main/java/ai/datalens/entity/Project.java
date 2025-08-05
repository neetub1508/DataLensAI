package ai.datalens.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "projects", indexes = {
    @Index(name = "idx_project_owner", columnList = "owner_id"),
    @Index(name = "idx_project_name", columnList = "name"),
    @Index(name = "idx_project_status", columnList = "status")
})
public class Project extends BaseEntity {

    @NotBlank
    @Size(max = 255)
    @Column(name = "name", nullable = false)
    private String name;

    @Size(max = 1000)
    @Column(name = "description")
    private String description;

    @NotBlank
    @Size(max = 20)
    @Column(name = "status", nullable = false)
    private String status = "ACTIVE";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false, 
               foreignKey = @ForeignKey(name = "fk_project_owner"))
    private User owner;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "project_members",
        joinColumns = @JoinColumn(name = "project_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id"),
        indexes = {
            @Index(name = "idx_project_members_project", columnList = "project_id"),
            @Index(name = "idx_project_members_user", columnList = "user_id")
        }
    )
    private Set<User> members = new HashSet<>();

    @Column(name = "settings")
    private String settings;

    @Column(name = "last_accessed_at")
    private LocalDateTime lastAccessedAt;

    // Constructors
    public Project() {}

    public Project(String name, String description, User owner) {
        this.name = name;
        this.description = description;
        this.owner = owner;
        this.lastAccessedAt = LocalDateTime.now();
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public Set<User> getMembers() {
        return members;
    }

    public void setMembers(Set<User> members) {
        this.members = members;
    }

    public String getSettings() {
        return settings;
    }

    public void setSettings(String settings) {
        this.settings = settings;
    }

    public LocalDateTime getLastAccessedAt() {
        return lastAccessedAt;
    }

    public void setLastAccessedAt(LocalDateTime lastAccessedAt) {
        this.lastAccessedAt = lastAccessedAt;
    }

    // Utility methods
    public void addMember(User user) {
        this.members.add(user);
    }

    public void removeMember(User user) {
        this.members.remove(user);
    }

    public boolean isMember(User user) {
        return this.owner.equals(user) || this.members.contains(user);
    }

    public boolean isOwner(User user) {
        return this.owner.equals(user);
    }

    public void updateLastAccessed() {
        this.lastAccessedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Project{" +
            "id=" + getId() +
            ", name='" + name + '\'' +
            ", description='" + description + '\'' +
            ", status='" + status + '\'' +
            ", ownerId=" + (owner != null ? owner.getId() : null) +
            '}';
    }
}