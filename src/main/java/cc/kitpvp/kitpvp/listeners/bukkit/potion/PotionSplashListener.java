package cc.kitpvp.kitpvp.listeners.bukkit.potion;

import cc.kitpvp.kitpvp.KitPvPPlugin;
import cc.kitpvp.kitpvp.duels.Occupation;
import cc.kitpvp.kitpvp.duels.Participant;
import cc.kitpvp.kitpvp.player.Profile;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionSplashEvent;

import java.util.ArrayList;
import java.util.List;

public class PotionSplashListener implements Listener {

    private KitPvPPlugin plugin;
    public PotionSplashListener(KitPvPPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPotionSplash(PotionSplashEvent event) {
        ThrownPotion thrownPotion = event.getPotion();
        List<LivingEntity> remove = new ArrayList<>();
        if(thrownPotion.getShooter() instanceof Player) {
            Player player = (Player) thrownPotion.getShooter();
            Profile profile = plugin.getPlayerManager().getProfile(player.getUniqueId());
            Occupation occupation = profile.getDuelOccupation();
            if(occupation != null) {
                if(occupation.getParticipants().containsKey(player.getUniqueId())) {
                    Participant participant = occupation.getParticipants().get(player.getUniqueId());
                    participant.thrownPotions++;
                    if(event.getIntensity(player) < 0.4) {
                        participant.missedPotions++;
                    }
                }
            }

            for(LivingEntity entity : event.getAffectedEntities()) {
                if(entity instanceof Player) {
                    Player p = (Player) entity;
                    Profile pr = plugin.getPlayerManager().getProfile(p.getUniqueId());;
                    Occupation o = pr.getDuelOccupation();

                    if(!p.canSee(player)) {
                        remove.add(entity);
                    }

                    if(o != null) {
                        if(!o.getParticipants().containsKey(p.getUniqueId())) {
                            remove.add(entity);
                        }
                    } else {
                        remove.add(entity);
                    }
                }
            }

            for(LivingEntity entity : remove) {
                event.setIntensity(entity, 0);
            }

            PacketListener particleListener = new PacketAdapter(plugin, PacketType.Play.Server.WORLD_EVENT) {
                @Override
                public void onPacketSending(PacketEvent event) {
                    Player p = event.getPlayer();
                    event.setCancelled(!p.canSee(player));
                }
            };
            plugin.getProtocolManager().addPacketListener(particleListener);
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin,
                    () -> plugin.getProtocolManager().removePacketListener(particleListener), 2L);
        }
    }
}
