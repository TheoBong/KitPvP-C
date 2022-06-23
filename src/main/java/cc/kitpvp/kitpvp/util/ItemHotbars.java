package cc.kitpvp.kitpvp.util;

import cc.kitpvp.kitpvp.util.item.ItemBuilder;
import cc.kitpvp.kitpvp.util.message.CC;
import cc.kitpvp.kitpvp.util.message.Color;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
    ),

    SPECTATOR_ITEMS(
            new ItemStack[]{
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    new ItemBuilder(Material.REDSTONE).name(Color.translate("&cStop Spectating &7(Right Click)")).build(),
    }
    );

    private final ItemStack[] hotbar;

    public void apply(Player player) {
        IntStream.range(0, hotbar.length).forEach(i -> {
            ItemStack item = hotbar[i];
            player.getInventory().setItem(i, item == null ? new ItemStack(Material.AIR) : item.clone());
        });

        player.updateInventory();
    }

}
