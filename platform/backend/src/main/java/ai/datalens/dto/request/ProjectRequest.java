package ai.datalens.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ProjectRequest {

    @NotBlank(message = "Project name is required")
    @Size(max = 255, message = "Project name must not exceed 255 characters")
    private String name;

    @Size(max = 1000, message = "Project description must not exceed 1000 characters")
    private String description;

    @NotNull(message = "Project active status is required")
    private Boolean isActive;

    // Constructors
    public ProjectRequest() {}

    public ProjectRequest(String name, String description, Boolean isActive) {
        this.name = name;
        this.description = description;
        this.isActive = isActive;
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

    @Override
    public String toString() {
        return "ProjectRequest{" +
            "name='" + name + '\'' +
            ", description='" + description + '\'' +
            ", isActive=" + isActive +
            '}';
    }
}