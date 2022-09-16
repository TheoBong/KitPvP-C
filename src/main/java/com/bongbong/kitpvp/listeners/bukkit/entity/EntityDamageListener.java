package com.bongbong.kitpvp.listeners.bukkit.entity;

import com.bongbong.kitpvp.KitPvPPlugin;
import com.bongbong.kitpvp.duels.Occupation;
import com.bongbong.kitpvp.player.PlayerState;
import com.bongbong.kitpvp.player.Profile;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class EntityDamageListener implements Listener {

    private KitPvPPlugin plugin;
    public EntityDamageListener(KitPvPPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if(event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            Profile profile = plugin.getPlayerManager().getProfile(player.getUniqueId());
            if(profile.getState().equals(PlayerState.IN_DUEL) || profile.getState().equals(PlayerState.WAITING_IN_DUEL)) {
                Occupation occupation = profile.getDuelOccupation();
                occupation.handleDamage(player, event);
            }
        }
    }

}
