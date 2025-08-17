package ai.datalens.controller;

import ai.datalens.dto.response.ProjectResponse;
import ai.datalens.dto.response.UserResponse;
import ai.datalens.entity.Project;
import ai.datalens.entity.User;
import ai.datalens.security.UserPrincipal;
import ai.datalens.service.ProjectService;
import ai.datalens.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private ProjectService projectService;

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {
            List<UserResponse> users = userService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/users/paginated")
    public ResponseEntity<Map<String, Object>> getUsersPaginated(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String search,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {
            Map<String, Object> result = userService.getUsersPaginated(page - 1, limit, search);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/users/{userId}")
    public ResponseEntity<?> updateUser(
            @PathVariable String userId,
            @RequestBody Map<String, Object> request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {
            userService.updateUser(userId, request);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<?> deleteUser(
            @PathVariable String userId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {
            userService.deleteUser(userId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/projects")
    public ResponseEntity<List<ProjectResponse>> getAllProjects(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {
            List<ProjectResponse> projects = projectService.getAllProjects();
            return ResponseEntity.ok(projects);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/users/{userId}/status")
    public ResponseEntity<?> updateUserStatus(
            @PathVariable String userId,
            @RequestBody Map<String, String> request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {
            String status = request.get("status");
            if (status == null || (!status.equals("ACTIVE") && !status.equals("INACTIVE"))) {
                return ResponseEntity.badRequest().body("Invalid status. Must be ACTIVE or INACTIVE");
            }

            userService.updateUserStatus(userId, status);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/projects/{projectId}/status")
    public ResponseEntity<?> updateProjectStatus(
            @PathVariable String projectId,
            @RequestBody Map<String, Boolean> request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {
            Boolean isActive = request.get("isActive");
            if (isActive == null) {
                return ResponseEntity.badRequest().body("isActive field is required");
            }

            projectService.updateProjectStatus(projectId, isActive, userPrincipal.getId());
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}