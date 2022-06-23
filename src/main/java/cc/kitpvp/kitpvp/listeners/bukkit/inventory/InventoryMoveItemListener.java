package cc.kitpvp.kitpvp.listeners.bukkit.inventory;

import cc.kitpvp.kitpvp.KitPvPPlugin;
import cc.kitpvp.kitpvp.duels.Occupation;
import cc.kitpvp.kitpvp.player.Profile;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.Inventory;

public class InventoryMoveItemListener implements Listener {

    private KitPvPPlugin plugin;
    public InventoryMoveItemListener(KitPvPPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryMoveItem(InventoryMoveItemEvent event) {
        Inventory source = event.getSource();
        Inventory destination = event.getDestination();

        if(source.getHolder() instanceof Player) {
            Player player = (Player) source.getHolder();
            Profile profile = plugin.getPlayerManager().getProfile(player.getUniqueId());
            Occupation occupation = profile.getDuelOccupation();

            if(occupation != null) {
                 if(!occupation.getCurrentPlaying().contains(player)) {
                     event.setCancelled(true);
                 }
            }
        }
    }
}
