package cc.kitpvp.kitpvp.listeners.bukkit.player;

import cc.kitpvp.kitpvp.KitPvPPlugin;
import cc.kitpvp.kitpvp.duels.Occupation;
import cc.kitpvp.kitpvp.player.Profile;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.util.Vector;

public class PlayerToggleSneakListener implements Listener {

    private KitPvPPlugin plugin;

    public PlayerToggleSneakListener(KitPvPPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {

        if (event.isSneaking()) {
            Player player = event.getPlayer();
            Profile profile = plugin.getPlayerManager().getProfile(player.getUniqueId());
            Occupation occupation = profile.getDuelOccupation();

            
            // bowspleef mode repulsors
            if (occupation != null) {
                if (occupation.getAlive().containsKey(player.getUniqueId()) && occupation.isBowSpleef()
                        && occupation.getState() == Occupation.State.ACTIVE) {
                    if (profile.getRepulsors() > 0) {
                        profile.setRepulsors(profile.getRepulsors() - 1);

                        for (Player p : occupation.getAlivePlayers()) {
                            if (p != player) {
                                if (p.getLocation().distance(player.getLocation()) < 10) {
                                    Vector v = p.getLocation().toVector().subtract(player.getLocation().subtract(0, 2, 0).toVector()).normalize().multiply(1.25);
                                    p.setVelocity(v);
                                }
                            }
                        }
                        
                    }
                }
            }
        }
    }
}
