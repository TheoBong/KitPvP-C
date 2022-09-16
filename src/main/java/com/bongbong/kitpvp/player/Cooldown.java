package com.bongbong.kitpvp.player;

import com.bongbong.kitpvp.util.message.Color;
import lombok.Data;
import org.bukkit.entity.Player;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public @Data class Cooldown {

    public enum Type {
        ENDER_PEARL, ENERGY;

        public int getDuration() {
            switch(this) {
                case ENDER_PEARL:
                    return 16;
                case ENERGY:
                    return 30;
                default:
                    return 0;
            }
        }

        public String blockedMessage() {
            switch(this) {
                case ENDER_PEARL:
                    return Color.translate("&cYou must wait <time> before pearling again.");
                case ENERGY:
                    return Color.translate("&cYou must wait <time> before using your energy again.");
                default:
                    return null;
            }
        }

        public String expireMessage() {
            switch(this) {
                case ENDER_PEARL:
                    return "&aYour pearl cooldown has expired!";
                case ENERGY:
                    return "&aYour energy cooldown has expired!";
                default:
                    return null;
            }
        }
    }

    private final Type type;
    private Profile profile;
    private Date issued;
    private boolean expired = false;

    public Cooldown(Type type, Profile profile) {
        this.type = type;
        this.profile = profile;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.SECOND, type.getDuration());
        this.issued = calendar.getTime();
    }

    public long getRemaining() {
        return issued.getTime() - new Date().getTime();
    }

    public Double getTicksRemaining() {
        long duration = getRemaining();
        long seconds = TimeUnit.MILLISECONDS.toSeconds(duration) % 60;
        long milliseconds = TimeUnit.MILLISECONDS.toMillis(duration) % 1000;

        return (double) (Math.round((float) milliseconds / 50) + (seconds * 20));
    }

    public String getBlockedMessage() {
        long seconds = TimeUnit.MILLISECONDS.toSeconds(getRemaining()) % 60;
        return type.blockedMessage().replace("<time>", String.valueOf(seconds));
    }

    public void check() {
        if(!expired) {
            if(getIssued().before(new Date())) {
                expired = true;
                expire();
            } else {
                if(getType().equals(Type.ENDER_PEARL)) {
                    Player player = profile.getPlayer();
                    long seconds = TimeUnit.MILLISECONDS.toSeconds(getRemaining()) % 60;
                    player.setLevel((int) seconds);
                    player.setExp((getTicksRemaining().floatValue() / (float) (getType().getDuration() * 20)));
                }
            }
        }
    }

    public void expire() {
        profile.getPlayer().sendMessage(Color.translate(getType().expireMessage()));
        remove();
    }

    public void remove() {
        expired = true;
        Player player = profile.getPlayer();
        player.setExp(0);
        player.setLevel(0);
    }
}
