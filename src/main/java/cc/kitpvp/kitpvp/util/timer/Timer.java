package cc.kitpvp.kitpvp.util.timer;

public interface Timer {
    boolean isActive(boolean autoReset);

    boolean isActive();

    String formattedExpiration();

    String formattedClock();

    void reset();
}
