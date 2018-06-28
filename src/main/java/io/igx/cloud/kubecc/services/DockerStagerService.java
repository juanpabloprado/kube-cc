package io.igx.cloud.kubecc.services;

import io.igx.cloud.kubecc.domain.ApplicationStagedEvent;
import io.igx.cloud.kubecc.domain.ApplicationUploadedEvent;
import io.igx.cloud.kubecc.domain.Job;
import org.apache.commons.io.IOUtils;
import org.cloudfoundry.client.v2.applications.ApplicationEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

@Service
public class DockerStagerService {

    private Logger logger = LoggerFactory.getLogger(DockerStagerService.class);
    private final ApplicationService applicationService;
    private final JobService jobService;
    private final ApplicationEventPublisher publisher;
    @Value("${app.staging.dir:/tmp}")
    private String baseFolder;

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
        File dockerFile = new File(baseFolder+"/"+event.getAppId()+"/Dockerfile");
        try {
            IOUtils.copy(DockerStagerService.class.getClassLoader().getResourceAsStream("Dockerfile.template"), new FileOutputStream(dockerFile));
            dockerBuild(applicationEntity.getName(), baseFolder+"/"+event.getAppId());
            fields.put("status", Job.COMPLETED);
            jobService.update(event.getJobId(), fields);
            publisher.publishEvent(new ApplicationStagedEvent(event.getAppId()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void dockerBuild(String name, String directory){
        String[] args = new String[]{"docker", "build", "-t", name, "."};
        Process build = null;
        try {
            build = new ProcessBuilder(args).directory(new File(directory)).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(build.getInputStream()))) {
            String line = null;
            while((line = reader.readLine()) != null){
               logger.info(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
