package cc.kitpvp.kitpvp.listeners.bukkit.player;

import cc.kitpvp.kitpvp.KitPvPPlugin;
import cc.kitpvp.kitpvp.duels.Occupation;
import cc.kitpvp.kitpvp.player.Profile;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleFlightEvent;

public class PlayerToggleFlightListener implements Listener {

    private KitPvPPlugin plugin;
    public PlayerToggleFlightListener(KitPvPPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();
        Profile profile = plugin.getPlayerManager().getProfile(player.getUniqueId());
        Occupation occupation = profile.getDuelOccupation();

        if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR || player.isFlying()) {
         

            return;
         
        } else {
            // bowspleef mode double jumps
            if(occupation.getAlive().containsKey(player.getUniqueId()) && occupation.isBowSpleef() && occupation.getState() == Occupation.State.ACTIVE) {
                if (profile.getDoubleJumps() > 0) {
                profile.setDoubleJumps(profile.getDoubleJumps() - 1);
                event.setCancelled(true);
                player.setAllowFlight(false);
                player.setVelocity(player.getLocation().getDirection().multiply(1.5D));

        
                    Bukkit.getScheduler().runTaskLater(plugin, ()-> {
                        if(occupation.getAlive().containsKey(player.getUniqueId()) && occupation.isBowSpleef() && occupation.getState() == Occupation.State.ACTIVE) {
                            if (profile.getDoubleJumps() > 0) {
                                player.setAllowFlight(true);
                            } else {
                                player.setAllowFlight(false);
                            }
                        }
                    }, 20);
                } else {
                    player.setAllowFlight(false);

                }
            }
        }
    }
}
