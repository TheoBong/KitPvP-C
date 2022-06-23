package cc.kitpvp.kitpvp.duels.impl;

import cc.kitpvp.kitpvp.KitPvPPlugin;
import cc.kitpvp.kitpvp.duels.*;
import cc.kitpvp.kitpvp.duels.arenas.Arena;
import cc.kitpvp.kitpvp.duels.kits.Kit;
import cc.kitpvp.kitpvp.managers.PlayerManager;
import cc.kitpvp.kitpvp.player.Cooldown;
import cc.kitpvp.kitpvp.player.PlayerState;
import cc.kitpvp.kitpvp.player.PreviousMatch;
import cc.kitpvp.kitpvp.player.Profile;
import cc.kitpvp.kitpvp.util.PlayerUtils;
import cc.kitpvp.kitpvp.util.message.Color;
import cc.kitpvp.kitpvp.util.time.TimeUtil;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class Duel extends Occupation {

    public Duel(KitPvPPlugin plugin, UUID uuid) {
        super(plugin, uuid);
    }

    @Override
    public void start() {
        List<Arena> list = new ArrayList<>();

        if(getArena() == null) {
            for(Arena a : plugin.getDuelsArenaManager().getArenas().values()) {
                if(a.isEnabled()) {
                    if(a.getType().equals(getKit().getType())){
                        list.add(a);
                    }
                }
            }

            if(list.isEmpty()) {
                for(Player p : getAlivePlayers()) {
                    Profile profile = plugin.getPlayerManager().getProfile(p.getUniqueId());
                    p.sendMessage(ChatColor.RED + "There are no arenas currently available for the ladder selected. Please notify a staff member.");
                    profile.setDuelOccupation(null);
                    profile.playerUpdate();
                }
                return;
            } else {
                Collections.shuffle(list);
                this.setArena(list.get(0));
            }
        }

        this.setState(State.STARTING);

        StringBuilder stringBuilder = new StringBuilder();
        List<Player> players = new ArrayList<>(this.getAlivePlayers());
        int s = 0;
        while(s != this.getAlive().size()) {
            Player p = players.get(0);
            stringBuilder.append(ChatColor.WHITE + p.getName());

            players.remove(p);
            s++;
            if(s == this.getAlivePlayers().size()) {
                stringBuilder.append(ChatColor.GRAY + ".");
            } else {
                stringBuilder.append(ChatColor.GRAY + ", ");
            }
        }

        for(Player p : this.getAlivePlayers()) {
            p.sendMessage(" ");
            p.sendMessage(Color.translate("&b&lMatch starting in 5 seconds."));
            p.sendMessage(Color.translate(" &7● &bMap: &f" + Color.translate(getArena().getDisplayName())));
            p.sendMessage(Color.translate(" &7● &bParticipants: &f" + stringBuilder.toString()));
            p.sendMessage(" ");
        }

        Map<Player, Location> locations = new HashMap<>();

        int position = 1;
        for(Map.Entry<UUID, Participant> entry : this.getParticipants().entrySet()) {
            Participant participant = entry.getValue();
            Player p = Bukkit.getPlayer(entry.getKey());
            Location location = null;
            if(position == 1) {
                location = getArena().getPos1();
            }

            if(position == 2) {
                location = getArena().getPos2();
            }

            locations.put(p, location);

            if(p != null) {
                Profile profile = plugin.getPlayerManager().getProfile(p.getUniqueId());
                profile.playerUpdateVisibility();
                profile.duelReset();
                p.teleport(locations.get(p));

                getKit().apply(p);
                participant.setKitApplied(true);

            }
            position++;
        }

        new BukkitRunnable() {
            int i = 5;
            public void run() {
                if(Duel.this.getState().equals(State.ENDED)) {
                    cancel();
                }

                if (i == 0) {
                    for(Player p : Duel.this.getAlivePlayers()) {
                        if(p != null) {
                            p.playSound(p.getLocation(), Sound.NOTE_PIANO, 1, 1);
                            p.sendMessage(ChatColor.GREEN + "The game has started, good luck!");
                        }
                    }

                    Duel duel = Duel.this;
                    duel.setStarted(new Date());
                    duel.setState(State.ACTIVE);

                    cancel();
                } else {
                    if (i > 0) {
                        for (Player p : Duel.this.getAlivePlayers()) {
                            p.playSound(p.getLocation(), Sound.CLICK, 1, 1);
                            p.sendMessage(ChatColor.GREEN.toString() + i + "...");
                        }
                    }

                    i -= 1;
                }
            }
        }.runTaskTimer(plugin, 20, 20);
    }

    @Override
    public void end() {
        PlayerManager pm = plugin.getPlayerManager();
        this.setEnded(new Date());
        this.setState(State.ENDED);

        String winner = null;
        String loser = null;
        Profile winnerProfile = null;
        Profile loserProfile = null;

        for(Map.Entry<UUID, Participant> entry : this.getParticipants().entrySet()) {
            Participant participant = entry.getValue();
            if(participant.isAlive()) {
                winner = participant.getName();
                winnerProfile = pm.getProfile(entry.getKey());
                participant.setGameInventory(new GameInventory(participant));
            } else {
                loser = participant.getName();
                loserProfile = pm.getProfile(entry.getKey());
            }
        }

        List<TextComponent> components = new ArrayList<>();
        for(Participant p : this.getParticipants().values()) {
            TextComponent text = new TextComponent(ChatColor.WHITE + p.getName());
            text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/inventory " + p.getGameInventory().getUuid()));
            text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.GREEN + "Click to view " + ChatColor.WHITE + p.getName() + "'s " + ChatColor.GREEN + "inventory.").create()));
            components.add(text);

            for(Participant p2 : this.getParticipants().values()) {
                Profile profile = pm.getProfile(p2.getUuid());
                if (profile != null) {
                    PreviousMatch previousMatch = new PreviousMatch(profile, p.getUuid(), p.getName(), getKit(), getArena(), plugin);
                    profile.setPreviousMatch(previousMatch);
                    if(p2 != p) {
                        p.getGameInventory().setPreviousInventory(p2.getGameInventory());
                        p.getGameInventory().setNextInventory(p2.getGameInventory());
                        break;
                    }
                }
            }
        }

        // Match Summary Message
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(" ");
        stringBuilder.append(Color.translate("&b&lMatch ended."));
        stringBuilder.append(Color.translate("\n &7● &bWinner: &f" + winner));

        TextComponent text = new TextComponent(ChatColor.GRAY + " ● " + ChatColor.AQUA + "Inventories: ");
        final int size = components.size();
        int x = 0;
        while(x != size) {
            TextComponent t = components.get(0);
            text.addExtra(t);
            components.remove(t);
            x++;
            if(x == size) {
                text.addExtra(new TextComponent(ChatColor.GRAY + "."));
            } else {
                text.addExtra(new TextComponent(ChatColor.GRAY + ", "));
            }
        }


        for(Player player : this.getAllPlayers()) {
            player.sendMessage(" ");
            player.sendMessage(stringBuilder.toString());
            player.spigot().sendMessage(text);
            player.sendMessage(" ");
        }

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            for(Map.Entry<UUID, Participant> entry : this.getParticipants().entrySet()) {
                Player player = Bukkit.getPlayer(entry.getKey());
                Profile profile = pm.getProfile(entry.getKey());
                if(player != null) {
                    if(entry.getValue().isAlive()) {
                        for(Cooldown cooldown : pm.getProfile(player.getUniqueId()).getCooldowns().values()) {
                            cooldown.remove();
                        }

                        player.getInventory().clear();
                        player.getInventory().setArmorContents(null);
                        profile.setDuelOccupation(null);
                        profile.playerUpdate();

                        profile.setState(PlayerState.SPAWN);
                    }
                }
            }

            Set<UUID> spectators = new HashSet<>(this.getSpectators().keySet());
            for(UUID uuid : spectators) {
                Player player = Bukkit.getPlayer(uuid);
                this.spectateEnd(player);
            }

            this.getSpectators().clear();

            for(Entity entity: getEntities()) {
                entity.remove();
            }

            for(Block block : this.getPlacedBlocks()) {
                block.setType(Material.AIR);
            }

            for(BrokenBlock block : this.getBrokenBlocks()) {
                Block b = block.getBlock();
                b.setType(block.getMaterial());
                b.setData(block.getData());
            }

