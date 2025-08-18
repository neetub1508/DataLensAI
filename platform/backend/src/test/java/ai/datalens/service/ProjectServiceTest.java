package ai.datalens.service;

import ai.datalens.dto.request.ProjectRequest;
import ai.datalens.dto.response.ProjectResponse;
import ai.datalens.entity.Project;
import ai.datalens.entity.User;
import ai.datalens.repository.ProjectRepository;
import ai.datalens.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ProjectService projectService;

    private User testUser;
    private Project testProject;
    private ProjectRequest validProjectRequest;
    private UUID userId;
    private UUID projectId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        projectId = UUID.randomUUID();

        testUser = new User();
        testUser.setId(userId);
        testUser.setEmail("test@example.com");
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());

        testProject = new Project();
        testProject.setId(projectId);
        testProject.setName("Test Project");
        testProject.setDescription("Test Description");
        testProject.setIsActive(true);
        testProject.setUpdateDate(LocalDateTime.now());
        testProject.setUpdateBy(userId);
        testProject.setUser(testUser);
        testProject.setCreatedAt(LocalDateTime.now());
        testProject.setUpdatedAt(LocalDateTime.now());

        validProjectRequest = new ProjectRequest();
        validProjectRequest.setName("Test Project");
        validProjectRequest.setDescription("Test Description");
        validProjectRequest.setIsActive(true);
    }

    // POSITIVE TEST CASES

    @Test
    void createProject_Success() {
        // Given
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(projectRepository.existsByUserAndNameIgnoreCase(testUser, "Test Project")).thenReturn(false);
        when(projectRepository.save(any(Project.class))).thenReturn(testProject);

        // When
        ProjectResponse result = projectService.createProject(userId, validProjectRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Test Project");
        assertThat(result.getDescription()).isEqualTo("Test Description");
        assertThat(result.getIsActive()).isTrue();
        assertThat(result.getUserId()).isEqualTo(userId);

        verify(userRepository).findById(userId);
        verify(projectRepository).existsByUserAndNameIgnoreCase(testUser, "Test Project");
        verify(projectRepository).save(any(Project.class));
    }

    @Test
    void createProject_WithNullDescription_Success() {
        // Given
        validProjectRequest.setDescription(null);
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(projectRepository.existsByUserAndNameIgnoreCase(testUser, "Test Project")).thenReturn(false);
        when(projectRepository.save(any(Project.class))).thenReturn(testProject);

        // When
        ProjectResponse result = projectService.createProject(userId, validProjectRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Test Project");
        verify(projectRepository).save(any(Project.class));
    }

    @Test
    void createProject_WithTrimmingWhitespace_Success() {
        // Given
        validProjectRequest.setName("  Test Project  ");
        validProjectRequest.setDescription("  Test Description  ");
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        // The service checks with the original name (with spaces)
        when(projectRepository.existsByUserAndNameIgnoreCase(testUser, "  Test Project  ")).thenReturn(false);
        when(projectRepository.save(any(Project.class))).thenReturn(testProject);

        // When
        ProjectResponse result = projectService.createProject(userId, validProjectRequest);

        // Then
        assertThat(result).isNotNull();
        // Verify that the saved project has trimmed values
        verify(projectRepository).save(argThat(project -> 
            "Test Project".equals(project.getName()) && 
            "Test Description".equals(project.getDescription())
        ));
    }

    @Test
    void getUserProjects_Success() {
        // Given
        List<Project> projects = Arrays.asList(testProject);
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(projectRepository.findByUserOrderByCreatedAtDesc(testUser)).thenReturn(projects);

        // When
        List<ProjectResponse> result = projectService.getUserProjects(userId);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Test Project");
        assertThat(result.get(0).getUserId()).isEqualTo(userId);

        verify(userRepository).findById(userId);
        verify(projectRepository).findByUserOrderByCreatedAtDesc(testUser);
    }

    @Test
    void getActiveUserProjects_Success() {
        // Given
        List<Project> activeProjects = Arrays.asList(testProject);
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(projectRepository.findActiveProjectsByUserOrderByUpdateDateDesc(testUser)).thenReturn(activeProjects);

        // When
        List<ProjectResponse> result = projectService.getActiveUserProjects(userId);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getIsActive()).isTrue();

        verify(userRepository).findById(userId);
        verify(projectRepository).findActiveProjectsByUserOrderByUpdateDateDesc(testUser);
    }

    @Test
    void getRecentProjects_Success() {
        // Given
        int limit = 5;
        Page<Project> projectPage = new PageImpl<>(Arrays.asList(testProject));
        when(projectRepository.findRecentProjectsByUserId(eq(userId), any(PageRequest.class))).thenReturn(projectPage);

        // When
        List<ProjectResponse> result = projectService.getRecentProjects(userId, limit);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Test Project");

        verify(projectRepository).findRecentProjectsByUserId(eq(userId), any(PageRequest.class));
    }

    @Test
    void getProject_Success() {
        // Given
        when(projectRepository.findByUserIdAndId(userId, projectId)).thenReturn(Optional.of(testProject));

        // When
        ProjectResponse result = projectService.getProject(userId, projectId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(projectId);
        assertThat(result.getName()).isEqualTo("Test Project");

        verify(projectRepository).findByUserIdAndId(userId, projectId);
    }

    @Test
    void updateProject_Success() {
        // Given
        ProjectRequest updateRequest = new ProjectRequest();
        updateRequest.setName("Updated Project");
        updateRequest.setDescription("Updated Description");
        updateRequest.setIsActive(false);

        Project updatedProject = new Project();
        updatedProject.setId(projectId);
        updatedProject.setName("Updated Project");
        updatedProject.setDescription("Updated Description");
        updatedProject.setIsActive(false);
        updatedProject.setUser(testUser);
        updatedProject.setCreatedAt(LocalDateTime.now());
        updatedProject.setUpdatedAt(LocalDateTime.now());

        when(projectRepository.findByUserIdAndId(userId, projectId)).thenReturn(Optional.of(testProject));
        when(projectRepository.existsByUserAndNameIgnoreCase(testUser, "Updated Project")).thenReturn(false);
        when(projectRepository.save(any(Project.class))).thenReturn(updatedProject);

        // When
        ProjectResponse result = projectService.updateProject(userId, projectId, updateRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Updated Project");
        assertThat(result.getDescription()).isEqualTo("Updated Description");
        assertThat(result.getIsActive()).isFalse();

        verify(projectRepository).findByUserIdAndId(userId, projectId);
        verify(projectRepository).save(any(Project.class));
    }

    @Test
    void updateProject_SameName_Success() {
        // Given
        ProjectRequest updateRequest = new ProjectRequest();
        updateRequest.setName("Test Project"); // Same name as existing
        updateRequest.setDescription("Updated Description");
        updateRequest.setIsActive(true);

        when(projectRepository.findByUserIdAndId(userId, projectId)).thenReturn(Optional.of(testProject));
        when(projectRepository.save(any(Project.class))).thenReturn(testProject);

        // When
        ProjectResponse result = projectService.updateProject(userId, projectId, updateRequest);

        // Then
        assertThat(result).isNotNull();
        verify(projectRepository).findByUserIdAndId(userId, projectId);
        verify(projectRepository, never()).existsByUserAndNameIgnoreCase(any(), any());
        verify(projectRepository).save(any(Project.class));
    }

    @Test
    void deleteProject_Success() {
        // Given
        when(projectRepository.findByUserIdAndId(userId, projectId)).thenReturn(Optional.of(testProject));

        // When
        assertThatCode(() -> projectService.deleteProject(userId, projectId))
            .doesNotThrowAnyException();

        // Then
        verify(projectRepository).findByUserIdAndId(userId, projectId);
        verify(projectRepository).delete(testProject);
    }

    @Test
    void getProjectStats_Success() {
        // Given
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(projectRepository.countByUserId(userId)).thenReturn(10L);
        when(projectRepository.countByUserAndIsActiveTrue(testUser)).thenReturn(7L);
        when(projectRepository.countByUserAndIsActive(testUser, false)).thenReturn(3L);

        // When
        ProjectService.ProjectStatsResponse result = projectService.getProjectStats(userId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTotalProjects()).isEqualTo(10L);
        assertThat(result.getActiveProjects()).isEqualTo(7L);
        assertThat(result.getInProgressProjects()).isEqualTo(7L);
        assertThat(result.getCompletedProjects()).isEqualTo(3L);

        verify(userRepository).findById(userId);
        verify(projectRepository).countByUserId(userId);
        verify(projectRepository).countByUserAndIsActiveTrue(testUser);
        verify(projectRepository).countByUserAndIsActive(testUser, false);
    }

    @Test
    void searchProjects_Success() {
        // Given
        String searchTerm = "Test";
        List<Project> projects = Arrays.asList(testProject);
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(projectRepository.findByUserAndNameContainingIgnoreCase(testUser, searchTerm)).thenReturn(projects);

        // When
        List<ProjectResponse> result = projectService.searchProjects(userId, searchTerm);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).contains("Test");

        verify(userRepository).findById(userId);
        verify(projectRepository).findByUserAndNameContainingIgnoreCase(testUser, searchTerm);
    }

    @Test
    void getAllProjects_Success() {
        // Given
        List<Project> projects = Arrays.asList(testProject);
        when(projectRepository.findAllWithUser()).thenReturn(projects);

        // When
        List<ProjectResponse> result = projectService.getAllProjects();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Test Project");

        verify(projectRepository).findAllWithUser();
    }

    @Test
    void updateProjectStatus_Success() {
        // Given
        UUID adminUserId = UUID.randomUUID();
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(testProject));
        when(projectRepository.save(any(Project.class))).thenReturn(testProject);

        // When
        assertThatCode(() -> projectService.updateProjectStatus(projectId.toString(), false, adminUserId))
            .doesNotThrowAnyException();

        // Then
        verify(projectRepository).findById(projectId);
        verify(projectRepository).save(argThat(project -> 
            !project.getIsActive() && adminUserId.equals(project.getUpdateBy())
        ));
    }

    // NEGATIVE TEST CASES

    @Test
    void createProject_UserNotFound_ThrowsException() {
        // Given
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> projectService.createProject(userId, validProjectRequest))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("User not found");

        verify(userRepository).findById(userId);
        verify(projectRepository, never()).save(any());
    }

    @Test
    void createProject_DuplicateName_ThrowsException() {
        // Given
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(projectRepository.existsByUserAndNameIgnoreCase(testUser, "Test Project")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> projectService.createProject(userId, validProjectRequest))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Project with name 'Test Project' already exists");

        verify(userRepository).findById(userId);
        verify(projectRepository).existsByUserAndNameIgnoreCase(testUser, "Test Project");
        verify(projectRepository, never()).save(any());
    }

    @Test
    void getUserProjects_UserNotFound_ThrowsException() {
        // Given
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> projectService.getUserProjects(userId))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("User not found");

        verify(userRepository).findById(userId);
        verify(projectRepository, never()).findByUserOrderByCreatedAtDesc(any());
    }

    @Test
    void getActiveUserProjects_UserNotFound_ThrowsException() {
        // Given
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> projectService.getActiveUserProjects(userId))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("User not found");

        verify(userRepository).findById(userId);
        verify(projectRepository, never()).findActiveProjectsByUserOrderByUpdateDateDesc(any());
    }

    @Test
    void getProject_ProjectNotFound_ThrowsException() {
        // Given
        when(projectRepository.findByUserIdAndId(userId, projectId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> projectService.getProject(userId, projectId))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Project not found");

        verify(projectRepository).findByUserIdAndId(userId, projectId);
    }

    @Test
    void updateProject_ProjectNotFound_ThrowsException() {
        // Given
        when(projectRepository.findByUserIdAndId(userId, projectId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> projectService.updateProject(userId, projectId, validProjectRequest))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Project not found");

        verify(projectRepository).findByUserIdAndId(userId, projectId);
        verify(projectRepository, never()).save(any());
    }

    @Test
    void updateProject_DuplicateName_ThrowsException() {
        // Given
        ProjectRequest updateRequest = new ProjectRequest();
        updateRequest.setName("Existing Project");
        updateRequest.setDescription("Updated Description");
        updateRequest.setIsActive(true);

        when(projectRepository.findByUserIdAndId(userId, projectId)).thenReturn(Optional.of(testProject));
        when(projectRepository.existsByUserAndNameIgnoreCase(testUser, "Existing Project")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> projectService.updateProject(userId, projectId, updateRequest))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Project with name 'Existing Project' already exists");

        verify(projectRepository).findByUserIdAndId(userId, projectId);
        verify(projectRepository).existsByUserAndNameIgnoreCase(testUser, "Existing Project");
        verify(projectRepository, never()).save(any());
    }

    @Test
    void deleteProject_ProjectNotFound_ThrowsException() {
        // Given
        when(projectRepository.findByUserIdAndId(userId, projectId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> projectService.deleteProject(userId, projectId))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Project not found");

        verify(projectRepository).findByUserIdAndId(userId, projectId);
        verify(projectRepository, never()).delete(any());
    }

    @Test
    void getProjectStats_UserNotFound_ThrowsException() {
        // Given
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> projectService.getProjectStats(userId))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("User not found");

        verify(userRepository).findById(userId);
        verify(projectRepository, never()).countByUserId(any());
    }

    @Test
    void searchProjects_UserNotFound_ThrowsException() {
        // Given
        String searchTerm = "Test";
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> projectService.searchProjects(userId, searchTerm))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("User not found");

        verify(userRepository).findById(userId);
        verify(projectRepository, never()).findByUserAndNameContainingIgnoreCase(any(), any());
    }

    @Test
    void updateProjectStatus_ProjectNotFound_ThrowsException() {
        // Given
        UUID adminUserId = UUID.randomUUID();
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> projectService.updateProjectStatus(projectId.toString(), false, adminUserId))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Project not found");

        verify(projectRepository).findById(projectId);
        verify(projectRepository, never()).save(any());
    }

    @Test
    void updateProjectStatus_InvalidUUID_ThrowsException() {
        // Given
        String invalidUuid = "invalid-uuid";
        UUID adminUserId = UUID.randomUUID();

        // When & Then
        assertThatThrownBy(() -> projectService.updateProjectStatus(invalidUuid, false, adminUserId))
            .isInstanceOf(IllegalArgumentException.class);

        verify(projectRepository, never()).findById(any());
        verify(projectRepository, never()).save(any());
    }

    @Test
    void searchProjects_EmptyResults_Success() {
        // Given
        String searchTerm = "NonExistent";
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(projectRepository.findByUserAndNameContainingIgnoreCase(testUser, searchTerm)).thenReturn(Collections.emptyList());

        // When
        List<ProjectResponse> result = projectService.searchProjects(userId, searchTerm);

        // Then
        assertThat(result).isEmpty();

        verify(userRepository).findById(userId);
        verify(projectRepository).findByUserAndNameContainingIgnoreCase(testUser, searchTerm);
    }

    @Test
    void getUserProjects_EmptyResults_Success() {
        // Given
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(projectRepository.findByUserOrderByCreatedAtDesc(testUser)).thenReturn(Collections.emptyList());

        // When
        List<ProjectResponse> result = projectService.getUserProjects(userId);

        // Then
        assertThat(result).isEmpty();

        verify(userRepository).findById(userId);
        verify(projectRepository).findByUserOrderByCreatedAtDesc(testUser);
    }

    @Test
    void getRecentProjects_WithSmallLimit_Success() {
        // Given
        int limit = 1;
        Page<Project> singlePage = new PageImpl<>(Arrays.asList(testProject));
        when(projectRepository.findRecentProjectsByUserId(eq(userId), any(PageRequest.class))).thenReturn(singlePage);

        // When
        List<ProjectResponse> result = projectService.getRecentProjects(userId, limit);

        // Then
        assertThat(result).hasSize(1);

        verify(projectRepository).findRecentProjectsByUserId(eq(userId), any(PageRequest.class));
    }
}