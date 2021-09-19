package cc.kitpvp.KitPvP;

import cc.kitpvp.KitPvP.commands.BaseCommand;
import cc.kitpvp.KitPvP.commands.impl.*;
import cc.kitpvp.KitPvP.commands.impl.staff.CreditsCommand;
import cc.kitpvp.KitPvP.commands.impl.staff.EditRegionCommand;
import cc.kitpvp.KitPvP.commands.impl.staff.SetSpawnCommand;
import cc.kitpvp.KitPvP.inventories.*;
import cc.kitpvp.KitPvP.listeners.EntityListener;
import cc.kitpvp.KitPvP.listeners.PlayerListener;
import cc.kitpvp.KitPvP.listeners.RegionListener;
import cc.kitpvp.KitPvP.listeners.WorldListener;
import cc.kitpvp.KitPvP.managers.KitManager;
import cc.kitpvp.KitPvP.managers.LeaderBoardManager;
import cc.kitpvp.KitPvP.managers.PlayerManager;
import cc.kitpvp.KitPvP.managers.RegionManager;
import cc.kitpvp.KitPvP.scoreboard.KitPvPAdapter;
import cc.kitpvp.KitPvP.storage.flatfile.Config;
import cc.kitpvp.KitPvP.storage.mongo.Mongo;
import cc.kitpvp.KitPvP.util.inventoryapi.InventoryManager;
import cc.kitpvp.KitPvP.util.scoreboardapi.ScoreboardApi;
import cc.kitpvp.KitPvP.util.structure.Cuboid;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandMap;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.Arrays;

public class KitPvPPlugin extends JavaPlugin {
    private CommandMap commandMap;

    @Getter private Config locationConfig;
    @Setter @Getter private Location spawnLocation;
    @Setter @Getter private Cuboid spawnCuboid;

    @Getter private Mongo mongo;

    @Getter private PlayerManager playerManager;
    @Getter private KitManager kitManager;
    @Getter private InventoryManager inventoryManager;
    @Getter private RegionManager regionManager;
    @Getter private LeaderBoardManager leaderBoardManager;

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

        mongo = new Mongo(this);

        playerManager = new PlayerManager(this);
        kitManager = new KitManager(this);
        regionManager = new RegionManager();
        inventoryManager = new InventoryManager(this);
        leaderBoardManager = new LeaderBoardManager(this);

        inventoryManager.registerPlayerWrapper(new KitPlayerWrapper(this));
        inventoryManager.registerPlayerWrapper(new KitSelectorPlayerWrapper(this));
        inventoryManager.registerPlayerWrapper(new KitShopPlayerWrapper(this));
        inventoryManager.registerWrapper(new ShopWrapper());
        inventoryManager.registerPlayerWrapper(new SettingsPlayerWrapper(this));

        new ScoreboardApi(this, new KitPvPAdapter(this), true);

        try {
            Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);

            commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());
        } catch (Exception e) {
            e.printStackTrace();
        }

        registerCommand(new SpawnCommand(this));
        registerCommand(new EditRegionCommand(this));
        registerCommand(new SetSpawnCommand(this));
        registerCommand(new CreditsCommand(this));
        registerCommand(new ClearKitCommand(this));
        registerCommand(new KitCommand(this));
        registerCommand(new KitShopCommand(this));
        registerCommand(new StatisticsCommand(this));
        registerCommand(new LeaderboardCommand(this));

        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new EntityListener(this), this);
        getServer().getPluginManager().registerEvents(new RegionListener(this), this);
        getServer().getPluginManager().registerEvents(new WorldListener(), this);

        disableGameRules(mainWorld,
                "doDaylightCycle",
                "doFireTick",
                "doMobSpawning",
                "showDeathMessages",
                "mobGriefing"
        );

    }

    @Override
    public void onDisable() {
        playerManager.saveAllProfiles();
        locationConfig.save();

        World mainWorld = getServer().getWorlds().get(0);

        mainWorld.getEntities().stream().filter(entity -> !(entity instanceof Player)).forEach(Entity::remove);
    }

    public void registerCommand(BaseCommand command) {
        commandMap.register(command.getName(), command);
    }

    private void disableGameRules(World world, String... gameRules) {
        Arrays.stream(gameRules).forEach(gameRule -> world.setGameRuleValue(gameRule, "false"));
    }
}
