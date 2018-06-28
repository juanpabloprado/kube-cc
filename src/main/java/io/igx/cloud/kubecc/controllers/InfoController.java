package io.igx.cloud.kubecc.controllers;

import org.cloudfoundry.client.v2.info.GetInfoResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InfoController extends V2Controller {

    @GetMapping("/info")
    public GetInfoResponse info() {
        return GetInfoResponse.builder()
                .apiVersion("2.114.0")
                .tokenEndpoint("http://localhost:8080/uaa")
                .authorizationEndpoint("http://localhost:8080/uaa")
                .build();
    }
}
