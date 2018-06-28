package io.igx.cloud.kubecc.controllers;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import io.igx.cloud.kubecc.services.OrganizationService;
import io.igx.cloud.kubecc.services.SpaceService;
import org.bson.Document;
import org.cloudfoundry.client.v2.Metadata;
import org.cloudfoundry.client.v2.organizations.*;
import org.cloudfoundry.client.v2.privatedomains.GetPrivateDomainResponse;
import org.cloudfoundry.client.v2.privatedomains.ListPrivateDomainsResponse;
import org.cloudfoundry.client.v2.privatedomains.PrivateDomainEntity;
import org.cloudfoundry.client.v2.privatedomains.PrivateDomainResource;
import org.cloudfoundry.client.v2.spaces.ListSpacesResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
public class OrganizationController extends V2Controller{

    private final String PATH = "/v2/organizations";

    private OrganizationService organizationService;
    private SpaceService spaceService;

    public OrganizationController(OrganizationService organizationService, SpaceService spaceService) {
        this.organizationService = organizationService;
        this.spaceService = spaceService;
    }

    @PostMapping("/organizations")
    public CreateOrganizationResponse createOrg(@RequestBody CreateOrganizationRequest request) {
        return organizationService.create(request);
    }

    @GetMapping("/organizations")
    public ListOrganizationsResponse list(){
        return organizationService.find(new Document(), Sorts.ascending("name"));
    }


    @DeleteMapping("/organizations/{id}")
    public ResponseEntity<DeleteOrganizationResponse> delete(@PathVariable("id") String id){
        organizationService.delete(id);
        return ResponseEntity.status(204).build();
    }

    @GetMapping("/organizations/{id}/spaces")
    public ListSpacesResponse getSpaces(@PathVariable("id") String id){
        return spaceService.find(Filters.eq("organization_guid", id), Sorts.ascending("name"));
    }

    @PutMapping("/organizations/{id}/users/{uid}")
    public GetOrganizationResponse addUser(@PathVariable("id") String orgId, @PathVariable("uid") String userId){
        return GetOrganizationResponse
                .builder()
                .metadata(Metadata.builder()
                        .id(orgId)
                        .url("/v2/users/"+orgId)
                        .build())
                .build();
    }

    @GetMapping("/organizations/{id}/private_domains")
    public ListPrivateDomainsResponse privateDomains(@PathVariable("id") String id) {


        return ListPrivateDomainsResponse.builder()
                .totalPages(1)
                .totalResults(1)
                .resource(PrivateDomainResource.builder()
                        .metadata(Metadata.builder()
                                .id("1234-abcd")
                                .url("/v2/private_domains/1234-abcd")
                                .build())
                        .entity(PrivateDomainEntity.builder()
                                .name("cluster.local")
                                .owningOrganizationId(id)
                                .owningOrganizationUrl("/v2/organizations/"+id)
                                .sharedOrganizationsUrl("/v2/private_domains/1234-abcd/shared_organizations")
                                .build())
                        .build())
                .build();


    }



}
