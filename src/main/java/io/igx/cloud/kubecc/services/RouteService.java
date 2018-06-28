package io.igx.cloud.kubecc.services;

import com.mongodb.client.FindIterable;
import io.igx.cloud.kubecc.utils.BsonUtils;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.cloudfoundry.client.v2.Metadata;
import org.cloudfoundry.client.v2.routes.*;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class RouteService extends BaseService {

    public RouteService() {
        super("routes");
    }

    public ListRoutesResponse find(Bson filter, Bson sort){
        FindIterable<Document> iterable = collection.find();
        List<RouteResource> resources = new LinkedList<>();
        long total = collection.count(filter);
        for(Document doc : iterable) {
            resources.add(RouteResource.builder()
                    .metadata(Metadata.builder()
                            .id(doc.getString("_id"))
                            .url("/v2/routes/"+doc.getString("_id"))
                            .build())
                    .entity(fromDoc(doc))
                    .build());
        }
        return ListRoutesResponse.builder()
                .resources(resources)
                .totalPages(1)
                .totalResults((int)total)
                .build();
    }

    public CreateRouteResponse create(CreateRouteRequest request){
        Document dbo = BsonUtils.fromPojo(request);
        collection.insertOne(dbo);
        return CreateRouteResponse.builder()
                .metadata(Metadata.builder()
                        .id(dbo.getString("_id"))
                        .url("/v2/routes/"+dbo.get("id"))
                        .build())
                .entity(fromDoc(dbo))
                .build();
    }

    private RouteEntity fromDoc(Document doc) {
        return RouteEntity.builder()
                .host(doc.getString("host"))
                .path(doc.getString("path"))
                .domainId(doc.getString("domain_guid"))
                .spaceId(doc.getString("space_guid"))
                .serviceInstanceId(doc.getString("service_instance_guid"))
                .port(doc.getInteger("port"))
                .domainUrl(doc.getString("domain_url"))
                .spaceUrl(doc.getString("space_url"))
                .serviceInstanceUrl(doc.getString("service_instance_url"))
                .applicationsUrl(doc.getString("apps_url"))
                .routeMappingsUrl(doc.getString("route_mappings_url"))
                .build();
    }

}
