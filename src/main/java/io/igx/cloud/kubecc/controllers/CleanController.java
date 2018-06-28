package io.igx.cloud.kubecc.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Hack controller to clean db
 */
@RestController
public class CleanController {

    @Autowired
    private MongoTemplate template;


    @PostMapping("/clean")
    public String clean() {
        template.getCollection("applications").drop();
        template.getCollection("routes").drop();
        template.getCollection("jobs").drop();
        return "ok";
    }
}
