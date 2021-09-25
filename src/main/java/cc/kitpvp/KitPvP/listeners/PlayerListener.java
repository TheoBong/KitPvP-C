package cc.kitpvp.KitPvP.listeners;

import cc.kitpvp.KitPvP.KitPvPPlugin;
import cc.kitpvp.KitPvP.events.DeathEvent;
import cc.kitpvp.KitPvP.inventories.KitSelectorPlayerWrapper;
import cc.kitpvp.KitPvP.inventories.KitShopPlayerWrapper;
import cc.kitpvp.KitPvP.inventories.SettingsPlayerWrapper;
import cc.kitpvp.KitPvP.kits.Kit;
import cc.kitpvp.KitPvP.player.PlayerDamageData;
import cc.kitpvp.KitPvP.player.PlayerState;
import cc.kitpvp.KitPvP.player.Profile;
import cc.kitpvp.KitPvP.util.MathUtil;
import cc.kitpvp.KitPvP.util.message.CC;
import cc.kitpvp.KitPvP.util.player.PlayerUtil;
import cc.kitpvp.KitPvP.util.timer.Timer;
import lombok.RequiredArgsConstructor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RequiredArgsConstructor
public class PlayerListener implements Listener {
    private final KitPvPPlugin plugin;

    @EventHandler
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        if (event.getLoginResult() == AsyncPlayerPreLoginEvent.Result.ALLOWED) {
            plugin.getPlayerManager().createProfile(event.getUniqueId(), event.getName());
        }
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        Profile profile = plugin.getPlayerManager().getProfile(player);

