package cc.kitpvp.kitpvp.commands.impl;

import cc.kitpvp.kitpvp.commands.BaseCommand;
import cc.kitpvp.kitpvp.util.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PingCommand extends BaseCommand {

    public PingCommand() {
        super("ping");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            int ping = PlayerUtils.getPing(player);
            if(args.length > 0) {
                Player target = Bukkit.getPlayer(args[0]);
                if(target != null) {
                    int targetPing = PlayerUtils.getPing(target);
                    int difference = targetPing - ping;
                    player.sendMessage(ChatColor.WHITE + target.getName() + "'s" + ChatColor.GREEN + " ping is " + getColor(targetPing) + targetPing + " ms" + ChatColor.GREEN + ".");
                    player.sendMessage(ChatColor.GREEN + "Ping difference: " + getColor(difference) + (difference > 0 ? "+" + difference : difference) + " ms");
                } else {
                    player.sendMessage(ChatColor.RED + "The target you specified is not on this server.");
                }
            } else {
                player.sendMessage(ChatColor.GREEN + "Your ping is " + getColor(ping) + ping + " ms" + ChatColor.GREEN + ".");
            }
        }
    }

    public ChatColor getColor(int i) {
        if(i < 60) {
            return ChatColor.GREEN;
        } else if(i < 80) {
            return ChatColor.DARK_GREEN;
        } else if(i < 100) {
            return ChatColor.RED;
        } else if(i < 150) {
            return ChatColor.DARK_RED;
        } else {
            return ChatColor.WHITE;
        }
    }
}
