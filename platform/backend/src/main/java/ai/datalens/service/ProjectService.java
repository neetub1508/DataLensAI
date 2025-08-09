package ai.datalens.service;

import ai.datalens.constants.ProjectStatus;

import ai.datalens.dto.request.ProjectRequest;
import ai.datalens.dto.response.ProjectResponse;
import ai.datalens.entity.Project;
import ai.datalens.entity.User;
import ai.datalens.repository.ProjectRepository;
import ai.datalens.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    @Autowired
    public ProjectService(ProjectRepository projectRepository, UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    public List<ProjectResponse> getUserProjects(UUID userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<Project> projects = projectRepository.findActiveProjectsByUserOrderByLastAccessedAtDesc(user);
        
        return projects.stream()
            .map(project -> ProjectResponse.fromProject(project, project.isOwner(user)))
            .collect(Collectors.toList());
    }

    public ProjectResponse createProject(UUID userId, ProjectRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        if (projectRepository.existsByNameAndOwner(request.getName(), user)) {
            throw new RuntimeException("Project with this name already exists");
        }

        Project project = new Project(request.getName(), request.getDescription(), user);
        project.setSettings(request.getSettings());
        project.setLastAccessedAt(LocalDateTime.now());

        Project savedProject = projectRepository.save(project);
        return ProjectResponse.fromProject(savedProject, true);
    }

    public ProjectResponse getProject(UUID userId, UUID projectId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        Project project = projectRepository.findByIdAndUserAccess(projectId, user)
            .orElseThrow(() -> new RuntimeException("Project not found or access denied"));

        project.updateLastAccessed();
        projectRepository.save(project);

        return ProjectResponse.fromProject(project, project.isOwner(user));
    }

    public ProjectResponse updateProject(UUID userId, UUID projectId, ProjectRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        Project project = projectRepository.findByIdAndOwner(projectId, user)
            .orElseThrow(() -> new RuntimeException("Project not found or you don't have permission to edit"));

        if (!project.getName().equals(request.getName()) && 
            projectRepository.existsByNameAndOwner(request.getName(), user)) {
            throw new RuntimeException("Project with this name already exists");
        }

        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setSettings(request.getSettings());
        project.updateLastAccessed();

        Project updatedProject = projectRepository.save(project);
        return ProjectResponse.fromProject(updatedProject, true);
    }

    public void deleteProject(UUID userId, UUID projectId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        Project project = projectRepository.findByIdAndOwner(projectId, user)
            .orElseThrow(() -> new RuntimeException("Project not found or you don't have permission to delete"));

        projectRepository.delete(project);
    }

    public void archiveProject(UUID userId, UUID projectId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        Project project = projectRepository.findByIdAndOwner(projectId, user)
            .orElseThrow(() -> new RuntimeException("Project not found or you don't have permission to archive"));

        project.setStatus(ProjectStatus.ARCHIVED);
        projectRepository.save(project);
    }

    public void restoreProject(UUID userId, UUID projectId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        Project project = projectRepository.findByIdAndOwner(projectId, user)
            .orElseThrow(() -> new RuntimeException("Project not found or you don't have permission to restore"));

        project.setStatus(ProjectStatus.ACTIVE);
        projectRepository.save(project);
    }

    public void addMemberToProject(UUID userId, UUID projectId, UUID memberId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        User member = userRepository.findById(memberId)
            .orElseThrow(() -> new RuntimeException("Member not found"));

        Project project = projectRepository.findByIdAndOwner(projectId, user)
            .orElseThrow(() -> new RuntimeException("Project not found or you don't have permission to add members"));

        project.addMember(member);
        projectRepository.save(project);
    }

    public void removeMemberFromProject(UUID userId, UUID projectId, UUID memberId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        User member = userRepository.findById(memberId)
            .orElseThrow(() -> new RuntimeException("Member not found"));

        Project project = projectRepository.findByIdAndOwner(projectId, user)
            .orElseThrow(() -> new RuntimeException("Project not found or you don't have permission to remove members"));

        project.removeMember(member);
        projectRepository.save(project);
    }

    @Transactional(readOnly = true)
    public boolean hasProjectAccess(UUID userId, UUID projectId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        return projectRepository.findByIdAndUserAccess(projectId, user).isPresent();
    }

    @Transactional(readOnly = true)
    public long getUserActiveProjectCount(UUID userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        return projectRepository.countActiveProjectsByOwner(user);
    }

    @Transactional(readOnly = true)
    public List<ProjectResponse> searchUserProjects(UUID userId, String searchTerm) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        List<Project> projects = projectRepository.searchProjectsByNameAndUserAccess(searchTerm, user);

        return projects.stream()
            .map(project -> ProjectResponse.fromProject(project, project.isOwner(user)))
            .collect(Collectors.toList());
    }
}