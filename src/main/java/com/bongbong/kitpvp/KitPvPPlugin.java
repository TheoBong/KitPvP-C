package com.bongbong.kitpvp;

import com.bongbong.kitpvp.commands.BaseCommand;
import com.bongbong.kitpvp.commands.impl.staff.*;
import com.bongbong.kitpvp.duels.DuelsOccupationManager;
import com.bongbong.kitpvp.duels.arenas.DuelsArenaManager;
import com.bongbong.kitpvp.duels.kits.DuelsKitManager;
import com.bongbong.kitpvp.inventories.*;
import com.bongbong.kitpvp.listeners.*;
import com.bongbong.kitpvp.listeners.bukkit.entity.EntityChangeBlockListener;
import com.bongbong.kitpvp.listeners.bukkit.entity.EntityDamageByEntityListener;
import com.bongbong.kitpvp.listeners.bukkit.entity.EntityDamageListener;
import com.bongbong.kitpvp.listeners.bukkit.entity.EntityExplodeListener;
import com.bongbong.kitpvp.listeners.bukkit.entity.EntityRegainHealthListener;
import com.bongbong.kitpvp.listeners.bukkit.entity.EntitySpawnListener;
import com.bongbong.kitpvp.listeners.bukkit.inventory.InventoryClickListener;
import com.bongbong.kitpvp.listeners.bukkit.inventory.InventoryMoveItemListener;
import com.bongbong.kitpvp.listeners.bukkit.player.*;
import com.bongbong.kitpvp.listeners.bukkit.potion.PotionSplashListener;
import com.bongbong.kitpvp.listeners.bukkit.projectile.ProjectileHitListener;
import com.bongbong.kitpvp.listeners.bukkit.projectile.ProjectileLaunchListener;
import com.bongbong.kitpvp.listeners.bukkit.world.BlockBreakListener;
import com.bongbong.kitpvp.listeners.bukkit.world.BlockFromToListener;
import com.bongbong.kitpvp.listeners.bukkit.world.BlockPlaceListener;
import com.bongbong.kitpvp.listeners.packets.EnderpearlSound;
import com.bongbong.kitpvp.managers.KitManager;
import com.bongbong.kitpvp.managers.LeaderBoardManager;
import com.bongbong.kitpvp.managers.PlayerManager;
import com.bongbong.kitpvp.managers.RegionManager;
import com.bongbong.kitpvp.scoreboard.KitPvPAdapter;
import com.bongbong.kitpvp.storage.flatfile.Config;
import com.bongbong.kitpvp.storage.mongo.Mongo;
import com.bongbong.kitpvp.tasks.CooldownRunnable;
import com.bongbong.kitpvp.util.EntityHider;
import com.bongbong.kitpvp.util.inventoryapi.InventoryManager;
import com.bongbong.kitpvp.util.scoreboardapi.ScoreboardApi;
import com.bongbong.kitpvp.util.structure.Cuboid;
import com.bongbong.kitpvp.commands.impl.*;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import lombok.Getter;
import lombok.Setter;
import me.jumper251.replay.api.ReplayAPI;
import me.jumper251.replay.replaysystem.Replay;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandMap;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.lang.reflect.Field;
import java.util.Arrays;

public class KitPvPPlugin extends JavaPlugin {
    private CommandMap commandMap;

    @Getter private Config locationConfig;
    @Setter @Getter private Location spawnLocation;
    @Setter @Getter private Location killLeaderboardLoc;
    @Setter @Getter private Location killStreakLeaderboardLoc;
    @Setter @Getter private Location creditsLeaderboardLoc;
    @Setter @Getter private Cuboid spawnCuboid;
    @Getter private ReplayAPI replayAPI;
    @Getter @Setter private Replay FFAReplay;
    @Setter @Getter private Hologram killHologram;
    @Setter @Getter private Hologram killStreakHologram;
    @Setter @Getter private Hologram creditsHologram;

    @Getter private ProtocolManager protocolManager;
    @Getter private Mongo mongo;

