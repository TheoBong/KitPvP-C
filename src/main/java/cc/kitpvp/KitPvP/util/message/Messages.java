package cc.kitpvp.KitPvP.util.message;

public class Messages {
    public static String PLAYER_NOT_FOUND = CC.RED + "Player not found.";
    public static String DATA_LOAD_FAIL = CC.RED + "Something went wrong; try re-logging." +
            "\nIf this problem persists, please contact staff for support.";
    private static final String APPEAL_MESSAGE = CC.GRAY + "\nIf you require assistance, open a ticket at " + CC.RED + "https://cheatin.net/discord";
    public static String BANNED_PERMANENTLY = CC.D_RED + "You have been permanently banned from Cheatin Network." + APPEAL_MESSAGE;
    public static String BANNED_TEMPORARILY = CC.D_RED + "You are temporarily banned from Cheatin Network.\nThis ban will expire in %s." + APPEAL_MESSAGE;

    private Messages() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
