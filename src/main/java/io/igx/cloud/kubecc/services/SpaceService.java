package io.igx.cloud.kubecc.services;

import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Filters;
import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.NamespaceBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.cloudfoundry.client.v2.Metadata;
import org.cloudfoundry.client.v2.spaces.*;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

import static io.igx.cloud.kubecc.utils.BsonUtils.fromPojo;

@Service
public class SpaceService extends BaseService{

    private KubernetesClient client;

    public SpaceService(KubernetesClient client) {
        super("spaces");
        this.client = client;
    }

    public CreateSpaceResponse create(CreateSpaceRequest request){
        try {
            Namespace namespace = new NamespaceBuilder().withNewMetadata().withName(request.getName()).addToLabels("org", request.getOrganizationId()).endMetadata().build();
            client.namespaces().create(namespace);
        }catch (Exception e){
            throw new IllegalStateException(e);
        }
        Document dbo = fromPojo(request);
        collection.insertOne(dbo);


        return CreateSpaceResponse.builder()
                .entity(fromDoc(dbo))
                .metadata(Metadata.builder()
                        .id(dbo.get("_id").toString())
                        .url("/v2/spaces/"+dbo.get("_id"))
                        .build())
                .build();
    }

    public boolean delete(String id){
        Document space = collection.findOneAndDelete(Filters.eq("_id", id));
        Namespace namespace =new NamespaceBuilder().withNewMetadata().withName(space.getString("name")).endMetadata().build();
        client.namespaces().delete(namespace);
        return true;
    }

    public ListSpacesResponse find(Bson filter, Bson sort){
        FindIterable<Document> documents = collection.find(filter).sort(sort);
        long total = collection.count(filter);
        List<SpaceResource> resources = new LinkedList<>();
        for(Document  doc : documents){
            resources.add(SpaceResource.builder()
                    .entity(fromDoc(doc))
                    .metadata(Metadata.builder()
                            .id(doc.getString("_id"))
                            .url("/v2/spaces/"+doc.getString("_id"))
                            .build())
                    .build());
        }

        ListSpacesResponse response = ListSpacesResponse.builder()
                .totalPages(1)
                .resources(resources)
                .totalResults((int)total)
                .build();

        return response;
    }
    private SpaceEntity fromDoc(Document doc){
        String id = doc.getString("_id");
        return SpaceEntity.builder()
                .organizationId(doc.getString("organization_guid"))
                .organizationUrl("/v2/organizations/"+doc.get("organization_guid"))
                .name(doc.getString("name"))
                .allowSsh(doc.getBoolean("allow_ssh"))
                .developersUrl(createUrl(id, "developers"))
                .managersUrl(createUrl(id, "managers"))
                .auditorsUrl(createUrl(id, "auditors"))
                .applicationsUrl(createUrl(id, "apps"))
                .routesUrl(createUrl(id, "routes"))
                .domainsUrl(createUrl(id, "domains"))
                .serviceInstancesUrl(createUrl(id, "service_instances"))
                .applicationEventsUrl(createUrl(id, "app_events"))
                .securityGroupsUrl(createUrl(id, "security_groups"))
                .stagingSecurityGroupsUrl(createUrl(id, "staging_security_groups"))
                .build();
    }

    private String createUrl(String guid, String endpoint){
        return String.format("%s/%s/%s", "/v2/spaces", guid, endpoint);
    }

}
