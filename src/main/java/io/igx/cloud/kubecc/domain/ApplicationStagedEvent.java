package io.igx.cloud.kubecc.domain;

public class ApplicationStagedEvent {

    private String appId;

    public ApplicationStagedEvent(String appId) {
        this.appId = appId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }
}
