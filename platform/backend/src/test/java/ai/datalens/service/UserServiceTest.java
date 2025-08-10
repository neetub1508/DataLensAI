package ai.datalens.service;

import ai.datalens.constants.UserStatus;
import ai.datalens.dto.response.UserResponse;
import ai.datalens.entity.Role;
import ai.datalens.entity.User;
import ai.datalens.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private Role userRole;

    @BeforeEach
    void setUp() {
        userRole = new Role();
        userRole.setId(UUID.randomUUID());
        userRole.setName("USER");

        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setEmail("test@example.com");
        testUser.setPasswordHash("hashedPassword");
        testUser.setStatus(UserStatus.ACTIVE);
        testUser.setIsVerified(true);
        testUser.setLocale("en");
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());
        testUser.setRoles(Collections.singleton(userRole));
    }

    @Test
    void findById_Success() {
        // Given
        UUID userId = testUser.getId();
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        // When
        User foundUser = userRepository.findById(userId).orElse(null);

        // Then
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getId()).isEqualTo(userId);
        assertThat(foundUser.getEmail()).isEqualTo("test@example.com");
        assertThat(foundUser.getStatus()).isEqualTo(UserStatus.ACTIVE);

        verify(userRepository).findById(userId);
    }

    @Test
    void findById_UserNotFound() {
        // Given
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When
        Optional<User> result = userRepository.findById(userId);

        // Then
        assertThat(result).isEmpty();
        verify(userRepository).findById(userId);
    }

    @Test
    void findByEmail_Success() {
        // Given
        String email = "test@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));

        // When
        Optional<User> result = userRepository.findByEmail(email);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo(email);

        verify(userRepository).findByEmail(email);
    }

    @Test
    void findByEmail_UserNotFound() {
        // Given
        String email = "nonexistent@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When
        Optional<User> result = userRepository.findByEmail(email);

        // Then
        assertThat(result).isEmpty();
        verify(userRepository).findByEmail(email);
    }

    @Test
    void saveUser_Success() {
        // Given
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        User savedUser = userRepository.save(testUser);

        // Then
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo("test@example.com");
        verify(userRepository).save(testUser);
    }

    @Test
    void deleteUser_Success() {
        // Given
        UUID userId = testUser.getId();

        // When
        userRepository.delete(testUser);

        // Then
        verify(userRepository).delete(testUser);
    }

    @Test
    void existsByEmail_Success() {
        // Given
        String email = "test@example.com";
        when(userRepository.existsByEmail(email)).thenReturn(true);

        // When
        boolean exists = userRepository.existsByEmail(email);

        // Then
        assertThat(exists).isTrue();
        verify(userRepository).existsByEmail(email);
    }

    @Test
    void existsByEmail_NotFound() {
        // Given
        String email = "nonexistent@example.com";
        when(userRepository.existsByEmail(email)).thenReturn(false);

        // When
        boolean exists = userRepository.existsByEmail(email);

        // Then
        assertThat(exists).isFalse();
        verify(userRepository).existsByEmail(email);
    }

    @Test
    void updateUserStatus_Success() {
        // Given
        testUser.setStatus(UserStatus.INACTIVE);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        User updatedUser = userRepository.save(testUser);

        // Then
        assertThat(updatedUser.getStatus()).isEqualTo(UserStatus.INACTIVE);
        verify(userRepository).save(testUser);
    }

    @Test
    void updateUserVerificationStatus_Success() {
        // Given
        testUser.setIsVerified(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        testUser.setIsVerified(true);
        testUser.setStatus(UserStatus.ACTIVE);
        User updatedUser = userRepository.save(testUser);

        // Then
        assertThat(updatedUser.getIsVerified()).isTrue();
        assertThat(updatedUser.getStatus()).isEqualTo(UserStatus.ACTIVE);
        verify(userRepository).save(testUser);
    }
}