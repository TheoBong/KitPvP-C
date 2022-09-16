package com.bongbong.kitpvp.commands.impl;

import com.bongbong.kitpvp.KitPvPPlugin;
import com.bongbong.kitpvp.commands.BaseCommand;
import com.bongbong.kitpvp.duels.DuelRequest;
import com.bongbong.kitpvp.player.PlayerState;
import com.bongbong.kitpvp.player.Profile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AcceptCommand extends BaseCommand {

    private KitPvPPlugin plugin;

    public AcceptCommand(KitPvPPlugin plugin) {
        super("accept");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(sender instanceof Player && args.length > 0) {
            Player player = (Player) sender;
            Profile profile = plugin.getPlayerManager().getProfile(player.getUniqueId());
            Player target = Bukkit.getPlayer(args[0]);

            if(target != null) {
                if(target.getUniqueId().equals(player.getUniqueId())) {
                    player.sendMessage(ChatColor.RED + "All I'm going to say is, bruh.");
                    return;
                }

                DuelRequest duelRequest = profile.getDuelRequests().get(target.getUniqueId());
                Profile targetProfile = plugin.getPlayerManager().getProfile(target.getUniqueId());
                if(duelRequest == null || duelRequest.isExpired()) {
                    player.sendMessage(ChatColor.RED + "You do not have a duel request from this player.");
                    return;
                }

                if(targetProfile.getState().equals(PlayerState.SPAWN) && profile.getState().equals(PlayerState.SPAWN)) {
                    duelRequest.start();
                } else {
                    player.sendMessage(ChatColor.RED + "You cannot duel this player right now.");
                }

            } else {
                player.sendMessage(ChatColor.RED + "The target you specified is not on this server.");
            }

        } else {
            sender.sendMessage(ChatColor.RED + "Usage: /accept <player>");
        }
    }
}
