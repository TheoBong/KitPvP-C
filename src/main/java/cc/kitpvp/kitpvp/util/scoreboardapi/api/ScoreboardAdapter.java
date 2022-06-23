package cc.kitpvp.kitpvp.util.scoreboardapi.api;

import cc.kitpvp.kitpvp.util.scoreboardapi.ScoreboardUpdateEvent;

public interface ScoreboardAdapter {

    void onUpdate(ScoreboardUpdateEvent event);

    int updateRate();
}
