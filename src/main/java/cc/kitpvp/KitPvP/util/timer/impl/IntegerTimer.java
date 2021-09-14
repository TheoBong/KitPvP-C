package cc.kitpvp.KitPvP.util.timer.impl;

import cc.kitpvp.KitPvP.util.time.TimeUtil;
import cc.kitpvp.KitPvP.util.timer.AbstractTimer;

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
