package ai.datalens.controller;

import ai.datalens.dto.request.ProjectRequest;
import ai.datalens.dto.response.ProjectResponse;
import ai.datalens.security.UserPrincipal;
import ai.datalens.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/projects")
@PreAuthorize("hasRole('USER')")
public class ProjectController {

    private final ProjectService projectService;

    @Autowired
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    public ResponseEntity<List<ProjectResponse>> getUserProjects(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<ProjectResponse> projects = projectService.getUserProjects(userPrincipal.getId());
        return ResponseEntity.ok(projects);
    }

    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody ProjectRequest request) {
        try {
            ProjectResponse project = projectService.createProject(userPrincipal.getId(), request);
            return ResponseEntity.status(HttpStatus.CREATED).body(project);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectResponse> getProject(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable UUID projectId) {
        try {
            ProjectResponse project = projectService.getProject(userPrincipal.getId(), projectId);
            return ResponseEntity.ok(project);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{projectId}")
    public ResponseEntity<ProjectResponse> updateProject(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable UUID projectId,
            @Valid @RequestBody ProjectRequest request) {
        try {
            ProjectResponse project = projectService.updateProject(userPrincipal.getId(), projectId, request);
            return ResponseEntity.ok(project);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<Void> deleteProject(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable UUID projectId) {
        try {
            projectService.deleteProject(userPrincipal.getId(), projectId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{projectId}/archive")
    public ResponseEntity<Void> archiveProject(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable UUID projectId) {
        try {
            projectService.archiveProject(userPrincipal.getId(), projectId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{projectId}/restore")
    public ResponseEntity<Void> restoreProject(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable UUID projectId) {
        try {
            projectService.restoreProject(userPrincipal.getId(), projectId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{projectId}/members/{memberId}")
    public ResponseEntity<Void> addMemberToProject(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable UUID projectId,
            @PathVariable UUID memberId) {
        try {
            projectService.addMemberToProject(userPrincipal.getId(), projectId, memberId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{projectId}/members/{memberId}")
    public ResponseEntity<Void> removeMemberFromProject(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable UUID projectId,
            @PathVariable UUID memberId) {
        try {
            projectService.removeMemberFromProject(userPrincipal.getId(), projectId, memberId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProjectResponse>> searchProjects(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam String q) {
        List<ProjectResponse> projects = projectService.searchUserProjects(userPrincipal.getId(), q);
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getUserActiveProjectCount(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        long count = projectService.getUserActiveProjectCount(userPrincipal.getId());
        return ResponseEntity.ok(count);
    }

    @GetMapping("/{projectId}/access")
    public ResponseEntity<Boolean> checkProjectAccess(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable UUID projectId) {
        boolean hasAccess = projectService.hasProjectAccess(userPrincipal.getId(), projectId);
        return ResponseEntity.ok(hasAccess);
    }
}