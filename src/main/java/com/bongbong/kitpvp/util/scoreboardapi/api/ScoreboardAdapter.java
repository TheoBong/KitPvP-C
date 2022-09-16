package com.bongbong.kitpvp.util.scoreboardapi.api;

import com.bongbong.kitpvp.util.scoreboardapi.ScoreboardUpdateEvent;

public interface ScoreboardAdapter {

    void onUpdate(ScoreboardUpdateEvent event);

    int updateRate();
}
