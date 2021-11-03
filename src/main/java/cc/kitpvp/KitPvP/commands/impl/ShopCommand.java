package cc.kitpvp.KitPvP.commands.impl;

import cc.kitpvp.KitPvP.KitPvPPlugin;
import cc.kitpvp.KitPvP.commands.BaseCommand;
import cc.kitpvp.KitPvP.inventories.ShopWrapper;
import cc.kitpvp.KitPvP.player.PlayerState;
import cc.kitpvp.KitPvP.player.Profile;
import cc.kitpvp.KitPvP.util.message.CC;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ShopCommand extends BaseCommand {
    private final KitPvPPlugin plugin;

    public ShopCommand(KitPvPPlugin plugin) {
        super("shop");
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
        if (profile.getState() == PlayerState.SPAWN || profile.getState() == PlayerState.FFA) {
            plugin.getInventoryManager().getWrapper(ShopWrapper.class).open(player);
        } else {
            player.sendMessage(CC.RED + "You can't use the shop right now!");
        }
    }
}