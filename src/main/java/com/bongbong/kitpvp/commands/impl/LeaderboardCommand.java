package com.bongbong.kitpvp.commands.impl;

import com.bongbong.kitpvp.KitPvPPlugin;
import com.bongbong.kitpvp.commands.BaseCommand;
import com.bongbong.kitpvp.inventories.LeaderboardWrapper;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LeaderboardCommand extends BaseCommand {
    private final KitPvPPlugin plugin;

    public LeaderboardCommand(KitPvPPlugin plugin) {
        super("leaderboard");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Players only!");
            return;
        }

        Player player = (Player) sender;

        plugin.getInventoryManager().getPlayerWrapper(LeaderboardWrapper.class).open(player);
    }
}
