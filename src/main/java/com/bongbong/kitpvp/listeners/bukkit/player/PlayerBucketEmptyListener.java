package com.bongbong.kitpvp.listeners.bukkit.player;

import com.bongbong.kitpvp.KitPvPPlugin;
import com.bongbong.kitpvp.duels.Occupation;
import com.bongbong.kitpvp.player.Profile;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketEmptyEvent;

public class PlayerBucketEmptyListener implements Listener {

    private KitPvPPlugin plugin;
    public PlayerBucketEmptyListener(KitPvPPlugin plugin) {
        this.plugin = plugin;
    }

    private BlockFace[] faces = {
            BlockFace.SELF,
            BlockFace.UP,
            BlockFace.DOWN,
            BlockFace.NORTH,
            BlockFace.EAST,
            BlockFace.SOUTH,
            BlockFace.WEST
    };

    @EventHandler
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
        Player player = event.getPlayer();
        Profile profile = plugin.getPlayerManager().getProfile(player.getUniqueId());
        Block blockClicked = event.getBlockClicked();

        Bukkit.getScheduler().runTaskLater(plugin, ()-> {
            Block block = null;
            for(BlockFace face : faces) {
                Block b = blockClicked.getRelative(face, 1);
                if(b.isLiquid()) {
                    block = b;
                    break;
                }
            }

            if(block != null) {
                Occupation occupation = profile.getDuelOccupation();
                if (profile.getDuelOccupation() != null) {
                    occupation.getPlacedBlocks().add(block);
                }
            }
        }, 1);
    }
}
