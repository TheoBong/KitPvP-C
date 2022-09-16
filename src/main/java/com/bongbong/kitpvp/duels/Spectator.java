package com.bongbong.kitpvp.duels;

import lombok.Data;
import org.bukkit.entity.Player;

import java.util.UUID;

public @Data class Spectator {
    private final UUID uuid;
    private final String name;
    private Player target;
}
