package ai.datalens.service;

import ai.datalens.dto.response.UserResponse;
import ai.datalens.entity.Role;
import ai.datalens.entity.User;
import ai.datalens.repository.UserRepository;
import ai.datalens.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    public UserResponse getCurrentUser(UUID userId) {
        User user = userRepository.findByIdWithRolesAndPermissions(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return convertToUserResponse(user);
    }

    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAllWithRoles();
        return users.stream()
                .map(this::convertToUserResponse)
                .collect(Collectors.toList());
    }

    public void updateUserStatus(String userId, String status) {
        UUID userUuid = UUID.fromString(userId);
        User user = userRepository.findById(userUuid)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setStatus(status);
        userRepository.save(user);
    }

    public void updateUserLocale(UUID userId, String locale) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Basic validation for locale format (2-letter language code)
        if (locale.length() < 2 || locale.length() > 10) {
            throw new RuntimeException("Invalid locale format");
        }
        
        user.setLocale(locale);
        userRepository.save(user);
    }

    public Map<String, Object> getUsersPaginated(int page, int limit, String search) {
        Pageable pageable = PageRequest.of(page, limit);
        Page<User> userPage;
        
        if (search != null && !search.trim().isEmpty()) {
            userPage = userRepository.findByEmailContainingIgnoreCase(search.trim(), pageable);
        } else {
            userPage = userRepository.findAll(pageable);
        }
        
        // Load roles for each user since pagination might not include them
        List<UserResponse> users = userPage.getContent().stream()
                .map(user -> {
                    // Fetch user with roles loaded
                    User userWithRoles = userRepository.findByIdWithRoles(user.getId())
                            .orElse(user);
                    return convertToUserResponse(userWithRoles);
                })
                .collect(Collectors.toList());
        
        Map<String, Object> result = new HashMap<>();
        result.put("users", users);
        result.put("total", userPage.getTotalElements());
        result.put("totalPages", userPage.getTotalPages());
        result.put("currentPage", page + 1);
        
        return result;
    }

    public void updateUser(String userId, Map<String, Object> updates) {
        UUID userUuid = UUID.fromString(userId);
        User user = userRepository.findByIdWithRoles(userUuid)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (updates.containsKey("email")) {
            String email = (String) updates.get("email");
            if (email == null || email.trim().isEmpty()) {
                throw new RuntimeException("Email cannot be empty");
            }
            // Check if email already exists for another user
            if (userRepository.existsByEmailIgnoreCase(email.trim()) && 
                !user.getEmail().equalsIgnoreCase(email.trim())) {
                throw new RuntimeException("Email already exists");
            }
            user.setEmail(email.trim());
        }
        
        if (updates.containsKey("status")) {
            String status = (String) updates.get("status");
            if (status != null && (status.equals("ACTIVE") || status.equals("INACTIVE"))) {
                user.setStatus(status);
            }
        }
        
        if (updates.containsKey("locale")) {
            String locale = (String) updates.get("locale");
            if (locale != null && locale.length() >= 2 && locale.length() <= 10) {
                user.setLocale(locale);
            }
        }
        
        if (updates.containsKey("roles")) {
            @SuppressWarnings("unchecked")
            List<String> roleNames = (List<String>) updates.get("roles");
            if (roleNames != null) {
                Set<Role> newRoles = new HashSet<>();
                for (String roleName : roleNames) {
                    Role role = roleRepository.findByName(roleName)
                            .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
                    newRoles.add(role);
                }
                user.setRoles(newRoles);
            }
        }
        
        userRepository.save(user);
    }

    public void deleteUser(String userId) {
        UUID userUuid = UUID.fromString(userId);
        User user = userRepository.findById(userUuid)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Prevent deletion of admin users (optional safety check)
        if (user.getRoles().stream().anyMatch(role -> role.getName().equals("admin"))) {
            throw new RuntimeException("Cannot delete admin users");
        }
        
        userRepository.delete(user);
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