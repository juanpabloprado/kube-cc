package io.igx.cloud.kubecc.services;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import io.igx.cloud.kubecc.utils.BsonUtils;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.cloudfoundry.client.v2.Metadata;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsResponse;
import org.cloudfoundry.client.v2.organizations.OrganizationEntity;
import org.cloudfoundry.client.v2.organizations.OrganizationResource;
import org.cloudfoundry.client.v2.organizations.CreateOrganizationRequest;
import org.cloudfoundry.client.v2.organizations.CreateOrganizationResponse;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class OrganizationService extends BaseService{

    public OrganizationService() {
        super("organizations");
    }

    public ListOrganizationsResponse find(Bson filter, Bson sort){
        FindIterable<Document> iterable = collection.find();
        List<OrganizationResource> resources = new LinkedList<>();
        for(Document dbo : iterable){
            resources.add(
                    OrganizationResource.builder()
                            .entity(fromDoc(dbo))
                            .metadata(Metadata.builder()
                                    .id(dbo.getString("_id"))
                                    .url("/v2/organizations/"+dbo.getString("_id"))
                                    .build())
                            .build()
            );
        }
        return ListOrganizationsResponse.builder()
                .totalPages(1)
                .totalResults((int)collection.count())
                .resources(resources)
                .build();
    }

    public CreateOrganizationResponse create(CreateOrganizationRequest request){
        Document dbo = BsonUtils.fromPojo(request);
        collection.insertOne(dbo);
        return CreateOrganizationResponse.builder()
                .entity(fromDoc(dbo))
                .metadata(Metadata.builder()
                        .id(dbo.getString("_id"))
                        .url("/v2/organizations/"+dbo.get("_id"))
                        .build())
                .build();
    }

    public boolean delete(String id){
        Document doc = new Document();
        doc.put("_id", id);
        return collection.deleteOne(doc).getDeletedCount() > 0;
    }

    private OrganizationEntity fromDoc(Document dbo){
        return OrganizationEntity.builder()
                .billingEnabled(dbo.getBoolean("billing_enabled"))
                .name(dbo.getString("name"))
                .status(dbo.getString("status"))
                .applicationEventsUrl(createUrl(dbo.getString("_id"), "app_events"))
                .auditorsUrl(createUrl(dbo.getString("_id"), "auditors"))
                .billingManagersUrl(createUrl(dbo.getString("_id"), "billing_managers"))
                .managersUrl(createUrl(dbo.getString("_id"), "managers"))
                .usersUrl(createUrl(dbo.getString("_id"), "users"))
                .spacesUrl(createUrl(dbo.getString("_id"), "spaces"))
                .spaceQuotaDefinitionsUrl(createUrl(dbo.getString("_id"), "space_quota_definitions"))
                .privateDomainsUrl(createUrl(dbo.getString("_id"), "private_domains"))
                .domainsUrl(createUrl(dbo.getString("_id"), "domains"))
                .quotaDefinitionUrl("/v2/quota_definitions/"+dbo.getString("quota_definition_guid"))
                .build();
    }

    private String createUrl(String guid, String endpoint){
        return String.format("%s/%s/%s", "/v2/organizations", guid, endpoint);
    }

}
