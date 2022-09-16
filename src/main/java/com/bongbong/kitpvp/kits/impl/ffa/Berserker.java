package com.bongbong.kitpvp.kits.impl.ffa;

import com.bongbong.kitpvp.events.DeathEvent;
import com.bongbong.kitpvp.kits.KitContents;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import com.bongbong.kitpvp.util.item.ItemBuilder;
import com.bongbong.kitpvp.util.message.CC;
import com.bongbong.kitpvp.util.player.PlayerUtil;
import com.bongbong.kitpvp.KitPvPPlugin;
import com.bongbong.kitpvp.kits.Kit;

import java.util.List;

public class Berserker extends Kit {
    public Berserker(KitPvPPlugin plugin) {
        super(plugin, "Berserker", Material.BLAZE_POWDER, "You receive Strength I for 30 seconds after killing another player.");
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

        builder.addItem(new ItemBuilder(Material.DIAMOND_SWORD).name(CC.GOLD + "Berserker Sword").unbreakable(true).build());
        builder.addItem(new ItemBuilder(Material.FISHING_ROD).unbreakable(true).build());
        builder.addArmor(new ItemBuilder(Material.LEATHER_BOOTS).color(Color.GRAY).enchant(Enchantment.DURABILITY, 100).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4).build(),
                new ItemBuilder(Material.LEATHER_LEGGINGS).color(Color.RED).enchant(Enchantment.DURABILITY, 100).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4).build(),
                new ItemBuilder(Material.LEATHER_CHESTPLATE).color(Color.RED).enchant(Enchantment.DURABILITY, 100).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4).build(),
                new ItemBuilder(Material.LEATHER_HELMET).color(Color.GRAY).enchant(Enchantment.DURABILITY, 100).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4).build()
        );

        return builder;
    }

    @EventHandler
    public void onKill(DeathEvent event) {
        Player berserker = event.getKiller();
        if (event.getKillerProfile().getCurrentKit() == this) {
            if (PlayerUtil.hasEffect(berserker, PotionEffectType.INCREASE_DAMAGE)) {
                berserker.getActivePotionEffects().stream().filter(potionEffect -> potionEffect.getType().equals(PotionEffectType.INCREASE_DAMAGE)).map(PotionEffect::getType).forEach(berserker::removePotionEffect);
            }
            berserker.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 30, 0));
            berserker.sendMessage(CC.GREEN + "You have received strength for killing a player!");
        }
    }
}
