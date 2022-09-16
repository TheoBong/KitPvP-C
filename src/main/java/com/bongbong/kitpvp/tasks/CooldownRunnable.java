package com.bongbong.kitpvp.tasks;


import com.bongbong.kitpvp.KitPvPPlugin;
import com.bongbong.kitpvp.player.Cooldown;
import com.bongbong.kitpvp.player.Profile;

import java.util.HashMap;
import java.util.Map;

public class CooldownRunnable implements Runnable {

    private KitPvPPlugin plugin;
    public CooldownRunnable(KitPvPPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        for(Profile profile : plugin.getPlayerManager().getProfiles().values()) {
            Map<Cooldown.Type, Cooldown> cooldowns = new HashMap<>(profile.getCooldowns());
            for(Cooldown cooldown : cooldowns.values()) {
                cooldown.check();
            }
        }
    }
}
