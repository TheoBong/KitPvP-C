package cc.kitpvp.KitPvP.commands.impl;

import cc.kitpvp.KitPvP.KitPvPPlugin;
import cc.kitpvp.KitPvP.commands.BaseCommand;
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
        ThreadUtil.runTask(true, plugin, () -> {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Players only!");
                return;
            }

            plugin.getLeaderBoardManager().sortTop();

            int position = 0;
            Iterator<Document> killsSorted = plugin.getLeaderBoardManager().getKillsSorted();
            while(killsSorted.hasNext()) {
                Document document = killsSorted.next();
                String name = document.getString("name");

                sender.sendMessage("#" + position++ + " - " + name + " - " + document.getInteger("kills"));
            }
        });
    }
}
