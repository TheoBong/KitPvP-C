package com.bongbong.kitpvp.kits.impl.ffa;

import com.bongbong.kitpvp.KitPvPPlugin;
import com.bongbong.kitpvp.kits.Kit;
import com.bongbong.kitpvp.kits.KitContents;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import com.bongbong.kitpvp.util.item.ItemBuilder;

import java.util.List;

public class Turtle extends Kit {
    public Turtle(KitPvPPlugin plugin) {
        super(plugin, "Turtle", Material.ANVIL, "Takes no knockback while sneaking.");
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
        builder.addItem(new ItemBuilder(Material.FISHING_ROD).unbreakable(true).build());
        builder.addArmor(
                new ItemStack(Material.DIAMOND_BOOTS),
                new ItemStack(Material.IRON_LEGGINGS),
                new ItemStack(Material.IRON_CHESTPLATE),
                new ItemStack(Material.DIAMOND_HELMET)
        );

        return builder;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerDamageByPlayer(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)
                || (!(event.getDamager() instanceof Player) && (!(event.getDamager() instanceof Arrow)
                || !(((Arrow) event.getDamager()).getShooter() instanceof Player)))) {
            return;
        }

        final Player damager = event.getDamager() instanceof Player ? (Player) event.getDamager() : (Player) ((Arrow) event.getDamager()).getShooter();


        final Player victim = (Player) event.getEntity();


        if (!isInvalidKit(victim) && victim.isSneaking() || !isInvalidKit(damager) && damager.isSneaking()) {
            victim.damage(event.getFinalDamage());
            event.setCancelled(true);
        }
    }

}
