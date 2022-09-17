package com.bongbong.kitpvp.scoreboard;

import com.bongbong.kitpvp.KitPvPPlugin;
import com.bongbong.kitpvp.duels.Occupation;
import com.bongbong.kitpvp.player.PlayerStatistics;
import com.bongbong.kitpvp.player.Profile;
import com.bongbong.kitpvp.util.Levels;
import com.bongbong.kitpvp.util.item.ItemBuilder;
import com.bongbong.kitpvp.util.message.CC;
import com.bongbong.kitpvp.util.message.Color;
import com.bongbong.kitpvp.util.player.PlayerUtil;
import com.bongbong.kitpvp.util.scoreboardapi.ScoreboardUpdateEvent;
import com.bongbong.kitpvp.util.scoreboardapi.api.ScoreboardAdapter;
import com.bongbong.kitpvp.util.timer.Timer;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

import java.text.SimpleDateFormat;
import java.util.Date;

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

            int gold = calculateGold(target);

            switch (profile.getState()) {
                case FFA:
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
        PlayerStatistics stats = profile.getStatistics();
        switch (profile.getState()) {
            case SPAWN:
                event.setTitle(Color.translate("&e&lLOBBY &7(" + plugin.getServer().getOnlinePlayers().size() + "&7)"));
                event.addLine("&7" + new SimpleDateFormat("MM/dd/yy hh:mm").format(new Date()));
                event.addLine("");
                event.addLine("&fOnline: &a" + Bukkit.getOnlinePlayers().size());
                event.addLine("&fBits: &a" + stats.getCredits());
                event.addLine("");
                event.addLine("&fKills: &a" + stats.getKills());
                event.addLine("&fKillstreak: &a" + stats.getKillStreak());
                event.addLine("&fKDR: &a" + stats.getKillDeathRatio());
                event.addLine("");
                event.addLine("&7bongbong.com");
                break;
            case FFA:
                event.setTitle(Color.translate("&e&lFFA &7(" + plugin.getServer().getOnlinePlayers().size() + "&7)"));
                event.addLine("&7" + new SimpleDateFormat("MM/dd/yy hh:mm").format(new Date()));
                event.addLine("");
                event.addLine("&fHeld Bits: &a" + calculateGold(player));
                event.addLine("&fKill Streak: &a" + stats.getKillStreak());
                event.addLine(profile.getCurrentKit() == null ?
                        "&fKit: &aNone" : "&fKit: &a" + profile.getCurrentKit().getName());
                event.addLine("");
                if (profile.getCurrentKit() != null) {
                    if (profile.getCurrentKit().getCooldownTimer(player, profile.getCurrentKit().getName()) != null) {
                        Timer cooldown = profile.getCurrentKit().getCooldownTimer(player, profile.getCurrentKit().getName());
                        event.addLine("");
                        event.addLine("&fCooldown: &a" + cooldown.formattedClock());
                    }
                }

                event.addLine("");
                event.addLine("&7bongbong.com");
                break;
            case IN_DUEL:
                event.setTitle(Color.translate("&e&lDUELS &7(" + plugin.getServer().getOnlinePlayers().size() + "&7)"));
                event.addLine("&7" + new SimpleDateFormat("MM/dd/yy hh:mm").format(new Date()));
                event.addLine("");
                occupation.getScoreboard(profile).forEach(event::addLine);
                event.addLine("");

                event.addLine("&7bongbong.com");
                break;
            case SPECTATING_DUEL:
                event.setTitle(Color.translate("&e&lDUELS &7(" + plugin.getServer().getOnlinePlayers().size() + "&7)"));
                event.addLine("&7" + new SimpleDateFormat("MM/dd/yy hh:mm").format(new Date()));
                event.addLine("");
                occupation.getSpectatorScoreboard(profile).forEach(event::addLine);
                event.addLine("");

                event.addLine("&7bongbong.com");
                break;
        }

    }

    @Override
    public int updateRate() {
        return 10;
    }

    private int calculateGold(Player target) {
        ItemStack goldItem = new ItemBuilder(Material.GOLD_INGOT).name(CC.GOLD + CC.B + "Gold").lore(CC.GRAY + "Deposit gold in spawn using /deposit!").build();

        int gold = 0;
        for (ItemStack itemStack : target.getInventory().getContents()) {
            if (itemStack == null) continue;

            if (itemStack.isSimilar(goldItem)) {
                gold = gold + itemStack.getAmount();
            }
        }

        return gold;
    }
}
