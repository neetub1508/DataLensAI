package ai.datalens.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark entities or methods that should be scoped to a specific project.
 * When applied to entities, it ensures that all database operations are filtered by project.
 * When applied to methods, it validates that the current user has access to the specified project.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ProjectScoped {
    /**
     * Whether project access is required for this operation.
     * If true, the operation will fail if no valid project context is set.
     */
    boolean required() default true;
    
    /**
     * Whether the user must be the project owner for this operation.
     * If true, the operation will fail if the user is not the project owner.
     */
    boolean ownerRequired() default false;
}