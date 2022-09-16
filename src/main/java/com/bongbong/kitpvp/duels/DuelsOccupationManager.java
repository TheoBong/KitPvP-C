package com.bongbong.kitpvp.duels;

import com.bongbong.kitpvp.KitPvPPlugin;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

public @Data class DuelsOccupationManager {

    private KitPvPPlugin plugin;
    private Map<String, Occupation> occupations;
    public DuelsOccupationManager(KitPvPPlugin plugin) {
        this.plugin = plugin;
        this.occupations = new HashMap<>();
    }

    public int getInGame() {
        int i = 0;
        for(Occupation occupation : occupations.values()) {
            if(!occupation.getState().equals(Occupation.State.ENDED) && !occupation.getState().equals(Occupation.State.STOPPED) && !occupation.getState().equals(Occupation.State.CREATED)) {
                i += occupation.getAllPlayers().size();
            }
        }

        return i;
    }
}
