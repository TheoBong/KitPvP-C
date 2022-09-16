package com.bongbong.kitpvp.commands.impl;

import com.bongbong.kitpvp.KitPvPPlugin;
import com.bongbong.kitpvp.commands.BaseCommand;
import com.bongbong.kitpvp.player.PlayerState;
import com.bongbong.kitpvp.player.Profile;
import com.bongbong.kitpvp.util.message.CC;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.Repairable;
import org.bukkit.potion.PotionEffect;

import java.util.Arrays;

public class ClearKitCommand extends BaseCommand {
    private final KitPvPPlugin plugin;

    public ClearKitCommand(KitPvPPlugin plugin) {
        super("clearkit");
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
            player.sendMessage(CC.RED + "You can't clear your kit while not in spawn!");
            return;
        }

        profile.setCurrentKit(null);

        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        Arrays.stream(player.getInventory().getArmorContents()).filter(armor -> armor instanceof Repairable).forEach(armor -> armor.setDurability((short) 0));
        player.getActivePotionEffects().stream().map(PotionEffect::getType).forEach(player::removePotionEffect);

        plugin.getPlayerManager().giveSpawnItems(player);
        player.sendMessage(CC.GREEN + "Kit cleared!");
    }
}

