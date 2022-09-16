package com.bongbong.kitpvp.commands.impl;

import com.bongbong.kitpvp.KitPvPPlugin;
import com.bongbong.kitpvp.commands.BaseCommand;
import com.bongbong.kitpvp.inventories.KitSelectorPlayerWrapper;
import com.bongbong.kitpvp.kits.Kit;
import com.bongbong.kitpvp.player.PlayerState;
import com.bongbong.kitpvp.player.Profile;
import com.bongbong.kitpvp.util.message.CC;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KitCommand extends BaseCommand {
    private final KitPvPPlugin plugin;

    public KitCommand(KitPvPPlugin plugin) {
        super("kit");
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
            player.sendMessage(CC.RED + "You can't choose a kit right now!");
            return;
        }

        if (args.length < 1) {
            plugin.getInventoryManager().getPlayerWrapper(KitSelectorPlayerWrapper.class).open(player);
            return;
        }

        Kit kit = plugin.getKitManager().getFfaKitByName(args[0]);

        if (kit == null) {
            plugin.getInventoryManager().getPlayerWrapper(KitSelectorPlayerWrapper.class).open(player);
            return;
        }

        kit.apply(player);
    }
}