        if (profile == null) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, CC.RED + "Your data failed to load for KitPvP. Try logging in again.");
        } else if (!profile.isLoaded()) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, CC.RED + "Your data failed to load for KitPvP (2). Try logging in again.");
        } else if (event.getResult() != PlayerLoginEvent.Result.ALLOWED) {
            plugin.getPlayerManager().removeProfile(player);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        PlayerUtil.clearPlayer(player);

        plugin.getPlayerManager().giveSpawnItems(player);

        if (player.hasPermission("kitpvp.donor")) {
            player.setAllowFlight(true);
            player.setFlying(true);
        }

        player.teleport(plugin.getSpawnLocation());

        player.sendMessage(CC.SEPARATOR);
        player.sendMessage(CC.WHITE + "Welcome to KitPvP!");
        player.sendMessage(CC.SEPARATOR);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Profile profile = plugin.getPlayerManager().getProfile(player);
        if (profile == null) {
            return;
        }

        List<Player> nearbyPlayers = player.getNearbyEntities(32.0, 32.0, 32.0).stream()
                .filter(Player.class::isInstance)
                .map(Player.class::cast)
                .collect(Collectors.toList());
        boolean kill = nearbyPlayers.stream().map(nearbyPlayer -> plugin.getPlayerManager().getProfile(nearbyPlayer)).anyMatch(nearbyProfile -> nearbyProfile.getState() == PlayerState.FFA);

        if (kill) {
            player.setHealth(0.0);
        }

        profile.push(true);
        plugin.getPlayerManager().removeProfile(player);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPearl(PlayerInteractEvent event) {
        if (!event.hasItem() || event.getItem().getType() != Material.ENDER_PEARL
                || event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        Profile profile = plugin.getPlayerManager().getProfile(player);

        if (profile.getState() != PlayerState.FFA) {
            event.setCancelled(true);
            player.updateInventory();
            return;
        }

        Timer timer = profile.getPearlTimer();

        if (timer.isActive(false)) {
            event.setCancelled(true);
            player.updateInventory();
            player.sendMessage(CC.PRIMARY + "You can't throw pearls for another " + CC.SECONDARY + timer.formattedExpiration() + CC.PRIMARY + ".");
        }
    }

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        if (!event.hasItem() || event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        Profile profile = plugin.getPlayerManager().getProfile(player);

        if (player.getGameMode() != GameMode.CREATIVE
                && event.getItem().getType() == Material.POTION) {
            event.setCancelled(true);
            player.updateInventory();
        }


        if (profile.getCurrentKit() != null) {
            return;
        }

        if (profile.getState() != PlayerState.SPAWN) {
            return;
        }

        switch (event.getItem().getType()) {
            case CHEST:
                plugin.getInventoryManager().getPlayerWrapper(KitSelectorPlayerWrapper.class).open(player);
                break;
            case WATCH:
                Kit kit = profile.getLastKit();

                if (kit != null) {
                    kit.apply(player);
                }
                break;
            case PAPER:
                player.performCommand("stats");
                break;
            case ENDER_CHEST:
                plugin.getInventoryManager().getPlayerWrapper(KitShopPlayerWrapper.class).open(player);
                break;
            case EYE_OF_ENDER:
                plugin.getInventoryManager().getPlayerWrapper(SettingsPlayerWrapper.class).open(player);
                break;
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();

        if (player.getGameMode() != GameMode.CREATIVE) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Profile profile = plugin.getPlayerManager().getProfile(player);

        if (!profile.isAwaitingTeleport()) {
            return;
        }

        Location to = event.getTo();
        Location from = event.getFrom();

        if (MathUtil.isWithin(to.getX(), from.getX(), 0.1) && MathUtil.isWithin(to.getZ(), from.getZ(), 0.1)) {
            return;
        }

        profile.setAwaitingTeleport(false);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        event.getDrops().clear();

        Player player = event.getEntity();
        Profile profile = plugin.getPlayerManager().getProfile(player);
        PlayerDamageData damageData = profile.getDamageData();
        double totalDamage = damageData.total();
        Map<UUID, Double> sortedDamage = damageData.sortedMap();
        boolean killer = true;
        profile.setLastAttacked(null);

        for (Map.Entry<UUID, Double> entry : sortedDamage.entrySet()) {
            UUID damagerId = entry.getKey();
            Player damager = plugin.getServer().getPlayer(damagerId);
            Profile damagerProfile = plugin.getPlayerManager().getProfile(damager);
            double damage = entry.getValue();
            double percent = damage / totalDamage;

            if (!killer && percent < 0.15) {
                continue;
            }

            int worth = killer ? 10 : (int) (10 * percent);

            String strPercent = String.format("%.1f", percent * 100);

            //add credits
            damagerProfile.getStatistics().setCredits(damagerProfile.getStatistics().getCredits() + worth);

            //add xp
            int xp = killer ? 5 : 10;
            damagerProfile.setXp(damagerProfile.getXp() + xp);

            if (killer) {
                killer = false;
                plugin.getServer().getPluginManager().callEvent(new DeathEvent(player, damager, damagerProfile));
                damagerProfile.getStatistics().handleKill();
                damager.sendMessage("You killed " + player.getDisplayName()
                        + " and received " + worth + " credits "
                        + "(" + strPercent + "% of damage)" + ".");

                IntStream.range(0, 8).forEach(i -> damager.getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE)));

                player.sendMessage("You died to " + damager.getDisplayName());
            } else {
                damager.sendMessage("You got an assist on " + player.getDisplayName()
                        + " and received " + worth + " credits "
                        + "(" + strPercent + "% of damage)" + ".");
            }
        }

        profile.setControllable(true);
        profile.setCurrentKit(null);
        damageData.clear();
        plugin.getServer().getOnlinePlayers().forEach(loopPlayer -> {
            Profile loopProfile = plugin.getPlayerManager().getProfile(loopPlayer);
            loopProfile.getDamageData().getAttackerDamage().remove(loopPlayer.getUniqueId());
        });

        profile.getStatistics().handleDeath();

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (player.isOnline()) {
                player.spigot().respawn();
            }
        }, 20);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        plugin.getPlayerManager().giveSpawnItems(player);
        plugin.getPlayerManager().acquireSpawnProtection(player);
    }
}
