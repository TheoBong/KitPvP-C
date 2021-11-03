package cc.kitpvp.KitPvP.util.message;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;

public class CC {
    public static String WHITE = ChatColor.WHITE.toString();
    public static String GREEN = ChatColor.GREEN.toString();
    public static String D_GREEN = ChatColor.DARK_GREEN.toString();
    public static String RED = ChatColor.RED.toString();
    public static String D_RED = ChatColor.DARK_RED.toString();
    public static String GRAY = ChatColor.GRAY.toString();
    public static String D_GRAY = ChatColor.DARK_GRAY.toString();
    public static String YELLOW = ChatColor.YELLOW.toString();
    public static String GOLD = ChatColor.GOLD.toString();
    public static String AQUA = ChatColor.AQUA.toString();
    public static String D_AQUA = ChatColor.DARK_AQUA.toString();
    public static String BLACK = ChatColor.BLACK.toString();
    public static String BLUE = ChatColor.BLUE.toString();
    public static String PINK = ChatColor.LIGHT_PURPLE.toString();
    public static String PURPLE = ChatColor.DARK_PURPLE.toString();
    public static String B = ChatColor.BOLD.toString();
    public static String I = ChatColor.ITALIC.toString();
    public static String U = ChatColor.UNDERLINE.toString();
    public static String S = ChatColor.STRIKETHROUGH.toString();
    public static String R = ChatColor.RESET.toString();
    public static String PRIMARY = ChatColor.YELLOW.toString();
    public static String SECONDARY = ChatColor.GREEN.toString();
    public static String ACCENT = ChatColor.GOLD.toString();
    public static String SPLITTER = "┃";
    public static String BOARD_SEPARATOR = GRAY + S + StringUtils.repeat("-", 18); //18
    public static String SEPARATOR = GRAY + S + StringUtils.repeat("-", 36); //36

    private CC() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
