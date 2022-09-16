package com.bongbong.kitpvp.util;

import com.bongbong.kitpvp.KitPvPPlugin;

public class ThreadUtil {
    public static void runTask(boolean async, KitPvPPlugin plugin, Runnable runnable) {
        if(async) {
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, runnable);
        } else {
            runnable.run();
        }
    }
}
