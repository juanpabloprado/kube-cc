package io.igx.cloud.kubecc.domain;

public class ApplicationDeploymentRequest {
    private String id;
    private String name;
    private String image;
    private Integer instances;
    private String space;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getInstances() {
        return instances;
    }

    public void setInstances(Integer instances) {
        this.instances = instances;
    }

    public String getSpace() {
        return space;
    }

    public void setSpace(String space) {
        this.space = space;
    }
}
