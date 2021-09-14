package cc.kitpvp.KitPvP.util.timer.impl;

import cc.kitpvp.KitPvP.util.time.TimeUtil;
import cc.kitpvp.KitPvP.util.timer.AbstractTimer;

import java.util.concurrent.TimeUnit;

public class DoubleTimer extends AbstractTimer {
    public DoubleTimer(int seconds) {
        super(TimeUnit.SECONDS, seconds);
    }

    @Override
    public String formattedExpiration() {
        double seconds = (expiry - System.currentTimeMillis()) / 1000.0;
        return String.format("%.1f seconds", seconds);
    }

    @Override
    public String formattedClock() {
        return TimeUtil.formatTimeSecondsToClock(expiry - System.currentTimeMillis());
    }


}
