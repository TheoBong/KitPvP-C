package com.bongbong.kitpvp.listeners;

import com.bongbong.kitpvp.KitPvPPlugin;
import com.bongbong.kitpvp.kits.impl.ffa.Mineman;
import com.bongbong.kitpvp.player.PlayerState;
import com.bongbong.kitpvp.player.Profile;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

public class WorldListener implements Listener {
    private final KitPvPPlugin plugin;

    public WorldListener(KitPvPPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("kitpvp.admin") && player.getGameMode() == GameMode.CREATIVE) {
            return;
        }
        event.setCancelled(true);
    }

//    @EventHandler(priority = EventPriority.HIGH)
//    public void onPlace(BlockPlaceEvent event) {
//        Player player = event.getPlayer();
//
//        if (player.hasPermission("kitpvp.admin") && player.getGameMode() == GameMode.CREATIVE) {
//            return;
//        }
//
//        event.setCancelled(true);
//    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        if (player.hasPermission("kitpvp.admin") && player.getGameMode() == GameMode.CREATIVE) {
            event.setCancelled(false);
            return;
        }

        Profile profile = plugin.getPlayerManager().getProfile(player);
        if (profile.getState() == PlayerState.FFA && profile.getCurrentKit() instanceof Mineman) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onRain(WeatherChangeEvent event) {
        if (event.toWeatherState()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        event.blockList().clear();
    }

    @EventHandler
    public void onTrample(PlayerInteractEvent event) {
        if (event.getPlayer().getGameMode() != GameMode.CREATIVE && event.getAction() == Action.PHYSICAL && event.getClickedBlock().getType() == Material.SOIL) {
            event.setCancelled(true);
        }
    }
}

