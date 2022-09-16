package com.bongbong.kitpvp.util.timer.impl;

import com.bongbong.kitpvp.util.time.TimeUtil;
import com.bongbong.kitpvp.util.timer.AbstractTimer;

import java.util.concurrent.TimeUnit;

public class IntegerTimer extends AbstractTimer {
    public IntegerTimer(TimeUnit unit, int amount) {
        super(unit, amount);
    }

    @Override
    public String formattedExpiration() {
        return TimeUtil.formatTimeMillis(expiry - System.currentTimeMillis());
    }

    @Override
    public String formattedClock() {
        return TimeUtil.formatTimeMillisToClock(expiry - System.currentTimeMillis());
    }
}
