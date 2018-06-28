package io.igx.cloud.kubecc.services;

import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.ContainerBuilder;
import io.fabric8.kubernetes.api.model.extensions.Deployment;
import io.fabric8.kubernetes.api.model.extensions.DeploymentBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.igx.cloud.kubecc.domain.ApplicationStagedEvent;
import org.cloudfoundry.client.v2.applications.ApplicationEntity;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class KubeDeployerService {

    private KubernetesClient client;
    private ApplicationService applicationService;

    public KubeDeployerService(KubernetesClient client, ApplicationService applicationService) {
        this.client = client;
        this.applicationService = applicationService;
    }


    @EventListener
    public void deployApplication(ApplicationStagedEvent event) {
        ApplicationEntity applicationEntity = applicationService.find(event.getAppId()).getEntity();
        Deployment deployment = new DeploymentBuilder()
                .withNewMetadata()
                    .withName(applicationEntity.getName())
                .endMetadata()
                .withNewSpec()
                    .withReplicas(applicationEntity.getInstances())
                        .withNewTemplate()
                            .withNewMetadata()
                                .addToLabels("app", applicationEntity.getName())
                            .endMetadata()
                .withNewSpec()
                    .addToContainers(createContainer(applicationEntity))
                .endSpec()
                .endTemplate()
                .endSpec()
                .build();
        deployment.toString();
    }


    private Container createContainer(ApplicationEntity entity) {
        return new ContainerBuilder()
                .withName(entity.getName())
                .withImage(entity.getName())
                .addNewPort()
                .withContainerPort(8080)
                .endPort()
                .build();
    }
}
