package ai.datalens.controller;

import ai.datalens.dto.request.ProjectRequest;
import ai.datalens.dto.response.ProjectResponse;
import ai.datalens.security.UserPrincipal;
import ai.datalens.service.ProjectService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProjectController.class)
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProjectService projectService;

    @Autowired
    private ObjectMapper objectMapper;

    private ProjectRequest validProjectRequest;
    private ProjectResponse projectResponse;
    private UserPrincipal userPrincipal;
    private UUID userId;
    private UUID projectId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        projectId = UUID.randomUUID();

        userPrincipal = new UserPrincipal(
            userId,
            "test@example.com",
            "hashedPassword",
            true, // enabled
            true, // accountNonExpired
            true, // accountNonLocked
            true, // credentialsNonExpired
            Collections.emptyList() // authorities
        );

        validProjectRequest = new ProjectRequest();
        validProjectRequest.setName("Test Project");
        validProjectRequest.setDescription("Test Description");
        validProjectRequest.setIsActive(true);

        projectResponse = new ProjectResponse();
        projectResponse.setId(projectId);
        projectResponse.setName("Test Project");
        projectResponse.setDescription("Test Description");
        projectResponse.setIsActive(true);
        projectResponse.setUserId(userId);
        projectResponse.setUserEmail("test@example.com");
        projectResponse.setCreatedAt(LocalDateTime.now());
        projectResponse.setUpdatedAt(LocalDateTime.now());
        projectResponse.setUpdateDate(LocalDateTime.now());
        projectResponse.setUpdateBy(userId);
    }

    // POSITIVE TEST CASES

    @Test
    @WithMockUser
    void createProject_Success() throws Exception {
        // Given
        when(projectService.createProject(eq(userId), any(ProjectRequest.class))).thenReturn(projectResponse);

        // When & Then
        mockMvc.perform(post("/projects")
                .with(user(userPrincipal))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validProjectRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(projectId.toString()))
                .andExpect(jsonPath("$.name").value("Test Project"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.is_active").value(true))
                .andExpect(jsonPath("$.user_id").value(userId.toString()));

        verify(projectService).createProject(eq(userId), any(ProjectRequest.class));
    }

    @Test
    @WithMockUser
    void getUserProjects_Success() throws Exception {
        // Given
        List<ProjectResponse> projects = Arrays.asList(projectResponse);
        when(projectService.getUserProjects(userId)).thenReturn(projects);

        // When & Then
        mockMvc.perform(get("/projects")
                .with(user(userPrincipal)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(projectId.toString()))
                .andExpect(jsonPath("$[0].name").value("Test Project"));

        verify(projectService).getUserProjects(userId);
    }

    @Test
    @WithMockUser
    void getActiveUserProjects_Success() throws Exception {
        // Given
        List<ProjectResponse> activeProjects = Arrays.asList(projectResponse);
        when(projectService.getActiveUserProjects(userId)).thenReturn(activeProjects);

        // When & Then
        mockMvc.perform(get("/projects/active")
                .with(user(userPrincipal)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].is_active").value(true));

        verify(projectService).getActiveUserProjects(userId);
    }

    @Test
    @WithMockUser
    void getRecentProjects_Success() throws Exception {
        // Given
        List<ProjectResponse> recentProjects = Arrays.asList(projectResponse);
        when(projectService.getRecentProjects(userId, 10)).thenReturn(recentProjects);

        // When & Then
        mockMvc.perform(get("/projects/recent")
                .with(user(userPrincipal)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(projectService).getRecentProjects(userId, 10);
    }

    @Test
    @WithMockUser
    void getRecentProjects_WithCustomLimit_Success() throws Exception {
        // Given
        int customLimit = 5;
        List<ProjectResponse> recentProjects = Arrays.asList(projectResponse);
        when(projectService.getRecentProjects(userId, customLimit)).thenReturn(recentProjects);

        // When & Then
        mockMvc.perform(get("/projects/recent")
                .param("limit", String.valueOf(customLimit))
                .with(user(userPrincipal)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(projectService).getRecentProjects(userId, customLimit);
    }

    @Test
    @WithMockUser
    void getProject_Success() throws Exception {
        // Given
        when(projectService.getProject(userId, projectId)).thenReturn(projectResponse);

        // When & Then
        mockMvc.perform(get("/projects/{id}", projectId)
                .with(user(userPrincipal)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(projectId.toString()))
                .andExpect(jsonPath("$.name").value("Test Project"));

        verify(projectService).getProject(userId, projectId);
    }

    @Test
    @WithMockUser
    void updateProject_Success() throws Exception {
        // Given
        ProjectRequest updateRequest = new ProjectRequest();
        updateRequest.setName("Updated Project");
        updateRequest.setDescription("Updated Description");
        updateRequest.setIsActive(false);

        ProjectResponse updatedResponse = new ProjectResponse();
        updatedResponse.setId(projectId);
        updatedResponse.setName("Updated Project");
        updatedResponse.setDescription("Updated Description");
        updatedResponse.setIsActive(false);

        when(projectService.updateProject(eq(userId), eq(projectId), any(ProjectRequest.class))).thenReturn(updatedResponse);

        // When & Then
        mockMvc.perform(put("/projects/{id}", projectId)
                .with(user(userPrincipal))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Project"))
                .andExpect(jsonPath("$.description").value("Updated Description"))
                .andExpect(jsonPath("$.is_active").value(false));

        verify(projectService).updateProject(eq(userId), eq(projectId), any(ProjectRequest.class));
    }

    @Test
    @WithMockUser
    void deleteProject_Success() throws Exception {
        // Given
        doNothing().when(projectService).deleteProject(userId, projectId);

        // When & Then
        mockMvc.perform(delete("/projects/{id}", projectId)
                .with(user(userPrincipal)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Project deleted successfully"));

        verify(projectService).deleteProject(userId, projectId);
    }

    @Test
    @WithMockUser
    void getProjectStats_Success() throws Exception {
        // Given
        ProjectService.ProjectStatsResponse stats = new ProjectService.ProjectStatsResponse();
        stats.setTotalProjects(10L);
        stats.setActiveProjects(7L);
        stats.setInProgressProjects(7L);
        stats.setCompletedProjects(3L);

        when(projectService.getProjectStats(userId)).thenReturn(stats);

        // When & Then
        mockMvc.perform(get("/projects/stats")
                .with(user(userPrincipal)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalProjects").value(10))
                .andExpect(jsonPath("$.activeProjects").value(7))
                .andExpect(jsonPath("$.inProgressProjects").value(7))
                .andExpect(jsonPath("$.completedProjects").value(3));

        verify(projectService).getProjectStats(userId);
    }

    @Test
    @WithMockUser
    void searchProjects_Success() throws Exception {
        // Given
        String searchTerm = "Test";
        List<ProjectResponse> searchResults = Arrays.asList(projectResponse);
        when(projectService.searchProjects(userId, searchTerm)).thenReturn(searchResults);

        // When & Then
        mockMvc.perform(get("/projects/search")
                .param("q", searchTerm)
                .with(user(userPrincipal)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Test Project"));

        verify(projectService).searchProjects(userId, searchTerm);
    }

    // NEGATIVE TEST CASES

    @Test
    @WithMockUser
    void createProject_InvalidRequest_BadRequest() throws Exception {
        // Given
        ProjectRequest invalidRequest = new ProjectRequest();
        // Missing required fields

        // When & Then
        mockMvc.perform(post("/projects")
                .with(user(userPrincipal))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(projectService, never()).createProject(any(), any());
    }

    @Test
    @WithMockUser
    void createProject_BlankName_BadRequest() throws Exception {
        // Given
        ProjectRequest invalidRequest = new ProjectRequest();
        invalidRequest.setName("");
        invalidRequest.setDescription("Valid description");
        invalidRequest.setIsActive(true);

        // When & Then
        mockMvc.perform(post("/projects")
                .with(user(userPrincipal))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(projectService, never()).createProject(any(), any());
    }

    @Test
    @WithMockUser
    void createProject_NameTooLong_BadRequest() throws Exception {
        // Given
        ProjectRequest invalidRequest = new ProjectRequest();
        invalidRequest.setName("A".repeat(256)); // Exceeds 255 character limit
        invalidRequest.setDescription("Valid description");
        invalidRequest.setIsActive(true);

        // When & Then
        mockMvc.perform(post("/projects")
                .with(user(userPrincipal))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(projectService, never()).createProject(any(), any());
    }

    @Test
    @WithMockUser
    void createProject_DescriptionTooLong_BadRequest() throws Exception {
        // Given
        ProjectRequest invalidRequest = new ProjectRequest();
        invalidRequest.setName("Valid Name");
        invalidRequest.setDescription("A".repeat(1001)); // Exceeds 1000 character limit
        invalidRequest.setIsActive(true);

        // When & Then
        mockMvc.perform(post("/projects")
                .with(user(userPrincipal))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(projectService, never()).createProject(any(), any());
    }

    @Test
    @WithMockUser
    void createProject_ServiceThrowsException_BadRequest() throws Exception {
        // Given
        when(projectService.createProject(eq(userId), any(ProjectRequest.class)))
                .thenThrow(new RuntimeException("Duplicate project name"));

        // When & Then
        mockMvc.perform(post("/projects")
                .with(user(userPrincipal))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validProjectRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Duplicate project name"));

        verify(projectService).createProject(eq(userId), any(ProjectRequest.class));
    }

    @Test
    @WithMockUser
    void getUserProjects_ServiceThrowsException_BadRequest() throws Exception {
        // Given
        when(projectService.getUserProjects(userId))
                .thenThrow(new RuntimeException("User not found"));

        // When & Then
        mockMvc.perform(get("/projects")
                .with(user(userPrincipal)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("User not found"));

        verify(projectService).getUserProjects(userId);
    }

    @Test
    @WithMockUser
    void getProject_ProjectNotFound_NotFound() throws Exception {
        // Given
        when(projectService.getProject(userId, projectId))
                .thenThrow(new RuntimeException("Project not found"));

        // When & Then
        mockMvc.perform(get("/projects/{id}", projectId)
                .with(user(userPrincipal)))
                .andExpect(status().isNotFound());

        verify(projectService).getProject(userId, projectId);
    }

    @Test
    @WithMockUser
    void updateProject_InvalidRequest_BadRequest() throws Exception {
        // Given
        ProjectRequest invalidRequest = new ProjectRequest();
        // Missing required fields

        // When & Then
        mockMvc.perform(put("/projects/{id}", projectId)
                .with(user(userPrincipal))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(projectService, never()).updateProject(any(), any(), any());
    }

    @Test
    @WithMockUser
    void updateProject_ServiceThrowsException_BadRequest() throws Exception {
        // Given
        when(projectService.updateProject(eq(userId), eq(projectId), any(ProjectRequest.class)))
                .thenThrow(new RuntimeException("Project not found"));

        // When & Then
        mockMvc.perform(put("/projects/{id}", projectId)
                .with(user(userPrincipal))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validProjectRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Project not found"));

        verify(projectService).updateProject(eq(userId), eq(projectId), any(ProjectRequest.class));
    }

    @Test
    @WithMockUser
    void deleteProject_ServiceThrowsException_BadRequest() throws Exception {
        // Given
        doThrow(new RuntimeException("Project not found"))
                .when(projectService).deleteProject(userId, projectId);

        // When & Then
        mockMvc.perform(delete("/projects/{id}", projectId)
                .with(user(userPrincipal)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Project not found"));

        verify(projectService).deleteProject(userId, projectId);
    }

    @Test
    @WithMockUser
    void getProjectStats_ServiceThrowsException_BadRequest() throws Exception {
        // Given
        when(projectService.getProjectStats(userId))
                .thenThrow(new RuntimeException("User not found"));

        // When & Then
        mockMvc.perform(get("/projects/stats")
                .with(user(userPrincipal)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("User not found"));

        verify(projectService).getProjectStats(userId);
    }

    @Test
    @WithMockUser
    void searchProjects_ServiceThrowsException_BadRequest() throws Exception {
        // Given
        String searchTerm = "Test";
        when(projectService.searchProjects(userId, searchTerm))
                .thenThrow(new RuntimeException("User not found"));

        // When & Then
        mockMvc.perform(get("/projects/search")
                .param("q", searchTerm)
                .with(user(userPrincipal)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("User not found"));

        verify(projectService).searchProjects(userId, searchTerm);
    }

    @Test
    void createProject_Unauthorized_Returns401() throws Exception {
        // When & Then
        mockMvc.perform(post("/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validProjectRequest)))
                .andExpect(status().isUnauthorized());

        verify(projectService, never()).createProject(any(), any());
    }

    @Test
    void getUserProjects_Unauthorized_Returns401() throws Exception {
        // When & Then
        mockMvc.perform(get("/projects"))
                .andExpect(status().isUnauthorized());

        verify(projectService, never()).getUserProjects(any());
    }

    @Test
    @WithMockUser
    void getUserProjects_EmptyResults_Success() throws Exception {
        // Given
        when(projectService.getUserProjects(userId)).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/projects")
                .with(user(userPrincipal)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(projectService).getUserProjects(userId);
    }

    @Test
    @WithMockUser
    void searchProjects_EmptyResults_Success() throws Exception {
        // Given
        String searchTerm = "NonExistent";
        when(projectService.searchProjects(userId, searchTerm)).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/projects/search")
                .param("q", searchTerm)
                .with(user(userPrincipal)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(projectService).searchProjects(userId, searchTerm);
    }

    @Test
    @WithMockUser
    void createProject_NullIsActive_UsesDefault() throws Exception {
        // Given
        ProjectRequest requestWithNullIsActive = new ProjectRequest();
        requestWithNullIsActive.setName("Test Project");
        requestWithNullIsActive.setDescription("Test Description");
        requestWithNullIsActive.setIsActive(null);

        when(projectService.createProject(eq(userId), any(ProjectRequest.class))).thenReturn(projectResponse);

        // When & Then
        mockMvc.perform(post("/projects")
                .with(user(userPrincipal))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestWithNullIsActive)))
                .andExpect(status().isCreated());

        verify(projectService).createProject(eq(userId), any(ProjectRequest.class));
    }

    @Test
    @WithMockUser
    void getProject_InvalidUUIDFormat_BadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/projects/{id}", "invalid-uuid")
                .with(user(userPrincipal)))
                .andExpect(status().isBadRequest());

        verify(projectService, never()).getProject(any(), any());
    }

    @Test
    @WithMockUser
    void updateProject_InvalidUUIDFormat_BadRequest() throws Exception {
        // When & Then
        mockMvc.perform(put("/projects/{id}", "invalid-uuid")
                .with(user(userPrincipal))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validProjectRequest)))
                .andExpect(status().isBadRequest());

        verify(projectService, never()).updateProject(any(), any(), any());
    }

    @Test
    @WithMockUser
    void deleteProject_InvalidUUIDFormat_BadRequest() throws Exception {
        // When & Then
        mockMvc.perform(delete("/projects/{id}", "invalid-uuid")
                .with(user(userPrincipal)))
                .andExpect(status().isBadRequest());

        verify(projectService, never()).deleteProject(any(), any());
    }
}