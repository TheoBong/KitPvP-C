package com.bongbong.kitpvp.commands.impl;

import com.bongbong.kitpvp.KitPvPPlugin;
import com.bongbong.kitpvp.commands.BaseCommand;
import com.bongbong.kitpvp.inventories.KitShopPlayerWrapper;
import com.bongbong.kitpvp.player.PlayerState;
import com.bongbong.kitpvp.player.Profile;
import com.bongbong.kitpvp.util.message.CC;
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
