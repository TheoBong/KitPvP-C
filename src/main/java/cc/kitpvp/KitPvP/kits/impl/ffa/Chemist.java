package cc.kitpvp.KitPvP.kits.impl.ffa;

import com.google.common.collect.ImmutableList;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import cc.kitpvp.KitPvP.util.item.ItemBuilder;
import cc.kitpvp.KitPvP.KitPvPPlugin;
import cc.kitpvp.KitPvP.events.DeathEvent;
import cc.kitpvp.KitPvP.kits.Kit;
import cc.kitpvp.KitPvP.kits.KitContents;

import java.util.Collections;
import java.util.List;

public class Chemist extends Kit {

    private final ImmutableList<ItemStack> items;

    public Chemist(KitPvPPlugin plugin) {
        super(plugin, "Chemist", new ItemBuilder(Material.POTION).durability(16428).build(), "You receive debuff potions after killing another player.");
        ItemStack harm = new ItemBuilder(Material.POTION).durability(16428).amount(2).build();
        ItemStack slow = new ItemBuilder(Material.POTION).durability(16426).amount(2).build();
        ItemStack poison = new ItemBuilder(Material.POTION).durability(16388).amount(2).build();
        items = ImmutableList.of(harm, slow, poison);
    }

    @Override
    protected void onEquip(Player player) {
        int x = 0;
        for (ItemStack itemStack : items) {
            player.getInventory().setItem(++x, itemStack);
        }
    }

    @Override
    protected List<PotionEffect> effects() {
        return null;
    }

    @Override
    protected KitContents.Builder contentsBuilder() {
        KitContents.Builder builder = KitContents.newBuilder();

        builder.addItem(new ItemBuilder(Material.DIAMOND_SWORD).unbreakable(true).build());
        builder.addItem(new ItemBuilder(Material.FISHING_ROD).unbreakable(true).build());
        builder.addArmor(
                new ItemStack(Material.IRON_BOOTS),
                new ItemBuilder(Material.GOLD_LEGGINGS).enchant(Enchantment.DURABILITY, 4).build(),
                new ItemStack(Material.IRON_CHESTPLATE),
                new ItemStack(Material.IRON_HELMET)
        );

        return builder;
    }

    @EventHandler
    public void onKill(DeathEvent event) {
        if (event.getKillerProfile().getCurrentKit() == this) {
            items.forEach(itemStack -> event.getKiller().getInventory().addItem(itemStack));
        }
    }
}
