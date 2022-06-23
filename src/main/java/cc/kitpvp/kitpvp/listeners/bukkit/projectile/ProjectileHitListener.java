package cc.kitpvp.kitpvp.listeners.bukkit.projectile;

import cc.kitpvp.kitpvp.KitPvPPlugin;
import cc.kitpvp.kitpvp.duels.Occupation;
import cc.kitpvp.kitpvp.player.Profile;
import cc.kitpvp.kitpvp.util.EntityHider;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;

public class ProjectileHitListener implements Listener {

    private KitPvPPlugin plugin;
    private EntityHider entityHider;
    public ProjectileHitListener(KitPvPPlugin plugin) {
        this.plugin = plugin;
        this.entityHider = plugin.getEntityHider();
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if(event.getEntity().getShooter() instanceof Player) {
            Player player = (Player) event.getEntity().getShooter();
            Profile profile = plugin.getPlayerManager().getProfile(player.getUniqueId());
            Occupation occupation = profile.getDuelOccupation();
            if(occupation != null) {
                occupation.addEntity(event.getEntity());
            }

            PacketListener particleListener = new PacketAdapter(plugin, PacketType.Play.Server.NAMED_SOUND_EFFECT) {
                @Override
                public void onPacketSending(PacketEvent event) {
                    PacketContainer packet = event.getPacket();
                    Player p = event.getPlayer();
                    String sound = packet.getStrings().read(0);
                    if(sound.equalsIgnoreCase("random.bowhit")) {
                        event.setCancelled(!p.canSee(player));
                    }
                }
            };
            plugin.getProtocolManager().addPacketListener(particleListener);
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin,
                    () -> plugin.getProtocolManager().removePacketListener(particleListener), 2L);
        }
    }
}
