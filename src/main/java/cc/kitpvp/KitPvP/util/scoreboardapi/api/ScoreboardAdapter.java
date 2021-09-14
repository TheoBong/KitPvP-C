package cc.kitpvp.KitPvP.util.scoreboardapi.api;

import cc.kitpvp.KitPvP.util.scoreboardapi.ScoreboardUpdateEvent;

public interface ScoreboardAdapter {

    void onUpdate(ScoreboardUpdateEvent event);

    int updateRate();
}
