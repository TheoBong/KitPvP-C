package com.bongbong.kitpvp.listeners;

import com.bongbong.kitpvp.KitPvPPlugin;
import com.bongbong.kitpvp.managers.KitManager;
import com.bongbong.kitpvp.player.PlayerState;
import com.bongbong.kitpvp.player.Profile;
import com.bongbong.kitpvp.util.message.CC;
import com.bongbong.kitpvp.util.structure.RegionData;
import lombok.RequiredArgsConstructor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
public class RegionListener implements Listener {
    private final KitPvPPlugin plugin;

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        Action action = event.getAction();

        if (action == Action.LEFT_CLICK_AIR || action == Action.RIGHT_CLICK_AIR || !event.hasItem() || !event.hasBlock()) {
            return;
        }

        ItemStack item = event.getItem();

        if (item.getType() != Material.GOLD_AXE) {
            return;
        }

        Player player = event.getPlayer();

        if (!plugin.getRegionManager().isEditingRegion(player)) {
            return;
        }

        RegionData data = plugin.getRegionManager().getData(player);
        Location location = event.getClickedBlock().getLocation();
        boolean a = action == Action.LEFT_CLICK_BLOCK;

        if (a) {
            data.setA(location);
            player.sendMessage(CC.GREEN + "Set point a.");
        } else {
            data.setB(location);
            player.sendMessage(CC.GREEN + "Set point b.");
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Profile profile = plugin.getPlayerManager().getProfile(player);

        if (profile.getState() != PlayerState.SPAWN) {
            return;
        }

        if (player.hasPermission("kitpvp.admin") && player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        Location to = event.getTo();
        Location from = event.getFrom();

        if (to.getBlockX() == from.getBlockX() && to.getBlockZ() == from.getBlockZ()) {
            return;
        }

        if (!plugin.getSpawnCuboid().contains(to)) {
            KitManager kitManager = plugin.getKitManager();
            if (profile.getCurrentKit() == null) {
                if (profile.getLastKit() != null) {
                    profile.getLastKit().apply(player);
                } else {
                    kitManager.getDefaultKit().apply(player);
                }
            }

            plugin.getPlayerManager().loseSpawnProtection(player);

            plugin.addFFAReplayPlayer(player);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        plugin.getRegionManager().stopEditingRegion(player);
    }
}