//            if(this.getKit().getType().equals(Kit.Type.BUILD)) {
//                this.getArena().setInUse(false);
//            }

            this.setState(State.STOPPED);
        }, 60);
    }

    @Override
    public void forceEnd() {
        // TODO: Force end games.
    }

    @Override
    public String getTitle(Profile profile) {
        return Color.strip(getKit().getDisplayName());
    }

    @Override
    public List<String> getScoreboard(Profile profile) {
        List<String> lines = new ArrayList<>();
        Player opponent = null;
        for(Player player : getCurrentPlaying()) {
            if(player.getUniqueId() != profile.getUuid()) {
                opponent = player;
            }
        }
        switch(this.getState()) {
            case STARTING:
                lines.add("&fArena: &a" + Color.translate(getArena().getDisplayName()));
//                lines.add("&bQueue: &f" + getQueueType().toString());
//                lines.add("&bKit: &f" + Colors.get(getKit().getDisplayName()));
                if(opponent != null) {
                    lines.add("&fEnemy: &a" + opponent.getName());
                }
                break;
            case ACTIVE:
                if (getKit().getType() == Kit.Type.BOWSPLEEF) {
                    lines.add("&fDouble Jumps: &a" + profile.getDoubleJumps());
                    lines.add("&fTriple Shots: &a" + profile.getTripleShots());
                    lines.add("&fRepulsors: &a" + profile.getRepulsors());
                    lines.add(" ");
                }
                lines.add("&fYour Ping: &a" + PlayerUtils.getPing(profile.getPlayer()) + " ms");
                if(opponent != null) {
                    lines.add("&fEnemy Ping: &a" + PlayerUtils.getPing(opponent) + " ms");
                }
                break;
            case ENDED:
                if (getEnded() != null && getStarted() != null) {
                lines.add("&fDuration: &a" + TimeUtil.get(getEnded(), getStarted()));
                } else {
                    lines.add("&fDuration: &a00:00");
                }
                Participant winner = new ArrayList<>(getAlive().values()).get(0);

                if(winner != null) {
                    lines.add("&fWinner: &a" + winner.getName());
                }
                break;
        }
        return lines;
    }

    @Override
    public List<String> getSpectatorScoreboard(Profile profile) {
        List<String> lines = new ArrayList<>();
        Spectator spectator = getSpectators().get(profile.getUuid());
        if(spectator != null) {
            switch (this.getState()) {
                case STARTING:
                    lines.add("&fPlayers:");
                    for (Participant participant : getParticipants().values()) {
                        if (participant.isAlive()) {
                            lines.add(" &a" + participant.getName());
                        } else {
                            lines.add(" &c&m" + participant.getName());
                        }
                    }
                    break;
                case ACTIVE:
                    lines.add("&fDuration: &a" + TimeUtil.get(new Date(), getStarted()));
                    if(spectator.getTarget() != null) {
                        lines.add("&fWatching: &a" + spectator.getTarget().getName());
                    }
                    lines.add("&fPlayers:");
                    for (Participant participant : getParticipants().values()) {
                        if (participant.isAlive()) {
                            lines.add(" &7- &a" + participant.getName());
                        } else {
                            lines.add(" &7- &c&m" + participant.getName());
                        }
                    }
                    break;
                case ENDED:
                    lines.add("&fDuration: &a" + TimeUtil.get(getEnded(), getStarted()));

                    Participant winner = new ArrayList<>(getAlive().values()).get(0);
                    if (winner != null) {
                        lines.add("&fWinner: &a" + winner.getName());
                    }
                    break;
            }
        }
        return lines;
    }

    @Override
    public void eliminate(Player player) {
        super.eliminate(player);
        if(this.getAlive().size() < 2) {
            this.end();
        }
    }
}
