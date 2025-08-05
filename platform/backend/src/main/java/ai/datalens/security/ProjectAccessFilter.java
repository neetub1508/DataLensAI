package ai.datalens.security;

import ai.datalens.service.ProjectService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
public class ProjectAccessFilter implements Filter {

    private final ProjectService projectService;

    @Autowired
    public ProjectAccessFilter(ProjectService projectService) {
        this.projectService = projectService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        try {
            // Set user context from authentication
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal) {
                UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
                ProjectContext.setCurrentUser(userPrincipal.getId());
            }

            // Check for project ID in header or path
            String projectIdHeader = httpRequest.getHeader("X-Project-ID");
            String projectIdFromPath = extractProjectIdFromPath(httpRequest.getRequestURI());
            
            String projectIdStr = projectIdHeader != null ? projectIdHeader : projectIdFromPath;
            
            if (projectIdStr != null) {
                try {
                    UUID projectId = UUID.fromString(projectIdStr);
                    UUID userId = ProjectContext.getCurrentUser();
                    
                    // Verify user has access to this project
                    if (userId != null && projectService.hasProjectAccess(userId, projectId)) {
                        ProjectContext.setCurrentProject(projectId);
                    } else if (userId != null) {
                        // User doesn't have access to this project
                        httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        httpResponse.getWriter().write("{\"error\":\"Access denied to project\"}");
                        return;
                    }
                } catch (IllegalArgumentException e) {
                    // Invalid UUID format
                    httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    httpResponse.getWriter().write("{\"error\":\"Invalid project ID format\"}");
                    return;
                }
            }

            chain.doFilter(request, response);
        } finally {
            // Clear context after request
            ProjectContext.clear();
        }
    }

    private String extractProjectIdFromPath(String requestURI) {
        // Extract project ID from paths like /api/projects/{projectId}/...
        if (requestURI.startsWith("/api/projects/")) {
            String[] pathParts = requestURI.split("/");
            if (pathParts.length >= 4) {
                String potentialProjectId = pathParts[3];
                // Check if it's a valid UUID format
                try {
                    UUID.fromString(potentialProjectId);
                    return potentialProjectId;
                } catch (IllegalArgumentException e) {
                    // Not a valid UUID, might be an endpoint like /api/projects/search
                    return null;
                }
            }
        }
        return null;
    }
}