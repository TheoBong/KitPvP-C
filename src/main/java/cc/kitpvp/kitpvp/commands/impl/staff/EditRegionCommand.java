package cc.kitpvp.kitpvp.commands.impl.staff;

import cc.kitpvp.kitpvp.KitPvPPlugin;
import cc.kitpvp.kitpvp.commands.BaseCommand;
import cc.kitpvp.kitpvp.util.message.CC;
import cc.kitpvp.kitpvp.util.structure.Cuboid;
import cc.kitpvp.kitpvp.util.structure.RegionData;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class EditRegionCommand extends BaseCommand {
    private final KitPvPPlugin plugin;

    public EditRegionCommand(KitPvPPlugin plugin) {
        super("editregion");
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

        if (args.length < 1) {
            player.sendMessage("Usage: /editregion <start/stop/finish>");
            return;
        }

        switch (args[0].toLowerCase()) {
            case "start":
                if (plugin.getRegionManager().isEditingRegion(player)) {
                    player.sendMessage(CC.RED + "You're already editing a region!");
                    return;
                }

                plugin.getRegionManager().startEditingRegion(player);
                player.sendMessage(CC.GREEN + "Begun editing region. Use the wand to select points.");
                player.getInventory().clear();
                player.getInventory().setItem(1, new ItemStack(Material.GOLD_AXE));
                break;
            case "stop":
                if (!plugin.getRegionManager().isEditingRegion(player)) {
                    player.sendMessage(CC.RED + "You aren't editing a region!");
                    return;
                }

                plugin.getRegionManager().stopEditingRegion(player);
                player.sendMessage(CC.RED + "Stopped editing region.");
                break;
            case "finish":
                if (!plugin.getRegionManager().isEditingRegion(player)) {
                    player.sendMessage(CC.RED + "You aren't editing a region!");
                    return;
                }

                if (!plugin.getRegionManager().isDataValid(player)) {
                    player.sendMessage(CC.RED + "You must set both points with the wand before you can finish!");
                    return;
                }

                RegionData data = plugin.getRegionManager().getData(player);
                Cuboid cuboid = new Cuboid(data.getA(), data.getB());

                plugin.setSpawnCuboid(cuboid);
                plugin.getLocationConfig().set("spawn-cuboid", cuboid);
                plugin.getLocationConfig().save();

                plugin.getRegionManager().stopEditingRegion(player);

                player.sendMessage(CC.GREEN + "Finished editing region.");
                break;
        }
    }
}
