package com.bongbong.kitpvp.kits.impl.ffa;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import com.bongbong.kitpvp.util.item.ItemBuilder;
import com.bongbong.kitpvp.KitPvPPlugin;
import com.bongbong.kitpvp.kits.Kit;
import com.bongbong.kitpvp.kits.KitContents;

import java.util.Collections;
import java.util.List;

public class Flash extends Kit {
    public Flash(KitPvPPlugin plugin) {
        super(plugin, "Flash", Material.SUGAR, "Zoom around the map at the speed of sound.");
    }

    @Override
    protected void onEquip(Player player) {
        player.setWalkSpeed(0.3f);
    }

    @Override
    protected List<PotionEffect> effects() {
        return Collections.singletonList(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 2));
    }

    @Override
    protected KitContents.Builder contentsBuilder() {
        KitContents.Builder builder = KitContents.newBuilder();
        builder.addItem(new ItemBuilder(Material.DIAMOND_SWORD).enchant(Enchantment.DAMAGE_ALL, 2).unbreakable(true).build());
        builder.addItem(new ItemBuilder(Material.FISHING_ROD).unbreakable(true).build());
        return builder;
    }
}
