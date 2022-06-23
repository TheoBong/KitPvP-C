package cc.kitpvp.kitpvp.inventories;

import cc.kitpvp.kitpvp.KitPvPPlugin;
import cc.kitpvp.kitpvp.player.PlayerTimeType;
import cc.kitpvp.kitpvp.player.Profile;
import cc.kitpvp.kitpvp.util.inventoryapi.InventoryWrapper;
import cc.kitpvp.kitpvp.util.inventoryapi.PlayerAction;
import cc.kitpvp.kitpvp.util.inventoryapi.PlayerInventoryWrapper;
import cc.kitpvp.kitpvp.util.item.ItemBuilder;
import cc.kitpvp.kitpvp.util.message.CC;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SettingsPlayerWrapper extends PlayerInventoryWrapper {
    private final KitPvPPlugin plugin;

    public SettingsPlayerWrapper(KitPvPPlugin plugin) {
        super("Settings", 3);
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

        Profile profile = plugin.getPlayerManager().getProfile(player);
        String[] scoreboard = getSettingLore(profile.isScoreboardEnabled());
        PlayerTimeType timeType = profile.getCurrentTimeType();
        String[] time = getTimeLore(profile.getCurrentTimeType());

        inventoryWrapper.fillBorder(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 1));

        inventoryWrapper.setItem(2, 2, new ItemBuilder(Material.SIGN).name(CC.PRIMARY + "Toggle Scoreboard")
                .lore(scoreboard).build(), new PlayerAction((actionPlayer, clickType) -> {
            actionPlayer.performCommand("togglescoreboard");
            update(player, inventoryWrapper);
        }, false));
        inventoryWrapper.setItem(2, 3, new ItemBuilder(Material.WATCH).name(CC.PRIMARY + "Set Your Time").lore(
                time).build(), new PlayerAction((actionPlayer, clickType) -> { PlayerTimeType nextTimeType = timeType.nextTimeType();
            profile.setCurrentTimeType(nextTimeType);
            nextTimeType.apply(player);
            update(player, inventoryWrapper);
        }, false));
    }

    private String[] getTimeLore(PlayerTimeType currentTimeType) {
        String one = (currentTimeType == PlayerTimeType.DAY ? CC.GREEN : CC.GRAY) + "Day";
        String two = (currentTimeType == PlayerTimeType.SUNSET ? CC.GREEN : CC.GRAY) + "Sunset";
        String three = (currentTimeType == PlayerTimeType.NIGHT ? CC.GREEN : CC.GRAY) + "Night";
        return new String[] {one, two, three};
    }

    private String[] getSettingLore(boolean value) {
        String one = value ? CC.GREEN + "Enabled" : CC.GRAY + "Enabled";
        String two = !value ? CC.RED + "Disabled" : CC.GRAY + "Disabled";
        return new String[]{one, two};
    }
}
