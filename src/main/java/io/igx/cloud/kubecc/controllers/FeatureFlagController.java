package io.igx.cloud.kubecc.controllers;

import org.cloudfoundry.client.v2.featureflags.GetFeatureFlagResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class FeatureFlagController extends V2Controller {

    private Map<String, GetFeatureFlagResponse> featureFlags;

    public FeatureFlagController(){
        this.featureFlags = new HashMap<>();
        String[] features = new String[]{"user_org_creation", "private_domain_creation", "app_bits_upload", "app_scaling", "route_creation", "service_instance_creation",
        "diego_docker","set_roles_by_username","unset_roles_by_username","task_creation","env_var_visibility","space_scoped_private_broker_creation", "space_developer_env_var_visibility",
                "service_instance_sharing" };

        for(String feature: features){
            boolean enabled = !feature.matches("diego_docker|task_creation|service_instance_sharing|private_domain_creation");
            this.featureFlags.put(feature, GetFeatureFlagResponse.builder()
                    .enabled(enabled)
                    .name(feature)
                    .url("/v2/config/feature_flags/"+feature)
                    .build());
        }
    }

    @GetMapping("/config/feature_flags")
    public Map<String, GetFeatureFlagResponse> allFeatures(){
        return this.featureFlags;
    }

    @GetMapping("/config/feature_flags/{flag}")
    public GetFeatureFlagResponse getFlag(@PathVariable("flag") String flag){
        return this.featureFlags.get(flag);
    }
}
