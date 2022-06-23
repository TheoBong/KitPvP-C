package cc.kitpvp.kitpvp.player;

import cc.kitpvp.kitpvp.KitPvPPlugin;
import cc.kitpvp.kitpvp.duels.DuelRequest;
import cc.kitpvp.kitpvp.duels.Occupation;
import cc.kitpvp.kitpvp.kits.Kit;
import cc.kitpvp.kitpvp.managers.PlayerManager;
import cc.kitpvp.kitpvp.storage.mongo.MongoUpdate;
import cc.kitpvp.kitpvp.util.ItemHotbars;
import cc.kitpvp.kitpvp.util.Levels;
import cc.kitpvp.kitpvp.util.item.ItemBuilder;
import cc.kitpvp.kitpvp.util.message.CC;
import cc.kitpvp.kitpvp.util.timer.Timer;
import cc.kitpvp.kitpvp.util.timer.impl.DoubleTimer;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.*;


public @Data class Profile {
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
    private PlayerTimeType currentTimeType = PlayerTimeType.DAY;

    private boolean scoreboardEnabled = true;

    private Map<UUID, DuelRequest> duelRequests;
    private PreviousMatch previousMatch;
    private Occupation duelOccupation;
    private Map<Cooldown.Type, Cooldown> cooldowns;
    private Integer doubleJumps;
    private Integer tripleShots;
    private Integer repulsors;
    private boolean spectatorVisibility;

    public Profile(KitPvPPlugin plugin, String name, UUID uuid) {
        this.plugin = plugin;
        this.name = name;
        this.uuid = uuid;

        this.duelRequests = new HashMap<>();
        this.cooldowns = new HashMap<>();
        this.doubleJumps = 10;
        this.tripleShots = 10;
        this.repulsors = 10;

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


    public void duelReset() {
        Player player = getPlayer();
        doubleJumps = 10;
        tripleShots = 10;
        repulsors = 10;

        setCurrentKit(null);

        if(player != null) {
            player.setFoodLevel(20);
            player.setMaxHealth(20);
            player.setHealth(20);
            player.setGameMode(GameMode.SURVIVAL);
            player.setLevel(0);
            player.setExp(0);

            player.getInventory().clear();
            player.getInventory().setArmorContents(null);

            player.setAllowFlight(false);
            player.setFlying(false);
            player.setWalkSpeed(0.2F);
            player.setFlySpeed(0.1F);
            player.setFireTicks(0);
            player.setSaturation(20);
            for(PotionEffect effect : player.getActivePotionEffects()){
                player.removePotionEffect(effect.getType());
            }

            ItemHotbars.SPAWN_ITEMS.apply(player);

            if (getLastKit() != null) {
                player.getInventory().setItem(1, new ItemBuilder(Material.WATCH).name(CC.YELLOW + "Last Kit " + CC.SECONDARY + "(" + getLastKit().getName() + ")").build());
                player.updateInventory();
            }
        }
    }

    public void playerUpdate() {
        Player player = getPlayer();
        if(player != null) {
            playerUpdateVisibility();
            plugin.getPlayerManager().acquireSpawnProtection(player);
            ItemHotbars.SPAWN_ITEMS.apply(player);
            if (getLastKit() != null) {
                player.getInventory().setItem(1, new ItemBuilder(Material.WATCH).name(CC.YELLOW + "Last Kit " + CC.SECONDARY + "(" + getLastKit().getName() + ")").build());
                player.updateInventory();
            }

            Location location = plugin.getSpawnLocation();
            if (location != null) {
                player.teleport(plugin.getSpawnLocation());
            } else {
                player.sendMessage(ChatColor.RED + "You could not be teleported to the lobby. Please notify staff!");
            }
        }
    }

    public void playerUpdateVisibility() {
        Player player = getPlayer();
        if(player != null) {
            if(duelOccupation != null) {
                if(duelOccupation.getCurrentPlaying().contains(player)) {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        if(!duelOccupation.seeEveryone() && !duelOccupation.getCurrentPlaying().contains(p)) {
                            player.hidePlayer(p);
                        } else {
                            player.showPlayer(p);
                        }
                    }
                } else {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        boolean b =  duelOccupation.getAllPlayers().contains(p);
                        if (b) {
                            player.showPlayer(p);
                        } else {
                            player.hidePlayer(p);
                        }
                    }
                }
            } else {
                PlayerManager pm = plugin.getPlayerManager();
                for(Player p : Bukkit.getOnlinePlayers()) {
                    Profile profile = pm.getProfile(p.getUniqueId());
                    if(profile.getDuelOccupation() != null && profile.getDuelOccupation().getSpectators().get(p.getUniqueId()) != null) {
                        player.hidePlayer(p);
                    } else {
                        player.showPlayer(p);
                    }
                }
            }
        }
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public void push(boolean async) {
        MongoUpdate mu = new MongoUpdate("profiles", uuid);
        mu.setUpdate(this.serialize());
        plugin.getMongo().massUpdate(async, mu);
    }
}
