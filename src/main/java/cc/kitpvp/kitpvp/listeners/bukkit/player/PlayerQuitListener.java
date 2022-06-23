package cc.kitpvp.kitpvp.listeners.bukkit.player;

import cc.kitpvp.kitpvp.KitPvPPlugin;
import cc.kitpvp.kitpvp.duels.Occupation;
import cc.kitpvp.kitpvp.player.Profile;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    private KitPvPPlugin plugin;
    public PlayerQuitListener(KitPvPPlugin plugin) {
        this.plugin = plugin;
    }

    public void onDisconnect(Player player) {
        Profile profile = plugin.getPlayerManager().getProfile(player.getUniqueId());

        if(profile != null) {
            Occupation occupation = profile.getDuelOccupation();

            if (profile.getDuelOccupation() != null) {
                occupation.leave(player);
            }

            if (profile.getPreviousMatch() != null) {
                profile.getPreviousMatch().terminate();
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        onDisconnect(event.getPlayer());
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        onDisconnect(event.getPlayer());
    }
}
