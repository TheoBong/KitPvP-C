package cc.kitpvp.KitPvP.commands.impl;

import cc.kitpvp.KitPvP.KitPvPPlugin;
import cc.kitpvp.KitPvP.commands.BaseCommand;
import cc.kitpvp.KitPvP.inventories.KitSelectorPlayerWrapper;
import cc.kitpvp.KitPvP.inventories.LeaderboardWrapper;
import cc.kitpvp.KitPvP.util.ThreadUtil;
import org.bson.Document;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Iterator;

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
