package io.igx.cloud.kubecc.controllers;

import org.cloudfoundry.uaa.serverinformation.ApplicationInfo;
import org.cloudfoundry.uaa.serverinformation.GetInfoResponse;
import org.cloudfoundry.uaa.serverinformation.Links;
import org.cloudfoundry.uaa.serverinformation.Prompts;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LoginController {


    @GetMapping("/login")
    public GetInfoResponse info() {
        return GetInfoResponse.builder()

                .prompts(Prompts.builder()

                        .username("text", "Email")
                        .password("password", "Password")
                        .build())
                .app(ApplicationInfo.builder()
                        .version("4.5.5")
                        .build())
                .zoneName("uaa")
                .links(Links.builder()
                        .login("http://localhost:8080/login")
                        .password("http://localhost:8080/pwd")
                        .register("http://localhost:8080/register")
                        .uaa("http://localhost:8080/uaa")
                        .build())
                .build();
    }

    @PostMapping(value = "/oauth/token", consumes = "application/x-www-form-urlencoded")
    public Map<String, String> auth(@RequestParam Map<String, Object> creds) {
        Map<String, String> token = new HashMap<>();
        token.put("access_token", UUID.randomUUID().toString());
        token.put("refresh_token", UUID.randomUUID().toString());
        token.put("jti", UUID.randomUUID().toString());
        token.put("scope", "openid uaa.user cloud_controller.read");
        token.put("expires_in","86400");
        return token;
    }

}
