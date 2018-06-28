package io.igx.cloud.kubecc.controllers;

import org.cloudfoundry.client.v2.resourcematch.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedList;
import java.util.List;

@RestController
public class ResourceMatchController extends V2Controller {


    @PutMapping("/resource_match")
    public List<Resource> match(@RequestBody List<Resource> resources){
        Integer totalSize = 0;
        for(Resource resource : resources){
            totalSize += resource.getSize();
        }
        logger.info("Total bytes to be processed {}", totalSize);
        return new LinkedList<Resource>();
    }
}
