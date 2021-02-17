package me.cunzai.chatlog.database;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexOptions;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;

/**
 * @Author: EmptyIrony
 * @Date: 2021/2/8 20:23
 */

@Slf4j
@Getter
public class MongoDB {
    private static final String address = "10.191.171.69";
    private static final int port = 27017;

    private final MongoClient client;
    private final MongoDatabase database;
    private final MongoCollection<Document> documents;

    public MongoDB() {
        this.client = new MongoClient(address,port);
        this.database = this.client.getDatabase("chat");
        this.documents = this.database.getCollection("data");
        if (this.documents.listIndexes().first() == null){
            this.documents.createIndex(Filters.eq("uuid",1),new IndexOptions().background(true).unique(true));
        }
    }
}
