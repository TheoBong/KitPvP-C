package cc.kitpvp.kitpvp.listeners.bukkit.entity;

import cc.kitpvp.kitpvp.KitPvPPlugin;
import cc.kitpvp.kitpvp.duels.Occupation;
import cc.kitpvp.kitpvp.player.PlayerState;
import cc.kitpvp.kitpvp.player.Profile;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class EntityDamageListener implements Listener {

    private KitPvPPlugin plugin;
    public EntityDamageListener(KitPvPPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if(event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            Profile profile = plugin.getPlayerManager().getProfile(player.getUniqueId());
            if(profile.getState().equals(PlayerState.IN_DUEL)) {
                Occupation occupation = profile.getDuelOccupation();
                occupation.handleDamage(player, event);
            } else {
                event.setCancelled(true);
            }
        }
    }

}
