package cc.kitpvp.KitPvP.player;

import cc.kitpvp.KitPvP.KitPvPPlugin;
import cc.kitpvp.KitPvP.kits.Kit;
import cc.kitpvp.KitPvP.storage.mongo.MongoUpdate;
import cc.kitpvp.KitPvP.util.Levels;
import cc.kitpvp.KitPvP.util.timer.Timer;
import cc.kitpvp.KitPvP.util.timer.impl.DoubleTimer;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;

import java.util.*;

@Getter
@Setter
public class Profile {
    private final KitPvPPlugin plugin;

    private boolean loaded = false;
    private UUID uuid;
    private String name;
    private PlayerDamageData damageData = new PlayerDamageData();
    private PlayerStatistics statistics = new PlayerStatistics();
    private int xp = 0;
    private int level = 1;
    private Timer pearlTimer = new DoubleTimer(16);
    private PlayerState state = PlayerState.SPAWN;
    private List<String> freeKits = new ArrayList<>(Arrays.asList("PvP"));
    private List<String> purchasedKits = new ArrayList<>();
    private Kit currentKit;
    private Kit lastKit;
    private boolean awaitingTeleport;
    private boolean controllable = true;
    private UUID lastAttacked;
    private int bounty = 0;
    private PlayerTimeType currentTimeType = PlayerTimeType.DAY;

    private boolean scoreboardEnabled = true;

    public Profile(KitPvPPlugin plugin, String name, UUID uuid) {
        this.plugin = plugin;
        this.name = name;
        this.uuid = uuid;

        pull(false);
    }

    @SuppressWarnings("unchecked")
    public void deserialize(Document document) {
        this.purchasedKits = (List<String>) document.get("purchased_kits");
        String lastKitName = document.getString("last_kit_name");
        this.scoreboardEnabled = document.getBoolean("scoreboard_enabled", true);
        if (lastKitName != null) {
            lastKit = plugin.getKitManager().getFfaKitByName(lastKitName);
        }

        xp = document.getInteger("xp");
        level = Levels.calculateLevel(xp);

        statistics.setDeaths(document.getInteger("deaths", 0));
        statistics.setHighestKillStreak(document.getInteger("highest_kill_streak", 0));
        statistics.setKills(document.getInteger("kills", 0));
        statistics.setKillStreak(document.getInteger("kill_streak", 0));
        statistics.setCredits(document.getInteger("credits", 0));
        switch ((String) document.get("ptime")) {
            case "day":
                currentTimeType = PlayerTimeType.DAY;
                break;
            case "sunset":
                currentTimeType = PlayerTimeType.SUNSET;
                break;
            case "night":
                currentTimeType = PlayerTimeType.NIGHT;
                break;
        }
    }

    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("deaths", statistics.getDeaths());
        map.put("highest_kill_streak", statistics.getHighestKillStreak());
        map.put("kills", statistics.getKills());
        map.put("kill_streak", statistics.getKillStreak());
        map.put("xp", xp);
        map.put("credits", statistics.getCredits());
        map.put("purchased_kits", purchasedKits);
        map.put("scoreboard_enabled", scoreboardEnabled);
        map.put("name", name);
        map.put("ptime", currentTimeType.toString());
        if (lastKit != null) {
            map.put("last_kit_name", lastKit.getName());
        }

        return map;
    }

    public void setKit(Kit kit) {
        this.currentKit = this.lastKit = kit;
    }

    public void addPurchasedKit(Kit kit) {
        this.purchasedKits.add(kit.getName());
    }

    public List<String> getPurchasedKits() {
        List<String> ownedKits = new ArrayList<>(purchasedKits);
        freeKits.stream().filter(freeKit -> !ownedKits.contains(freeKit)).forEach(ownedKits::add);
        return ownedKits;
    }

    public void pull(boolean async) {
        plugin.getMongo().getDocument(async, "profiles", uuid, document -> {
            if (document != null) {
                deserialize(document);
            }
        });

        loaded = true;
    }

    public void push(boolean async) {
        MongoUpdate mu = new MongoUpdate("profiles", uuid);
        mu.setUpdate(this.serialize());
        plugin.getMongo().massUpdate(async, mu);
    }
}
