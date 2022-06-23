package cc.kitpvp.kitpvp.util.timer.impl;

import cc.kitpvp.kitpvp.util.time.TimeUtil;
import cc.kitpvp.kitpvp.util.timer.AbstractTimer;

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
