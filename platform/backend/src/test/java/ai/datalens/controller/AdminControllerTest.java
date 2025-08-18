package ai.datalens.controller;

import ai.datalens.dto.response.ProjectResponse;
import ai.datalens.dto.response.UserResponse;
import ai.datalens.security.UserPrincipal;
import ai.datalens.service.ProjectService;
import ai.datalens.service.UserService;
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

@WebMvcTest(AdminController.class)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private ProjectService projectService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserPrincipal adminPrincipal;
    private UserPrincipal regularUserPrincipal;
    private UserResponse testUser;
    private ProjectResponse testProject;
    private UUID adminUserId;
    private UUID regularUserId;
    private UUID testUserId;

    @BeforeEach
    void setUp() {
        adminUserId = UUID.randomUUID();
        regularUserId = UUID.randomUUID();
        testUserId = UUID.randomUUID();

        adminPrincipal = new UserPrincipal(
                adminUserId,
                "admin@datalens.ai",
                "hashedPassword",
                true, true, true, true,
                Collections.singletonList(() -> "ROLE_ADMIN")
        );

        regularUserPrincipal = new UserPrincipal(
                regularUserId,
                "user@datalens.ai",
                "hashedPassword",
                true, true, true, true,
                Collections.singletonList(() -> "ROLE_USER")
        );

        testUser = new UserResponse();
        testUser.setId(testUserId);
        testUser.setEmail("testuser@example.com");
        testUser.setStatus("ACTIVE");
        testUser.setIsVerified(true);
        testUser.setLocale("en");
        testUser.setRoles(Set.of("USER"));
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());

        testProject = new ProjectResponse();
        testProject.setId(UUID.randomUUID());
        testProject.setName("Test Project");
        testProject.setDescription("Test Description");
        testProject.setIsActive(true);
        testProject.setUserId(testUserId);
        testProject.setUserEmail("testuser@example.com");
    }

    // POSITIVE TEST CASES

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllUsers_Success() throws Exception {
        // Given
        List<UserResponse> users = Arrays.asList(testUser);
        when(userService.getAllUsers()).thenReturn(users);

        // When & Then
        mockMvc.perform(get("/admin/users")
                .with(user(adminPrincipal)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(testUserId.toString()))
                .andExpect(jsonPath("$[0].email").value("testuser@example.com"))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"));

        verify(userService).getAllUsers();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUsersPaginated_WithDefaultParams_Success() throws Exception {
        // Given
        Map<String, Object> paginatedResult = new HashMap<>();
        paginatedResult.put("users", Arrays.asList(testUser));
        paginatedResult.put("total", 1L);
        paginatedResult.put("totalPages", 1);
        paginatedResult.put("currentPage", 1);

        when(userService.getUsersPaginated(0, 10, null)).thenReturn(paginatedResult);

        // When & Then
        mockMvc.perform(get("/admin/users/paginated")
                .with(user(adminPrincipal)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.users").isArray())
                .andExpect(jsonPath("$.users.length()").value(1))
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.currentPage").value(1));

        verify(userService).getUsersPaginated(0, 10, null);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUsersPaginated_WithCustomParams_Success() throws Exception {
        // Given
        String searchTerm = "test";
        Map<String, Object> paginatedResult = new HashMap<>();
        paginatedResult.put("users", Arrays.asList(testUser));
        paginatedResult.put("total", 1L);
        paginatedResult.put("totalPages", 1);
        paginatedResult.put("currentPage", 1);

        when(userService.getUsersPaginated(1, 5, searchTerm)).thenReturn(paginatedResult);

        // When & Then
        mockMvc.perform(get("/admin/users/paginated")
                .param("page", "2")
                .param("limit", "5")
                .param("search", searchTerm)
                .with(user(adminPrincipal)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.users").isArray())
                .andExpect(jsonPath("$.total").value(1));

        verify(userService).getUsersPaginated(1, 5, searchTerm);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUser_Success() throws Exception {
        // Given
        Map<String, Object> updateRequest = new HashMap<>();
        updateRequest.put("email", "newemail@example.com");
        updateRequest.put("status", "INACTIVE");
        updateRequest.put("locale", "es");

        doNothing().when(userService).updateUser(testUserId.toString(), updateRequest);

        // When & Then
        mockMvc.perform(put("/admin/users/{userId}", testUserId)
                .with(user(adminPrincipal))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk());

        verify(userService).updateUser(testUserId.toString(), updateRequest);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUserStatus_Success() throws Exception {
        // Given
        Map<String, String> statusRequest = new HashMap<>();
        statusRequest.put("status", "INACTIVE");

        doNothing().when(userService).updateUserStatus(testUserId.toString(), "INACTIVE");

        // When & Then
        mockMvc.perform(patch("/admin/users/{userId}/status", testUserId)
                .with(user(adminPrincipal))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(statusRequest)))
                .andExpect(status().isOk());

        verify(userService).updateUserStatus(testUserId.toString(), "INACTIVE");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteUser_Success() throws Exception {
        // Given
        doNothing().when(userService).deleteUser(testUserId.toString());

        // When & Then
        mockMvc.perform(delete("/admin/users/{userId}", testUserId)
                .with(user(adminPrincipal)))
                .andExpect(status().isOk());

        verify(userService).deleteUser(testUserId.toString());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllProjects_Success() throws Exception {
        // Given
        List<ProjectResponse> projects = Arrays.asList(testProject);
        when(projectService.getAllProjects()).thenReturn(projects);

        // When & Then
        mockMvc.perform(get("/admin/projects")
                .with(user(adminPrincipal)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(testProject.getId().toString()))
                .andExpect(jsonPath("$[0].name").value("Test Project"));

        verify(projectService).getAllProjects();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateProjectStatus_Success() throws Exception {
        // Given
        Map<String, Boolean> statusRequest = new HashMap<>();
        statusRequest.put("isActive", false);

        doNothing().when(projectService).updateProjectStatus(
                testProject.getId().toString(), false, adminUserId);

        // When & Then
        mockMvc.perform(patch("/admin/projects/{projectId}/status", testProject.getId())
                .with(user(adminPrincipal))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(statusRequest)))
                .andExpect(status().isOk());

        verify(projectService).updateProjectStatus(
                testProject.getId().toString(), false, adminUserId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUserStatus_WithActiveStatus_Success() throws Exception {
        // Given
        Map<String, String> statusRequest = new HashMap<>();
        statusRequest.put("status", "ACTIVE");

        doNothing().when(userService).updateUserStatus(testUserId.toString(), "ACTIVE");

        // When & Then
        mockMvc.perform(patch("/admin/users/{userId}/status", testUserId)
                .with(user(adminPrincipal))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(statusRequest)))
                .andExpect(status().isOk());

        verify(userService).updateUserStatus(testUserId.toString(), "ACTIVE");
    }

    // NEGATIVE TEST CASES

    @Test
    void getAllUsers_Unauthorized_Returns401() throws Exception {
        // When & Then
        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isUnauthorized());

        verify(userService, never()).getAllUsers();
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllUsers_InsufficientRole_Returns403() throws Exception {
        // When & Then
        mockMvc.perform(get("/admin/users")
                .with(user(regularUserPrincipal)))
                .andExpect(status().isForbidden());

        verify(userService, never()).getAllUsers();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllUsers_ServiceThrowsException_Returns400() throws Exception {
        // Given
        when(userService.getAllUsers()).thenThrow(new RuntimeException("Database error"));

        // When & Then
        mockMvc.perform(get("/admin/users")
                .with(user(adminPrincipal)))
                .andExpect(status().isBadRequest());

        verify(userService).getAllUsers();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUsersPaginated_ServiceThrowsException_Returns400() throws Exception {
        // Given
        when(userService.getUsersPaginated(anyInt(), anyInt(), any()))
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        mockMvc.perform(get("/admin/users/paginated")
                .with(user(adminPrincipal)))
                .andExpect(status().isBadRequest());

        verify(userService).getUsersPaginated(0, 10, null);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUser_ServiceThrowsException_Returns400() throws Exception {
        // Given
        Map<String, Object> updateRequest = new HashMap<>();
        updateRequest.put("email", "invalid@email");

        doThrow(new RuntimeException("Invalid email")).when(userService)
                .updateUser(eq(testUserId.toString()), any());

        // When & Then
        mockMvc.perform(put("/admin/users/{userId}", testUserId)
                .with(user(adminPrincipal))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid email"));

        verify(userService).updateUser(eq(testUserId.toString()), any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUserStatus_InvalidStatus_Returns400() throws Exception {
        // Given
        Map<String, String> statusRequest = new HashMap<>();
        statusRequest.put("status", "INVALID_STATUS");

        // When & Then
        mockMvc.perform(patch("/admin/users/{userId}/status", testUserId)
                .with(user(adminPrincipal))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(statusRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid status. Must be ACTIVE or INACTIVE"));

        verify(userService, never()).updateUserStatus(any(), any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUserStatus_MissingStatus_Returns400() throws Exception {
        // Given
        Map<String, String> statusRequest = new HashMap<>();
        // No status field

        // When & Then
        mockMvc.perform(patch("/admin/users/{userId}/status", testUserId)
                .with(user(adminPrincipal))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(statusRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid status. Must be ACTIVE or INACTIVE"));

        verify(userService, never()).updateUserStatus(any(), any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUserStatus_ServiceThrowsException_Returns400() throws Exception {
        // Given
        Map<String, String> statusRequest = new HashMap<>();
        statusRequest.put("status", "ACTIVE");

        doThrow(new RuntimeException("User not found")).when(userService)
                .updateUserStatus(testUserId.toString(), "ACTIVE");

        // When & Then
        mockMvc.perform(patch("/admin/users/{userId}/status", testUserId)
                .with(user(adminPrincipal))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(statusRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User not found"));

        verify(userService).updateUserStatus(testUserId.toString(), "ACTIVE");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteUser_ServiceThrowsException_Returns400() throws Exception {
        // Given
        doThrow(new RuntimeException("Cannot delete admin users")).when(userService)
                .deleteUser(testUserId.toString());

        // When & Then
        mockMvc.perform(delete("/admin/users/{userId}", testUserId)
                .with(user(adminPrincipal)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Cannot delete admin users"));

        verify(userService).deleteUser(testUserId.toString());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllProjects_ServiceThrowsException_Returns400() throws Exception {
        // Given
        when(projectService.getAllProjects()).thenThrow(new RuntimeException("Database error"));

        // When & Then
        mockMvc.perform(get("/admin/projects")
                .with(user(adminPrincipal)))
                .andExpect(status().isBadRequest());

        verify(projectService).getAllProjects();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateProjectStatus_MissingIsActive_Returns400() throws Exception {
        // Given
        Map<String, Boolean> statusRequest = new HashMap<>();
        // No isActive field

        // When & Then
        mockMvc.perform(patch("/admin/projects/{projectId}/status", testProject.getId())
                .with(user(adminPrincipal))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(statusRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("isActive field is required"));

        verify(projectService, never()).updateProjectStatus(any(), any(), any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateProjectStatus_ServiceThrowsException_Returns400() throws Exception {
        // Given
        Map<String, Boolean> statusRequest = new HashMap<>();
        statusRequest.put("isActive", false);

        doThrow(new RuntimeException("Project not found")).when(projectService)
                .updateProjectStatus(testProject.getId().toString(), false, adminUserId);

        // When & Then
        mockMvc.perform(patch("/admin/projects/{projectId}/status", testProject.getId())
                .with(user(adminPrincipal))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(statusRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Project not found"));

        verify(projectService).updateProjectStatus(
                testProject.getId().toString(), false, adminUserId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUser_InvalidUserId_Returns400() throws Exception {
        // Given
        String invalidUserId = "invalid-uuid";
        Map<String, Object> updateRequest = new HashMap<>();
        updateRequest.put("email", "test@example.com");

        doThrow(new IllegalArgumentException("Invalid UUID format")).when(userService)
                .updateUser(eq(invalidUserId), any());

        // When & Then
        mockMvc.perform(put("/admin/users/{userId}", invalidUserId)
                .with(user(adminPrincipal))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid UUID format"));

        verify(userService).updateUser(eq(invalidUserId), any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteUser_InvalidUserId_Returns400() throws Exception {
        // Given
        String invalidUserId = "invalid-uuid";

        doThrow(new IllegalArgumentException("Invalid UUID format")).when(userService)
                .deleteUser(invalidUserId);

        // When & Then
        mockMvc.perform(delete("/admin/users/{userId}", invalidUserId)
                .with(user(adminPrincipal)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid UUID format"));

        verify(userService).deleteUser(invalidUserId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateProjectStatus_InvalidProjectId_Returns400() throws Exception {
        // Given
        String invalidProjectId = "invalid-uuid";
        Map<String, Boolean> statusRequest = new HashMap<>();
        statusRequest.put("isActive", true);

        doThrow(new IllegalArgumentException("Invalid UUID format")).when(projectService)
                .updateProjectStatus(eq(invalidProjectId), any(), any());

        // When & Then
        mockMvc.perform(patch("/admin/projects/{projectId}/status", invalidProjectId)
                .with(user(adminPrincipal))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(statusRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid UUID format"));

        verify(projectService).updateProjectStatus(eq(invalidProjectId), eq(true), eq(adminUserId));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUsersPaginated_WithEmptyResults_Success() throws Exception {
        // Given
        Map<String, Object> emptyResult = new HashMap<>();
        emptyResult.put("users", Collections.emptyList());
        emptyResult.put("total", 0L);
        emptyResult.put("totalPages", 0);
        emptyResult.put("currentPage", 1);

        when(userService.getUsersPaginated(0, 10, "nonexistent")).thenReturn(emptyResult);

        // When & Then
        mockMvc.perform(get("/admin/users/paginated")
                .param("search", "nonexistent")
                .with(user(adminPrincipal)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.users").isArray())
                .andExpect(jsonPath("$.users.length()").value(0))
                .andExpect(jsonPath("$.total").value(0));

        verify(userService).getUsersPaginated(0, 10, "nonexistent");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllProjects_WithEmptyResults_Success() throws Exception {
        // Given
        when(projectService.getAllProjects()).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/admin/projects")
                .with(user(adminPrincipal)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(projectService).getAllProjects();
    }
}