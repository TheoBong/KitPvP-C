package com.bongbong.kitpvp.listeners.bukkit.world;

import com.bongbong.kitpvp.KitPvPPlugin;
import com.bongbong.kitpvp.duels.Occupation;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;

public class BlockFromToListener implements Listener {

    private KitPvPPlugin plugin;
    public BlockFromToListener(KitPvPPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockFormTo(BlockFromToEvent event) {
        Block from = event.getBlock();
        Block to = event.getToBlock();

        for(Occupation o : plugin.getDuelsOccupationManager().getOccupations().values()) {
            if(o.getPlacedBlocks().contains(from)) {
                o.getPlacedBlocks().add(to);
            }
        }
    }
}
