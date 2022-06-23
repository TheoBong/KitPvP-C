package cc.kitpvp.kitpvp.commands.impl;

import cc.kitpvp.kitpvp.KitPvPPlugin;
import cc.kitpvp.kitpvp.commands.BaseCommand;
import cc.kitpvp.kitpvp.duels.Occupation;
import cc.kitpvp.kitpvp.player.PlayerState;
import cc.kitpvp.kitpvp.player.Profile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpectateCommand extends BaseCommand {

    private KitPvPPlugin plugin;

    public SpectateCommand(KitPvPPlugin plugin) {
        super("spectate");
        this.plugin = plugin;
        this.setAliases("spec");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(sender instanceof Player && args.length > 0) {
            Player player = (Player) sender;
            Profile profile = plugin.getPlayerManager().getProfile(player.getUniqueId());
            if(profile.getState().equals(PlayerState.SPAWN)) {
                Player target = Bukkit.getPlayer(args[0]);
                if(target != null) {
                    Profile targetProfile = plugin.getPlayerManager().getProfile(target.getUniqueId());
                    Occupation occupation = targetProfile.getDuelOccupation();
                    if(occupation != null) {
                        occupation.spectateStart(player, target);
                    } else {
                        player.sendMessage(ChatColor.RED + "The target you specified is not in a game.");
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "The target you specified is not on this server.");
                }
            } else {
                player.sendMessage(ChatColor.RED + "You cannot spectate a match right now.");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Usage: /spectate <target>");
        }
    }
}
