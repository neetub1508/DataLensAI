package ai.datalens.service;

import ai.datalens.dto.response.UserResponse;
import ai.datalens.entity.Role;
import ai.datalens.entity.User;
import ai.datalens.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public UserResponse getCurrentUser(UUID userId) {
        User user = userRepository.findByIdWithRolesAndPermissions(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return convertToUserResponse(user);
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