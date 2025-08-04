package ai.datalens.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ErrorResponse {
    
    @JsonProperty("detail")
    private String detail;
    
    @JsonProperty("error")
    private String error;
    
    @JsonProperty("message")
    private String message;
    
    @JsonProperty("status")
    private int status;
    
    // Constructors
    public ErrorResponse() {}
    
    public ErrorResponse(String message) {
        this.detail = message;
        this.message = message;
        this.error = "Bad Request";
        this.status = 400;
    }
    
    public ErrorResponse(String error, String message, int status) {
        this.detail = message;
        this.error = error;
        this.message = message;
        this.status = status;
    }
    
    // Getters and Setters
    public String getDetail() {
        return detail;
    }
    
    public void setDetail(String detail) {
        this.detail = detail;
    }
    
    public String getError() {
        return error;
    }
    
    public void setError(String error) {
        this.error = error;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public int getStatus() {
        return status;
    }
    
    public void setStatus(int status) {
        this.status = status;
    }
}