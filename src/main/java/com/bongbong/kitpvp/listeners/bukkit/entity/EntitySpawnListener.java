package com.bongbong.kitpvp.listeners.bukkit.entity;

import com.bongbong.kitpvp.KitPvPPlugin;
import com.bongbong.kitpvp.duels.Occupation;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class EntitySpawnListener implements Listener {

    private KitPvPPlugin plugin;
    public EntitySpawnListener(KitPvPPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        if(event.getEntity() instanceof Item) {
            Item item = (Item) event.getEntity();
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                for (Occupation occupation : plugin.getDuelsOccupationManager().getOccupations().values()) {
                    if(occupation.getEntities().contains(item)) {
                        if(item.getItemStack().getType().equals(Material.GLASS_BOTTLE) || item.getItemStack().getType().equals(Material.BOWL)) {
                            item.remove();
                        }

                        new BukkitRunnable() {
                            public void run() {
                                item.remove();
                            }
                        }.runTaskLater(plugin, 500L);
                    }
                }
            }, 1);
        }
    }
}
