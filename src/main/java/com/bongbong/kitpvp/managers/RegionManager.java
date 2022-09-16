package com.bongbong.kitpvp.managers;

import com.bongbong.kitpvp.util.structure.RegionData;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RegionManager {
    private Map<UUID, RegionData> regionData = new HashMap<>();

    public void startEditingRegion(Player player) {
        regionData.put(player.getUniqueId(), new RegionData());
    }

    public boolean isEditingRegion(Player player) {
        return regionData.containsKey(player.getUniqueId());
    }

    public boolean isDataValid(Player player) {
        return isEditingRegion(player) && regionData.get(player.getUniqueId()).getA() != null
                && regionData.get(player.getUniqueId()).getB() != null;
    }

    public RegionData getData(Player player) {
        return regionData.get(player.getUniqueId());
    }

    public void stopEditingRegion(Player player) {
        regionData.remove(player.getUniqueId());
    }
}
