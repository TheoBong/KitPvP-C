package com.bongbong.kitpvp.duels;

import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.UUID;

public @Data class Participant {

    private final UUID uuid;
    public final String name;
    public boolean alive = true, kitApplied = false;
    public int kills, hits, currentCombo, longestCombo, thrownPotions, missedPotions;
    private UUID attacker;
    private EntityDamageEvent.DamageCause lastDamageCause;
    private GameInventory gameInventory;

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }
}
