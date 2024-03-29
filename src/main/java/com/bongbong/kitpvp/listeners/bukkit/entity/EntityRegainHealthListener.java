package com.bongbong.kitpvp.listeners.bukkit.entity;

import com.bongbong.kitpvp.KitPvPPlugin;
import com.bongbong.kitpvp.duels.Occupation;
import com.bongbong.kitpvp.player.Profile;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;

public class EntityRegainHealthListener implements Listener {

    private KitPvPPlugin plugin;
    public EntityRegainHealthListener(KitPvPPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityRegainHealth(EntityRegainHealthEvent event) {
        if(event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            Profile profile = plugin.getPlayerManager().getProfile(player.getUniqueId());
            if(event.getRegainReason().equals(EntityRegainHealthEvent.RegainReason.SATIATED) && profile.getDuelOccupation() != null) {
                Occupation occupation = profile.getDuelOccupation();
                if(occupation.getKit() != null && !occupation.getKit().isRegen()) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
