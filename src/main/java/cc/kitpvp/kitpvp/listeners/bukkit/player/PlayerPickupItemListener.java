package cc.kitpvp.kitpvp.listeners.bukkit.player;

import cc.kitpvp.kitpvp.KitPvPPlugin;
import cc.kitpvp.kitpvp.duels.Occupation;
import cc.kitpvp.kitpvp.player.Profile;
import cc.kitpvp.kitpvp.util.EntityHider;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class PlayerPickupItemListener implements Listener {

    private KitPvPPlugin plugin;
    private EntityHider entityHider;
    public PlayerPickupItemListener(KitPvPPlugin plugin) {
        this.plugin = plugin;
        this.entityHider = plugin.getEntityHider();
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        Profile profile = plugin.getPlayerManager().getProfile(player.getUniqueId());
        Occupation occupation = profile.getDuelOccupation();
        Item item = event.getItem();

        if(occupation != null) {
            if(occupation.getCurrentPlaying().contains(player)) {
                if (!entityHider.canSee(player, item)) {
                    event.setCancelled(true);
                }
            } else {
                event.setCancelled(true);
            }
        }
    }
}
