package io.igx.cloud.kubecc.services;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import io.igx.cloud.kubecc.domain.ApplicationDeploymentRequest;
import io.igx.cloud.kubecc.domain.ApplicationStagedEvent;
import org.cloudfoundry.client.v2.applications.ApplicationEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class DeployerService {

    private final ApplicationService applicationService;
    private final SpaceService spaceService;
    private final RestTemplate deployerClient;
    @Value("${deployer.endpoint}")
    private String deployerEndpoint;
    private Logger logger = LoggerFactory.getLogger(DeployerService.class);

    public DeployerService(ApplicationService applicationService, SpaceService spaceService) {
        this.applicationService = applicationService;
        this.spaceService = spaceService;
        this.deployerClient = new RestTemplate();
    }

    @EventListener
    public void onEvent(ApplicationStagedEvent event){
        logger.info("Processing deployment for appId {}", event.getAppId());
        ApplicationEntity applicationEntity = applicationService.find(event.getAppId()).getEntity();
        String space = spaceService.find(Filters.eq("_id", applicationEntity.getSpaceId()), Sorts.ascending("name")).getResources().get(0).getEntity().getName();
        ApplicationDeploymentRequest applicationDeploymentRequest = new ApplicationDeploymentRequest();
        applicationDeploymentRequest.setId(event.getAppId());
        applicationDeploymentRequest.setName(applicationEntity.getName());
        applicationDeploymentRequest.setInstances(applicationEntity.getInstances());
        applicationDeploymentRequest.setSpace(space);
        applicationDeploymentRequest.setImage(event.getImage());
        logger.info("Application deployment request {}", applicationDeploymentRequest);
        //deployerClient.postForObject(deployerEndpoint, applicationDeploymentRequest, String.class);
        Map<String, Object> fields = new HashMap<>();
        fields.put("command", "java -jar app.jar");
        fields.put("detected_buildpack", "hack_builpack");
        fields.put("detected_buildpack_guid", "12345");
        fields.put("detected_start_command", "java -jar app.jar");
        applicationService.update(Filters.eq("_id", event.getAppId()), fields);
    }
}
