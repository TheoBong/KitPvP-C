package cc.kitpvp.KitPvP.kits.impl.ffa;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import cc.kitpvp.KitPvP.util.item.ItemBuilder;
import cc.kitpvp.KitPvP.util.message.CC;
import cc.kitpvp.KitPvP.KitPvPPlugin;
import cc.kitpvp.KitPvP.kits.Kit;
import cc.kitpvp.KitPvP.kits.KitContents;

import java.util.Collections;
import java.util.List;

public class Clout extends Kit {

    public Clout(KitPvPPlugin plugin) {
        super(plugin, "Clout", Material.GOLD_SWORD, "Increase the amount of credits you gain while flashing your immense clout!");
    }

    @Override
    protected void onEquip(Player player) {

    }

    @Override
    protected List<PotionEffect> effects() {
        return null;
    }

    @Override
    protected KitContents.Builder contentsBuilder() {
        KitContents.Builder builder = KitContents.newBuilder();

        builder.addItem(new ItemBuilder(Material.GOLD_SWORD)
                .enchant(Enchantment.DAMAGE_ALL, 2)
                .unbreakable(true)
                .name(CC.GOLD + "Clout Stick")
                .build());
        builder.addItem(new ItemBuilder(Material.FISHING_ROD).unbreakable(true).build());
        builder.addArmor(
                new ItemBuilder(Material.GOLD_BOOTS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).enchant(Enchantment.DURABILITY, 5).build(),
                new ItemBuilder(Material.GOLD_LEGGINGS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).enchant(Enchantment.DURABILITY, 5).build(),
                new ItemBuilder(Material.GOLD_CHESTPLATE).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).enchant(Enchantment.DURABILITY, 5).build(),
                new ItemBuilder(Material.GOLD_HELMET).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).enchant(Enchantment.DURABILITY, 5).build()
        );

        return builder;
    }
}
