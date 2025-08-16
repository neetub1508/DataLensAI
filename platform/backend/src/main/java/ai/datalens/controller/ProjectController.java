package ai.datalens.controller;

import ai.datalens.dto.request.ProjectRequest;
import ai.datalens.dto.response.ProjectResponse;
import ai.datalens.security.UserPrincipal;
import ai.datalens.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/projects")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    /**
     * Create a new project
     */
    @PostMapping
    public ResponseEntity<?> createProject(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody ProjectRequest request) {
        try {
            ProjectResponse project = projectService.createProject(userPrincipal.getId(), request);
            return ResponseEntity.status(HttpStatus.CREATED).body(project);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get all projects for the current user
     */
    @GetMapping
    public ResponseEntity<?> getUserProjects(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {
            List<ProjectResponse> projects = projectService.getUserProjects(userPrincipal.getId());
            return ResponseEntity.ok(projects);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get only active projects for the current user
     */
    @GetMapping("/active")
    public ResponseEntity<?> getActiveUserProjects(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {
            List<ProjectResponse> projects = projectService.getActiveUserProjects(userPrincipal.getId());
            return ResponseEntity.ok(projects);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get recent projects for the current user
     */
    @GetMapping("/recent")
    public ResponseEntity<?> getRecentProjects(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            List<ProjectResponse> projects = projectService.getRecentProjects(userPrincipal.getId(), limit);
            return ResponseEntity.ok(projects);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get a specific project by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getProject(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable UUID id) {
        try {
            ProjectResponse project = projectService.getProject(userPrincipal.getId(), id);
            return ResponseEntity.ok(project);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Update a project
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProject(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable UUID id,
            @Valid @RequestBody ProjectRequest request) {
        try {
            ProjectResponse project = projectService.updateProject(userPrincipal.getId(), id, request);
            return ResponseEntity.ok(project);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Delete a project
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProject(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable UUID id) {
        try {
            projectService.deleteProject(userPrincipal.getId(), id);
            return ResponseEntity.ok(new SuccessResponse("Project deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get project statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<?> getProjectStats(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {
            ProjectService.ProjectStatsResponse stats = projectService.getProjectStats(userPrincipal.getId());
            return ResponseEntity.ok(stats);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Search projects by name
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchProjects(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam String q) {
        try {
            List<ProjectResponse> projects = projectService.searchProjects(userPrincipal.getId(), q);
            return ResponseEntity.ok(projects);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    // Response DTOs
    public static class ErrorResponse {
        private String error;

        public ErrorResponse(String error) {
            this.error = error;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }
    }

    public static class SuccessResponse {
        private String message;

        public SuccessResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}