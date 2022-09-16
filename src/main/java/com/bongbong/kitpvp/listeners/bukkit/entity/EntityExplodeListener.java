package com.bongbong.kitpvp.listeners.bukkit.entity;

import com.bongbong.kitpvp.KitPvPPlugin;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

public class EntityExplodeListener implements Listener {

    private KitPvPPlugin plugin;
    public EntityExplodeListener(KitPvPPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if (event.getEntity().getType() == EntityType.PRIMED_TNT) {
            event.setCancelled(true);
        }
    }
}
