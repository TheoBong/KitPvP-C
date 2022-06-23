package cc.kitpvp.kitpvp.util;

import cc.kitpvp.kitpvp.KitPvPPlugin;

public class ThreadUtil {
    public static void runTask(boolean async, KitPvPPlugin plugin, Runnable runnable) {
        if(async) {
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, runnable);
        } else {
            runnable.run();
        }
    }
}
