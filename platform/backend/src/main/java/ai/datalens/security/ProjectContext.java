package ai.datalens.security;

import java.util.UUID;

public class ProjectContext {
    private static final ThreadLocal<UUID> currentProjectId = new ThreadLocal<>();
    private static final ThreadLocal<UUID> currentUserId = new ThreadLocal<>();

    public static void setCurrentProject(UUID projectId) {
        currentProjectId.set(projectId);
    }

    public static UUID getCurrentProject() {
        return currentProjectId.get();
    }

    public static void setCurrentUser(UUID userId) {
        currentUserId.set(userId);
    }

    public static UUID getCurrentUser() {
        return currentUserId.get();
    }

    public static void clear() {
        currentProjectId.remove();
        currentUserId.remove();
    }

    public static boolean hasProjectContext() {
        return currentProjectId.get() != null;
    }

    public static boolean hasUserContext() {
        return currentUserId.get() != null;
    }
}