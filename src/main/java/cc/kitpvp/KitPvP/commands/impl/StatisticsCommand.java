package cc.kitpvp.KitPvP.commands.impl;

import cc.kitpvp.KitPvP.KitPvPPlugin;
import cc.kitpvp.KitPvP.commands.BaseCommand;
import cc.kitpvp.KitPvP.inventories.KitShopPlayerWrapper;
import cc.kitpvp.KitPvP.inventories.StatsPlayerWrapper;
import cc.kitpvp.KitPvP.player.Profile;
import cc.kitpvp.KitPvP.util.ThreadUtil;
import cc.kitpvp.KitPvP.util.WebPlayer;
import cc.kitpvp.KitPvP.util.message.Color;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class StatisticsCommand extends BaseCommand {
    private final KitPvPPlugin plugin;

    private String targetName;
    private int kills, deaths, kill_streak, credits, highest_kill_streak;

    public StatisticsCommand(KitPvPPlugin plugin) {
        super("stats");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        ThreadUtil.runTask(true, plugin, () -> {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Players only!");
                return;
            }

            Player player = (Player) sender;
            Profile profile = plugin.getPlayerManager().getProfile(player);

            if (args.length == 1) {
                Player target = Bukkit.getPlayer(args[0]);
                if (target != null && target.isOnline()) {
                    Profile targetProfile = plugin.getPlayerManager().getProfile(target);

                    targetName = target.getDisplayName() + "'s";
                    kills = targetProfile.getStatistics().getKills();
                    deaths = targetProfile.getStatistics().getDeaths();
                    kill_streak = targetProfile.getStatistics().getKillStreak();
                    credits = targetProfile.getStatistics().getCredits();
                    highest_kill_streak = targetProfile.getStatistics().getHighestKillStreak();

                    sendGui(player);
                } else {
                    WebPlayer webPlayer;
                    try {
                        webPlayer = new WebPlayer(args[0]);
                    } catch (Exception e) {
                        sender.sendMessage("Invalid Player!");
                        return;
                    }

                    if (!webPlayer.isValid()) {
                        sender.sendMessage("Invalid Player!");
                        return;
                    }

                    UUID uuid = webPlayer.getUuid();

                    plugin.getMongo().getDocument(false, "profiles", uuid, document -> {
                        if (document != null) {
                            targetName = document.getString("name") + "'s";
                            kills = document.getInteger("kills");
                            deaths = document.getInteger("deaths");
                            kill_streak = document.getInteger("kill_streak");
                            highest_kill_streak = document.getInteger("highest_kill_streak");
                            credits = document.getInteger("credits");

                            sendGui(player);
                        }
                    });
                }
            } else {
                targetName = "Your";
                kills = profile.getStatistics().getKills();
                deaths = profile.getStatistics().getDeaths();
                kill_streak = profile.getStatistics().getKillStreak();
                credits = profile.getStatistics().getCredits();
                highest_kill_streak = profile.getStatistics().getHighestKillStreak();

                sendGui(player);
            }
        });
    }

    private void sendGui(Player player) {
        plugin.getInventoryManager().registerPlayerWrapper(new StatsPlayerWrapper(plugin, targetName, kills, deaths, kill_streak, credits, highest_kill_streak));
        plugin.getInventoryManager().getPlayerWrapper(StatsPlayerWrapper.class).open(player);
    }
}
