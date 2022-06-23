package cc.kitpvp.kitpvp.kits.impl.ffa;

import cc.kitpvp.kitpvp.KitPvPPlugin;
import cc.kitpvp.kitpvp.kits.Kit;
import cc.kitpvp.kitpvp.kits.KitContents;
import cc.kitpvp.kitpvp.util.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.List;

public class PvP extends Kit {
    public PvP(KitPvPPlugin plugin) {
        super(plugin, "PvP", Material.DIAMOND_SWORD, "The standard PvP kit.");
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

        builder.addItem(new ItemBuilder(Material.DIAMOND_SWORD).enchant(Enchantment.DAMAGE_ALL, 1).unbreakable(true).build());
        builder.addItem(new ItemBuilder(Material.FISHING_ROD).unbreakable(true).build());
        builder.addArmor(
                new ItemStack(Material.IRON_BOOTS),
                new ItemStack(Material.IRON_LEGGINGS),
                new ItemStack(Material.IRON_CHESTPLATE),
                new ItemStack(Material.IRON_HELMET)
        );

        return builder;
    }
}
