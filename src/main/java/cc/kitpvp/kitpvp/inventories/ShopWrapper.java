package cc.kitpvp.kitpvp.inventories;

import cc.kitpvp.kitpvp.KitPvPPlugin;
import cc.kitpvp.kitpvp.player.Profile;
import cc.kitpvp.kitpvp.util.inventoryapi.PlayerAction;
import cc.kitpvp.kitpvp.util.inventoryapi.SimpleInventoryWrapper;
import cc.kitpvp.kitpvp.util.item.ItemBuilder;
import cc.kitpvp.kitpvp.util.message.CC;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ShopWrapper extends SimpleInventoryWrapper {
    private final KitPvPPlugin plugin;

    private int count = 2;
    private int row = 2;

    public ShopWrapper(KitPvPPlugin plugin) {
        super("Shop", 3);
        this.plugin = plugin;
    }

    @Override
    public void init() {
        fillBorder(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 1));

        if (count > 8) {
            row++;
            count = 2;
        }

        ItemStack repair = new ItemBuilder(Material.ANVIL).name(CC.GOLD + "Repair").lore(CC.YELLOW + "Click to repair for 50 gold.").build();
        ItemStack healthFish = new ItemBuilder(Material.RAW_FISH).name(CC.GOLD + "Health fish").lore(CC.YELLOW + "Purchase health fish for 20 gold.").build();
        ItemStack regenFish = new ItemBuilder(Material.RAW_FISH).name(CC.GOLD + "Regen fish").lore(CC.YELLOW + "Purchase regen fish for 20 gold.").build();
        ItemStack resFish = new ItemBuilder(Material.RAW_FISH).name(CC.GOLD + "Resistance fish").lore(CC.YELLOW + "Purchase resistance fish for 40 gold.").build();
        ItemStack strengthFish = new ItemBuilder(Material.RAW_FISH).name(CC.GOLD + "Strength fish").lore(CC.YELLOW + "Purchase strength fish for 60 gold.").build();
        ItemStack speedFish = new ItemBuilder(Material.RAW_FISH).name(CC.GOLD + "Speed fish").lore(CC.YELLOW + "Purchase speed fish for 80 gold.").build();

        ItemStack healthFish2 = new ItemBuilder(Material.RAW_FISH).name(CC.GOLD + "Health fish").build();
        ItemStack regenFish2 = new ItemBuilder(Material.RAW_FISH).name(CC.GOLD + "Regen fish").build();
        ItemStack resFish2 = new ItemBuilder(Material.RAW_FISH).name(CC.GOLD + "Resistance fish").build();
        ItemStack strengthFish2 = new ItemBuilder(Material.RAW_FISH).name(CC.GOLD + "Strength fish").build();
        ItemStack speedFish2 = new ItemBuilder(Material.RAW_FISH).name(CC.GOLD + "Speed fish").build();

        setItem(row, count++, repair, new PlayerAction((actionPlayer, clickType) -> actionPlayer.performCommand("repair"), true));

        setItem(row, count++, healthFish, new PlayerAction((actionPlayer, clickType) -> {
            Profile profile = plugin.getPlayerManager().getProfile(actionPlayer);
            if (profile.getStatistics().getCredits() >= 20) {
                actionPlayer.getInventory().addItem(healthFish2);
                profile.getStatistics().setCredits(profile.getStatistics().getCredits() - 20);
            } else {
                actionPlayer.sendMessage("Not enough gold in bank!");
            }
        }, false));

        setItem(row, count++, regenFish, new PlayerAction((actionPlayer, clickType) -> {
            Profile profile = plugin.getPlayerManager().getProfile(actionPlayer);
            if (profile.getStatistics().getCredits() >= 20) {
                actionPlayer.getInventory().addItem(regenFish2);
                profile.getStatistics().setCredits(profile.getStatistics().getCredits() - 20);
            } else {
                actionPlayer.sendMessage("Not enough gold in bank!");
            }
        }, false));

        setItem(row, count++, resFish, new PlayerAction((actionPlayer, clickType) -> {
            Profile profile = plugin.getPlayerManager().getProfile(actionPlayer);
            if (profile.getStatistics().getCredits() >= 40) {
                actionPlayer.getInventory().addItem(resFish2);
                profile.getStatistics().setCredits(profile.getStatistics().getCredits() - 40);
            } else {
                actionPlayer.sendMessage("Not enough gold in bank!");
            }
        }, false));

        setItem(row, count++, strengthFish, new PlayerAction((actionPlayer, clickType) -> {
            Profile profile = plugin.getPlayerManager().getProfile(actionPlayer);
            if (profile.getStatistics().getCredits() >= 60) {
                actionPlayer.getInventory().addItem(strengthFish2);
                profile.getStatistics().setCredits(profile.getStatistics().getCredits() - 60);
            } else {
                actionPlayer.sendMessage("Not enough gold in bank!");
            }
        }, false));

        setItem(row, count++, speedFish, new PlayerAction((actionPlayer, clickType) -> {
            Profile profile = plugin.getPlayerManager().getProfile(actionPlayer);
            if (profile.getStatistics().getCredits() >= 60) {
                actionPlayer.getInventory().addItem(speedFish2);
                profile.getStatistics().setCredits(profile.getStatistics().getCredits() - 60);
            } else {
                actionPlayer.sendMessage("Not enough gold in bank!");
            }
        }, false));
    }


    @Override
    public void update() {
        // NO-OP
    }
}
