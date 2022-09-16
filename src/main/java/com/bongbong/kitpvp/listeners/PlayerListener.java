package com.bongbong.kitpvp.listeners;

import com.bongbong.kitpvp.KitPvPPlugin;
import com.bongbong.kitpvp.events.DeathEvent;
import com.bongbong.kitpvp.inventories.KitSelectorPlayerWrapper;
import com.bongbong.kitpvp.inventories.KitShopPlayerWrapper;
import com.bongbong.kitpvp.inventories.SettingsPlayerWrapper;
import com.bongbong.kitpvp.kits.Kit;
import com.bongbong.kitpvp.player.PlayerDamageData;
import com.bongbong.kitpvp.player.PlayerState;
import com.bongbong.kitpvp.player.Profile;
import com.bongbong.kitpvp.util.MathUtil;
import com.bongbong.kitpvp.util.item.ItemBuilder;
import com.bongbong.kitpvp.util.message.CC;
import com.bongbong.kitpvp.util.message.Color;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
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
import org.bukkit.inventory.meta.ItemMeta;

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
        Profile profile = plugin.getPlayerManager().getProfile(player);

        profile.playerUpdateVisibility();

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

    public void onDisconnect(Player player) {
        Profile profile = plugin.getPlayerManager().getProfile(player);
        if (profile == null) {
            return;
        }

        if (profile.getState() == PlayerState.SPAWN) {
            ItemStack goldItem = new ItemBuilder(Material.GOLD_INGOT).name(CC.GOLD + CC.B + "Gold").lore(CC.GRAY + "Deposit gold in spawn using /deposit!").build();

            int gold = 0;
            for (ItemStack itemStack : player.getInventory().getContents()) {
                if (itemStack == null) continue;

                if (itemStack.isSimilar(goldItem)) {
                    gold = gold + itemStack.getAmount();
                }
            }

            profile.getStatistics().setCredits(profile.getStatistics().getCredits() + (gold));
        }

        if (profile.getState() == PlayerState.FFA) {
            List<Player> nearbyPlayers = player.getNearbyEntities(32.0, 32.0, 32.0).stream()
                    .filter(Player.class::isInstance)
                    .map(Player.class::cast)
                    .collect(Collectors.toList());
            boolean kill = nearbyPlayers.stream().map(nearbyPlayer -> plugin.getPlayerManager().getProfile(nearbyPlayer)).anyMatch(nearbyProfile -> nearbyProfile != null && nearbyProfile.getState() == PlayerState.FFA);

            if (kill) {
                player.setHealth(0.0);
            } else {
                plugin.removeFFAReplayPlayer(player);
            }
        }

        profile.push(true);
        plugin.getPlayerManager().removeProfile(player);
    }

    public void onKick(PlayerKickEvent event) {
        onDisconnect(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onQuit(PlayerQuitEvent event) {
        onDisconnect(event.getPlayer());
    }

//    @EventHandler(priority = EventPriority.LOWEST)
//    public void onPearl(PlayerInteractEvent event) {
//        if (!event.hasItem() || event.getItem().getType() != Material.ENDER_PEARL
//                || event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
//            return;
//        }
//
//        Player player = event.getPlayer();
//        Profile profile = plugin.getPlayerManager().getProfile(player);
//
//        if (profile.getState() != PlayerState.FFA) {
//            event.setCancelled(true);
//            player.updateInventory();
//            return;
//        }
//
//        Timer timer = profile.getPearlTimer();
//
//        if (timer.isActive(false)) {
//            event.setCancelled(true);
//            player.updateInventory();
//            player.sendMessage(CC.PRIMARY + "You can't throw pearls for another " + CC.SECONDARY + timer.formattedExpiration() + CC.PRIMARY + ".");
//        }
//    }

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        if (!event.hasItem() || event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        Profile profile = plugin.getPlayerManager().getProfile(player);

//        if (player.getGameMode() != GameMode.CREATIVE
//                && event.getItem().getType() == Material.POTION) {
//            event.setCancelled(true);
//            player.updateInventory();
//        }

        if (profile.getState() == PlayerState.SPECTATING_DUEL) {
            switch (event.getItem().getType()) {
                case INK_SACK:
                    profile.setSpectatorVisibility(!profile.isSpectatorVisibility());
                    profile.playerUpdateVisibility();

                    if (profile.getState().equals(PlayerState.SPECTATING_DUEL)) {
                        player.setItemInHand(this.test(profile));
                    }

                    if (profile.isSpectatorVisibility()) {
                        player.sendMessage(ChatColor.GREEN + "You can now see other spectators.");
                    } else {
                        player.sendMessage(ChatColor.RED + "You can no longer see other spectators.");
                    }
                    break;
                case REDSTONE:
                    profile.getDuelOccupation().spectateEnd(player);
                    break;
            }
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

    public ItemStack test(Profile profile) {
        ItemStack item = new ItemStack(Material.INK_SACK, 1);
        ItemMeta meta = item.getItemMeta();
        if (profile.isSpectatorVisibility()) {
            item.setDurability((short) 10);
            meta.setDisplayName(Color.translate("&aHide Spectators &7(Right Click)"));
        } else {
            item.setDurability((short) 8);
            meta.setDisplayName(Color.translate("&cShow Spectators &7(Right Click)"));
        }

        item.setItemMeta(meta);
        return item;
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        Profile profile = plugin.getPlayerManager().getProfile(player);

        if (player.getGameMode() != GameMode.CREATIVE && (profile.getState() == PlayerState.SPAWN || profile.getState() == PlayerState.FFA)) {
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
            ItemStack itemStack = new ItemBuilder(Material.GOLD_INGOT).name(CC.GOLD + CC.B + "Gold").lore(CC.GRAY + "Deposit gold in spawn using /deposit!").amount(worth).build();
            damager.getInventory().addItem(itemStack);

            //Give the killer the gold that the victim had
            if (killer) {
                int goldAmount = 0;
                for (ItemStack itemStack1 : player.getInventory().getContents()) {
                    if (itemStack1 == null) continue;

                    if (itemStack1.isSimilar(new ItemBuilder(Material.GOLD_INGOT).name(CC.GOLD + CC.B + "Gold").lore(CC.GRAY + "Deposit gold in spawn using /deposit!").build())) {
                        goldAmount = goldAmount + itemStack1.getAmount();
                    }
                }

                ItemStack itemStack2 = new ItemBuilder(Material.GOLD_INGOT).name(CC.GOLD + CC.B + "Gold").lore(CC.GRAY + "Deposit gold in spawn using /deposit!").amount(goldAmount).build();
                if (goldAmount != 0) damager.getInventory().addItem(itemStack2);

                damager.sendMessage("You were given " + player.getDisplayName() + "'s undeposited gold (" + goldAmount + ")!");
            }

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
        plugin.removeFFAReplayPlayer(player);

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
