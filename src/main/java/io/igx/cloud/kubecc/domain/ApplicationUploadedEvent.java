package io.igx.cloud.kubecc.domain;

public class ApplicationUploadedEvent {

    private String appId;
    private String jobId;

    public ApplicationUploadedEvent(String appId, String jobId) {
        this.appId = appId;
        this.jobId = jobId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }
}
