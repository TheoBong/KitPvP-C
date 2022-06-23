package cc.kitpvp.kitpvp.storage.mongo;

import org.bson.Document;

public interface MongoResult {
    void call(Document document);
}
