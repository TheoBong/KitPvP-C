package cc.kitpvp.kitpvp.commands.impl;

import cc.kitpvp.kitpvp.KitPvPPlugin;
import cc.kitpvp.kitpvp.commands.BaseCommand;
import cc.kitpvp.kitpvp.player.PlayerState;
import cc.kitpvp.kitpvp.player.Profile;
import cc.kitpvp.kitpvp.util.message.CC;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.stream.Collectors;

public class SpawnCommand extends BaseCommand {
    private final KitPvPPlugin plugin;

    public SpawnCommand(KitPvPPlugin plugin) {
        super("spawn");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Players only!");
            return;
        }

        Player player = (Player) sender;
        Profile profile = plugin.getPlayerManager().getProfile(player);

        if (profile.isAwaitingTeleport()) {
            player.sendMessage(CC.RED + "You're already waiting to go to spawn.");
            return;
        }

        if (profile.getState() == PlayerState.SPAWN) {
            sendToSpawn(player, false);
        } else {
            List<Player> nearbyPlayers = player.getNearbyEntities(32.0, 32.0, 32.0).stream()
                    .filter(Player.class::isInstance)
                    .map(Player.class::cast)
                    .collect(Collectors.toList());
            boolean wait = nearbyPlayers.stream()
                    .map(nearbyPlayer -> plugin.getPlayerManager().getProfile(nearbyPlayer))
                    .anyMatch(nearbyProfile -> nearbyProfile.getState() == PlayerState.FFA);

            if (!wait) {
                sendToSpawn(player, true);
            } else {
                profile.setAwaitingTeleport(true);
                player.sendMessage("Players are nearby! You will be teleported in 5 seconds.");

                new BukkitRunnable() {
                    private int count = 6;

                    @Override
                    public void run() {
                        if (!player.isOnline()) {
                            cancel();
                        } else if (!profile.isAwaitingTeleport()) {
                            player.sendMessage(CC.RED + "You moved! The teleportation has been cancelled.");
                            cancel();
                        } else if (--count == 0) {
                            sendToSpawn(player, true);
                            cancel();
                        } else {
                            player.sendMessage(CC.PRIMARY + count + "...");
                        }
                    }
                }.runTaskTimer(plugin, 0L, 20L);
            }
        }
        profile.setLastAttacked(null);
    }



    private void sendToSpawn(Player player, boolean withProtection) {
        if (withProtection) {
            plugin.getPlayerManager().acquireSpawnProtection(player);
        }

        player.teleport(plugin.getSpawnLocation());
        player.sendMessage(CC.GREEN + "Teleported to spawn.");
    }
}

