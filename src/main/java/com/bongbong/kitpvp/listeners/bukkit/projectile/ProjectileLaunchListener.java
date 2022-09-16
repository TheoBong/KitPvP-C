package com.bongbong.kitpvp.listeners.bukkit.projectile;

import com.bongbong.kitpvp.KitPvPPlugin;
import com.bongbong.kitpvp.duels.Occupation;
import com.bongbong.kitpvp.player.Cooldown;
import com.bongbong.kitpvp.player.Profile;
import com.bongbong.kitpvp.util.EntityHider;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;

public class ProjectileLaunchListener implements Listener {

    private KitPvPPlugin plugin;
    private EntityHider entityHider;
    public ProjectileLaunchListener(KitPvPPlugin plugin) {
        this.plugin = plugin;
        this.entityHider = plugin.getEntityHider();
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if(event.getEntity().getShooter() instanceof Player) {
            Player player = (Player) event.getEntity().getShooter();
            Profile profile = plugin.getPlayerManager().getProfile(player.getUniqueId());
            Occupation occupation = profile.getDuelOccupation();
            for(Player p : Bukkit.getOnlinePlayers()) {
                if(!p.canSee(player)) {
                    entityHider.hideEntity(p, event.getEntity());
                }
            }

            if(occupation != null) {
                occupation.addEntity(event.getEntity());
                if(event.getEntity() instanceof EnderPearl) {
                    Cooldown cooldown = profile.getCooldowns().get(Cooldown.Type.ENDER_PEARL);
                    if(profile.getDuelOccupation().getCurrentPlaying().contains(player)) {
                        if(profile.getDuelOccupation().getState().equals(Occupation.State.ACTIVE)) {
                            if (cooldown != null && !cooldown.isExpired()) {
                                event.setCancelled(true);
                            } else {
                                cooldown = new Cooldown(Cooldown.Type.ENDER_PEARL, profile);
                                profile.getCooldowns().put(Cooldown.Type.ENDER_PEARL, cooldown);
                            }
                        } else {
                            event.setCancelled(true);
                            player.getInventory().addItem(new ItemStack(Material.ENDER_PEARL));
                        }
                    }
                }
            }
        }
    }
}
