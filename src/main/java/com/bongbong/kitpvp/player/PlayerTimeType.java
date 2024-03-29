package com.bongbong.kitpvp.player;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public enum PlayerTimeType {
    DAY(0),
    SUNSET(12000),
    NIGHT(13000);

    private final int time;

    public PlayerTimeType nextTimeType() {
        switch (this) {
            case DAY:
                return SUNSET;
            case SUNSET:
                return NIGHT;
            case NIGHT:
                return DAY;
            default:
                return this;
        }
    }

    public void apply(Player player) {
        player.setPlayerTime(time, true);
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}

