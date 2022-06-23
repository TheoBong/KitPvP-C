package cc.kitpvp.kitpvp.duels.kits;

import cc.kitpvp.kitpvp.KitPvPPlugin;
import cc.kitpvp.kitpvp.storage.mongo.Mongo;
import cc.kitpvp.kitpvp.storage.mongo.MongoDeserializedResult;
import cc.kitpvp.kitpvp.storage.mongo.MongoUpdate;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DuelsKitManager {

    private KitPvPPlugin plugin;
    private @Getter Map<UUID, Kit> kits;
    public DuelsKitManager(KitPvPPlugin plugin) {
        this.plugin = plugin;
        this.kits = new HashMap<>();

        Mongo mongo = plugin.getMongo();
        mongo.createCollection(false, "duels_kits");
        mongo.getCollectionIterable(false, "duels_kits",
                iterable -> iterable.forEach(
                document ->
                pull(false, document.get("_id", UUID.class),
                obj -> {})));
    }

    public Kit get(UUID uuid) {
        return kits.get(uuid);
    }

    public Kit get(String s) {
        for(Kit kit : kits.values()) {
            if(kit.getName().equalsIgnoreCase(s)) {
                return kit;
            }
        }

        return null;
    }

    public Kit createKit(String name) {
        for(Kit kit : kits.values()) {
            if(kit.getName().equalsIgnoreCase(name)) {
                return null;
            }
        }

        Kit kit = new Kit(UUID.randomUUID(), plugin);
        kit.setName(name.toLowerCase());
        kit.setDisplayName(name);
        kits.put(kit.getUuid(), kit);
        push(true, kit);
        return kit;
    }

    public void pull(boolean async, UUID uuid, MongoDeserializedResult mdr) {
        plugin.getMongo().getDocument(async, "duels_kits", uuid, document -> {
            if(document != null) {
                Kit kit = new Kit(uuid, plugin);
                kit.importFromDocument(document);
                kits.put(kit.getUuid(), kit);
                mdr.call(kit);
            } else {
                mdr.call(null);
            }
        });
    }

    public void push(boolean async, Kit kit) {
        MongoUpdate mu = new MongoUpdate("duels_kits", kit.getUuid());
        mu.setUpdate(kit.export());
        plugin.getMongo().massUpdate(async, mu);
    }

    public void remove(boolean async, Kit kit) {
        plugin.getMongo().deleteDocument(async, "duels_kits", kit.getUuid());
        kits.remove(kit.getUuid());
    }
}
