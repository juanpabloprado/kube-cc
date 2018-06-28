package io.igx.cloud.kubecc.services;

import io.igx.cloud.kubecc.domain.ApplicationStagedEvent;
import io.igx.cloud.kubecc.domain.ApplicationUploadedEvent;
import io.igx.cloud.kubecc.domain.Job;
import org.cloudfoundry.client.v2.applications.ApplicationEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class DockerStagerService {

    private Logger logger = LoggerFactory.getLogger(DockerStagerService.class);
    private final ApplicationService applicationService;
    private final JobService jobService;
    private final ApplicationEventPublisher publisher;

    public DockerStagerService(ApplicationService applicationService, JobService jobService, ApplicationEventPublisher publisher) {
        this.applicationService = applicationService;
        this.jobService = jobService;
        this.publisher = publisher;
    }

    @EventListener
    public void onApplicationUploaded(ApplicationUploadedEvent event){
        logger.info("Staging application " + event.getAppId());
        ApplicationEntity applicationEntity = applicationService.find(event.getAppId()).getEntity();
        Map<String,Object> fields = new HashMap<>();
        fields.put("status", Job.RUNNING);
        jobService.update(event.getJobId(), fields);
        publisher.publishEvent(new ApplicationStagedEvent(event.getAppId(), "viniciusccarvalho/boot-docker"));

    }




}
