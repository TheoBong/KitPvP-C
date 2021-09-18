package cc.kitpvp.KitPvP.commands.impl;

import cc.kitpvp.KitPvP.KitPvPPlugin;
import cc.kitpvp.KitPvP.commands.BaseCommand;
import cc.kitpvp.KitPvP.inventories.KitShopPlayerWrapper;
import cc.kitpvp.KitPvP.player.PlayerState;
import cc.kitpvp.KitPvP.player.Profile;
import cc.kitpvp.KitPvP.util.message.CC;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KitShopCommand extends BaseCommand {
    private final KitPvPPlugin plugin;

    public KitShopCommand(KitPvPPlugin plugin) {
        super("kitshop");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Players only!");
            return;
        }

        Player player = (Player) sender;
        Profile profile = plugin.getPlayerManager().getProfile(player);

        if (profile.getState() != PlayerState.SPAWN) {
            player.sendMessage(CC.RED + "You can't purchase a kit right now!");
            return;
        }

        if (args.length < 1) {
            plugin.getInventoryManager().getPlayerWrapper(KitShopPlayerWrapper.class).open(player);
        }
    }
}
