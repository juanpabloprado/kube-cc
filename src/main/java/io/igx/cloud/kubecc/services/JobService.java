package io.igx.cloud.kubecc.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import io.igx.cloud.kubecc.domain.Job;
import io.igx.cloud.kubecc.utils.BsonUtils;
import org.bson.Document;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Service
public class JobService extends BaseService {

    private ObjectMapper mapper = new ObjectMapper();

    public JobService() {
        super("jobs");
    }


    public Job create(Map<String, Object> metadata) {
        Job job = new Job();
        job.setMetadata(metadata);
        Document doc = BsonUtils.fromPojo(job);
        this.collection.insertOne(doc);
        try {
            job = mapper.readValue(doc.toJson(), Job.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return job;
    }

    public boolean update(String id, Map<String, Object> fields){
        Document update = new Document("$set", new Document(fields));
        FindOneAndUpdateOptions updateOptions = new FindOneAndUpdateOptions().upsert(true);
        Document result = collection.findOneAndUpdate(Filters.eq("_id", id), update, updateOptions);
        return true;
    }

    public Job find(String id) {
        Document document = collection.find(Filters.eq("_id", id)).first();
        Job job = null;
        try {
            job = mapper.readValue(document.toJson(), Job.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return job;
    }


}
