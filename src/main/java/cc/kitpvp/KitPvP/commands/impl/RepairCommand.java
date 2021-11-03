package cc.kitpvp.KitPvP.commands.impl;

import cc.kitpvp.KitPvP.KitPvPPlugin;
import cc.kitpvp.KitPvP.commands.BaseCommand;
import cc.kitpvp.KitPvP.player.PlayerState;
import cc.kitpvp.KitPvP.player.PlayerStatistics;
import cc.kitpvp.KitPvP.player.Profile;
import cc.kitpvp.KitPvP.util.message.CC;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RepairCommand extends BaseCommand {

    private final KitPvPPlugin plugin;

    public RepairCommand(KitPvPPlugin plugin) {
        super("repair");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        if (player == null) {
            sender.sendMessage("must be player!");
            return;
        }

        Profile profile = plugin.getPlayerManager().getProfile(player);

        if (profile.getState() != PlayerState.FFA) {
            player.sendMessage(CC.RED + "You can't repair right now!");
            return;
        }

        PlayerStatistics playerStatistics = profile.getStatistics();
        int cost = 50;
        if (profile.getStatistics().getCredits() < cost) {
            player.sendMessage(CC.RED + "You can't afford to repair your armor.");
            return;
        }

        profile.getCurrentKit().repairKit(player);
        playerStatistics.setCredits(playerStatistics.getCredits() - cost);

        player.sendMessage(CC.GREEN + "You have been given a new set of armor for 50 credits!");
    }
}


