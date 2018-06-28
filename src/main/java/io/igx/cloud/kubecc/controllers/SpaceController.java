package io.igx.cloud.kubecc.controllers;

import com.mongodb.client.model.Sorts;
import io.igx.cloud.kubecc.services.ApplicationService;
import io.igx.cloud.kubecc.services.SpaceService;
import io.igx.cloud.kubecc.utils.BsonUtils;
import org.cloudfoundry.client.v2.Metadata;
import org.cloudfoundry.client.v2.applications.ListApplicationsResponse;
import org.cloudfoundry.client.v2.spaces.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import static com.mongodb.client.model.Sorts.*;

@RestController
public class SpaceController extends V2Controller {

    private final String PATH = "/v2/spaces";
    private SpaceService service;
    private ApplicationService applicationService;

    public SpaceController(SpaceService service, ApplicationService applicationService) {
        this.service = service;
        this.applicationService = applicationService;
    }

    @PostMapping("/spaces")
    public CreateSpaceResponse create(@RequestBody CreateSpaceRequest request) {
        return service.create(request);
    }


    @PutMapping("/spaces/{id}/managers/{uid}")
    public GetSpaceResponse addManager(@PathVariable("id") String sid, @PathVariable("uid") String uid){
        return GetSpaceResponse.builder()
                .metadata(Metadata.builder()
                        .id(sid)
                        .url("/v2/users/"+sid)
                        .build())
                .build();
    }

    @PutMapping("/spaces/{id}/developers/{uid}")
    public GetSpaceResponse addDeveloper(@PathVariable("id") String sid, @PathVariable("uid") String uid){
        return GetSpaceResponse.builder()
                .metadata(Metadata.builder()
                        .id(sid)
                        .url("/v2/users/"+sid)
                        .build())
                .build();
    }

    @GetMapping("/spaces")
    public ListSpacesResponse listSpaces(@RequestParam("q") String query, @RequestParam(value = "order-by", defaultValue = "name") String orderBy){
        return service.find(BsonUtils.createFiter(query), ascending(orderBy));
    }

    @GetMapping("/spaces/{id}/summary")
    public GetSpaceSummaryResponse summary(@PathVariable("id") String id){
        return GetSpaceSummaryResponse.builder()
                .id(id)
                .build();
    }


    @GetMapping("/spaces/{id}/apps")
    public ListSpaceApplicationsResponse listApps(@PathVariable("id") String id, @RequestParam("q") String query){
        ListApplicationsResponse listApplicationsResponse = applicationService.find(BsonUtils.createFiter(query), Sorts.ascending("name"));
        return ListSpaceApplicationsResponse.builder()
                .resources(listApplicationsResponse.getResources())
                .totalPages(listApplicationsResponse.getTotalPages())
                .totalResults(listApplicationsResponse.getTotalResults())
                .build();
    }


    @DeleteMapping("/spaces/{id}")
    public ResponseEntity<DeleteSpaceResponse> delete(@PathVariable("id") String id){
        service.delete(id);
        return ResponseEntity.status(204).build();
    }


}
