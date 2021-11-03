package cc.kitpvp.KitPvP.scoreboard;

import cc.kitpvp.KitPvP.KitPvPPlugin;
import cc.kitpvp.KitPvP.player.PlayerStatistics;
import cc.kitpvp.KitPvP.player.Profile;
import cc.kitpvp.KitPvP.util.Levels;
import cc.kitpvp.KitPvP.util.item.ItemBuilder;
import cc.kitpvp.KitPvP.util.message.CC;
import cc.kitpvp.KitPvP.util.message.Color;
import cc.kitpvp.KitPvP.util.player.PlayerUtil;
import cc.kitpvp.KitPvP.util.scoreboardapi.ScoreboardUpdateEvent;
import cc.kitpvp.KitPvP.util.scoreboardapi.api.ScoreboardAdapter;
import cc.kitpvp.KitPvP.util.timer.Timer;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

@RequiredArgsConstructor
public class KitPvPAdapter implements ScoreboardAdapter {
    private final KitPvPPlugin plugin;

    private boolean showKills;
    private int tickCounter;

    @Override
    public void onUpdate(ScoreboardUpdateEvent event) {
        if (tickCounter++ >= 5) {
            showKills = !showKills;
            tickCounter = 0;
        }
        Player player = event.getPlayer();
        Profile profile = plugin.getPlayerManager().getProfile(player);

        plugin.getServer().getOnlinePlayers().forEach(target -> {
            Profile targetProfile = plugin.getPlayerManager().getProfile(target);

            Objective objective = player.getScoreboard().getObjective("objectiveBelow");
            Score score = objective.getScore(target);

            switch (profile.getState()) {
                case FFA:
                    ItemStack goldItem = new ItemBuilder(Material.GOLD_INGOT).name(CC.GOLD + CC.B + "Gold").lore(CC.GRAY + "Deposit gold in spawn using /deposit!").build();

                    int gold = 0;
                    for (ItemStack itemStack : target.getInventory().getContents()) {
                        if (itemStack == null) continue;

                        if (itemStack.isSimilar(goldItem)) {
                            gold = gold + itemStack.getAmount();
                        }
                    }

                    objective.setDisplayName("Held Gold:");
                    score.setScore(gold);
                    break;

                case SPAWN:
                default:
                    objective.setDisplayName("Kills");
                    score.setScore(targetProfile.getStatistics().getKills());
                    break;
            }
        });

        if (!profile.isScoreboardEnabled()) {
            return;
        }

        event.setTitle(Color.translate("&e&lKITPVP &7(" + plugin.getServer().getOnlinePlayers().size() + "&7)"));

        event.addLine("");
        PlayerStatistics stats = profile.getStatistics();
        event.addLine("Ping: &a" + PlayerUtil.getPing(player) + "&ams");
        event.addLine(profile.getCurrentKit() == null ?
                "Kit: &aNone" : "Kit: &a" + profile.getCurrentKit().getName());
        if (profile.getCurrentKit() != null) {
            if (profile.getCurrentKit().getCooldownTimer(player, profile.getCurrentKit().getName()) != null) {
                Timer cooldown = profile.getCurrentKit().getCooldownTimer(player, profile.getCurrentKit().getName());
                event.addLine("");
                event.addLine("Cooldown: &a" + cooldown.formattedClock());
            }
        }
        event.addLine("Level: &a" + profile.getLevel() + " (" + Levels.progress(profile.getXp()) + ")");
        event.addLine("Kills: &a" + stats.getKills());
        event.addLine("Deaths: &a" + stats.getDeaths());
        event.addLine("Kill Streak: &a" + stats.getKillStreak());
        event.addLine("KDR: &a" + stats.getKillDeathRatio());
        event.addLine("Gold: &a" + stats.getCredits());

        event.addLine("");
        event.addLine("&7kitpvp.cc");
    }

    @Override
    public int updateRate() {
        return 10;
    }
}
