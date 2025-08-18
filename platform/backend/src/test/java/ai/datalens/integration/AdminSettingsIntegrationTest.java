package ai.datalens.integration;

import ai.datalens.entity.Role;
import ai.datalens.entity.User;
import ai.datalens.repository.RoleRepository;
import ai.datalens.repository.UserRepository;
import ai.datalens.repository.ProjectRepository;
import ai.datalens.security.JwtUtils;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AdminSettingsIntegrationTest {

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

    private String adminToken;
    private String userToken;
    private User adminUser;
    private User regularUser;
    private Role adminRole;
    private Role userRole;

    @BeforeEach
    void setUp() {
        // Clean up existing data
        projectRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();

        // Create roles
        adminRole = new Role();
        adminRole.setName("ADMIN");
        adminRole.setDescription("Administrator role");
        adminRole = roleRepository.save(adminRole);

        userRole = new Role();
        userRole.setName("USER");
        userRole.setDescription("Regular user role");
        userRole = roleRepository.save(userRole);

        // Create admin user
        adminUser = new User();
        adminUser.setEmail("admin@datalens.ai");
        adminUser.setPasswordHash(passwordEncoder.encode("adminpass"));
        adminUser.setIsVerified(true);
        adminUser.setStatus("ACTIVE");
        adminUser.setLocale("en");
        adminUser.setRoles(Set.of(adminRole));
        adminUser = userRepository.save(adminUser);

        // Create regular user
        regularUser = new User();
        regularUser.setEmail("user@datalens.ai");
        regularUser.setPasswordHash(passwordEncoder.encode("userpass"));
        regularUser.setIsVerified(true);
        regularUser.setStatus("ACTIVE");
        regularUser.setLocale("en");
        regularUser.setRoles(Set.of(userRole));
        regularUser = userRepository.save(regularUser);

        // Generate tokens
        adminToken = jwtUtils.generateTokenFromUserId(adminUser.getId().toString(), jwtUtils.getJwtExpirationMs());
        userToken = jwtUtils.generateTokenFromUserId(regularUser.getId().toString(), jwtUtils.getJwtExpirationMs());
    }

    // POSITIVE INTEGRATION TEST CASES

    @Test
    void getAllUsers_AsAdmin_Success() throws Exception {
        // When & Then
        mockMvc.perform(get("/admin/users")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].email").exists())
                .andExpect(jsonPath("$[0].status").exists())
                .andExpect(jsonPath("$[0].roles").exists());
    }

    @Test
    void getUsersPaginated_AsAdmin_Success() throws Exception {
        // When & Then
        mockMvc.perform(get("/admin/users/paginated")
                .param("page", "1")
                .param("limit", "10")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.users").isArray())
                .andExpect(jsonPath("$.total").value(2))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.currentPage").value(1));
    }

    @Test
    void getUsersPaginated_WithSearch_Success() throws Exception {
        // When & Then
        mockMvc.perform(get("/admin/users/paginated")
                .param("search", "admin")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.users").isArray())
                .andExpect(jsonPath("$.users.length()").value(1))
                .andExpect(jsonPath("$.users[0].email").value("admin@datalens.ai"));
    }

    @Test
    void updateUser_AsAdmin_Success() throws Exception {
        // Given
        Map<String, Object> updateRequest = new HashMap<>();
        updateRequest.put("email", "updated@datalens.ai");
        updateRequest.put("status", "INACTIVE");
        updateRequest.put("locale", "es");

        // When & Then
        mockMvc.perform(put("/admin/users/{userId}", regularUser.getId())
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk());

        // Verify in database
        User updatedUser = userRepository.findById(regularUser.getId()).orElseThrow();
        assertThat(updatedUser.getEmail()).isEqualTo("updated@datalens.ai");
        assertThat(updatedUser.getStatus()).isEqualTo("INACTIVE");
        assertThat(updatedUser.getLocale()).isEqualTo("es");
    }

    @Test
    void updateUserStatus_AsAdmin_Success() throws Exception {
        // Given
        Map<String, String> statusRequest = new HashMap<>();
        statusRequest.put("status", "INACTIVE");

        // When & Then
        mockMvc.perform(patch("/admin/users/{userId}/status", regularUser.getId())
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(statusRequest)))
                .andExpect(status().isOk());

        // Verify in database
        User updatedUser = userRepository.findById(regularUser.getId()).orElseThrow();
        assertThat(updatedUser.getStatus()).isEqualTo("INACTIVE");
    }

    @Test
    void deleteUser_AsAdmin_Success() throws Exception {
        // Given - Create additional user to delete
        User userToDelete = new User();
        userToDelete.setEmail("delete@example.com");
        userToDelete.setPasswordHash(passwordEncoder.encode("password"));
        userToDelete.setIsVerified(true);
        userToDelete.setStatus("ACTIVE");
        userToDelete.setLocale("en");
        userToDelete.setRoles(Set.of(userRole));
        userToDelete = userRepository.save(userToDelete);

        // When & Then
        mockMvc.perform(delete("/admin/users/{userId}", userToDelete.getId())
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());

        // Verify user was deleted
        assertThat(userRepository.findById(userToDelete.getId())).isEmpty();
    }

    @Test
    void updateUserRoles_AsAdmin_Success() throws Exception {
        // Given
        Map<String, Object> updateRequest = new HashMap<>();
        updateRequest.put("roles", Arrays.asList("USER", "ADMIN"));

        // When & Then
        mockMvc.perform(put("/admin/users/{userId}", regularUser.getId())
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk());

        // Verify in database
        User updatedUser = userRepository.findByIdWithRoles(regularUser.getId()).orElseThrow();
        Set<String> roleNames = new HashSet<>();
        updatedUser.getRoles().forEach(role -> roleNames.add(role.getName()));
        assertThat(roleNames).contains("USER", "ADMIN");
    }

    @Test
    void adminWorkflow_CompleteUserManagement_Success() throws Exception {
        // Step 1: Get all users
        mockMvc.perform(get("/admin/users")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        // Step 2: Search for specific user
        mockMvc.perform(get("/admin/users/paginated")
                .param("search", "user@datalens.ai")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.users.length()").value(1));

        // Step 3: Update user status
        Map<String, String> statusUpdate = new HashMap<>();
        statusUpdate.put("status", "INACTIVE");
        
        mockMvc.perform(patch("/admin/users/{userId}/status", regularUser.getId())
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(statusUpdate)))
                .andExpect(status().isOk());

        // Step 4: Update user details
        Map<String, Object> userUpdate = new HashMap<>();
        userUpdate.put("locale", "fr");
        userUpdate.put("status", "ACTIVE");

        mockMvc.perform(put("/admin/users/{userId}", regularUser.getId())
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userUpdate)))
                .andExpect(status().isOk());

        // Verify final state
        User finalUser = userRepository.findById(regularUser.getId()).orElseThrow();
        assertThat(finalUser.getStatus()).isEqualTo("ACTIVE");
        assertThat(finalUser.getLocale()).isEqualTo("fr");
    }

    // NEGATIVE INTEGRATION TEST CASES

    @Test
    void getAllUsers_AsRegularUser_Returns403() throws Exception {
        // When & Then
        mockMvc.perform(get("/admin/users")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAllUsers_Unauthorized_Returns401() throws Exception {
        // When & Then
        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getUsersPaginated_AsRegularUser_Returns403() throws Exception {
        // When & Then
        mockMvc.perform(get("/admin/users/paginated")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateUser_AsRegularUser_Returns403() throws Exception {
        // Given
        Map<String, Object> updateRequest = new HashMap<>();
        updateRequest.put("email", "hacked@example.com");

        // When & Then
        mockMvc.perform(put("/admin/users/{userId}", regularUser.getId())
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateUser_DuplicateEmail_Returns400() throws Exception {
        // Given
        Map<String, Object> updateRequest = new HashMap<>();
        updateRequest.put("email", "admin@datalens.ai"); // Already exists

        // When & Then
        mockMvc.perform(put("/admin/users/{userId}", regularUser.getId())
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Email already exists"));
    }

    @Test
    void updateUser_EmptyEmail_Returns400() throws Exception {
        // Given
        Map<String, Object> updateRequest = new HashMap<>();
        updateRequest.put("email", "");

        // When & Then
        mockMvc.perform(put("/admin/users/{userId}", regularUser.getId())
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Email cannot be empty"));
    }

    @Test
    void updateUser_NonExistentUser_Returns400() throws Exception {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        Map<String, Object> updateRequest = new HashMap<>();
        updateRequest.put("email", "test@example.com");

        // When & Then
        mockMvc.perform(put("/admin/users/{userId}", nonExistentId)
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User not found"));
    }

    @Test
    void updateUserStatus_InvalidStatus_Returns400() throws Exception {
        // Given
        Map<String, String> statusRequest = new HashMap<>();
        statusRequest.put("status", "INVALID_STATUS");

        // When & Then
        mockMvc.perform(patch("/admin/users/{userId}/status", regularUser.getId())
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(statusRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid status. Must be ACTIVE or INACTIVE"));
    }

    @Test
    void updateUserStatus_MissingStatus_Returns400() throws Exception {
        // Given
        Map<String, String> statusRequest = new HashMap<>();
        // No status field

        // When & Then
        mockMvc.perform(patch("/admin/users/{userId}/status", regularUser.getId())
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(statusRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid status. Must be ACTIVE or INACTIVE"));
    }

    @Test
    void deleteUser_AdminUser_Returns400() throws Exception {
        // When & Then
        mockMvc.perform(delete("/admin/users/{userId}", adminUser.getId())
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Cannot delete admin users"));

        // Verify admin user still exists
        assertThat(userRepository.findById(adminUser.getId())).isPresent();
    }

    @Test
    void deleteUser_NonExistentUser_Returns400() throws Exception {
        // Given
        UUID nonExistentId = UUID.randomUUID();

        // When & Then
        mockMvc.perform(delete("/admin/users/{userId}", nonExistentId)
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User not found"));
    }

    @Test
    void updateUser_InvalidRole_Returns400() throws Exception {
        // Given
        Map<String, Object> updateRequest = new HashMap<>();
        updateRequest.put("roles", Arrays.asList("NONEXISTENT_ROLE"));

        // When & Then
        mockMvc.perform(put("/admin/users/{userId}", regularUser.getId())
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Role not found: NONEXISTENT_ROLE"));
    }

    @Test
    void updateUser_InvalidUUID_Returns400() throws Exception {
        // Given
        String invalidUuid = "invalid-uuid";
        Map<String, Object> updateRequest = new HashMap<>();
        updateRequest.put("email", "test@example.com");

        // When & Then
        mockMvc.perform(put("/admin/users/{userId}", invalidUuid)
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUsersPaginated_WithEmptyResults_Success() throws Exception {
        // When & Then
        mockMvc.perform(get("/admin/users/paginated")
                .param("search", "nonexistent@email.com")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.users").isArray())
                .andExpect(jsonPath("$.users.length()").value(0))
                .andExpect(jsonPath("$.total").value(0));
    }

    @Test
    void getUsersPaginated_WithPagination_Success() throws Exception {
        // Given - Create more users for pagination testing
        for (int i = 0; i < 15; i++) {
            User user = new User();
            user.setEmail("user" + i + "@example.com");
            user.setPasswordHash(passwordEncoder.encode("password"));
            user.setIsVerified(true);
            user.setStatus("ACTIVE");
            user.setLocale("en");
            user.setRoles(Set.of(userRole));
            userRepository.save(user);
        }

        // When & Then - Test first page
        mockMvc.perform(get("/admin/users/paginated")
                .param("page", "1")
                .param("limit", "5")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.users.length()").value(5))
                .andExpect(jsonPath("$.currentPage").value(1));

        // Test second page
        mockMvc.perform(get("/admin/users/paginated")
                .param("page", "2")
                .param("limit", "5")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.users.length()").value(5))
                .andExpect(jsonPath("$.currentPage").value(2));
    }

    @Test
    void updateUserStatus_BothStatuses_Success() throws Exception {
        // Test ACTIVE status
        Map<String, String> activeStatus = new HashMap<>();
        activeStatus.put("status", "ACTIVE");

        mockMvc.perform(patch("/admin/users/{userId}/status", regularUser.getId())
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(activeStatus)))
                .andExpect(status().isOk());

        User user = userRepository.findById(regularUser.getId()).orElseThrow();
        assertThat(user.getStatus()).isEqualTo("ACTIVE");

        // Test INACTIVE status
        Map<String, String> inactiveStatus = new HashMap<>();
        inactiveStatus.put("status", "INACTIVE");

        mockMvc.perform(patch("/admin/users/{userId}/status", regularUser.getId())
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inactiveStatus)))
                .andExpect(status().isOk());

        user = userRepository.findById(regularUser.getId()).orElseThrow();
        assertThat(user.getStatus()).isEqualTo("INACTIVE");
    }
}