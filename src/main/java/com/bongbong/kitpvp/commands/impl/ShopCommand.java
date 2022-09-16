package com.bongbong.kitpvp.commands.impl;

import com.bongbong.kitpvp.KitPvPPlugin;
import com.bongbong.kitpvp.commands.BaseCommand;
import com.bongbong.kitpvp.inventories.ShopWrapper;
import com.bongbong.kitpvp.player.PlayerState;
import com.bongbong.kitpvp.player.Profile;
import com.bongbong.kitpvp.util.message.CC;
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