package cc.kitpvp.KitPvP.util;

public class Levels {
    public static int calculateLevel(int xp) {
        if (xp <= 49) {
            return 1;
        } else {
            int level = 1;
            int i = 50;

            while (xp >= i) {
                i = i + i/2;
                level = level + 1;
            }

            return level;
        }
    }

    public static String progress(int xp) {
        if (xp < 50) {
            return 50 - xp + "/" + 50;
        }

        int i = 50;
        int prevAmount = 50;

        while (xp >= i) {
            prevAmount = i;
            i = i + i/2;
        }

        return (xp - prevAmount) + "/" + i;
    }
}
