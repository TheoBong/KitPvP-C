package com.bongbong.kitpvp.inventories;

import com.bongbong.kitpvp.KitPvPPlugin;
import com.bongbong.kitpvp.util.inventoryapi.InventoryWrapper;
import com.bongbong.kitpvp.util.inventoryapi.PlayerAction;
import com.bongbong.kitpvp.util.inventoryapi.PlayerInventoryWrapper;
import com.bongbong.kitpvp.util.item.ItemBuilder;
import com.bongbong.kitpvp.util.message.CC;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class StatsPlayerWrapper extends PlayerInventoryWrapper {
    private final KitPvPPlugin plugin;

    private int kills, deaths, kill_streak, credits, highest_kill_streak;
    private double kdr;

    public StatsPlayerWrapper(KitPvPPlugin plugin, String target, int kills, int deaths, int kill_streak, int credits, int highest_kill_streak) {
        super(target + " Stats", 3);

        this.plugin = plugin;

        this.kills = kills;
        this.deaths = deaths;
        this.kill_streak = kill_streak;
        this.credits = credits;
        this.highest_kill_streak = highest_kill_streak;

        this.kdr = kills == 0 ? 0.0 : deaths == 0 ? kills : Math.round(((double) kills / deaths) * 10.0) / 10.0;
    }

    @Override
    public void init(Player player, InventoryWrapper inventoryWrapper) {
        format(player, inventoryWrapper);
    }

    @Override
    public void update(Player player, InventoryWrapper inventoryWrapper) {
        format(player, inventoryWrapper);
    }

    private void format(Player player, InventoryWrapper inventoryWrapper) {
        int count = 2;
        int row = 2;

        if (count > 8) {
            row++;
            count = 2;
        }

        ItemStack skeletonSkull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);

        inventoryWrapper.fillBorder(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 1));

        inventoryWrapper.setItem(row, count++, new ItemBuilder(Material.DIAMOND_SWORD).name(CC.PRIMARY + "Kills")
                .lore(CC.ACCENT + kills).build(), new PlayerAction((actionPlayer, clickType) -> {
                    update(player, inventoryWrapper);
        }, false));
        inventoryWrapper.setItem(row, count++, ItemBuilder.from(skeletonSkull).name(CC.PRIMARY + "Deaths")
                .lore(CC.ACCENT + deaths).build(), new PlayerAction((actionPlayer, clickType) -> {
            update(player, inventoryWrapper);
        }, false));
        inventoryWrapper.setItem(row, count++, new ItemBuilder(Material.BLAZE_POWDER).name(CC.PRIMARY + "Kill Streak")
                .lore(CC.ACCENT + kill_streak).build(), new PlayerAction((actionPlayer, clickType) -> {
            update(player, inventoryWrapper);
        }, false));
        inventoryWrapper.setItem(row, count++, new ItemBuilder(Material.REDSTONE).name(CC.PRIMARY + "KDR")
                .lore(CC.ACCENT + kdr).build(), new PlayerAction((actionPlayer, clickType) -> {
            update(player, inventoryWrapper);
        }, false));
        inventoryWrapper.setItem(row, count++, new ItemBuilder(Material.GOLD_INGOT).name(CC.PRIMARY + "Credits")
                .lore(CC.ACCENT + credits).build(), new PlayerAction((actionPlayer, clickType) -> {
            actionPlayer.isOnline();
            update(player, inventoryWrapper);
        }, false));
        inventoryWrapper.setItem(row, count++, new ItemBuilder(Material.BLAZE_ROD).name(CC.PRIMARY + "Highest Kill Streak")
                .lore(CC.ACCENT + highest_kill_streak).build(), new PlayerAction((actionPlayer, clickType) -> {
            update(player, inventoryWrapper);
        }, false));
    }
}
