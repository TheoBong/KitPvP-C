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

import java.util.List;

public class KitShopPlayerWrapper extends PlayerInventoryWrapper {
    private final KitPvPPlugin plugin;

    public KitShopPlayerWrapper(KitPvPPlugin plugin) {
        super("Kit Shop", 6);
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
            boolean kitOwned = plugin.getPlayerManager().getProfile(player).getPurchasedKits().contains(kit.getName());
            String owned = kitOwned ? CC.GREEN + " Owned" : CC.RED + " Unowned";
            List<String> oldLore = kit.getIcon().getItemMeta().getLore();
            ItemBuilder kitItem = ItemBuilder.from(kit.getIcon().clone());
            kitItem.name(kit.getIcon().getItemMeta().getDisplayName() + owned);
            if (!kitOwned) {
                kitItem.lore(oldLore.get(0), CC.GREEN + "Click to purchase for 500 credits.");
            }
            inventoryWrapper.addItem(kitItem.build(), new PlayerAction((actionPlayer, clickType) -> kit.purchaseKit(actionPlayer), true));
        });
    }
}
