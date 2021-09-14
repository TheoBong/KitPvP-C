package cc.kitpvp.KitPvP.inventories;

import cc.kitpvp.KitPvP.KitPvPPlugin;
import cc.kitpvp.KitPvP.util.inventoryapi.InventoryWrapper;
import cc.kitpvp.KitPvP.util.inventoryapi.PlayerAction;
import cc.kitpvp.KitPvP.util.inventoryapi.PlayerInventoryWrapper;
import cc.kitpvp.KitPvP.util.item.ItemBuilder;
import cc.kitpvp.KitPvP.util.message.CC;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class KitPlayerWrapper extends PlayerInventoryWrapper {
    private final KitPvPPlugin plugin;

    public KitPlayerWrapper(KitPvPPlugin plugin) {
        super("Kit", 3);
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
        inventoryWrapper.fillBorder(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 1));

        inventoryWrapper.setItem(2, 2, new ItemBuilder(Material.CHEST).name(CC.GOLD + "Choose a Kit").build(), new PlayerAction((actionPlayer, clickType) -> {
            actionPlayer.getOpenInventory().close();
            plugin.getInventoryManager().getPlayerWrapper(KitSelectorPlayerWrapper.class).open(actionPlayer);
        }));
        inventoryWrapper.setItem(2, 8, new ItemBuilder(Material.ENDER_CHEST).name(CC.GOLD + "Purchase a Kit").build(), new PlayerAction((actionPlayer, clickType)-> {
            actionPlayer.getOpenInventory().close();
            plugin.getInventoryManager().getPlayerWrapper(KitShopPlayerWrapper.class).open(actionPlayer);
        }));
    }
}
