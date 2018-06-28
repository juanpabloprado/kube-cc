package io.igx.cloud.kubecc.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import io.igx.cloud.kubecc.domain.ApplicationUploadedEvent;
import io.igx.cloud.kubecc.domain.Job;
import io.igx.cloud.kubecc.services.ApplicationService;
import io.igx.cloud.kubecc.services.JobService;
import io.igx.cloud.kubecc.services.S3StorageService;
import org.cloudfoundry.client.v2.Metadata;
import org.cloudfoundry.client.v2.applications.*;
import org.cloudfoundry.client.v2.jobs.JobEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
public class AppsController extends V2Controller{

    private ApplicationService service;
    private JobService jobService;
    private ObjectMapper mapper = new ObjectMapper();
    private final ApplicationEventPublisher publisher;

    private S3StorageService storageService;

    @Value("${app.staging.dir:/tmp}")
    private String baseFolder;

    public AppsController(ApplicationService service, JobService jobService, ApplicationEventPublisher publisher, S3StorageService storageService) {
        this.service = service;
        this.jobService = jobService;
        this.publisher = publisher;
        this.storageService = storageService;
    }

    @PostMapping("/apps")
    public CreateApplicationResponse create(@RequestBody CreateApplicationRequest request){
        return service.create(request);
    }

    @PutMapping("/apps/{id}")
    public UpdateApplicationResponse update(@PathVariable("id") String id, @RequestBody Map<String, Object> request){
        return service.update(Filters.eq("_id", id), request);
    }

    @GetMapping("/apps/{id}")
    public GetApplicationResponse find(@PathVariable("id") String id){
        return service.find(id);
    }

    @GetMapping("/apps/{id}/stats")
    public ApplicationStatisticsResponse stats(@PathVariable("id") String id){
        ApplicationEntity entity = service.find(id).getEntity();
        return ApplicationStatisticsResponse.builder()
                .instance("0", InstanceStatistics.builder()
                        .state("RUNNING")
                        .statistics(Statistics.builder()
                                .name(entity.getName())
                                .host("10.0.0.1")
                                .usage(Usage.builder()
                                        .cpu(0.5)
                                        .disk(1000L)
                                        .memory(512L)
                                        .build())
                                .port(6000)
                                .memoryQuota(1024L)
                                .diskQuota(1024L)
                                .uptime(600L)
                                .fdsQuota(16384)
                                .build())
                        .build())
                .build();
    }

    @GetMapping("/apps/{id}/instances")
    public ApplicationInstancesResponse instances(@PathVariable("id") String id){
        return ApplicationInstancesResponse.builder()
                .instance("0", ApplicationInstanceInfo.builder()
                        .state("RUNNING")
                        .uptime(600L)
                        .since(new Double(System.currentTimeMillis()))
                        .build())
                .build();
    }


    @PutMapping("/apps/{id}/routes/{rid}")
    public AssociateApplicationRouteResponse addRoute(@PathVariable("id") String id, @PathVariable("rid") String rid){
        service.addRoute(id, rid);
        ListApplicationsResponse applicationsResponse = service.find(Filters.eq("_id", id), Sorts.ascending("name"));
        return AssociateApplicationRouteResponse.builder()
                .metadata(Metadata.builder()
                        .id(id)
                        .url("/v2/apps/"+id)
                        .build())
                .entity(applicationsResponse.getResources().get(0).getEntity())
                .build();
    }

    @PutMapping(value = "/apps/{id}/bits")
    public ResponseEntity<UploadApplicationResponse> upload(@RequestParam("application") MultipartFile file, @RequestParam("resources") String resources, @PathVariable("id") String id){
        Job job = null;
        try {
            Integer total = 0;
            List<Resource> resourceList = mapper.readValue(resources, new TypeReference<List<Resource>>() {});
            for(Resource r : resourceList){
                total += r.getSize();
            }
            logger.info("Received {} bytes", total);
            new File(baseFolder+"/"+id).mkdirs();
            String fileName = "application.zip";
            String destinationFile = baseFolder+"/"+id+"/"+fileName;
            File file1 = new File(destinationFile);
            file.transferTo(file1);
            storageService.s3Store(id, fileName, file1);
            Map<String, Object> jobMetadata = new HashMap<>();
            jobMetadata.put("appId", id);
            jobMetadata.put("fileLocation", destinationFile);
            job = jobService.create(jobMetadata);
        } catch (IOException e) {
            e.printStackTrace();
        }
        publisher.publishEvent(new ApplicationUploadedEvent(id, job.getId()));
        return new ResponseEntity<UploadApplicationResponse>(UploadApplicationResponse.builder()
                .metadata(Metadata.builder()
                        .id(job.getId())
                        .url("/v2/jobs/"+job.getId())
                        .build())
                .entity(JobEntity.builder()
                        .id(job.getId())
                        .status(job.getStatus())
                        .build())
                .build(), HttpStatus.CREATED);
    }


}
