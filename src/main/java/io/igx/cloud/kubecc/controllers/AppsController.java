package io.igx.cloud.kubecc.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import io.igx.cloud.kubecc.domain.ApplicationUploadedEvent;
import io.igx.cloud.kubecc.domain.Job;
import io.igx.cloud.kubecc.services.ApplicationService;
import io.igx.cloud.kubecc.services.JobService;
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

    @Value("${app.staging.dir:/tmp}")
    private String baseFolder;

    public AppsController(ApplicationService service, JobService jobService, ApplicationEventPublisher publisher) {
        this.service = service;
        this.jobService = jobService;
        this.publisher = publisher;
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
            String destinationFile = baseFolder+"/"+id+"/application.zip";
            file.transferTo(new File(destinationFile));
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
