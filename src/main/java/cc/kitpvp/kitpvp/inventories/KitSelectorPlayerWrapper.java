package cc.kitpvp.kitpvp.inventories;

import cc.kitpvp.kitpvp.KitPvPPlugin;
import cc.kitpvp.kitpvp.util.inventoryapi.InventoryWrapper;
import cc.kitpvp.kitpvp.util.inventoryapi.PlayerAction;
import cc.kitpvp.kitpvp.util.inventoryapi.PlayerInventoryWrapper;
import cc.kitpvp.kitpvp.util.item.ItemBuilder;
import cc.kitpvp.kitpvp.util.message.CC;
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
            boolean kitOwned = kit.ownsKit(player);
            String owned = kitOwned ? CC.GREEN + " Owned" : CC.RED + " Locked";
            ItemBuilder kitItem = ItemBuilder.from(kit.getIcon().clone());
            kitItem.name(kit.getIcon().getItemMeta().getDisplayName() + owned);
            inventoryWrapper.addItem(kitItem.build(), new PlayerAction((actionPlayer, clickType) -> {
                if (kitOwned) kit.apply(actionPlayer); else actionPlayer.sendMessage("You don't own this kit!");
            }, true));
        });
    }
}
