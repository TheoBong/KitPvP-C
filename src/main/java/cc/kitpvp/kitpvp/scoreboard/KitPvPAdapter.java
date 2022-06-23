package cc.kitpvp.kitpvp.scoreboard;

import cc.kitpvp.kitpvp.KitPvPPlugin;
import cc.kitpvp.kitpvp.duels.Occupation;
import cc.kitpvp.kitpvp.player.PlayerStatistics;
import cc.kitpvp.kitpvp.player.Profile;
import cc.kitpvp.kitpvp.util.Levels;
import cc.kitpvp.kitpvp.util.item.ItemBuilder;
import cc.kitpvp.kitpvp.util.message.CC;
import cc.kitpvp.kitpvp.util.message.Color;
import cc.kitpvp.kitpvp.util.player.PlayerUtil;
import cc.kitpvp.kitpvp.util.scoreboardapi.ScoreboardUpdateEvent;
import cc.kitpvp.kitpvp.util.scoreboardapi.api.ScoreboardAdapter;
import cc.kitpvp.kitpvp.util.timer.Timer;
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

                case IN_DUEL:
                case SPECTATING_DUEL:
                case WAITING_IN_DUEL:
                    objective.setDisplayName("Ping: ");
                    score.setScore(PlayerUtil.getPing(target));
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

        Occupation occupation = profile.getDuelOccupation();
        switch (profile.getState()) {
            case SPAWN:
            case FFA:
                event.setTitle(Color.translate("&e&lKITPVP &7(" + plugin.getServer().getOnlinePlayers().size() + "&7)"));

                event.addLine("");
                PlayerStatistics stats = profile.getStatistics();
                event.addLine("&fPing: &a" + PlayerUtil.getPing(player) + "&ams");
                event.addLine(profile.getCurrentKit() == null ?
                        "&fKit: &aNone" : "&fKit: &a" + profile.getCurrentKit().getName());
                if (profile.getCurrentKit() != null) {
                    if (profile.getCurrentKit().getCooldownTimer(player, profile.getCurrentKit().getName()) != null) {
                        Timer cooldown = profile.getCurrentKit().getCooldownTimer(player, profile.getCurrentKit().getName());
                        event.addLine("");
                        event.addLine("&fCooldown: &a" + cooldown.formattedClock());
                    }
                }
                event.addLine("&fLevel: &a" + profile.getLevel() + " (" + Levels.progress(profile.getXp()) + ")");
                event.addLine("&fKills: &a" + stats.getKills());
                event.addLine("&fDeaths: &a" + stats.getDeaths());
                event.addLine("&fKill Streak: &a" + stats.getKillStreak());
                event.addLine("&fKDR: &a" + stats.getKillDeathRatio());
                event.addLine("&fGold: &a" + stats.getCredits());

                event.addLine("");
                event.addLine("&7kitpvp.cc");
                break;

            case IN_DUEL:
                event.setTitle(Color.translate("&e&lDUELS &7(" + plugin.getServer().getOnlinePlayers().size() + "&7)"));

                event.addLine("");
                occupation.getScoreboard(profile).forEach(event::addLine);
                event.addLine("");

                event.addLine("&7kitpvp.cc");
                break;
            case SPECTATING_DUEL:
                event.setTitle(Color.translate("&e&lDUELS &7(" + plugin.getServer().getOnlinePlayers().size() + "&7)"));

                event.addLine("");
                occupation.getSpectatorScoreboard(profile).forEach(event::addLine);
                event.addLine("");

                event.addLine("&7kitpvp.cc");
                break;
        }

    }

    @Override
    public int updateRate() {
        return 10;
    }
}
