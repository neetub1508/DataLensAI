package ai.datalens.service;

import ai.datalens.dto.response.SnowflakeStageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Service
public class SnowflakeStageService {
    
    private static final Logger logger = LoggerFactory.getLogger(SnowflakeStageService.class);
    
    @Value("${snowflake.url:}")
    private String snowflakeUrl;
    
    @Value("${snowflake.user:}")
    private String snowflakeUser;
    
    @Value("${snowflake.password:}")
    private String snowflakePassword;
    
    @Value("${snowflake.database:}")
    private String snowflakeDatabase;
    
    @Value("${snowflake.schema:}")
    private String snowflakeSchema;
    
    @Value("${snowflake.warehouse:}")
    private String snowflakeWarehouse;
    
    @Value("${snowflake.role:}")
    private String snowflakeRole;
    
    public List<SnowflakeStageResponse> getStagesFromSnowflake() {
        List<SnowflakeStageResponse> stages = new ArrayList<>();
        
        try {
            logger.info("Connecting to Snowflake to fetch stages");
            
            // Build connection properties
            Properties props = buildConnectionProperties();
            String connectionUrl = buildConnectionUrl();
            
            try (Connection connection = DriverManager.getConnection(connectionUrl, props)) {
                
                // Set session parameters
                setSessionParameters(connection);
                
                // Query to get stages from Snowflake
                String query = "SHOW STAGES";
                try (Statement stmt = connection.createStatement();
                     ResultSet rs = stmt.executeQuery(query)) {
                    
                    while (rs.next()) {
                        SnowflakeStageResponse stage = new SnowflakeStageResponse();
                        stage.setStageName(rs.getString("name"));
                        stage.setStageSchema(rs.getString("schema_name"));
                        stage.setStageDatabase(rs.getString("database_name"));
                        stage.setStageType(rs.getString("type"));
                        stage.setStageLocation(rs.getString("url"));
                        stage.setComment(rs.getString("comment"));
                        stage.setOwner(rs.getString("owner"));
                        stage.setCreated(rs.getString("created_on"));
                        
                        stages.add(stage);
                    }
                }
                
            }
            
            logger.info("Successfully fetched {} stages from Snowflake", stages.size());
            
        } catch (Exception e) {
            logger.error("Error connecting to Snowflake: {}", e.getMessage(), e);
            
            // Return mock data for development/testing
            stages = createMockStages();
            logger.info("Returning mock stages for development");
        }
        
        return stages;
    }
    
    private String buildConnectionUrl() {
        if (snowflakeUrl != null && !snowflakeUrl.isEmpty()) {
            return snowflakeUrl;
        } else {
            return "jdbc:snowflake://account.snowflakecomputing.com";
        }
    }
    
    private Properties buildConnectionProperties() {
        Properties props = new Properties();
        
        if (snowflakeUser != null && !snowflakeUser.isEmpty()) {
            props.put("user", snowflakeUser);
        }
        if (snowflakePassword != null && !snowflakePassword.isEmpty()) {
            props.put("password", snowflakePassword);
        }
        if (snowflakeWarehouse != null && !snowflakeWarehouse.isEmpty()) {
            props.put("warehouse", snowflakeWarehouse);
        }
        if (snowflakeDatabase != null && !snowflakeDatabase.isEmpty()) {
            props.put("db", snowflakeDatabase);
        }
        if (snowflakeSchema != null && !snowflakeSchema.isEmpty()) {
            props.put("schema", snowflakeSchema);
        }
        if (snowflakeRole != null && !snowflakeRole.isEmpty()) {
            props.put("role", snowflakeRole);
        }
        
        return props;
    }
    
    private void setSessionParameters(Connection connection) throws SQLException {
        if (snowflakeRole != null && !snowflakeRole.isEmpty()) {
            try (PreparedStatement stmt = connection.prepareStatement("USE ROLE ?")) {
                stmt.setString(1, snowflakeRole);
                stmt.execute();
            }
        }
        
        if (snowflakeWarehouse != null && !snowflakeWarehouse.isEmpty()) {
            try (PreparedStatement stmt = connection.prepareStatement("USE WAREHOUSE ?")) {
                stmt.setString(1, snowflakeWarehouse);
                stmt.execute();
            }
        }
        
        if (snowflakeDatabase != null && !snowflakeDatabase.isEmpty()) {
            try (PreparedStatement stmt = connection.prepareStatement("USE DATABASE ?")) {
                stmt.setString(1, snowflakeDatabase);
                stmt.execute();
            }
        }
        
        if (snowflakeSchema != null && !snowflakeSchema.isEmpty()) {
            try (PreparedStatement stmt = connection.prepareStatement("USE SCHEMA ?")) {
                stmt.setString(1, snowflakeSchema);
                stmt.execute();
            }
        }
    }
    
    private List<SnowflakeStageResponse> createMockStages() {
        List<SnowflakeStageResponse> mockStages = new ArrayList<>();
        
        String[] stageNames = {
            "RAW_DATA_STAGE",
            "PROCESSED_DATA_STAGE", 
            "ANALYTICS_STAGE",
            "EXPORT_STAGE",
            "BACKUP_STAGE",
            "TEMP_STAGE"
        };
        
        String[] stageTypes = {"Internal", "External", "Internal", "External", "Internal", "Internal"};
        String[] locations = {
            "@~/raw_data/",
            "s3://my-bucket/processed/",
            "@~/analytics/",
            "s3://export-bucket/",
            "@~/backups/",
            "@~/temp/"
        };
        
        for (int i = 0; i < stageNames.length; i++) {
            SnowflakeStageResponse stage = new SnowflakeStageResponse();
            stage.setStageName(stageNames[i]);
            stage.setStageSchema(snowflakeSchema != null ? snowflakeSchema : "PUBLIC");
            stage.setStageDatabase(snowflakeDatabase != null ? snowflakeDatabase : "SALES");
            stage.setStageType(stageTypes[i]);
            stage.setStageLocation(locations[i]);
            stage.setComment("Mock stage for development - " + stageNames[i]);
            stage.setOwner("SYSTEM");
            stage.setCreated("2024-01-" + String.format("%02d", i + 1) + " 10:00:00");
            
            mockStages.add(stage);
        }
        
        return mockStages;
    }
    
    public List<SnowflakeStageResponse> refreshStagesFromSnowflake() {
        logger.info("Refreshing stages from Snowflake");
        
        // Get fresh data from Snowflake
        return getStagesFromSnowflake();
    }
}