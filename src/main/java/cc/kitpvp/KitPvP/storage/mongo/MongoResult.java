package cc.kitpvp.KitPvP.storage.mongo;

import org.bson.Document;

public interface MongoResult {
    void call(Document document);
}
