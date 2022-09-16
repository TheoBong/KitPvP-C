package com.bongbong.kitpvp.listeners.packets;

import com.bongbong.kitpvp.KitPvPPlugin;
import com.bongbong.kitpvp.duels.Occupation;
import com.bongbong.kitpvp.player.Cooldown;
import com.bongbong.kitpvp.player.Profile;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class EnderpearlSound extends PacketAdapter {

    private KitPvPPlugin plugin;

    public EnderpearlSound(KitPvPPlugin plugin) {
        super(plugin, PacketType.Play.Server.NAMED_SOUND_EFFECT);
        this.plugin = plugin;
    }

    @Override
    public void onPacketSending(PacketEvent e) {
        PacketContainer packet = e.getPacket();
        Player player = e.getPlayer();
        Location soundLocation = new Location(player.getWorld(), packet.getIntegers().read(0) / 8.0, packet.getIntegers().read(1) / 8.0, packet.getIntegers().read(2) / 8.0);
        String soundName = packet.getStrings().read(0);

        if(soundName.equalsIgnoreCase("random.bow")) {
            Profile profile = plugin.getPlayerManager().getProfile(player.getUniqueId());
            Player closest = null;
            double bestDistance = Double.MAX_VALUE;

            for (Player p : player.getWorld().getPlayers()) {
                if (p.getLocation().distance(soundLocation) < bestDistance) {
                    bestDistance = p.getLocation().distance(soundLocation);
                    closest = p;
                }
            }

            if (!player.canSee(closest)) {
                e.setCancelled(true);
            }

            if (player.getItemInHand().getType().equals(Material.ENDER_PEARL)) {
                Occupation occupation = profile.getDuelOccupation();
                if(occupation != null && occupation.getState().equals(Occupation.State.ACTIVE)) {
                    Cooldown cooldown = profile.getCooldowns().get(Cooldown.Type.ENDER_PEARL);
                    if(cooldown != null) {
                        if(!cooldown.isExpired()) {
                            e.setCancelled(true);
                        }
                    }
                } else {
                    e.setCancelled(true);
                }
            }
        }
    }
}
