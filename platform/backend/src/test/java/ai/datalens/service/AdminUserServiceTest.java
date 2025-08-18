package ai.datalens.service;

import ai.datalens.dto.response.UserResponse;
import ai.datalens.entity.Role;
import ai.datalens.entity.User;
import ai.datalens.repository.RoleRepository;
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
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private User adminUser;
    private Role userRole;
    private Role adminRole;
    private UUID testUserId;
    private UUID adminUserId;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        adminUserId = UUID.randomUUID();

        // Setup roles
        userRole = new Role();
        userRole.setId(UUID.randomUUID());
        userRole.setName("USER");
        userRole.setCreatedAt(LocalDateTime.now());
        userRole.setUpdatedAt(LocalDateTime.now());

        adminRole = new Role();
        adminRole.setId(UUID.randomUUID());
        adminRole.setName("admin");
        adminRole.setCreatedAt(LocalDateTime.now());
        adminRole.setUpdatedAt(LocalDateTime.now());

        // Setup test user
        testUser = new User();
        testUser.setId(testUserId);
        testUser.setEmail("testuser@example.com");
        testUser.setPasswordHash("hashedPassword");
        testUser.setStatus("ACTIVE");
        testUser.setLocale("en");
        testUser.setIsVerified(true);
        testUser.setRoles(Set.of(userRole));
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());

        // Setup admin user
        adminUser = new User();
        adminUser.setId(adminUserId);
        adminUser.setEmail("admin@example.com");
        adminUser.setPasswordHash("hashedPassword");
        adminUser.setStatus("ACTIVE");
        adminUser.setLocale("en");
        adminUser.setIsVerified(true);
        adminUser.setRoles(Set.of(adminRole));
        adminUser.setCreatedAt(LocalDateTime.now());
        adminUser.setUpdatedAt(LocalDateTime.now());
    }

    // POSITIVE TEST CASES

    @Test
    void getAllUsers_Success() {
        // Given
        List<User> users = Arrays.asList(testUser, adminUser);
        when(userRepository.findAllWithRoles()).thenReturn(users);

        // When
        List<UserResponse> result = userService.getAllUsers();

        // Then
        assertThat(result).hasSize(2);
        
        UserResponse firstUser = result.get(0);
        assertThat(firstUser.getId()).isEqualTo(testUserId);
        assertThat(firstUser.getEmail()).isEqualTo("testuser@example.com");
        assertThat(firstUser.getStatus()).isEqualTo("ACTIVE");
        assertThat(firstUser.getRoles()).contains("USER");

        verify(userRepository).findAllWithRoles();
    }

    @Test
    void getAllUsers_EmptyResults_Success() {
        // Given
        when(userRepository.findAllWithRoles()).thenReturn(Collections.emptyList());

        // When
        List<UserResponse> result = userService.getAllUsers();

        // Then
        assertThat(result).isEmpty();
        verify(userRepository).findAllWithRoles();
    }

    @Test
    void getUsersPaginated_WithoutSearch_Success() {
        // Given
        int page = 0;
        int limit = 10;
        Pageable pageable = PageRequest.of(page, limit);
        Page<User> userPage = new PageImpl<>(Arrays.asList(testUser), pageable, 1);

        when(userRepository.findAll(pageable)).thenReturn(userPage);
        when(userRepository.findByIdWithRoles(testUserId)).thenReturn(Optional.of(testUser));

        // When
        Map<String, Object> result = userService.getUsersPaginated(page, limit, null);

        // Then
        assertThat(result).containsKeys("users", "total", "totalPages", "currentPage");
        assertThat((List<?>) result.get("users")).hasSize(1);
        assertThat(result.get("total")).isEqualTo(1L);
        assertThat(result.get("totalPages")).isEqualTo(1);
        assertThat(result.get("currentPage")).isEqualTo(1);

        verify(userRepository).findAll(pageable);
        verify(userRepository).findByIdWithRoles(testUserId);
    }

    @Test
    void getUsersPaginated_WithSearch_Success() {
        // Given
        int page = 0;
        int limit = 10;
        String search = "test";
        Pageable pageable = PageRequest.of(page, limit);
        Page<User> userPage = new PageImpl<>(Arrays.asList(testUser), pageable, 1);

        when(userRepository.findByEmailContainingIgnoreCase(search.trim(), pageable)).thenReturn(userPage);
        when(userRepository.findByIdWithRoles(testUserId)).thenReturn(Optional.of(testUser));

        // When
        Map<String, Object> result = userService.getUsersPaginated(page, limit, search);

        // Then
        assertThat(result).containsKeys("users", "total", "totalPages", "currentPage");
        assertThat((List<?>) result.get("users")).hasSize(1);
        assertThat(result.get("total")).isEqualTo(1L);

        verify(userRepository).findByEmailContainingIgnoreCase(search.trim(), pageable);
        verify(userRepository).findByIdWithRoles(testUserId);
    }

    @Test
    void getUsersPaginated_WithEmptySearch_Success() {
        // Given
        int page = 0;
        int limit = 10;
        String search = "   "; // Empty/whitespace search
        Pageable pageable = PageRequest.of(page, limit);
        Page<User> userPage = new PageImpl<>(Arrays.asList(testUser), pageable, 1);

        when(userRepository.findAll(pageable)).thenReturn(userPage);
        when(userRepository.findByIdWithRoles(testUserId)).thenReturn(Optional.of(testUser));

        // When
        Map<String, Object> result = userService.getUsersPaginated(page, limit, search);

        // Then
        assertThat(result).containsKeys("users", "total", "totalPages", "currentPage");
        verify(userRepository).findAll(pageable); // Should use findAll, not search
    }

    @Test
    void updateUserStatus_Success() {
        // Given
        String userId = testUserId.toString();
        String newStatus = "INACTIVE";
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        assertThatCode(() -> userService.updateUserStatus(userId, newStatus))
                .doesNotThrowAnyException();

        // Then
        verify(userRepository).findById(testUserId);
        verify(userRepository).save(argThat(user -> "INACTIVE".equals(user.getStatus())));
    }

    @Test
    void updateUser_EmailUpdate_Success() {
        // Given
        String userId = testUserId.toString();
        Map<String, Object> updates = new HashMap<>();
        updates.put("email", "newemail@example.com");

        when(userRepository.findByIdWithRoles(testUserId)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmailIgnoreCase("newemail@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        assertThatCode(() -> userService.updateUser(userId, updates))
                .doesNotThrowAnyException();

        // Then
        verify(userRepository).findByIdWithRoles(testUserId);
        verify(userRepository).existsByEmailIgnoreCase("newemail@example.com");
        verify(userRepository).save(argThat(user -> "newemail@example.com".equals(user.getEmail())));
    }

    @Test
    void updateUser_StatusUpdate_Success() {
        // Given
        String userId = testUserId.toString();
        Map<String, Object> updates = new HashMap<>();
        updates.put("status", "INACTIVE");

        when(userRepository.findByIdWithRoles(testUserId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        assertThatCode(() -> userService.updateUser(userId, updates))
                .doesNotThrowAnyException();

        // Then
        verify(userRepository).findByIdWithRoles(testUserId);
        verify(userRepository).save(argThat(user -> "INACTIVE".equals(user.getStatus())));
    }

    @Test
    void updateUser_LocaleUpdate_Success() {
        // Given
        String userId = testUserId.toString();
        Map<String, Object> updates = new HashMap<>();
        updates.put("locale", "es");

        when(userRepository.findByIdWithRoles(testUserId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        assertThatCode(() -> userService.updateUser(userId, updates))
                .doesNotThrowAnyException();

        // Then
        verify(userRepository).findByIdWithRoles(testUserId);
        verify(userRepository).save(argThat(user -> "es".equals(user.getLocale())));
    }

    @Test
    void updateUser_RolesUpdate_Success() {
        // Given
        String userId = testUserId.toString();
        Map<String, Object> updates = new HashMap<>();
        updates.put("roles", Arrays.asList("USER", "MODERATOR"));

        Role moderatorRole = new Role();
        moderatorRole.setName("MODERATOR");

        when(userRepository.findByIdWithRoles(testUserId)).thenReturn(Optional.of(testUser));
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(userRole));
        when(roleRepository.findByName("MODERATOR")).thenReturn(Optional.of(moderatorRole));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        assertThatCode(() -> userService.updateUser(userId, updates))
                .doesNotThrowAnyException();

        // Then
        verify(userRepository).findByIdWithRoles(testUserId);
        verify(roleRepository).findByName("USER");
        verify(roleRepository).findByName("MODERATOR");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUser_MultipleFields_Success() {
        // Given
        String userId = testUserId.toString();
        Map<String, Object> updates = new HashMap<>();
        updates.put("email", "updated@example.com");
        updates.put("status", "INACTIVE");
        updates.put("locale", "fr");

        when(userRepository.findByIdWithRoles(testUserId)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmailIgnoreCase("updated@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        assertThatCode(() -> userService.updateUser(userId, updates))
                .doesNotThrowAnyException();

        // Then
        verify(userRepository).findByIdWithRoles(testUserId);
        verify(userRepository).save(argThat(user -> 
                "updated@example.com".equals(user.getEmail()) && 
                "INACTIVE".equals(user.getStatus()) && 
                "fr".equals(user.getLocale())
        ));
    }

    @Test
    void deleteUser_RegularUser_Success() {
        // Given
        String userId = testUserId.toString();
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));

        // When
        assertThatCode(() -> userService.deleteUser(userId))
                .doesNotThrowAnyException();

        // Then
        verify(userRepository).findById(testUserId);
        verify(userRepository).delete(testUser);
    }

    @Test
    void updateUserLocale_Success() {
        // Given
        String newLocale = "es";
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        assertThatCode(() -> userService.updateUserLocale(testUserId, newLocale))
                .doesNotThrowAnyException();

        // Then
        verify(userRepository).findById(testUserId);
        verify(userRepository).save(argThat(user -> "es".equals(user.getLocale())));
    }

    // NEGATIVE TEST CASES

    @Test
    void updateUserStatus_UserNotFound_ThrowsException() {
        // Given
        String userId = testUserId.toString();
        when(userRepository.findById(testUserId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.updateUserStatus(userId, "INACTIVE"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User not found");

        verify(userRepository).findById(testUserId);
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUserStatus_InvalidUUID_ThrowsException() {
        // Given
        String invalidUserId = "invalid-uuid";

        // When & Then
        assertThatThrownBy(() -> userService.updateUserStatus(invalidUserId, "INACTIVE"))
                .isInstanceOf(IllegalArgumentException.class);

        verify(userRepository, never()).findById(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUser_UserNotFound_ThrowsException() {
        // Given
        String userId = testUserId.toString();
        Map<String, Object> updates = new HashMap<>();
        updates.put("email", "test@example.com");

        when(userRepository.findByIdWithRoles(testUserId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.updateUser(userId, updates))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User not found");

        verify(userRepository).findByIdWithRoles(testUserId);
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUser_EmptyEmail_ThrowsException() {
        // Given
        String userId = testUserId.toString();
        Map<String, Object> updates = new HashMap<>();
        updates.put("email", "");

        when(userRepository.findByIdWithRoles(testUserId)).thenReturn(Optional.of(testUser));

        // When & Then
        assertThatThrownBy(() -> userService.updateUser(userId, updates))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Email cannot be empty");

        verify(userRepository).findByIdWithRoles(testUserId);
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUser_DuplicateEmail_ThrowsException() {
        // Given
        String userId = testUserId.toString();
        Map<String, Object> updates = new HashMap<>();
        updates.put("email", "existing@example.com");

        when(userRepository.findByIdWithRoles(testUserId)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmailIgnoreCase("existing@example.com")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> userService.updateUser(userId, updates))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Email already exists");

        verify(userRepository).findByIdWithRoles(testUserId);
        verify(userRepository).existsByEmailIgnoreCase("existing@example.com");
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUser_InvalidLocaleLength_IgnoresUpdate() {
        // Given
        String userId = testUserId.toString();
        Map<String, Object> updates = new HashMap<>();
        updates.put("locale", "x"); // Too short

        when(userRepository.findByIdWithRoles(testUserId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        assertThatCode(() -> userService.updateUser(userId, updates))
                .doesNotThrowAnyException();

        // Then - locale should not be updated
        verify(userRepository).save(argThat(user -> "en".equals(user.getLocale()))); // Original locale preserved
    }

    @Test
    void updateUser_RoleNotFound_ThrowsException() {
        // Given
        String userId = testUserId.toString();
        Map<String, Object> updates = new HashMap<>();
        updates.put("roles", Arrays.asList("NONEXISTENT_ROLE"));

        when(userRepository.findByIdWithRoles(testUserId)).thenReturn(Optional.of(testUser));
        when(roleRepository.findByName("NONEXISTENT_ROLE")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.updateUser(userId, updates))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Role not found: NONEXISTENT_ROLE");

        verify(userRepository).findByIdWithRoles(testUserId);
        verify(roleRepository).findByName("NONEXISTENT_ROLE");
        verify(userRepository, never()).save(any());
    }

    @Test
    void deleteUser_UserNotFound_ThrowsException() {
        // Given
        String userId = testUserId.toString();
        when(userRepository.findById(testUserId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.deleteUser(userId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User not found");

        verify(userRepository).findById(testUserId);
        verify(userRepository, never()).delete(any());
    }

    @Test
    void deleteUser_AdminUser_ThrowsException() {
        // Given
        String userId = adminUserId.toString();
        when(userRepository.findById(adminUserId)).thenReturn(Optional.of(adminUser));

        // When & Then
        assertThatThrownBy(() -> userService.deleteUser(userId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Cannot delete admin users");

        verify(userRepository).findById(adminUserId);
        verify(userRepository, never()).delete(any());
    }

    @Test
    void updateUserLocale_UserNotFound_ThrowsException() {
        // Given
        when(userRepository.findById(testUserId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.updateUserLocale(testUserId, "es"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User not found");

        verify(userRepository).findById(testUserId);
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUserLocale_InvalidLocaleLength_ThrowsException() {
        // Given
        String invalidLocale = "x"; // Too short
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));

        // When & Then
        assertThatThrownBy(() -> userService.updateUserLocale(testUserId, invalidLocale))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Invalid locale format");

        verify(userRepository).findById(testUserId);
        verify(userRepository, never()).save(any());
    }

    @Test
    void getUsersPaginated_EmptyResults_Success() {
        // Given
        int page = 0;
        int limit = 10;
        String search = "nonexistent";
        Pageable pageable = PageRequest.of(page, limit);
        Page<User> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(userRepository.findByEmailContainingIgnoreCase(search.trim(), pageable)).thenReturn(emptyPage);

        // When
        Map<String, Object> result = userService.getUsersPaginated(page, limit, search);

        // Then
        assertThat(result.get("total")).isEqualTo(0L);
        assertThat(result.get("totalPages")).isEqualTo(0);
        assertThat((List<?>) result.get("users")).isEmpty();

        verify(userRepository).findByEmailContainingIgnoreCase(search.trim(), pageable);
    }

    @Test
    void updateUser_InvalidStatusIgnored() {
        // Given
        String userId = testUserId.toString();
        Map<String, Object> updates = new HashMap<>();
        updates.put("status", "INVALID_STATUS");

        when(userRepository.findByIdWithRoles(testUserId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        assertThatCode(() -> userService.updateUser(userId, updates))
                .doesNotThrowAnyException();

        // Then - status should not be updated
        verify(userRepository).save(argThat(user -> "ACTIVE".equals(user.getStatus()))); // Original status preserved
    }

    @Test
    void updateUser_SameEmailCheck_Success() {
        // Given
        String userId = testUserId.toString();
        Map<String, Object> updates = new HashMap<>();
        updates.put("email", "testuser@example.com"); // Same as current email

        when(userRepository.findByIdWithRoles(testUserId)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmailIgnoreCase("testuser@example.com")).thenReturn(true);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        assertThatCode(() -> userService.updateUser(userId, updates))
                .doesNotThrowAnyException();

        // Then - service checks for duplicate but allows same email for same user
        verify(userRepository).findByIdWithRoles(testUserId);
        verify(userRepository).existsByEmailIgnoreCase("testuser@example.com");
        verify(userRepository).save(any(User.class));
    }
}