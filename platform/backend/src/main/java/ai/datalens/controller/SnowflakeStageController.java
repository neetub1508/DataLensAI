package ai.datalens.controller;

import ai.datalens.dto.response.SnowflakeStageResponse;
import ai.datalens.service.SnowflakeStageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projects/{projectId}/stages")
@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
public class SnowflakeStageController {
    
    private static final Logger logger = LoggerFactory.getLogger(SnowflakeStageController.class);
    
    @Autowired
    private SnowflakeStageService snowflakeStageService;
    
    @GetMapping
    public ResponseEntity<List<SnowflakeStageResponse>> getProjectStages(@PathVariable String projectId) {
        try {
            logger.info("Fetching stages from Snowflake for project: {}", projectId);
            
            // Get fresh data from Snowflake each time
            List<SnowflakeStageResponse> stages = snowflakeStageService.getStagesFromSnowflake();
            
            logger.info("Successfully retrieved {} stages for project: {}", stages.size(), projectId);
            return ResponseEntity.ok(stages);
            
        } catch (Exception e) {
            logger.error("Error fetching stages for project {}: {}", projectId, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/active")
    public ResponseEntity<List<SnowflakeStageResponse>> getActiveProjectStages(@PathVariable String projectId) {
        try {
            logger.info("Fetching stages from Snowflake for project: {}", projectId);
            
            // Get all stages from Snowflake (no filtering needed as we get live data)
            List<SnowflakeStageResponse> stages = snowflakeStageService.getStagesFromSnowflake();
            
            logger.info("Successfully retrieved {} stages for project: {}", stages.size(), projectId);
            return ResponseEntity.ok(stages);
            
        } catch (Exception e) {
            logger.error("Error fetching stages for project {}: {}", projectId, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<List<SnowflakeStageResponse>> refreshProjectStages(@PathVariable String projectId) {
        try {
            logger.info("Refreshing stages from Snowflake for project: {}", projectId);
            
            List<SnowflakeStageResponse> stages = snowflakeStageService.refreshStagesFromSnowflake();
            
            logger.info("Successfully refreshed {} stages for project: {}", stages.size(), projectId);
            return ResponseEntity.ok(stages);
            
        } catch (Exception e) {
            logger.error("Error refreshing stages for project {}: {}", projectId, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/count")
    public ResponseEntity<Long> getActiveStagesCount(@PathVariable String projectId) {
        try {
            logger.info("Counting stages from Snowflake for project: {}", projectId);
            
            List<SnowflakeStageResponse> stages = snowflakeStageService.getStagesFromSnowflake();
            long count = stages.size();
            
            logger.info("Found {} stages for project: {}", count, projectId);
            return ResponseEntity.ok(count);
            
        } catch (Exception e) {
            logger.error("Error counting stages for project {}: {}", projectId, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}