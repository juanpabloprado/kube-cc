package io.igx.cloud.kubecc.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BsonUtils {

    private static ObjectMapper mapper = new ObjectMapper();

    public static Document fromPojo(Object object){
        Document dbo = null;
        try {
            dbo = Document.parse(mapper.writeValueAsString(object));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
        dbo.put("_id", UUID.randomUUID().toString());
        return dbo;
    }

    public static Bson createFiter(String query){
        String[] queries = query.split(",");
        List<Bson> filters = new ArrayList<>();
        for(String q : queries){
            String[] values = q.split(":");
            filters.add(Filters.eq(values[0], values[1]));
        }
        return Filters.and(filters);
    }
}
