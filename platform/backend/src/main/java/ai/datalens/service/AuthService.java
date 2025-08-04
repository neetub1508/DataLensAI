package ai.datalens.service;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private EmailService emailService;

    public UserResponse register(RegisterRequest registerRequest) {
        // Check if user already exists
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Email is already in use");
        }

        // Create new user
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail(registerRequest.getEmail());
        user.setPasswordHash(passwordEncoder.encode(registerRequest.getPassword()));
        user.setLocale(registerRequest.getLocale() != null ? registerRequest.getLocale() : "en");
        user.setStatus("PENDING_VERIFICATION");
        user.setIsVerified(false);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        // Assign default user role
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("Default user role not found"));
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        user.setRoles(roles);

        // Generate verification token
        String verificationToken = jwtUtils.generateVerificationToken(user.getEmail());
        user.setVerificationToken(verificationToken);

        User savedUser = userRepository.save(user);

        // Send verification email
        emailService.sendVerificationEmail(savedUser.getEmail(), verificationToken);

        return convertToUserResponse(savedUser);
    }

    public AuthResponse login(LoginRequest loginRequest) {
        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        // Update last login
        User user = userRepository.findByIdWithRolesAndPermissions(userPrincipal.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setLastLoginAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        // Generate tokens
        String accessToken = jwtUtils.generateAccessToken(userPrincipal);
        String refreshToken = jwtUtils.generateRefreshToken(userPrincipal);
        Long expiresIn = jwtUtils.getAccessTokenExpiration();

        UserResponse userResponse = convertToUserResponse(user);

        return new AuthResponse(accessToken, refreshToken, expiresIn, userResponse);
    }

    public AuthResponse refreshToken(String refreshToken) {
        if (!jwtUtils.validateRefreshToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }

        String email = jwtUtils.getEmailFromRefreshToken(refreshToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserPrincipal userPrincipal = UserPrincipal.create(user);

        // Generate new tokens
        String newAccessToken = jwtUtils.generateAccessToken(userPrincipal);
        String newRefreshToken = jwtUtils.generateRefreshToken(userPrincipal);
        Long expiresIn = jwtUtils.getAccessTokenExpiration();

        UserResponse userResponse = convertToUserResponse(user);

        return new AuthResponse(newAccessToken, newRefreshToken, expiresIn, userResponse);
    }

    public void verifyEmail(String token) {
        if (!jwtUtils.validateVerificationToken(token)) {
            throw new RuntimeException("Invalid or expired verification token");
        }

        String email = jwtUtils.getEmailFromVerificationToken(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getIsVerified()) {
            throw new RuntimeException("Email is already verified");
        }

        user.setIsVerified(true);
        user.setStatus("ACTIVE");
        user.setVerificationToken(null);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    public void requestPasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String resetToken = jwtUtils.generatePasswordResetToken(email);
        user.setPasswordResetToken(resetToken);
        user.setPasswordResetTokenExpiresAt(LocalDateTime.now().plusHours(1));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        emailService.sendPasswordResetEmail(email, resetToken);
    }

    public void resetPassword(String token, String newPassword) {
        if (!jwtUtils.validatePasswordResetToken(token)) {
            throw new RuntimeException("Invalid or expired reset token");
        }

        String email = jwtUtils.getEmailFromPasswordResetToken(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getPasswordResetTokenExpiresAt() == null || 
            user.getPasswordResetTokenExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Reset token has expired");
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setPasswordResetToken(null);
        user.setPasswordResetTokenExpiresAt(null);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    private UserResponse convertToUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setIsVerified(user.getIsVerified());
        response.setStatus(user.getStatus());
        response.setLocale(user.getLocale());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        response.setLastLoginAt(user.getLastLoginAt());
        
        // Convert roles to string set
        Set<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
        response.setRoles(roles);
        
        return response;
    }
}