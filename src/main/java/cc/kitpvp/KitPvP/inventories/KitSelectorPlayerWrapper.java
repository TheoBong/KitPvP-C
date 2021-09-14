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

public class KitSelectorPlayerWrapper extends PlayerInventoryWrapper {
    private final KitPvPPlugin plugin;

    public KitSelectorPlayerWrapper(KitPvPPlugin plugin) {
        super("Kit Selector", 6);
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
        plugin.getKitManager().getKits().forEach(kit -> {
            final boolean kitOwned = kit.ownsKit(player);
            final String owned = kitOwned ? CC.GREEN + " Owned" : CC.RED + " Locked";
            final ItemBuilder kitItem = ItemBuilder.from(kit.getIcon().clone());
            kitItem.name(kit.getIcon().getItemMeta().getDisplayName() + owned);
            inventoryWrapper.addItem(kitItem.build(), new PlayerAction((actionPlayer, clickType) -> {
                if (kitOwned) kit.apply(actionPlayer); else actionPlayer.sendMessage("You don't own this kit!");
            }, true));
        });
    }
}
