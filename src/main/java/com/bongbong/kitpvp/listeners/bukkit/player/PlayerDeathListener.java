package com.bongbong.kitpvp.listeners.bukkit.player;

import com.bongbong.kitpvp.KitPvPPlugin;
import com.bongbong.kitpvp.duels.Occupation;
import com.bongbong.kitpvp.player.Profile;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathListener implements Listener {

    private KitPvPPlugin plugin;
    public PlayerDeathListener(KitPvPPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        event.setDeathMessage(null);
        Player player = event.getEntity();
        Profile profile = plugin.getPlayerManager().getProfile(player.getUniqueId());
        Occupation occupation = profile.getDuelOccupation();
        if(occupation != null && occupation.getAlivePlayers().contains(player)) {
            occupation.eliminate(player);
            player.spigot().respawn();
            player.teleport(player.getLocation());
            event.getDrops().clear();
        }
    }
}
