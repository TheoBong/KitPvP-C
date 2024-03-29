package com.bongbong.kitpvp.managers;

import com.bongbong.kitpvp.KitPvPPlugin;
import lombok.Getter;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LeaderBoardManager implements Runnable {
    private final KitPvPPlugin plugin;
    @Getter private List<String> killsLeaderboard = new ArrayList<>();
    @Getter private List<String> creditsLeaderboard = new ArrayList<>();
    @Getter private List<String> killStreakLeaderboard = new ArrayList<>();

    public LeaderBoardManager(KitPvPPlugin plugin) {
        this.plugin = plugin;
    }

    private Iterator<Document> sortKills() {
        return plugin.getMongo().sortNumber(false, "profiles", "kills");
    }

    private Iterator<Document> sortCredits() {
        return plugin.getMongo().sortNumber(false, "profiles", "credits");
    }

    private Iterator<Document> sortKillStreak() {
        return plugin.getMongo().sortNumber(false, "profiles", "highest_kill_streak");
    }

    public void run() {
        killStreakLeaderboard.clear();
        creditsLeaderboard.clear();
        killStreakLeaderboard.clear();

        Iterator<Document> killsSorted = sortKills();
        Iterator<Document> creditsSorted = sortCredits();
        Iterator<Document> killStreakSorted = sortKillStreak();

        int killsPosition = 0;
        while(killsSorted.hasNext()) {
            killsPosition = killsPosition + 1;

            Document document = killsSorted.next();
            String name = document.getString("name");

            killsLeaderboard.add("#" + killsPosition + " - " + name + " - " + document.getInteger("kills"));

            if (killsPosition > 3) break;
        }

        int creditsPosition = 0;
        while(creditsSorted.hasNext()) {
            creditsPosition = creditsPosition + 1;

            Document document = creditsSorted.next();
            String name = document.getString("name");

            creditsLeaderboard.add("#" + creditsPosition + " - " + name + " - " + document.getInteger("credits"));

            if (creditsPosition > 3) break;
        }

        int killStreakPosition = 0;
        while(killStreakSorted.hasNext()) {
            killStreakPosition = killStreakPosition + 1;

            Document document = killStreakSorted.next();
            String name = document.getString("name");

            killStreakLeaderboard.add("#" + killStreakPosition + " - " + name + " - " + document.getInteger("highest_kill_streak"));

            if (killStreakPosition > 3) break;
        }

        for(int i = 0; i < 4; i++) {
            try { killsLeaderboard.get(i); }
            catch (IndexOutOfBoundsException ignored) { killsLeaderboard.add("null"); }
            try { creditsLeaderboard.get(i); } catch (IndexOutOfBoundsException e) { creditsLeaderboard.add("null"); }
            try { killStreakLeaderboard.get(i); } catch (IndexOutOfBoundsException e) { killStreakLeaderboard.add("null"); }
        }

        plugin.getServer().getScheduler().runTask(plugin, ()-> {
            plugin.getKillHologram().clearLines();
            plugin.getKillStreakHologram().clearLines();
            plugin.getCreditsHologram().clearLines();

            plugin.getKillHologram().insertTextLine(0, "Kills Leaderboard");
            plugin.getKillHologram().insertTextLine(1, " ");
            plugin.getKillHologram().insertTextLine(2, killsLeaderboard.get(0));
            plugin.getKillHologram().insertTextLine(3, killsLeaderboard.get(1));
            plugin.getKillHologram().insertTextLine(4, killsLeaderboard.get(2));

            plugin.getKillStreakHologram().insertTextLine(0, "KillStreak Leaderboard");
            plugin.getKillStreakHologram().insertTextLine(1," ");
            plugin.getKillStreakHologram().insertTextLine(2, killStreakLeaderboard.get(0));
            plugin.getKillStreakHologram().insertTextLine(3, killStreakLeaderboard.get(1));
            plugin.getKillStreakHologram().insertTextLine(4, killStreakLeaderboard.get(2));

            plugin.getCreditsHologram().insertTextLine(0, "Credits Leaderboard");
            plugin.getCreditsHologram().insertTextLine(1, " ");
            plugin.getCreditsHologram().insertTextLine(2, creditsLeaderboard.get(0));
            plugin.getCreditsHologram().insertTextLine(3, creditsLeaderboard.get(1));
            plugin.getCreditsHologram().insertTextLine(4, creditsLeaderboard.get(2));
        });
    }
}
