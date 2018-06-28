package io.igx.cloud.kubecc.controllers;

import org.cloudfoundry.client.v2.info.GetInfoResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InfoController extends V2Controller {

    @Value("${token.endpoint}")
    private String tokenEndpoint;

    @Value("${auth.endpoint}")
    private String authEndpoint;

    @GetMapping("/info")
    public GetInfoResponse info() {
        return GetInfoResponse.builder()
                .apiVersion("2.114.0")
                .tokenEndpoint(tokenEndpoint)
                .authorizationEndpoint(authEndpoint)
                .build();
    }
}
