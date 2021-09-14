package cc.kitpvp.KitPvP.util;

import cc.kitpvp.KitPvP.util.item.ItemBuilder;
import cc.kitpvp.KitPvP.util.message.CC;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.stream.IntStream;

@RequiredArgsConstructor
public enum ItemHotbars {
    SPAWN_ITEMS(
            new ItemStack[]{
                    new ItemBuilder(Material.CHEST).name(CC.GOLD + "Kit Selector").build(),
                    null,
                    null,
                    null,
                    new ItemBuilder(Material.PAPER).name(CC.GRAY + "Your Stats").build(),
                    null,
                    null,
                    new ItemBuilder(Material.ENDER_CHEST).name(CC.GOLD + "Kit Shop").build(),
                    new ItemBuilder(Material.EYE_OF_ENDER).name(CC.GOLD + "Settings").build(),
            }
    );

    private final ItemStack[] hotbar;

    public void apply(final Player player) {
        IntStream.range(0, hotbar.length).forEach(i -> {
            final ItemStack item = hotbar[i];
            player.getInventory().setItem(i, item == null ? new ItemStack(Material.AIR) : item.clone());
        });

        player.updateInventory();
    }
}
