package cc.kitpvp.KitPvP.util;

public class Levels {
    public static int calculateLevel(int xp) {
        if (xp < 50) {
            return 1;
        } else {
            int level = 1;

            for (int i = 50; xp >= i; level++) {
                i = i + i/2;
            }

            return level;
        }
    }

    public static String progress(int xp) {
        if (xp < 50) {
            return xp + "/50";
        } else {
            String prog = "null/null";
            for (int i = 50; xp >= i; i = i + i/2) {
                int xp1 = xp - i;
                prog = xp1 + "/" + i;
            }

            return prog;
        }
    }
}
