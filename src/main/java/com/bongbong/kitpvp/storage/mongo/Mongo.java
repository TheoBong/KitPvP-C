package com.bongbong.kitpvp.storage.mongo;

import com.bongbong.kitpvp.KitPvPPlugin;
import com.bongbong.kitpvp.util.ThreadUtil;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.UuidRepresentation;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class Mongo {
    private final KitPvPPlugin plugin;
    private final MongoDatabase mongoDatabase;

    public Mongo(KitPvPPlugin plugin) {
        this.plugin = plugin;

        MongoClient mongoClient = MongoClients.create(MongoClientSettings.builder().uuidRepresentation(UuidRepresentation.STANDARD).build());
        mongoDatabase = mongoClient.getDatabase("kitpvp");
    }

    public void createCollection(boolean async, String collectionName) {
        ThreadUtil.runTask(async, plugin, () -> {
            AtomicBoolean exists = new AtomicBoolean(false);
            mongoDatabase.listCollectionNames().forEach(s -> {
                if(s.equals(collectionName)) {
                    exists.set(true);
                }
            });

            if(!exists.get()) {
                mongoDatabase.createCollection(collectionName);
            }
        });
    }

    public void getCollectionIterable(boolean async, String collectionName, MongoIterableResult mir) {
        ThreadUtil.runTask(async, plugin, ()-> mir.call(mongoDatabase.getCollection(collectionName).find()));
    }

    public void deleteDocument(boolean async, String collectionName, Object id) {
        ThreadUtil.runTask(async, plugin, () -> {
            MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);
            Document document = new Document("_id", id);

            collection.deleteMany(document);
        });
    }

    public void massUpdate(boolean async, MongoUpdate mongoUpdate) {
        massUpdate(async, mongoUpdate.getCollectionName(), mongoUpdate.getId(), mongoUpdate.getUpdate());
    }

    public void massUpdate(boolean async, String collectionName, Object id, Map<String, Object> updates) throws LinkageError {
        ThreadUtil.runTask(async, plugin, () -> {
            MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);

            Document document = collection.find(new Document("_id", id)).first();
            if(document == null) {
                collection.insertOne(new Document("_id", id));
            }

            updates.forEach((key, value) -> collection.updateOne(Filters.eq("_id", id), Updates.set(key, value)));
        });
    }

    public void getDocument(boolean async, String collectionName, Object id, MongoResult mongoResult) {
        ThreadUtil.runTask(async, plugin, () -> {
            MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);

            if (collection.find(Filters.eq("_id", id)).iterator().hasNext()) {
                mongoResult.call(collection.find(Filters.eq("_id", id)).first());
            } else {
                mongoResult.call(null);
            }
        });
    }

    public Iterator<Document> sortNumber(boolean async, String collectionName, String field) throws LinkageError {
        MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);

        FindIterable<Document> iterDoc = collection.find().sort(Sorts.descending(field));
        return iterDoc.iterator();
    }
}
