package com.bongbong.kitpvp.util;

import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

public class PlayerUtils {

    public static int getPing(Player player) {
        try {
            Object entityPlayer = player.getClass().getMethod("getHandle").invoke(player);
            return (int) entityPlayer.getClass().getField("ping").get(entityPlayer);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | NoSuchFieldException e) {
            e.printStackTrace();
        }

        return 0;
    }
}
