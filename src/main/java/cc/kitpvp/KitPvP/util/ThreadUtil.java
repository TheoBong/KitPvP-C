package cc.kitpvp.KitPvP.util;

import cc.kitpvp.KitPvP.KitPvPPlugin;

public class ThreadUtil {
    public static void runTask(boolean async, KitPvPPlugin plugin, Runnable runnable) {
        if(async) {
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, runnable);
        } else {
            runnable.run();
        }
    }
}
