package cc.kitpvp.kitpvp.listeners.bukkit.entity;

import cc.kitpvp.kitpvp.KitPvPPlugin;
import cc.kitpvp.kitpvp.duels.Occupation;
import cc.kitpvp.kitpvp.player.PlayerState;
import cc.kitpvp.kitpvp.player.Profile;
import org.bukkit.ChatColor;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class EntityDamageByEntityListener implements Listener {

    private KitPvPPlugin plugin;
    public EntityDamageByEntityListener(KitPvPPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Player player = null;
        Player attacker = null;
        boolean b = false;
        if(event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            player = (Player) event.getEntity();
            attacker = (Player) event.getDamager();
        }

        boolean sendArrowMsg = false;

        if(event.getDamager() instanceof Arrow && event.getEntity() instanceof Player) {
            Arrow arrow = (Arrow) event.getDamager();
            player = (Player) event.getEntity();
            if(arrow.getShooter() instanceof Player) {
                attacker = (Player) arrow.getShooter();
                sendArrowMsg = true;
                b = true;
            }
        }

        if(player != null && attacker != null) {
            Profile playerProfile = plugin.getPlayerManager().getProfile(player.getUniqueId());
            Profile attackerProfile = plugin.getPlayerManager().getProfile(attacker.getUniqueId());
            Occupation occupation = attackerProfile.getDuelOccupation();
            if(occupation != null && playerProfile.getDuelOccupation() != null) {
                if (occupation.getKit() != null && occupation.getKit().getType().isNoDamage()) {
                    event.setCancelled(true);
                    return;
                }

                if (sendArrowMsg) {
                    attacker.sendMessage(ChatColor.WHITE + player.getName() + ChatColor.GREEN + " is now at " + ChatColor.WHITE + Math.round(player.getHealth()) + " HP" + ChatColor.GREEN + ".");
                }

                if (attackerProfile.getState() == PlayerState.SPECTATING_DUEL) {
                    event.setCancelled(true);
                    return;
                }

                occupation.handleHit(player, attacker, event);
            } else {
                event.setCancelled(true);
            }
        }
    }
}
