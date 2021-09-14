package cc.kitpvp.KitPvP.inventories;

import cc.kitpvp.KitPvP.KitPvPPlugin;
import cc.kitpvp.KitPvP.player.PlayerStatistics;
import cc.kitpvp.KitPvP.util.inventoryapi.InventoryWrapper;
import cc.kitpvp.KitPvP.util.inventoryapi.PlayerAction;
import cc.kitpvp.KitPvP.util.inventoryapi.PlayerInventoryWrapper;
import cc.kitpvp.KitPvP.util.item.ItemBuilder;
import cc.kitpvp.KitPvP.util.message.CC;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class StatsPlayerWrapper extends PlayerInventoryWrapper {
    private final KitPvPPlugin plugin;

    public StatsPlayerWrapper(KitPvPPlugin plugin) {
        super("Stats", 3);
        this.plugin = plugin;
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

        PlayerStatistics statistics = plugin.getPlayerManager().getProfile(player).getStatistics();

        ItemStack skeletonSkull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);

        inventoryWrapper.fillBorder(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 1));

        inventoryWrapper.setItem(row, count++, new ItemBuilder(Material.DIAMOND_SWORD).name(CC.PRIMARY + "Kills")
                .lore(CC.ACCENT + statistics.getKills()).build(), new PlayerAction((actionPlayer, clickType) -> {
                    actionPlayer.isOnline();
                    update(player, inventoryWrapper);
        }, false));
        inventoryWrapper.setItem(row, count++, ItemBuilder.from(skeletonSkull).name(CC.PRIMARY + "Deaths")
                .lore(CC.ACCENT + statistics.getDeaths()).build(), new PlayerAction((actionPlayer, clickType) -> {
            actionPlayer.isOnline();
            update(player, inventoryWrapper);
        }, false));
        inventoryWrapper.setItem(row, count++, new ItemBuilder(Material.BLAZE_POWDER).name(CC.PRIMARY + "Kill Streak")
                .lore(CC.ACCENT + statistics.getKillStreak()).build(), new PlayerAction((actionPlayer, clickType) -> {
            actionPlayer.isOnline();
            update(player, inventoryWrapper);
        }, false));
        inventoryWrapper.setItem(row, count++, new ItemBuilder(Material.REDSTONE).name(CC.PRIMARY + "KDR")
                .lore(CC.ACCENT + statistics.getKillDeathRatio()).build(), new PlayerAction((actionPlayer, clickType) -> {
            actionPlayer.isOnline();
            update(player, inventoryWrapper);
        }, false));
        inventoryWrapper.setItem(row, count++, new ItemBuilder(Material.GOLD_INGOT).name(CC.PRIMARY + "Credits")
                .lore(CC.ACCENT + statistics.getCredits()).build(), new PlayerAction((actionPlayer, clickType) -> {
            actionPlayer.isOnline();
            update(player, inventoryWrapper);
        }, false));
        inventoryWrapper.setItem(row, count++, new ItemBuilder(Material.BLAZE_ROD).name(CC.PRIMARY + "Highest Kill Streak")
                .lore(CC.ACCENT + statistics.getHighestKillStreak()).build(), new PlayerAction((actionPlayer, clickType) -> {
            actionPlayer.isOnline();
            update(player, inventoryWrapper);
        }, false));
    }
}
