package com.bongbong.kitpvp.listeners.bukkit.player;

import com.bongbong.kitpvp.KitPvPPlugin;
import com.bongbong.kitpvp.duels.Occupation;
import com.bongbong.kitpvp.player.Profile;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class FoodLevelChangeListener implements Listener {

    private KitPvPPlugin plugin;
    public FoodLevelChangeListener(KitPvPPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if(event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            Profile profile = plugin.getPlayerManager().getProfile(player.getUniqueId());
            Occupation occupation = profile.getDuelOccupation();
            if(occupation != null) {
                if(occupation.getKit() != null && !occupation.getKit().isHunger()) {
                    event.setCancelled(true);
                }
            } else {
                event.setCancelled(true);
            }
        }
    }
}
