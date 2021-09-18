package cc.kitpvp.KitPvP.commands.impl;

import cc.kitpvp.KitPvP.KitPvPPlugin;
import cc.kitpvp.KitPvP.commands.BaseCommand;
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

                    player.sendMessage(Color.translate("&6&lStatistics of " + target.getDisplayName()) );
                    player.sendMessage("Kills: " + targetProfile.getStatistics().getKills());
                    player.sendMessage("Deaths: " + targetProfile.getStatistics().getDeaths());
                    player.sendMessage("Kill Streak: " + targetProfile.getStatistics().getKillStreak());
                    player.sendMessage("KDR: " + targetProfile.getStatistics().getKillDeathRatio());
                    player.sendMessage("Credits: " + targetProfile.getStatistics().getCredits());
                    player.sendMessage("Highest Kill Streak: " + targetProfile.getStatistics().getHighestKillStreak());
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
                            int kills = document.getInteger("kills");
                            int deaths = document.getInteger("deaths");
                            int killStreak = document.getInteger("kill_streak");
                            int highestKillStreak = document.getInteger("highest_kill_streak");
                            int credits = document.getInteger("credits");

                            double kdr = kills == 0 ? 0.0 : deaths == 0 ? kills : Math.round(((double) kills / deaths) * 10.0) / 10.0;

                            player.sendMessage(Color.translate("&6&lStatistics of " + webPlayer.getName()) );
                            player.sendMessage("Kills: " + kills);
                            player.sendMessage("Deaths: " + deaths);
                            player.sendMessage("Kill Streak: " + killStreak);
                            player.sendMessage("KDR: " + kdr);
                            player.sendMessage("Credits: " + credits);
                            player.sendMessage("Highest Kill Streak: " + highestKillStreak);
                        }
                    });
                }
            } else {
                player.sendMessage(Color.translate("&6&lYour Statistics") );
                player.sendMessage("Kills: " + profile.getStatistics().getKills());
                player.sendMessage("Deaths: " + profile.getStatistics().getDeaths());
                player.sendMessage("Kill Streak: " + profile.getStatistics().getKillStreak());
                player.sendMessage("KDR: " + profile.getStatistics().getKillDeathRatio());
                player.sendMessage("Credits: " + profile.getStatistics().getCredits());
                player.sendMessage("Highest Kill Streak: " + profile.getStatistics().getHighestKillStreak());
            }
        });
    }
}
