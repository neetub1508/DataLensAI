package ai.datalens.integration;

import ai.datalens.dto.request.ProjectRequest;
import ai.datalens.dto.response.ProjectResponse;
import ai.datalens.entity.Project;
import ai.datalens.entity.Role;
import ai.datalens.entity.User;
import ai.datalens.repository.ProjectRepository;
import ai.datalens.repository.RoleRepository;
import ai.datalens.repository.UserRepository;
import ai.datalens.security.JwtUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ProjectIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String authToken;
    private User testUser;
    private String baseUrl = "/projects";

    @BeforeEach
    void setUp() {
        // Clean up existing data
        projectRepository.deleteAll();
        userRepository.deleteAll();

        // Create test user with role
        Role userRole = roleRepository.findByName("USER")
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName("USER");
                    return roleRepository.save(role);
                });

        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setPasswordHash(passwordEncoder.encode("password"));
        testUser.setIsVerified(true);
        testUser.setRoles(Set.of(userRole));
        testUser = userRepository.save(testUser);

        // Generate auth token
        authToken = jwtUtils.generateTokenFromUserId(testUser.getId().toString(), jwtUtils.getJwtExpirationMs());
    }

    // POSITIVE INTEGRATION TEST CASES

    @Test
    void createProject_CompleteFlow_Success() throws Exception {
        // Given
        ProjectRequest request = new ProjectRequest();
        request.setName("Integration Test Project");
        request.setDescription("Test Description");
        request.setIsActive(true);

        // When & Then
        MvcResult result = mockMvc.perform(post(baseUrl)
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Integration Test Project"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.is_active").value(true))
                .andExpect(jsonPath("$.user_id").value(testUser.getId().toString()))
                .andExpect(jsonPath("$.user_email").value("test@example.com"))
                .andReturn();

        // Verify in database
        List<Project> projects = projectRepository.findByUser(testUser);
        assertThat(projects).hasSize(1);
        assertThat(projects.get(0).getName()).isEqualTo("Integration Test Project");
        assertThat(projects.get(0).getUser().getId()).isEqualTo(testUser.getId());
    }

    @Test
    void getUserProjects_CompleteFlow_Success() throws Exception {
        // Given - Create some test projects
        createTestProjects();

        // When & Then
        MvcResult result = mockMvc.perform(get(baseUrl)
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        List<ProjectResponse> projects = objectMapper.readValue(responseContent, new TypeReference<List<ProjectResponse>>() {});

        assertThat(projects).hasSize(3);
        assertThat(projects).extracting(ProjectResponse::getName)
                .containsExactlyInAnyOrder("Test Project 1", "Test Project 2", "Inactive Project");
    }

    @Test
    void getActiveUserProjects_CompleteFlow_Success() throws Exception {
        // Given
        createTestProjects();

        // When & Then
        mockMvc.perform(get(baseUrl + "/active")
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].is_active").value(true))
                .andExpect(jsonPath("$[1].is_active").value(true));
    }

    @Test
    void getRecentProjects_CompleteFlow_Success() throws Exception {
        // Given
        createTestProjects();

        // When & Then
        mockMvc.perform(get(baseUrl + "/recent")
                .param("limit", "2")
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getProject_CompleteFlow_Success() throws Exception {
        // Given
        Project project = createSingleProject("Test Project", "Test Description", true);

        // When & Then
        mockMvc.perform(get(baseUrl + "/{id}", project.getId())
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(project.getId().toString()))
                .andExpect(jsonPath("$.name").value("Test Project"))
                .andExpect(jsonPath("$.description").value("Test Description"));
    }

    @Test
    void updateProject_CompleteFlow_Success() throws Exception {
        // Given
        Project project = createSingleProject("Original Name", "Original Description", true);

        ProjectRequest updateRequest = new ProjectRequest();
        updateRequest.setName("Updated Name");
        updateRequest.setDescription("Updated Description");
        updateRequest.setIsActive(false);

        // When & Then
        mockMvc.perform(put(baseUrl + "/{id}", project.getId())
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.description").value("Updated Description"))
                .andExpect(jsonPath("$.is_active").value(false));

        // Verify in database
        Project updatedProject = projectRepository.findById(project.getId()).orElseThrow();
        assertThat(updatedProject.getName()).isEqualTo("Updated Name");
        assertThat(updatedProject.getDescription()).isEqualTo("Updated Description");
        assertThat(updatedProject.getIsActive()).isFalse();
    }

    @Test
    void deleteProject_CompleteFlow_Success() throws Exception {
        // Given
        Project project = createSingleProject("To Delete", "Description", true);

        // When & Then
        mockMvc.perform(delete(baseUrl + "/{id}", project.getId())
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Project deleted successfully"));

        // Verify in database
        assertThat(projectRepository.findById(project.getId())).isEmpty();
    }

    @Test
    void getProjectStats_CompleteFlow_Success() throws Exception {
        // Given
        createTestProjects();

        // When & Then
        MvcResult result = mockMvc.perform(get(baseUrl + "/stats")
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalProjects").value(3))
                .andExpect(jsonPath("$.activeProjects").value(2))
                .andExpect(jsonPath("$.inProgressProjects").value(2))
                .andExpect(jsonPath("$.completedProjects").value(1))
                .andReturn();
    }

    @Test
    void searchProjects_CompleteFlow_Success() throws Exception {
        // Given
        createTestProjects();

        // When & Then
        mockMvc.perform(get(baseUrl + "/search")
                .param("q", "Test")
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        // Search for inactive
        mockMvc.perform(get(baseUrl + "/search")
                .param("q", "Inactive")
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Inactive Project"));
    }

    @Test
    void projectWorkflow_CreateUpdateDelete_Success() throws Exception {
        // Create project
        ProjectRequest createRequest = new ProjectRequest();
        createRequest.setName("Workflow Project");
        createRequest.setDescription("Initial Description");
        createRequest.setIsActive(true);

        MvcResult createResult = mockMvc.perform(post(baseUrl)
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String createResponse = createResult.getResponse().getContentAsString();
        ProjectResponse createdProject = objectMapper.readValue(createResponse, ProjectResponse.class);

        // Update project
        ProjectRequest updateRequest = new ProjectRequest();
        updateRequest.setName("Updated Workflow Project");
        updateRequest.setDescription("Updated Description");
        updateRequest.setIsActive(false);

        mockMvc.perform(put(baseUrl + "/{id}", createdProject.getId())
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Workflow Project"));

        // Get project to verify update
        mockMvc.perform(get(baseUrl + "/{id}", createdProject.getId())
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Workflow Project"))
                .andExpect(jsonPath("$.is_active").value(false));

        // Delete project
        mockMvc.perform(delete(baseUrl + "/{id}", createdProject.getId())
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk());

        // Verify project is deleted
        mockMvc.perform(get(baseUrl + "/{id}", createdProject.getId())
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isNotFound());
    }

    // NEGATIVE INTEGRATION TEST CASES

    @Test
    void createProject_Unauthorized_Returns401() throws Exception {
        // Given
        ProjectRequest request = new ProjectRequest();
        request.setName("Test Project");
        request.setDescription("Test Description");
        request.setIsActive(true);

        // When & Then
        mockMvc.perform(post(baseUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createProject_InvalidToken_Returns401() throws Exception {
        // Given
        ProjectRequest request = new ProjectRequest();
        request.setName("Test Project");
        request.setDescription("Test Description");
        request.setIsActive(true);

        // When & Then
        mockMvc.perform(post(baseUrl)
                .header("Authorization", "Bearer invalid-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createProject_DuplicateName_Returns400() throws Exception {
        // Given - Create first project
        createSingleProject("Duplicate Name", "Description", true);

        ProjectRequest duplicateRequest = new ProjectRequest();
        duplicateRequest.setName("Duplicate Name");
        duplicateRequest.setDescription("Another Description");
        duplicateRequest.setIsActive(true);

        // When & Then
        mockMvc.perform(post(baseUrl)
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Project with name 'Duplicate Name' already exists"));
    }

    @Test
    void createProject_InvalidData_Returns400() throws Exception {
        // Given - Request with blank name
        ProjectRequest invalidRequest = new ProjectRequest();
        invalidRequest.setName("");
        invalidRequest.setDescription("Valid Description");
        invalidRequest.setIsActive(true);

        // When & Then
        mockMvc.perform(post(baseUrl)
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getProject_NonExistent_Returns404() throws Exception {
        // Given
        String nonExistentId = "123e4567-e89b-12d3-a456-426614174000";

        // When & Then
        mockMvc.perform(get(baseUrl + "/{id}", nonExistentId)
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void getProject_InvalidUUID_Returns400() throws Exception {
        // When & Then
        mockMvc.perform(get(baseUrl + "/{id}", "invalid-uuid")
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateProject_NonExistent_Returns400() throws Exception {
        // Given
        String nonExistentId = "123e4567-e89b-12d3-a456-426614174000";
        ProjectRequest updateRequest = new ProjectRequest();
        updateRequest.setName("Updated Name");
        updateRequest.setDescription("Updated Description");
        updateRequest.setIsActive(true);

        // When & Then
        mockMvc.perform(put(baseUrl + "/{id}", nonExistentId)
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Project not found"));
    }

    @Test
    void updateProject_DuplicateName_Returns400() throws Exception {
        // Given
        Project project1 = createSingleProject("Project 1", "Description 1", true);
        Project project2 = createSingleProject("Project 2", "Description 2", true);

        ProjectRequest updateRequest = new ProjectRequest();
        updateRequest.setName("Project 1"); // Trying to use existing name
        updateRequest.setDescription("Updated Description");
        updateRequest.setIsActive(true);

        // When & Then
        mockMvc.perform(put(baseUrl + "/{id}", project2.getId())
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Project with name 'Project 1' already exists"));
    }

    @Test
    void deleteProject_NonExistent_Returns400() throws Exception {
        // Given
        String nonExistentId = "123e4567-e89b-12d3-a456-426614174000";

        // When & Then
        mockMvc.perform(delete(baseUrl + "/{id}", nonExistentId)
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Project not found"));
    }

    @Test
    void accessOtherUserProject_Returns404() throws Exception {
        // Given - Create another user and their project
        User otherUser = new User();
        otherUser.setEmail("other@example.com");
        otherUser.setPasswordHash(passwordEncoder.encode("password"));
        otherUser.setIsVerified(true);
        otherUser = userRepository.save(otherUser);

        Project otherUserProject = new Project();
        otherUserProject.setName("Other User Project");
        otherUserProject.setDescription("Other Description");
        otherUserProject.setIsActive(true);
        otherUserProject.setUpdateDate(LocalDateTime.now());
        otherUserProject.setUpdateBy(otherUser.getId());
        otherUserProject.setUser(otherUser);
        otherUserProject = projectRepository.save(otherUserProject);

        // When & Then - Try to access other user's project
        mockMvc.perform(get(baseUrl + "/{id}", otherUserProject.getId())
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void searchProjects_EmptyResults_Success() throws Exception {
        // Given
        createTestProjects();

        // When & Then
        mockMvc.perform(get(baseUrl + "/search")
                .param("q", "NonExistentTerm")
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getUserProjects_EmptyResults_Success() throws Exception {
        // When & Then - User has no projects
        mockMvc.perform(get(baseUrl)
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getActiveUserProjects_NoActiveProjects_Success() throws Exception {
        // Given - Create only inactive projects
        createSingleProject("Inactive Project", "Description", false);

        // When & Then
        mockMvc.perform(get(baseUrl + "/active")
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // Helper methods

    private void createTestProjects() {
        createSingleProject("Test Project 1", "Description 1", true);
        createSingleProject("Test Project 2", "Description 2", true);
        createSingleProject("Inactive Project", "Description 3", false);
    }

    private Project createSingleProject(String name, String description, boolean isActive) {
        Project project = new Project();
        project.setName(name);
        project.setDescription(description);
        project.setIsActive(isActive);
        project.setUpdateDate(LocalDateTime.now());
        project.setUpdateBy(testUser.getId());
        project.setUser(testUser);
        return projectRepository.save(project);
    }
}