package cc.kitpvp.kitpvp.listeners.bukkit.world;

import cc.kitpvp.kitpvp.KitPvPPlugin;
import cc.kitpvp.kitpvp.duels.Occupation;
import cc.kitpvp.kitpvp.duels.arenas.Arena;
import cc.kitpvp.kitpvp.player.Profile;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockPlaceListener implements Listener {

    private KitPvPPlugin plugin;
    public BlockPlaceListener(KitPvPPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Profile profile = plugin.getPlayerManager().getProfile(player.getUniqueId());
        Block block = event.getBlock();
        Occupation occupation = profile.getDuelOccupation();

        if(occupation != null && occupation.isBuild()) {
            Arena arena = occupation.getArena();
            if(block.getY() > arena.getBuildMax()) {
                player.sendMessage(ChatColor.RED + "You cannot place blocks above the build limit.");
                event.setCancelled(true);
            } else {
                occupation.getPlacedBlocks().add(block);
            }
        }
    }
}
