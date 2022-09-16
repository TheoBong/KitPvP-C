package com.bongbong.kitpvp.listeners.bukkit.world;

import com.bongbong.kitpvp.KitPvPPlugin;
import com.bongbong.kitpvp.duels.BrokenBlock;
import com.bongbong.kitpvp.duels.Occupation;
import com.bongbong.kitpvp.player.Profile;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class BlockBreakListener implements Listener {

    private KitPvPPlugin plugin;
    public BlockBreakListener(KitPvPPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {

        Player player = event.getPlayer();
        Profile profile = plugin.getPlayerManager().getProfile(player.getUniqueId());
        Block block = event.getBlock();
        Occupation occupation = profile.getDuelOccupation();

        if(occupation != null) {
            if(occupation.isBuild()) {
                if (occupation.getPlacedBlocks().contains(block)) {
                    occupation.getPlacedBlocks().remove(block);
                    for (ItemStack item : block.getDrops()) {
                        Item i = block.getLocation().getWorld().dropItemNaturally(block.getLocation(), item);
                        occupation.addEntity(i);
                    }

                    block.getDrops().clear();
                } else {
                    player.sendMessage(ChatColor.RED + "You cannot break this block.");
                    event.setCancelled(true);
                }
            } else if(occupation.isSpleef() && occupation.getState() == Occupation.State.ACTIVE) {
                if (block.getType() == Material.SNOW_BLOCK)
                    occupation.getBrokenBlocks().add(new BrokenBlock(block, block.getType(), block.getData()));
                else
                    event.setCancelled(true);
            } else {
                event.setCancelled(true);
            }
        }
    }
}
