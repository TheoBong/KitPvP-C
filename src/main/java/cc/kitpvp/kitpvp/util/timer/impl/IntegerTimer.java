package cc.kitpvp.kitpvp.util.timer.impl;

import cc.kitpvp.kitpvp.util.time.TimeUtil;
import cc.kitpvp.kitpvp.util.timer.AbstractTimer;

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
