package cc.kitpvp.kitpvp.listeners.bukkit.entity;

import cc.kitpvp.kitpvp.KitPvPPlugin;
import cc.kitpvp.kitpvp.duels.BrokenBlock;
import cc.kitpvp.kitpvp.duels.Occupation;
import cc.kitpvp.kitpvp.player.Profile;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;

public class EntityChangeBlockListener implements Listener {

    private KitPvPPlugin plugin;
    public EntityChangeBlockListener(KitPvPPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        if (event.getEntity() instanceof Arrow && event.getBlock().getType() == Material.TNT) {
            Arrow arrow = (Arrow) event.getEntity();
            Block block = event.getBlock();

            Player player = (Player) arrow.getShooter();

            Profile profile = plugin.getPlayerManager().getProfile(player.getUniqueId());

            Occupation occupation = profile.getDuelOccupation();

            if(occupation != null) {
                if (occupation.isBowSpleef()) {
                    occupation.getBrokenBlocks().add(new BrokenBlock(block, block.getType(), block.getData()));

                    Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(plugin, arrow::remove, 20L);
                }
            }
        }
    }
}
