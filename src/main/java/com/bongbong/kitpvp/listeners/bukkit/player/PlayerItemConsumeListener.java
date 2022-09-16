package com.bongbong.kitpvp.listeners.bukkit.player;

import com.bongbong.kitpvp.KitPvPPlugin;
import com.bongbong.kitpvp.player.Profile;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PlayerItemConsumeListener implements Listener {

    private KitPvPPlugin plugin;
    public PlayerItemConsumeListener(KitPvPPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        Profile profile = plugin.getPlayerManager().getProfile(player.getUniqueId());
        ItemStack item = event.getItem();

        if(item.getItemMeta().getDisplayName() != null && item.getItemMeta().getDisplayName().contains("Golden Head")) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, 2));
        }
    }
}
