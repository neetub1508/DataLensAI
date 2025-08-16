package ai.datalens.service;

import ai.datalens.dto.request.ProjectRequest;
import ai.datalens.dto.response.ProjectResponse;
import ai.datalens.entity.Project;
import ai.datalens.entity.User;
import ai.datalens.repository.ProjectRepository;
import ai.datalens.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Create a new project for the user
     */
    public ProjectResponse createProject(UUID userId, ProjectRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if project with same name already exists for this user
        if (projectRepository.existsByUserAndNameIgnoreCase(user, request.getName())) {
            throw new RuntimeException("Project with name '" + request.getName() + "' already exists");
        }

        Project project = new Project();
        project.setName(request.getName().trim());
        project.setDescription(request.getDescription() != null ? request.getDescription().trim() : null);
        project.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        project.setUpdateDate(LocalDateTime.now());
        project.setUpdateBy(userId);
        project.setUser(user);

        Project savedProject = projectRepository.save(project);
        return convertToProjectResponse(savedProject);
    }

    /**
     * Get all projects for a user
     */
    public List<ProjectResponse> getUserProjects(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Project> projects = projectRepository.findByUserOrderByCreatedAtDesc(user);
        return projects.stream()
                .map(this::convertToProjectResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get only active projects for a user
     */
    public List<ProjectResponse> getActiveUserProjects(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Project> activeProjects = projectRepository.findActiveProjectsByUserOrderByUpdateDateDesc(user);
        return activeProjects.stream()
                .map(this::convertToProjectResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get recent projects for a user with pagination
     */
    public List<ProjectResponse> getRecentProjects(UUID userId, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        Page<Project> projectPage = projectRepository.findRecentProjectsByUserId(userId, pageable);
        return projectPage.getContent().stream()
                .map(this::convertToProjectResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get a specific project by ID for a user
     */
    public ProjectResponse getProject(UUID userId, UUID projectId) {
        Project project = projectRepository.findByUserIdAndId(userId, projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        return convertToProjectResponse(project);
    }

    /**
     * Update an existing project
     */
    public ProjectResponse updateProject(UUID userId, UUID projectId, ProjectRequest request) {
        Project project = projectRepository.findByUserIdAndId(userId, projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        // Check if project with same name already exists for this user (excluding current project)
        if (!project.getName().equalsIgnoreCase(request.getName()) && 
            projectRepository.existsByUserAndNameIgnoreCase(project.getUser(), request.getName())) {
            throw new RuntimeException("Project with name '" + request.getName() + "' already exists");
        }

        project.setName(request.getName().trim());
        project.setDescription(request.getDescription() != null ? request.getDescription().trim() : null);
        project.setIsActive(request.getIsActive() != null ? request.getIsActive() : project.getIsActive());
        project.setUpdateDate(LocalDateTime.now());
        project.setUpdateBy(userId);

        Project savedProject = projectRepository.save(project);
        return convertToProjectResponse(savedProject);
    }

    /**
     * Delete a project
     */
    public void deleteProject(UUID userId, UUID projectId) {
        Project project = projectRepository.findByUserIdAndId(userId, projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        projectRepository.delete(project);
    }

    /**
     * Get project statistics for a user
     */
    public ProjectStatsResponse getProjectStats(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        long totalProjects = projectRepository.countByUserId(userId);
        long activeProjects = projectRepository.countByUserAndIsActiveTrue(user);
        long inactiveProjects = projectRepository.countByUserAndIsActive(user, false);
        
        ProjectStatsResponse stats = new ProjectStatsResponse();
        stats.setTotalProjects(totalProjects);
        stats.setActiveProjects(activeProjects);
        stats.setInProgressProjects(activeProjects); // Consider active projects as in progress
        stats.setCompletedProjects(inactiveProjects); // Consider inactive projects as completed
        
        return stats;
    }

    /**
     * Search projects by name for a user
     */
    public List<ProjectResponse> searchProjects(UUID userId, String searchTerm) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Project> projects = projectRepository.findByUserAndNameContainingIgnoreCase(user, searchTerm);
        return projects.stream()
                .map(this::convertToProjectResponse)
                .collect(Collectors.toList());
    }

    /**
     * Convert Project entity to ProjectResponse DTO
     */
    private ProjectResponse convertToProjectResponse(Project project) {
        ProjectResponse response = new ProjectResponse();
        response.setId(project.getId());
        response.setName(project.getName());
        response.setDescription(project.getDescription());
        response.setIsActive(project.getIsActive());
        response.setUpdateDate(project.getUpdateDate());
        response.setUpdateBy(project.getUpdateBy());
        response.setUserId(project.getUser().getId());
        response.setUserEmail(project.getUser().getEmail());
        response.setCreatedAt(project.getCreatedAt());
        response.setUpdatedAt(project.getUpdatedAt());
        
        return response;
    }

    /**
     * Project statistics response DTO
     */
    public static class ProjectStatsResponse {
        private Long totalProjects;
        private Long activeProjects;
        private Long inProgressProjects;
        private Long completedProjects;

        // Constructors
        public ProjectStatsResponse() {}

        // Getters and Setters
        public Long getTotalProjects() {
            return totalProjects;
        }

        public void setTotalProjects(Long totalProjects) {
            this.totalProjects = totalProjects;
        }

        public Long getActiveProjects() {
            return activeProjects;
        }

        public void setActiveProjects(Long activeProjects) {
            this.activeProjects = activeProjects;
        }

        public Long getInProgressProjects() {
            return inProgressProjects;
        }

        public void setInProgressProjects(Long inProgressProjects) {
            this.inProgressProjects = inProgressProjects;
        }

        public Long getCompletedProjects() {
            return completedProjects;
        }

        public void setCompletedProjects(Long completedProjects) {
            this.completedProjects = completedProjects;
        }
    }
}