package com.bongbong.kitpvp.commands.impl;

import com.bongbong.kitpvp.KitPvPPlugin;
import com.bongbong.kitpvp.commands.BaseCommand;
import com.bongbong.kitpvp.duels.Occupation;
import com.bongbong.kitpvp.player.PlayerState;
import com.bongbong.kitpvp.player.Profile;
import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReportCommand extends BaseCommand {
    private KitPvPPlugin plugin;

    public ReportCommand(KitPvPPlugin plugin) {
        super("report");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("u not a player");
            return;
        }

        if (args.length < 1) {
            sender.sendMessage("Usage: /report <player>");
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null || !target.isOnline()) {
            sender.sendMessage("Target not online");
            return;
        }

        Player player = (Player) sender;

        Profile playerProfile = plugin.getPlayerManager().getProfile(player);
        Profile targetProfile = plugin.getPlayerManager().getProfile(target);

        if (targetProfile.getState() == PlayerState.FFA) {
            if (plugin.getFFAReplay() == null || !plugin.getFFAReplay().isRecording()) System.out.println("somethings wrong");
            plugin.getReplayAPI().saveReplay(plugin.getFFAReplay().getId(), RandomStringUtils.randomAlphanumeric(8).toUpperCase());
            sender.sendMessage("Report successful.");
            return;
        }

        //ToDO: need to associate but not have the same cuz 2 people could both report.
        if (playerProfile.getState() == PlayerState.IN_DUEL) {
            Occupation occupation = playerProfile.getDuelOccupation();
            if (!occupation.getAllPlayers().contains(target)) {
                sender.sendMessage("Player isn't in your duel.");
                return;
            }

            plugin.getReplayAPI().saveReplay(occupation.getId(), null);
            sender.sendMessage("Report successful.");
            return;
        }

        if (playerProfile.getDuels() != null && targetProfile.getDuels() != null) {
            if (playerProfile.getDuels().get(0) != null && targetProfile.getDuels().get(0) != null) {
                if (playerProfile.getDuels().get(0).equals(targetProfile.getDuels().get(0))) {
                    sender.sendMessage("Report successful.");
                    return;
                }
            }

            if (playerProfile.getDuels().get(0) != null && targetProfile.getDuels().get(1) != null) {
                if (playerProfile.getDuels().get(0).equals(targetProfile.getDuels().get(1))) {
                    sender.sendMessage("Report successful.");
                    return;
                }
            }
        }





        sender.sendMessage("Unable to find why you want to report this player for cheating.");
    }
}
