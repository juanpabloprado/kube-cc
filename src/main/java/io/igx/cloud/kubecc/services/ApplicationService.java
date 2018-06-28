package io.igx.cloud.kubecc.services;

import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.UpdateOptions;
import io.igx.cloud.kubecc.utils.BsonUtils;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.cloudfoundry.client.v2.Metadata;
import org.cloudfoundry.client.v2.applications.*;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;

@Service
public class ApplicationService extends BaseService{


    public ApplicationService() {
        super("applications");
    }

    public CreateApplicationResponse create(CreateApplicationRequest request){
        Document doc = BsonUtils.fromPojo(request);
        String id = UUID.randomUUID().toString();
        doc.put("_id", id);
        collection.insertOne(doc);
        return CreateApplicationResponse.builder()
                .metadata(Metadata.builder()
                        .id(id)
                        .url("/v2/apps/"+id)
                        .build())
                .entity(ApplicationEntity.builder()
                        .name(request.getName())
                        .buildpack(request.getBuildpack())
                        .memory(request.getMemory())
                        .instances(request.getInstances())
                        .diskQuota(request.getDiskQuota())
                        .spaceId(request.getSpaceId())
                        .stackId(request.getStackId())
                        .state(request.getState())
                        .command(request.getCommand())
                        .healthCheckHttpEndpoint(request.getHealthCheckHttpEndpoint())
                        .healthCheckType(request.getHealthCheckType())
                        .diego(request.getDiego())
                        .enableSsh(request.getEnableSsh())
                        .build())
                .build();
    }

    public UpdateApplicationResponse update(Bson filter, Map<String, Object> request){
        Document update = new Document("$set", new Document(request));
        FindOneAndUpdateOptions updateOptions = new FindOneAndUpdateOptions().upsert(true);
        Document result = collection.findOneAndUpdate(filter, update, updateOptions);
        return UpdateApplicationResponse.builder()
                .entity(fromDoc(result))
                .metadata(Metadata.builder()
                        .id(result.getString("_id"))
                        .url("/v2/apps/"+result.get("_id"))
                        .build())
                .build();
    }

    public GetApplicationResponse find(String id){
        Document result = collection.find(Filters.eq("_id", id)).first();
        return GetApplicationResponse.builder()
                .entity(fromDoc(result))
                .metadata(Metadata.builder()
                        .id(id)
                        .url("/v2/apps/"+id)
                        .build())
                .build();
    }

    public ListApplicationsResponse find(Bson filter, Bson sort) {
        FindIterable<Document> documents = collection.find(filter).sort(sort);
        long total = collection.count(filter);
        LinkedList<ApplicationResource> resources = new LinkedList<>();
        for(Document doc : documents){
            resources.add(ApplicationResource.builder()
                    .entity(fromDoc(doc))
                    .metadata(Metadata.builder()
                            .id(doc.getString("_id"))
                            .url("/v2/apps/"+doc.get("_id"))
                            .build())
                    .build());
        }
        return ListApplicationsResponse.builder()
                .totalPages(1)
                .totalResults((int)total)
                .resources(resources)
                .build();
    }

    public boolean addRoute(String appId, String routeId){
        Document arrayPush = new Document();
        arrayPush.put("$push", new Document().append("routes", routeId));
        return collection.updateOne(Filters.eq("_id", appId), arrayPush).getMatchedCount() > 0;
    }

    private ApplicationEntity fromDoc(Document doc){

        return ApplicationEntity.builder()
                .enableSsh(doc.getBoolean("enable_ssh"))
                .diego(doc.getBoolean("diego"))
                .healthCheckType(doc.getString("health_check_type"))
                .healthCheckHttpEndpoint(doc.getString("health_check_http_endpoint"))
                .name(doc.getString("name"))
                .spaceId(doc.getString("space_guid"))
                .command(doc.getString("command"))
                .buildpack(doc.getString("buildpack"))
                .stackId(doc.getString("stack_guid"))
                .instances(doc.getInteger("instances"))
                .memory(doc.getInteger("memory"))
                .state(doc.getString("state"))
                .spaceUrl("/v2/spaces/"+doc.get("space_guid"))
                .routesUrl("/v2/apps/"+doc.get("_id")+"/routes")
                .eventsUrl("/v2/apps/"+doc.get("_id")+"/events")
                .serviceBindingsUrl("/v2/apps/"+doc.get("_id")+"/service_bindings")
                .routeMappingsUrl("/v2/apps/"+doc.get("_id")+"/route_mappings")
                .build();
    }
}
