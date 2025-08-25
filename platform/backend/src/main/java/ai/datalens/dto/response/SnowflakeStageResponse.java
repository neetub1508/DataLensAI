package ai.datalens.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SnowflakeStageResponse {
    
    @JsonProperty("stage_name")
    private String stageName;
    
    @JsonProperty("stage_schema")
    private String stageSchema;
    
    @JsonProperty("stage_database")
    private String stageDatabase;
    
    @JsonProperty("stage_type")
    private String stageType;
    
    @JsonProperty("stage_location")
    private String stageLocation;
    
    @JsonProperty("file_format")
    private String fileFormat;
    
    @JsonProperty("copy_options")
    private String copyOptions;
    
    private String comment;
    
    @JsonProperty("owner")
    private String owner;
    
    @JsonProperty("created")
    private String created;
    
    // Default constructor
    public SnowflakeStageResponse() {}
    
    // Constructor with all fields
    public SnowflakeStageResponse(String stageName, String stageSchema, String stageDatabase, 
                                 String stageType, String stageLocation, String comment, String owner, String created) {
        this.stageName = stageName;
        this.stageSchema = stageSchema;
        this.stageDatabase = stageDatabase;
        this.stageType = stageType;
        this.stageLocation = stageLocation;
        this.comment = comment;
        this.owner = owner;
        this.created = created;
    }
    
    // Getters and Setters
    
    public String getStageName() {
        return stageName;
    }
    
    public void setStageName(String stageName) {
        this.stageName = stageName;
    }
    
    public String getStageSchema() {
        return stageSchema;
    }
    
    public void setStageSchema(String stageSchema) {
        this.stageSchema = stageSchema;
    }
    
    public String getStageDatabase() {
        return stageDatabase;
    }
    
    public void setStageDatabase(String stageDatabase) {
        this.stageDatabase = stageDatabase;
    }
    
    public String getStageType() {
        return stageType;
    }
    
    public void setStageType(String stageType) {
        this.stageType = stageType;
    }
    
    public String getStageLocation() {
        return stageLocation;
    }
    
    public void setStageLocation(String stageLocation) {
        this.stageLocation = stageLocation;
    }
    
    public String getFileFormat() {
        return fileFormat;
    }
    
    public void setFileFormat(String fileFormat) {
        this.fileFormat = fileFormat;
    }
    
    public String getCopyOptions() {
        return copyOptions;
    }
    
    public void setCopyOptions(String copyOptions) {
        this.copyOptions = copyOptions;
    }
    
    public String getComment() {
        return comment;
    }
    
    public void setComment(String comment) {
        this.comment = comment;
    }
    
    public String getOwner() {
        return owner;
    }
    
    public void setOwner(String owner) {
        this.owner = owner;
    }
    
    public String getCreated() {
        return created;
    }
    
    public void setCreated(String created) {
        this.created = created;
    }
}