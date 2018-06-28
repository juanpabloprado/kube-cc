package io.igx.cloud.kubecc.services;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import javax.annotation.PostConstruct;

public abstract class BaseService {

    protected MongoCollection<Document> collection;

    protected final String collectionName;

    @Autowired
    protected MongoTemplate template;

    public BaseService(String collectionName){
        this.collectionName = collectionName;
    }

    @PostConstruct
    public void init() {
        this.collection = template.getCollection(collectionName);
    }

}
