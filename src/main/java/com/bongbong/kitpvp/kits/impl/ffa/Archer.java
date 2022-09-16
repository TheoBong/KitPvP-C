package com.bongbong.kitpvp.kits.impl.ffa;

import com.bongbong.kitpvp.KitPvPPlugin;
import com.bongbong.kitpvp.kits.Kit;
import com.bongbong.kitpvp.kits.KitContents;
import com.bongbong.kitpvp.util.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.List;

public class Archer extends Kit {
    public Archer(KitPvPPlugin plugin) {
        super(plugin, "Archer", Material.DIAMOND_SWORD, "A PvP kit with a bow.");
    }

    @Override
    protected void onEquip(Player player) {
        // NO-OP
    }

    @Override
    protected List<PotionEffect> effects() {
        return null;
    }

    @Override
    protected KitContents.Builder contentsBuilder() {
        KitContents.Builder builder = KitContents.newBuilder();

        builder.addItem(new ItemBuilder(Material.IRON_SWORD).enchant(Enchantment.DAMAGE_ALL, 1).unbreakable(true).build());
        builder.addItem(new ItemBuilder(Material.BOW).enchant(Enchantment.ARROW_DAMAGE, 1).enchant(Enchantment.ARROW_INFINITE, 1).unbreakable(true).build());
        builder.addItem(new ItemBuilder(Material.FISHING_ROD).unbreakable(true).build());
        builder.addItem(new ItemStack(Material.ARROW));
        builder.addArmor(
                new ItemBuilder(Material.CHAINMAIL_BOOTS).unbreakable(true).build(),
                new ItemBuilder(Material.CHAINMAIL_LEGGINGS).unbreakable(true).build(),
                new ItemBuilder(Material.CHAINMAIL_CHESTPLATE).unbreakable(true).build(),
                new ItemBuilder(Material.CHAINMAIL_HELMET).unbreakable(true).build()
        );

        return builder;
    }
}
