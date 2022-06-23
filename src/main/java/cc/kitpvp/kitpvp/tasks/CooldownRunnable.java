package cc.kitpvp.kitpvp.tasks;


import cc.kitpvp.kitpvp.KitPvPPlugin;
import cc.kitpvp.kitpvp.player.Cooldown;
import cc.kitpvp.kitpvp.player.Profile;

import java.util.HashMap;
import java.util.Map;

public class CooldownRunnable implements Runnable {

    private KitPvPPlugin practice;
    public CooldownRunnable(KitPvPPlugin practice) {
        this.practice = practice;
    }

    @Override
    public void run() {
        for(Profile profile : practice.getPlayerManager().getProfiles().values()) {
            Map<Cooldown.Type, Cooldown> cooldowns = new HashMap<>(profile.getCooldowns());
            for(Cooldown cooldown : cooldowns.values()) {
                cooldown.check();
            }
        }
    }
}
