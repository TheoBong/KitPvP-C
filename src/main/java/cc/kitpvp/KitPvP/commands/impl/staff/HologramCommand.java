package cc.kitpvp.KitPvP.commands.impl.staff;

import cc.kitpvp.KitPvP.KitPvPPlugin;
import cc.kitpvp.KitPvP.commands.BaseCommand;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class HologramCommand extends BaseCommand {
    private final KitPvPPlugin plugin;

    public HologramCommand(KitPvPPlugin plugin) {
        super("hologram");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Players only!");
            return;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("kitpvp.admin")) {
            player.sendMessage("no perms");
            return;
        }

        if (args.length != 2) {
            player.sendMessage("usage: /hologram <create/delete> <kills/credits/killstreak>");
            return;
        }

        switch (args[0]) {
            case "create":
                Location location = player.getLocation();
                Hologram hologram;

                switch (args[1]) {
                    case "kills":
                        List<String> killsLeaderboard = plugin.getLeaderBoardManager().getKillsLeaderboard();

                        hologram = HologramsAPI.createHologram(plugin, location);

                        hologram.appendTextLine("Kills Leaderboard");
                        hologram.appendTextLine(" ");
                        hologram.appendTextLine(killsLeaderboard.get(0));
                        hologram.appendTextLine(killsLeaderboard.get(1));
                        hologram.appendTextLine(killsLeaderboard.get(2));
                        hologram.appendTextLine(killsLeaderboard.get(3));
                        hologram.appendTextLine(killsLeaderboard.get(4));

                        plugin.getLocationConfig().set("kills-leaderboard", location);
                        plugin.getLocationConfig().save();


                        player.sendMessage("Spawned.");
                        break;
                    case "credits":
                        List<String> creditsLeaderboard = plugin.getLeaderBoardManager().getCreditsLeaderboard();

                        hologram = HologramsAPI.createHologram(plugin, location);

                        hologram.appendTextLine("Credits Leaderboard");
                        hologram.appendTextLine(" ");
                        hologram.appendTextLine(creditsLeaderboard.get(0));
                        hologram.appendTextLine(creditsLeaderboard.get(1));
                        hologram.appendTextLine(creditsLeaderboard.get(2));
                        hologram.appendTextLine(creditsLeaderboard.get(3));
                        hologram.appendTextLine(creditsLeaderboard.get(4));

                        plugin.getLocationConfig().set("credits-leaderboard", location);
                        plugin.getLocationConfig().save();

                        player.sendMessage("Spawned.");
                        break;
                    case "killstreak":
                        List<String> killStreakLeaderboard = plugin.getLeaderBoardManager().getKillStreakLeaderboard();

                        hologram = HologramsAPI.createHologram(plugin, location);

                        hologram.appendTextLine("KillStreak Leaderboard");
                        hologram.appendTextLine(" ");
                        hologram.appendTextLine(killStreakLeaderboard.get(0));
                        hologram.appendTextLine(killStreakLeaderboard.get(1));
                        hologram.appendTextLine(killStreakLeaderboard.get(2));
                        hologram.appendTextLine(killStreakLeaderboard.get(3));
                        hologram.appendTextLine(killStreakLeaderboard.get(4));

                        plugin.getLocationConfig().set("killstreak-leaderboard", location);
                        plugin.getLocationConfig().save();

                        player.sendMessage("Spawned.");
                        break;
                    default:
                        player.sendMessage("Invalid hologram.");
                }

                break;
            case "delete":
                for (Hologram hologram1 : HologramsAPI.getHolograms(plugin)) {
                    hologram1.delete();
                }
                break;
        }
    }
}