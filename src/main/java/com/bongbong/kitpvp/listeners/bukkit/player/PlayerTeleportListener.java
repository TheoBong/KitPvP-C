package com.bongbong.kitpvp.listeners.bukkit.player;

import com.bongbong.kitpvp.KitPvPPlugin;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class PlayerTeleportListener implements Listener {

    private KitPvPPlugin plugin;
    public PlayerTeleportListener(KitPvPPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.getCause().equals(PlayerTeleportEvent.TeleportCause.ENDER_PEARL)) {
            Location location = event.getTo();
            location.setX(location.getBlockX() + 0.5);
            location.setY(location.getBlockY());
            location.setZ(location.getBlockZ() + 0.5);
            event.setTo(location);
        }
    }
}
