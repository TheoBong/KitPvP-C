package cc.kitpvp.KitPvP.inventories;

import cc.kitpvp.KitPvP.util.inventoryapi.PlayerAction;
import cc.kitpvp.KitPvP.util.inventoryapi.SimpleInventoryWrapper;
import cc.kitpvp.KitPvP.util.item.ItemBuilder;
import cc.kitpvp.KitPvP.util.message.CC;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ShopWrapper extends SimpleInventoryWrapper {
    private int count = 2;
    private int row = 2;

    public ShopWrapper() {
        super("Shop", 3);
    }

    @Override
    public void init() {
        fillBorder(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 1));

        if (count > 8) {
            row++;
            count = 2;
        }

        ItemStack repair = new ItemBuilder(Material.ANVIL).name(CC.GOLD + "Repair").lore(CC.YELLOW + "Click to repair for 50 credits.").build();
        ItemStack hotbarSoup = new ItemBuilder(Material.MUSHROOM_SOUP).amount(9).name(CC.GOLD + "Hotbar of Soup").lore(CC.YELLOW + "Click to purchase a hotbar of soup for 10 credits.").build();
        ItemStack refillSoup = new ItemBuilder(Material.MUSHROOM_SOUP).name(CC.GOLD + "Soup Refill").lore(CC.YELLOW + "Click to purchase a full inventory of soup for 25 credits.").build();
        ItemStack antiControl = new ItemBuilder(Material.FISHING_ROD).name(CC.GOLD + "Anti-Control").lore(CC.YELLOW + "Click to purchase immunity to scorpion for 50 credits.").build();

        setItem(row, count++, repair, new PlayerAction((actionPlayer, clickType) -> actionPlayer.performCommand("repair"), true));
        setItem(row, count++, hotbarSoup, new PlayerAction((actionPlayer, clickType) -> actionPlayer.performCommand("soup"), true));
        setItem(row, count++, refillSoup, new PlayerAction((actionPlayer, clickType) -> actionPlayer.performCommand("refill"), true));
        setItem(row, count++, antiControl, new PlayerAction((actionPlayer, clickType) -> actionPlayer.performCommand("anticontrol"), true));

    }


    @Override
    public void update() {
        // NO-OP
    }
}
