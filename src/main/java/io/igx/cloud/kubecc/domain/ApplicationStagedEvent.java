package io.igx.cloud.kubecc.domain;

public class ApplicationStagedEvent {

    private final String appId;
    private final String image;

    public String getImage() {
        return image;
    }



    public ApplicationStagedEvent(String appId, String image) {
        this.appId = appId;
        this.image = image;
    }

    public String getAppId() {
        return appId;
    }


}
