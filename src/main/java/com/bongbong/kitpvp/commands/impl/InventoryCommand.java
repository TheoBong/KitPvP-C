package com.bongbong.kitpvp.commands.impl;

import com.bongbong.kitpvp.KitPvPPlugin;
import com.bongbong.kitpvp.commands.BaseCommand;
import com.bongbong.kitpvp.duels.GameInventory;
import com.bongbong.kitpvp.duels.Occupation;
import com.bongbong.kitpvp.duels.Participant;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class InventoryCommand extends BaseCommand {

    private KitPvPPlugin plugin;

    public InventoryCommand(KitPvPPlugin plugin) {
        super("inventory");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(sender instanceof Player && args.length > 0) {
            Player player = (Player) sender;
            try {
                UUID uuid = UUID.fromString(args[0]);
                for(Occupation occupation : plugin.getDuelsOccupationManager().getOccupations().values()) {
                    for(Participant participant : occupation.getParticipants().values()) {
                        GameInventory gi = participant.getGameInventory();
                        if(gi.getUuid().equals(uuid)) {
                            gi.open(player);
                            return;
                        }
                    }
                }

                player.sendMessage(ChatColor.RED + "Inventory not found.");
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED + "Invalid inventory id.");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Usage: /inventory <id>");
        }
    }
}
