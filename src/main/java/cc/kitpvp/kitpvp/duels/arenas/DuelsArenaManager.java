package cc.kitpvp.kitpvp.duels.arenas;

import cc.kitpvp.kitpvp.KitPvPPlugin;
import cc.kitpvp.kitpvp.storage.mongo.Mongo;
import cc.kitpvp.kitpvp.storage.mongo.MongoDeserializedResult;
import cc.kitpvp.kitpvp.storage.mongo.MongoUpdate;
import cc.kitpvp.kitpvp.util.message.Color;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DuelsArenaManager {

    private KitPvPPlugin plugin;
    private @Getter Map<UUID, Arena> arenas;
    public DuelsArenaManager(KitPvPPlugin plugin) {
        this.plugin = plugin;
        this.arenas = new HashMap<>();

        Mongo mongo = plugin.getMongo();
        mongo.createCollection(false, "practice_arenas");
        mongo.getCollectionIterable(false, "practice_arenas",
                iterable -> iterable.forEach(
                document ->
                pull(false, document.get("_id", UUID.class),
                obj -> {})));
    }

    public Arena get(UUID uuid) {
        return arenas.get(uuid);
    }

    public Arena get(String s) {
        for(Arena arena : arenas.values()) {
            if(arena.getName().equalsIgnoreCase(s)) {
                return arena;
            }
        }

        return null;
    }

    public Arena createArena(String name) {
        for(Arena arena : arenas.values()) {
            if(arena.getName().equalsIgnoreCase(name)) {
                return null;
            }
        }

        Arena arena = new Arena(UUID.randomUUID());
        arena.setName(Color.strip(name.toLowerCase()));
        arena.setDisplayName(name);
        arenas.put(arena.getUuid(), arena);
        push(true, arena);
        return arena;
    }

    public void pull(boolean async, UUID uuid, MongoDeserializedResult mdr) {
        plugin.getMongo().getDocument(async, "practice_arenas", uuid, document -> {
            if(document != null) {
                Arena arena = new Arena(uuid);
                arena.importFromDocument(document);
                arenas.put(arena.getUuid(), arena);
                mdr.call(arena);
            } else {
                mdr.call(null);
            }
        });
    }

    public void push(boolean async, Arena arena) {
        MongoUpdate mu = new MongoUpdate("practice_arenas", arena.getUuid());
        mu.setUpdate(arena.export());
        plugin.getMongo().massUpdate(async, mu);
    }

    public void remove(boolean async, Arena arena) {
        plugin.getMongo().deleteDocument(async, "practice_arenas", arena.getUuid());
        arenas.remove(arena.getUuid());
    }

}
