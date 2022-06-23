package cc.kitpvp.kitpvp.listeners.bukkit.inventory;

import cc.kitpvp.kitpvp.KitPvPPlugin;
import cc.kitpvp.kitpvp.duels.Occupation;
import cc.kitpvp.kitpvp.duels.Participant;
import cc.kitpvp.kitpvp.player.PlayerState;
import cc.kitpvp.kitpvp.player.Profile;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryClickListener implements Listener {

    private KitPvPPlugin plugin;
    public InventoryClickListener(KitPvPPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Profile profile = plugin.getPlayerManager().getProfile(player.getUniqueId());

        if (profile.getState() == PlayerState.IN_DUEL) {
            Occupation occupation = profile.getDuelOccupation();
            if (occupation != null) {
                Participant participant = occupation.getAlive().get(player.getUniqueId());
                if (participant != null && !participant.isKitApplied()) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
