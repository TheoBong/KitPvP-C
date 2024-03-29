package com.bongbong.kitpvp.kits.impl.ffa;

import com.bongbong.kitpvp.KitPvPPlugin;
import com.bongbong.kitpvp.events.DeathEvent;
import com.bongbong.kitpvp.kits.Kit;
import com.bongbong.kitpvp.kits.KitContents;
import com.bongbong.kitpvp.player.PlayerState;
import com.bongbong.kitpvp.player.Profile;
import com.bongbong.kitpvp.util.item.ItemBuilder;
import com.bongbong.kitpvp.util.message.CC;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class Ninja extends Kit {

    public Ninja(KitPvPPlugin plugin) {
        super(plugin, "Ninja", Material.IRON_SWORD, "Teleports behind the last damaged player when sneaking.");
        registerCooldownTimer("ninja", 15);
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

        builder.addItem(new ItemBuilder(Material.IRON_SWORD).enchant(Enchantment.DAMAGE_ALL, 1).unbreakable(true).build());
        builder.addItem(new ItemBuilder(Material.FISHING_ROD).unbreakable(true).build());
        builder.addArmor(
                new ItemStack(Material.DIAMOND_BOOTS),
                new ItemStack(Material.IRON_LEGGINGS),
                new ItemStack(Material.IRON_CHESTPLATE)
        );

        return builder;
    }

    @EventHandler
    public void onKill(DeathEvent event) {
        if (event.getKillerProfile().getCurrentKit() == this) {
            event.getKillerProfile().setLastAttacked(null);
        }
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        Player ninja = event.getPlayer();
        Player victim = plugin.getServer().getPlayer(plugin.getPlayerManager().getProfile(ninja).getLastAttacked());

        if (ninja.isSneaking()) {
            return;
        }

        if (victim == null) {
            return;
        }

        Profile kitProfile = plugin.getPlayerManager().getProfile(ninja);

        if (kitProfile.getState() != PlayerState.FFA) {
            return;
        }

        if (isInvalidKit(ninja)) {
            return;
        }

        if (ninja.getLocation().distanceSquared(victim.getLocation()) >= 400) {
            ninja.sendMessage(CC.RED + "Target is too far away.");
            return;
        }

        if (isOnCooldown(ninja, "ninja")) {
            return;
        }
        ninja.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 60, 0));
        ninja.teleport(victim);
    }
}

