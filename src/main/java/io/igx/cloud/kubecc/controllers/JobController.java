package io.igx.cloud.kubecc.controllers;

import io.igx.cloud.kubecc.domain.Job;
import io.igx.cloud.kubecc.services.JobService;
import org.cloudfoundry.client.v2.Metadata;
import org.cloudfoundry.client.v2.jobs.GetJobResponse;
import org.cloudfoundry.client.v2.jobs.JobEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JobController extends V2Controller {

    private JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @GetMapping("/jobs/{id}")
    public GetJobResponse status(@PathVariable("id") String jobId) {
        Job job = jobService.find(jobId);

        return GetJobResponse.builder()
                .metadata(Metadata.builder()
                        .id(jobId)
                        .url("/v2/jobs/"+jobId)
                        .build())
                .entity(JobEntity.builder()
                        .id(jobId)
                        .status(job.getStatus())
                        .build())
                .build();
    }

}
