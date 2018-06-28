package io.igx.cloud.kubecc.controllers;

import org.cloudfoundry.client.v2.shareddomains.ListSharedDomainsResponse;
import org.cloudfoundry.client.v2.shareddomains.SharedDomainResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
public class SharedDomainController extends V2Controller{

    @GetMapping("/shared_domains")
    public ListSharedDomainsResponse list(){
        return ListSharedDomainsResponse.builder()
                .totalPages(1)
                .totalResults(0)
                .resources(new ArrayList<SharedDomainResource>())
                .build();
    }

}
