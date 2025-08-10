package ai.datalens.repository;

import ai.datalens.constants.UserStatus;
import ai.datalens.entity.Role;
import ai.datalens.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private User testUser;
    private Role userRole;

    @BeforeEach
    void setUp() {
        // Create and persist a role
        userRole = new Role();
        userRole.setId(UUID.randomUUID());
        userRole.setName("USER");
        userRole.setDescription("Standard user role");
        userRole.setCreatedAt(LocalDateTime.now());
        userRole.setUpdatedAt(LocalDateTime.now());
        entityManager.persistAndFlush(userRole);

        // Create a test user
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
    void shouldSaveAndFindUserById() {
        // When
        User savedUser = userRepository.save(testUser);
        entityManager.flush();
        
        Optional<User> foundUser = userRepository.findById(savedUser.getId());

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo("test@example.com");
        assertThat(foundUser.get().getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(foundUser.get().getIsVerified()).isTrue();
    }

    @Test
    void shouldFindUserByEmail() {
        // Given
        userRepository.save(testUser);
        entityManager.flush();

        // When
        Optional<User> foundUser = userRepository.findByEmail("test@example.com");

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo("test@example.com");
        assertThat(foundUser.get().getPasswordHash()).isEqualTo("hashedPassword");
    }

    @Test
    void shouldReturnEmptyWhenUserNotFoundByEmail() {
        // When
        Optional<User> foundUser = userRepository.findByEmail("nonexistent@example.com");

        // Then
        assertThat(foundUser).isEmpty();
    }

    @Test
    void shouldCheckIfUserExistsByEmail() {
        // Given
        userRepository.save(testUser);
        entityManager.flush();

        // When & Then
        assertThat(userRepository.existsByEmail("test@example.com")).isTrue();
        assertThat(userRepository.existsByEmail("nonexistent@example.com")).isFalse();
    }

    @Test
    void shouldUpdateUser() {
        // Given
        User savedUser = userRepository.save(testUser);
        entityManager.flush();

        // When
        savedUser.setStatus(UserStatus.INACTIVE);
        savedUser.setUpdatedAt(LocalDateTime.now());
        User updatedUser = userRepository.save(savedUser);
        entityManager.flush();

        // Then
        Optional<User> foundUser = userRepository.findById(updatedUser.getId());
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getStatus()).isEqualTo(UserStatus.INACTIVE);
    }

    @Test
    void shouldDeleteUser() {
        // Given
        User savedUser = userRepository.save(testUser);
        entityManager.flush();
        UUID userId = savedUser.getId();

        // When
        userRepository.delete(savedUser);
        entityManager.flush();

        // Then
        Optional<User> foundUser = userRepository.findById(userId);
        assertThat(foundUser).isEmpty();
    }

    @Test
    void shouldFindUserWithRoles() {
        // Given
        userRepository.save(testUser);
        entityManager.flush();

        // When
        Optional<User> foundUser = userRepository.findByEmail("test@example.com");

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getRoles()).hasSize(1);
        assertThat(foundUser.get().getRoles().iterator().next().getName()).isEqualTo("USER");
    }

    @Test
    void shouldHandleUserVerification() {
        // Given
        testUser.setIsVerified(false);
        testUser.setVerificationToken("verification-token-123");
        User savedUser = userRepository.save(testUser);
        entityManager.flush();

        // When
        savedUser.setIsVerified(true);
        savedUser.setVerificationToken(null);
        savedUser.setStatus(UserStatus.ACTIVE);
        User verifiedUser = userRepository.save(savedUser);
        entityManager.flush();

        // Then
        Optional<User> foundUser = userRepository.findById(verifiedUser.getId());
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getIsVerified()).isTrue();
        assertThat(foundUser.get().getVerificationToken()).isNull();
        assertThat(foundUser.get().getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    void shouldHandlePasswordReset() {
        // Given
        User savedUser = userRepository.save(testUser);
        entityManager.flush();

        // When
        savedUser.setPasswordResetToken("reset-token-123");
        savedUser.setPasswordResetTokenExpiresAt(LocalDateTime.now().plusHours(1));
        User userWithResetToken = userRepository.save(savedUser);
        entityManager.flush();

        // Then
        Optional<User> foundUser = userRepository.findById(userWithResetToken.getId());
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getPasswordResetToken()).isEqualTo("reset-token-123");
        assertThat(foundUser.get().getPasswordResetTokenExpiresAt()).isAfter(LocalDateTime.now());
    }

    @Test
    void shouldUpdateLastLogin() {
        // Given
        User savedUser = userRepository.save(testUser);
        entityManager.flush();

        // When
        LocalDateTime loginTime = LocalDateTime.now();
        savedUser.setLastLoginAt(loginTime);
        User userWithLogin = userRepository.save(savedUser);
        entityManager.flush();

        // Then
        Optional<User> foundUser = userRepository.findById(userWithLogin.getId());
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getLastLoginAt()).isEqualTo(loginTime);
    }
}