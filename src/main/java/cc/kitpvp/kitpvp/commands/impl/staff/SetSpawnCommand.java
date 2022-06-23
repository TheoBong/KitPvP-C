package cc.kitpvp.kitpvp.commands.impl.staff;

import cc.kitpvp.kitpvp.KitPvPPlugin;
import cc.kitpvp.kitpvp.commands.BaseCommand;
import cc.kitpvp.kitpvp.util.message.CC;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetSpawnCommand extends BaseCommand {
    private final KitPvPPlugin plugin;

    public SetSpawnCommand(KitPvPPlugin plugin) {
        super("setspawn");
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
            player.sendMessage(ChatColor.RED + "I'm sorry, but you do not have permission to perform this command. Please contact the server administrators if you believe that this is in error.");
            return;
        }

        Location location = player.getLocation();

        location.setX(location.getBlockX() + 0.5);
        location.setY(location.getBlockY() + 3.0);
        location.setZ(location.getBlockZ() + 0.5);

        plugin.setSpawnLocation(location);

        plugin.getLocationConfig().set("spawn", location);
        plugin.getLocationConfig().save();

        player.sendMessage(CC.GREEN + "Set the spawn!");
    }
}