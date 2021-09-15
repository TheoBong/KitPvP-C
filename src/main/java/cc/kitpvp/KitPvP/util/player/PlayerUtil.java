package cc.kitpvp.KitPvP.util.player;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PlayerUtil {
    private PlayerUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static void clearPlayer(Player player) {
        player.getActivePotionEffects().stream().map(PotionEffect::getType).forEach(player::removePotionEffect);

        player.setHealth(player.getMaxHealth());
        player.setMaximumNoDamageTicks(20);
        player.setFallDistance(0.0F);
        player.setWalkSpeed(.2F);
        player.setFoodLevel(20);
        player.setSaturation(5.0F);
        player.setFireTicks(0);
        player.setGameMode(GameMode.SURVIVAL);
        player.setAllowFlight(false);
        /*player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());*/
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.setItemOnCursor(null);
        player.updateInventory();
    }

    public static boolean hasEffect(Player player, PotionEffectType type) {
        return player.getActivePotionEffects().stream().anyMatch(effect -> effect.getType().equals(type));
    }

    public static int getPing(Player player) {
        try {
            Object entityPlayer = player.getClass().getMethod("getHandle").invoke(player);
            return (int) entityPlayer.getClass().getField("ping").get(entityPlayer);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
