package io.igx.cloud.kubecc.controllers;

import com.mongodb.client.model.Sorts;
import io.igx.cloud.kubecc.services.RouteService;
import io.igx.cloud.kubecc.utils.BsonUtils;
import org.cloudfoundry.client.v2.routes.CreateRouteRequest;
import org.cloudfoundry.client.v2.routes.CreateRouteResponse;
import org.cloudfoundry.client.v2.routes.ListRoutesResponse;
import org.springframework.web.bind.annotation.*;

@RestController
public class RouteController extends V2Controller {

    private RouteService service;

    public RouteController(RouteService service) {
        this.service = service;
    }

    @GetMapping("/routes")
    public ListRoutesResponse list(@RequestParam("q") String query){
        return service.find(BsonUtils.createFiter(query), Sorts.ascending("host"));
    }

    @PostMapping("/routes")
    public CreateRouteResponse create(@RequestBody CreateRouteRequest request){
        return service.create(request);
    }

}
