package com.bongbong.kitpvp.listeners.bukkit.player;

import com.bongbong.kitpvp.KitPvPPlugin;
import com.bongbong.kitpvp.duels.Occupation;
import com.bongbong.kitpvp.duels.kits.Kit;
import com.bongbong.kitpvp.player.Profile;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener {

    private KitPvPPlugin plugin;
    public PlayerMoveListener(KitPvPPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Profile profile = plugin.getPlayerManager().getProfile(player.getUniqueId());
        Occupation occupation = profile.getDuelOccupation();

        Location from = event.getFrom();
        Location to = event.getTo();

        if(occupation != null) {
            if(event.getTo().getBlock().isLiquid() && occupation.getCurrentPlaying().contains(player)) {
                if (occupation.getKit() != null && occupation.getKit().getType().equals(Kit.Type.SUMO) && occupation.getState().equals(Occupation.State.ACTIVE)) {
                    occupation.eliminate(player);
                }
            }

            if(!occupation.isMoveOnStart()) {
                if(occupation.getState().equals(Occupation.State.STARTING) && occupation.getCurrentPlaying().contains(player)) {
                    if (from.getX() != to.getX() || from.getY() != to.getY() || from.getZ() != to.getZ()) {
                        player.teleport(from);
                    }
                }
            }
        }
    }
}