    @Getter private PlayerManager playerManager;
    @Getter private KitManager kitManager;
    @Getter private InventoryManager inventoryManager;
    @Getter private RegionManager regionManager;
    @Getter private LeaderBoardManager leaderBoardManager;

    @Getter private BukkitTask cooldownTask;

    @Getter private EntityHider entityHider;
    @Getter private DuelsArenaManager duelsArenaManager;
    @Getter private DuelsKitManager duelsKitManager;
    @Getter private DuelsOccupationManager duelsOccupationManager;

    private void registerSerializableClass(Class<?> clazz) {
        if (ConfigurationSerializable.class.isAssignableFrom(clazz)) {
            Class<? extends ConfigurationSerializable> serializable = clazz.asSubclass(ConfigurationSerializable.class);
            ConfigurationSerialization.registerClass(serializable);
        }
    }

    @Override
    public void onEnable() {
        registerSerializableClass(Cuboid.class);

        World mainWorld = getServer().getWorlds().get(0);

        locationConfig = new Config(this, "locations");
        locationConfig.addDefault("spawn", mainWorld.getSpawnLocation());
        locationConfig.addDefault("spawn-cuboid", new Cuboid(mainWorld.getSpawnLocation()));
        locationConfig.copyDefaults();

        spawnLocation = locationConfig.getLocation("spawn");
        spawnCuboid = (Cuboid) locationConfig.get("spawn-cuboid");

        replayAPI = ReplayAPI.getInstance();

        killLeaderboardLoc = locationConfig.getLocation("kills-leaderboard");
        killStreakLeaderboardLoc = locationConfig.getLocation("killstreak-leaderboard");
        creditsLeaderboardLoc = locationConfig.getLocation("credits-leaderboard");

        mongo = new Mongo(this);

        this.protocolManager = ProtocolLibrary.getProtocolManager();
        this.entityHider = new EntityHider(this, EntityHider.Policy.BLACKLIST);

        playerManager = new PlayerManager(this);
        kitManager = new KitManager(this);
        regionManager = new RegionManager();
        inventoryManager = new InventoryManager(this);
        leaderBoardManager = new LeaderBoardManager(this);

        this.duelsArenaManager = new DuelsArenaManager(this);
        this.duelsKitManager = new DuelsKitManager(this);
        this.duelsOccupationManager = new DuelsOccupationManager(this);

        inventoryManager.registerPlayerWrapper(new KitPlayerWrapper(this));
        inventoryManager.registerPlayerWrapper(new LeaderboardWrapper(this));
        inventoryManager.registerPlayerWrapper(new KitSelectorPlayerWrapper(this));
        inventoryManager.registerPlayerWrapper(new KitShopPlayerWrapper(this));
        inventoryManager.registerWrapper(new ShopWrapper(this));
        inventoryManager.registerPlayerWrapper(new SettingsPlayerWrapper(this));

        new ScoreboardApi(this, new KitPvPAdapter(this), true);
        this.cooldownTask = Bukkit.getScheduler().runTaskTimerAsynchronously(this, new CooldownRunnable(this), 2, 2);

        try {
            Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);

            commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());
        } catch (Exception e) {
            e.printStackTrace();
        }

        registerCommand(new ReportCommand(this));

        registerCommand(new SpawnCommand(this));
        registerCommand(new EditRegionCommand(this));
        registerCommand(new SetSpawnCommand(this));
        registerCommand(new CreditsCommand(this));
        registerCommand(new ClearKitCommand(this));
        registerCommand(new KitCommand(this));
        registerCommand(new KitShopCommand(this));
        registerCommand(new StatisticsCommand(this));
        registerCommand(new LeaderboardCommand(this));
        registerCommand(new DepositCommand(this));
        registerCommand(new HologramCommand(this));
        registerCommand(new ShopCommand(this));
        registerCommand(new RepairCommand(this));
        registerCommand(new HelpCommand());

        registerCommand(new SpectateCommand(this));
        registerCommand(new DuelCommand(this));
        registerCommand(new AcceptCommand(this));
        registerCommand(new DKitCommand(this));
        registerCommand(new ArenaCommand(this));
        registerCommand(new InventoryCommand(this));
        registerCommand(new PingCommand());

        Listener[] listeners = {

                // Entity
                new EntityDamageByEntityListener(this),
                new EntityDamageListener(this),
                new EntityRegainHealthListener(this),
                new EntitySpawnListener(this),
                new EntityChangeBlockListener(this),
                new EntityExplodeListener(this),

                // Inventory
                new InventoryClickListener(this),
                new InventoryMoveItemListener(this),

                // Player
                new FoodLevelChangeListener(this),
                new PlayerBucketEmptyListener(this),
                new PlayerDeathListener(this),
                new PlayerDropItemListener(this),
                new PlayerInteractListener(this),
                new PlayerItemConsumeListener(this),
                new PlayerMoveListener(this),
                new PlayerPickupItemListener(this),
                new PlayerQuitListener(this),
                new PlayerTeleportListener(this),
                new PlayerToggleFlightListener(this),
                new PlayerToggleSneakListener(this),

                // Potion
                new PotionSplashListener(this),

                // Projectile
                new ProjectileHitListener(this),
                new ProjectileLaunchListener(this),

                // World
                new BlockBreakListener(this),
                new BlockFromToListener(this),
                new BlockPlaceListener(this)
        };

        Arrays.stream(listeners).forEach(this::registerListener);


        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new EntityListener(this), this);
        getServer().getPluginManager().registerEvents(new RegionListener(this), this);
        getServer().getPluginManager().registerEvents(new WorldListener(this), this);
        getServer().getPluginManager().registerEvents(new FishListener(this), this);

        disableGameRules(mainWorld,
                "doDaylightCycle",
                "doFireTick",
                "doMobSpawning",
                "showDeathMessages",
                "mobGriefing"
        );

        if (killLeaderboardLoc != null) {
            killHologram = HologramsAPI.createHologram(this, killLeaderboardLoc);
        }

        if (killStreakLeaderboardLoc != null) {
            killStreakHologram = HologramsAPI.createHologram(this, killStreakLeaderboardLoc);
        }

        if (creditsLeaderboardLoc != null) {
            creditsHologram = HologramsAPI.createHologram(this, creditsLeaderboardLoc);
        }

        getServer().getScheduler().runTaskAsynchronously(this, leaderBoardManager);
        getServer().getScheduler().runTaskTimerAsynchronously(this, leaderBoardManager, 20L * 60 * 10, 20L * 60L * 10);

        protocolManager.addPacketListener(new EnderpearlSound(this));
    }

    @Override
    public void onDisable() {
        playerManager.saveAllProfiles();
        locationConfig.save();

        if (getFFAReplay() != null) {
            getReplayAPI().stopReplay(getFFAReplay().getId(), false);
        }

        this.cooldownTask.cancel();

        World mainWorld = getServer().getWorlds().get(0);

        mainWorld.getEntities().stream().filter(entity -> !(entity instanceof Player)).forEach(Entity::remove);
    }

    public void registerCommand(BaseCommand command) {
        commandMap.register(command.getName(), command);
    }

    public KitPvPPlugin registerListener(Listener listener) {
        getServer().getPluginManager().registerEvents(listener, this);
        return this;
    }

    public void addFFAReplayPlayer(Player player) {
        if (getFFAReplay() == null) {
            setFFAReplay(getReplayAPI().recordReplay(null, getServer().getConsoleSender(), player));
        } else {
            replayAPI.addPlayerToReplay(getFFAReplay().getId(), player);
        }
    }

    public void removeFFAReplayPlayer(Player player) {
        replayAPI.removePlayerFromReplay(getFFAReplay().getId(), player);
    }

    private void disableGameRules(World world, String... gameRules) {
        Arrays.stream(gameRules).forEach(gameRule -> world.setGameRuleValue(gameRule, "false"));
    }
}
