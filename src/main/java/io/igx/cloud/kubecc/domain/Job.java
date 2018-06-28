package io.igx.cloud.kubecc.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class Job {

    public static final String QUEUED = "queued";
    public static final String RUNNING = "running";
    public static final String COMPLETED = "finished";

    public Job() {
        this.status = QUEUED;
    }

    private String id;
    private String status;
    private Map<String, Object> metadata;
    private String errorMessage;
    private Integer errorCode;
    private String errorDescription;

    @JsonProperty("_id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }
}
