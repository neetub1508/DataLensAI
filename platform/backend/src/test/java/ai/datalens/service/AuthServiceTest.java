package ai.datalens.service;

import ai.datalens.constants.RoleNames;
import ai.datalens.constants.UserStatus;
import ai.datalens.dto.request.LoginRequest;
import ai.datalens.dto.request.RegisterRequest;
import ai.datalens.dto.response.AuthResponse;
import ai.datalens.dto.response.UserResponse;
import ai.datalens.entity.Role;
import ai.datalens.entity.User;
import ai.datalens.repository.RoleRepository;
import ai.datalens.repository.UserRepository;
import ai.datalens.security.JwtUtils;
import ai.datalens.security.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    
    @Mock
    private RoleRepository roleRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @Mock
    private JwtUtils jwtUtils;
    
    @Mock
    private AuthenticationManager authenticationManager;
    
    @Mock
    private EmailService emailService;
    
    @InjectMocks
    private AuthService authService;
    
    private User testUser;
    private Role userRole;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        userRole = new Role();
        userRole.setId(UUID.randomUUID());
        userRole.setName(RoleNames.USER);
        
        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setEmail("test@example.com");
        testUser.setPasswordHash("encodedPassword");
        testUser.setStatus(UserStatus.ACTIVE);
        testUser.setIsVerified(true);
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());
        testUser.setRoles(Collections.singleton(userRole));
        
        registerRequest = new RegisterRequest();
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");
        
        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");
    }

    @Test
    void register_Success() {
        // Given
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(roleRepository.findByName(RoleNames.USER)).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(jwtUtils.generateVerificationToken(anyString())).thenReturn("verification-token");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        doNothing().when(emailService).sendVerificationEmail(anyString(), anyString());
        
        // When
        UserResponse response = authService.register(registerRequest);
        
        // Then
        assertThat(response).isNotNull();
        assertThat(response.getEmail()).isEqualTo("test@example.com");
        
        verify(userRepository).existsByEmail("test@example.com");
        verify(roleRepository).findByName(RoleNames.USER);
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
        verify(emailService).sendVerificationEmail(anyString(), anyString());
    }

    @Test
    void register_UserAlreadyExists() {
        // Given
        when(userRepository.existsByEmail(anyString())).thenReturn(true);
        
        // When & Then
        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Email is already in use");
        
        verify(userRepository).existsByEmail("test@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_Success() {
        // Given
        Authentication authentication = mock(Authentication.class);
        UserPrincipal userPrincipal = UserPrincipal.create(testUser);
        
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userRepository.findByIdWithRolesAndPermissions(any(UUID.class))).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtUtils.generateAccessToken(any(UserPrincipal.class))).thenReturn("access-token");
        when(jwtUtils.generateRefreshToken(any(UserPrincipal.class))).thenReturn("refresh-token");
        when(jwtUtils.getAccessTokenExpiration()).thenReturn(86400000L);
        
        // When
        AuthResponse response = authService.login(loginRequest);
        
        // Then
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("access-token");
        assertThat(response.getRefreshToken()).isEqualTo("refresh-token");
        assertThat(response.getUser()).isNotNull();
        assertThat(response.getUser().getEmail()).isEqualTo("test@example.com");
        
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByIdWithRolesAndPermissions(any(UUID.class));
        verify(jwtUtils).generateAccessToken(any(UserPrincipal.class));
        verify(jwtUtils).generateRefreshToken(any(UserPrincipal.class));
    }

    @Test
    void login_InvalidCredentials() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));
        
        // When & Then
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Bad credentials");
        
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository, never()).findByIdWithRolesAndPermissions(any(UUID.class));
    }

    @Test
    void verifyEmail_Success() {
        // Given
        String verificationToken = "valid-token";
        testUser.setIsVerified(false);
        
        when(jwtUtils.validateVerificationToken(verificationToken)).thenReturn(true);
        when(jwtUtils.getEmailFromVerificationToken(verificationToken)).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        
        // When
        authService.verifyEmail(verificationToken);
        
        // Then
        verify(jwtUtils).validateVerificationToken(verificationToken);
        verify(jwtUtils).getEmailFromVerificationToken(verificationToken);
        verify(userRepository).findByEmail("test@example.com");
        verify(userRepository).save(testUser);
        assertThat(testUser.getIsVerified()).isTrue();
        assertThat(testUser.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    void verifyEmail_InvalidToken() {
        // Given
        String invalidToken = "invalid-token";
        when(jwtUtils.validateVerificationToken(invalidToken)).thenReturn(false);
        
        // When & Then
        assertThatThrownBy(() -> authService.verifyEmail(invalidToken))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Invalid or expired verification token");
        
        verify(jwtUtils).validateVerificationToken(invalidToken);
        verify(userRepository, never()).findByEmail(anyString());
    }

    @Test
    void refreshToken_Success() {
        // Given
        String refreshToken = "valid-refresh-token";
        String email = "test@example.com";
        
        when(jwtUtils.validateRefreshToken(refreshToken)).thenReturn(true);
        when(jwtUtils.getEmailFromRefreshToken(refreshToken)).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
        when(jwtUtils.generateAccessToken(any(UserPrincipal.class))).thenReturn("new-access-token");
        when(jwtUtils.generateRefreshToken(any(UserPrincipal.class))).thenReturn("new-refresh-token");
        when(jwtUtils.getAccessTokenExpiration()).thenReturn(86400000L);
        
        // When
        AuthResponse response = authService.refreshToken(refreshToken);
        
        // Then
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("new-access-token");
        assertThat(response.getRefreshToken()).isEqualTo("new-refresh-token");
        
        verify(jwtUtils).validateRefreshToken(refreshToken);
        verify(jwtUtils).getEmailFromRefreshToken(refreshToken);
        verify(userRepository).findByEmail(email);
    }

    @Test
    void refreshToken_InvalidToken() {
        // Given
        String invalidRefreshToken = "invalid-refresh-token";
        when(jwtUtils.validateRefreshToken(invalidRefreshToken)).thenReturn(false);
        
        // When & Then
        assertThatThrownBy(() -> authService.refreshToken(invalidRefreshToken))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Invalid refresh token");
        
        verify(jwtUtils).validateRefreshToken(invalidRefreshToken);
        verify(jwtUtils, never()).getEmailFromRefreshToken(anyString());
    }

    @Test
    void requestPasswordReset_Success() {
        // Given
        String email = "test@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
        when(jwtUtils.generatePasswordResetToken(email)).thenReturn("reset-token");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        doNothing().when(emailService).sendPasswordResetEmail(email, "reset-token");
        
        // When
        authService.requestPasswordReset(email);
        
        // Then
        verify(userRepository).findByEmail(email);
        verify(jwtUtils).generatePasswordResetToken(email);
        verify(userRepository).save(testUser);
        verify(emailService).sendPasswordResetEmail(email, "reset-token");
    }

    @Test
    void resetPassword_Success() {
        // Given
        String resetToken = "valid-reset-token";
        String newPassword = "newPassword123";
        String email = "test@example.com";
        testUser.setPasswordResetTokenExpiresAt(LocalDateTime.now().plusHours(1));
        
        when(jwtUtils.validatePasswordResetToken(resetToken)).thenReturn(true);
        when(jwtUtils.getEmailFromPasswordResetToken(resetToken)).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode(newPassword)).thenReturn("encodedNewPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        
        // When
        authService.resetPassword(resetToken, newPassword);
        
        // Then
        verify(jwtUtils).validatePasswordResetToken(resetToken);
        verify(jwtUtils).getEmailFromPasswordResetToken(resetToken);
        verify(userRepository).findByEmail(email);
        verify(passwordEncoder).encode(newPassword);
        verify(userRepository).save(testUser);
        assertThat(testUser.getPasswordHash()).isEqualTo("encodedNewPassword");
    }
}