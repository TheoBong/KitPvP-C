package cc.kitpvp.kitpvp.duels;

import cc.kitpvp.kitpvp.KitPvPPlugin;
import cc.kitpvp.kitpvp.duels.arenas.Arena;
import cc.kitpvp.kitpvp.duels.impl.Duel;
import cc.kitpvp.kitpvp.duels.kits.Kit;
import cc.kitpvp.kitpvp.managers.PlayerManager;
import cc.kitpvp.kitpvp.player.PlayerState;
import cc.kitpvp.kitpvp.player.Profile;
import cc.kitpvp.kitpvp.util.message.Color;
import lombok.Data;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public @Data class DuelRequest {

    private final UUID to, from;
    private final Kit kit;
    private final Arena arena;
    private KitPvPPlugin plugin;
    private Date expires;

    public DuelRequest(KitPvPPlugin plugin, UUID to, UUID from, Kit kit, Arena arena) {
        this.to = to;
        this.from = from;
        this.kit = kit;
        this.arena = arena;
        this.plugin = plugin;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.SECOND, 30);
        this.expires = calendar.getTime();
    }

    public void start() {
        PlayerManager pm = plugin.getPlayerManager();
        Player to = Bukkit.getPlayer(getTo());
        Player from = Bukkit.getPlayer(getFrom());
        if(to != null && from != null && !isExpired()) {
            Profile toProfile = pm.getProfile(getTo());
            Profile fromProfile = pm.getProfile(getFrom());
            if(toProfile != null && fromProfile != null && toProfile.getState().equals(PlayerState.SPAWN) && fromProfile.getState().equals(PlayerState.SPAWN)) {
                Duel duel = new Duel(plugin, UUID.randomUUID());
                duel.join(to);
                duel.join(from);
                duel.setKit(kit);
                duel.setArena(arena);
                duel.start();
                plugin.getDuelsOccupationManager().getOccupations().put(duel.getUuid(), duel);

                toProfile.setState(PlayerState.IN_DUEL);
                fromProfile.setState(PlayerState.IN_DUEL);

                this.expires = new Date();
            }
        }
    }

    public void send() {
        Player to = Bukkit.getPlayer(getTo());
        Player from = Bukkit.getPlayer(getFrom());

        StringBuilder sb = new StringBuilder();
        if(arena != null) {
            sb.append("&bYou received a " + kit.getDisplayName() + "&b duel request from &f" + from.getName() + "&b at the &f" + arena.getDisplayName() + "&b arena, click to accept.");
        } else {
            sb.append("&bYou received a " + Color.translate(kit.getDisplayName()) + "&b duel request from &f" + from.getName() + "&b, click to accept.");
        }

        TextComponent msg = new TextComponent(Color.translate(sb.toString()));
        msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/accept " + from.getName()));
        msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Color.translate("&aClick to accept!")).create()));
        to.spigot().sendMessage(msg);

        from.sendMessage(ChatColor.GREEN + "You sent a duel request to " + ChatColor.WHITE + to.getName() + ChatColor.GREEN + ".");
    }

    public boolean isExpired() {
        return expires.before(new Date());
    }
}
