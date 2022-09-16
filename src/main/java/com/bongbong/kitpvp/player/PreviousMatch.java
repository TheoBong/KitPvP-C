package com.bongbong.kitpvp.player;

import com.bongbong.kitpvp.KitPvPPlugin;
import com.bongbong.kitpvp.duels.arenas.Arena;
import com.bongbong.kitpvp.duels.kits.Kit;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.UUID;

public @Data class PreviousMatch {

    private final Profile profile;
    private final UUID uuid;
    private final String name;
    private final Kit kit;
    private final Arena arena;
    private boolean expired;
    private BukkitTask task;
    public PreviousMatch(Profile profile, UUID uuid, String name, Kit kit, Arena arena, KitPvPPlugin plugin) {
        this.profile = profile;
        this.uuid = uuid;
        this.name = name;
        this.kit = kit;
        this.arena = arena;
        this.expired = false;

        this.task = Bukkit.getScheduler().runTaskLater(plugin, ()-> {
            expired = true;
            if(profile.getPreviousMatch().equals(this)) {
                if (profile.getState().equals(PlayerState.SPAWN)) {
                    profile.duelReset();
                }
            }
        }, 400);
    }

    public void terminate() {
        task.cancel();
    }
}
