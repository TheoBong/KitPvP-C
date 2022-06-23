package cc.kitpvp.kitpvp.inventories;

import cc.kitpvp.kitpvp.KitPvPPlugin;
import cc.kitpvp.kitpvp.util.inventoryapi.InventoryWrapper;
import cc.kitpvp.kitpvp.util.inventoryapi.PlayerAction;
import cc.kitpvp.kitpvp.util.inventoryapi.PlayerInventoryWrapper;
import cc.kitpvp.kitpvp.util.item.ItemBuilder;
import cc.kitpvp.kitpvp.util.message.CC;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

public class LeaderboardWrapper extends PlayerInventoryWrapper {
    private final KitPvPPlugin plugin;

    public LeaderboardWrapper(KitPvPPlugin plugin) {
        super("Leaderboards", 3);
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
        List<String> killsLeaderboard = plugin.getLeaderBoardManager().getKillsLeaderboard();
        List<String> creditsLeaderboard = plugin.getLeaderBoardManager().getCreditsLeaderboard();
        List<String> killStreakLeaderboard = plugin.getLeaderBoardManager().getKillStreakLeaderboard();

        inventoryWrapper.setItem(2, 4, new ItemBuilder(Material.DIAMOND_SWORD)
                .name(CC.PRIMARY + "Kills Leaderboard")
                .loreLine(killsLeaderboard.get(0))
                .loreLine(killsLeaderboard.get(1))
                .loreLine(killsLeaderboard.get(2))
                .build(), new PlayerAction((actionPlayer, clickType) -> {
            update(player, inventoryWrapper);
        }, false));

        inventoryWrapper.setItem(2, 5, new ItemBuilder(Material.GOLD_INGOT)
                .name(CC.PRIMARY + "Credits Leaderboard")
                .loreLine(creditsLeaderboard.get(0))
                .loreLine(creditsLeaderboard.get(1))
                .loreLine(creditsLeaderboard.get(2))
                .build(), new PlayerAction((actionPlayer, clickType) -> {
            update(player, inventoryWrapper);
        }, false));

        inventoryWrapper.setItem(2, 6, new ItemBuilder(Material.DIAMOND_CHESTPLATE)
                .name(CC.PRIMARY + "KillStreak Leaderboard")
                .loreLine(killStreakLeaderboard.get(0))
                .loreLine(killStreakLeaderboard.get(1))
                .loreLine(killStreakLeaderboard.get(2))
                .build(), new PlayerAction((actionPlayer, clickType) -> {
            update(player, inventoryWrapper);
        }, false));
    }
}
