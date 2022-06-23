package cc.kitpvp.kitpvp.commands.impl.staff;

import cc.kitpvp.kitpvp.KitPvPPlugin;
import cc.kitpvp.kitpvp.commands.BaseCommand;
import cc.kitpvp.kitpvp.duels.arenas.Arena;
import cc.kitpvp.kitpvp.duels.arenas.DuelsArenaManager;
import cc.kitpvp.kitpvp.duels.kits.Kit;
import cc.kitpvp.kitpvp.util.message.Color;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ArenaCommand extends BaseCommand {

    private KitPvPPlugin plugin;

    public ArenaCommand(KitPvPPlugin plugin) {
        super("arena");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof Player && !sender.hasPermission("kitpvp.admin")) {
            sender.sendMessage(ChatColor.RED + "I'm sorry, but you do not have permission to perform this command. Please contact the server administrators if you believe that this is in error.");
            return;
        }

        if(sender instanceof Player && args.length > 0) {
            Player player = (Player) sender;
            DuelsArenaManager am = plugin.getDuelsArenaManager();
            switch(args[0].toLowerCase()) {
                case "list":
                    List<Arena> list = new ArrayList<>(am.getArenas().values());
                    if(!list.isEmpty()) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("&aArenas &7(" + list.size() + ")&a: ");
                        while(!list.isEmpty()) {
                            final Arena arena = list.get(0);
                            sb.append("&f" + arena.getName());
                            list.remove(arena);
                            if(list.isEmpty()) {
                                sb.append("&7.");
                            } else {
                                sb.append("&7, ");
                            }
                        }

                        sender.sendMessage(Color.translate(sb.toString()));
                    } else {
                        sender.sendMessage(ChatColor.RED + "There are no arenas.");
                    }
                    break;
                case "create":
                    if(args.length > 1) {
                        Arena arena = am.get(args[1]);
                        if (arena == null) {
                            arena = am.createArena(args[1]);
                            sender.sendMessage(ChatColor.GREEN + "Arena " + ChatColor.WHITE + arena.getName() + ChatColor.GREEN + " has been created.");
                        } else {
                            sender.sendMessage(ChatColor.RED + "The arena you specified already exists.");
                        }
                    } else {
                        helpMessage(sender);
                    }
                    break;
                case "delete":
                    if(args.length > 1) {
                        Arena arena = am.get(args[1]);
                        if(arena != null) {
                            plugin.getDuelsArenaManager().remove(true, arena);
                            sender.sendMessage(ChatColor.GREEN + "Arena " + ChatColor.WHITE + arena.getName() + ChatColor.GREEN + " has been removed.");
                        } else {
                            sender.sendMessage(ChatColor.RED + "The arena you specified does not exist.");
                        }
                    } else {
                        helpMessage(sender);
                    }
                    break;
                case "rename":
                    if(args.length > 2) {
                        Arena arena = am.get(args[1]);
                        Arena newName = am.get(args[2]);
                        if(arena != null && newName == null) {
                            sender.sendMessage(ChatColor.GREEN + "Arena " + ChatColor.WHITE + arena.getName() + ChatColor.GREEN + " has been renamed to " + ChatColor.WHITE + args[2].toLowerCase() + ChatColor.GREEN + ".");
                            arena.setName(Color.strip(args[2].toLowerCase()));
                            am.push(true, arena);
                        } else {
                            sender.sendMessage(ChatColor.RED + "The kit you specified either does not exist or the new name you specified belongs to an existing kit.");
                        }
                    } else {
                        helpMessage(sender);
                    }
                    break;
                case "setdisplayname":
                    if(args.length > 2) {
                        Arena arena = am.get(args[1]);
                        if(arena != null) {
                            sender.sendMessage(ChatColor.GREEN + "Arena " + ChatColor.WHITE + arena.getName() + ChatColor.GREEN + " has been renamed to " + ChatColor.WHITE + args[2].toLowerCase() + ChatColor.GREEN + ".");
                            arena.setDisplayName(args[2]);
                            am.push(true, arena);
                        } else {
                            sender.sendMessage(ChatColor.RED + "The kit you specified does not exist.");
                        }
                    } else {
                        helpMessage(sender);
                    }
                    break;
                case "setpos":
                    if(args.length > 2) {
                        Arena arena = am.get(args[1]);
                        if(arena != null) {
                            Location location = player.getLocation();
                            switch(args[2].toLowerCase()) {
                                case "1":
                                    arena.setPos1(location);
                                    break;
                                case "2":
                                    arena.setPos2(location);
                                    break;
                                case "3":
                                    arena.setPos3(location);
                                    break;
                                case "c1":
                                case "corner1":
                                    arena.setCorner1(location);
                                    break;
                                case "c2":
                                case "corner2":
                                    arena.setCorner2(location);
                                    break;
                                default:
                                    helpMessage(sender);
                                    return;
                            }

                            sender.sendMessage(ChatColor.GREEN + "Arena " + ChatColor.WHITE + arena.getName() + ChatColor.GREEN + " position " + ChatColor.WHITE + args[2].toLowerCase() + ChatColor.GREEN + " has been set to your current location.");
                            am.push(true, arena);
                        } else {
                            sender.sendMessage(ChatColor.RED + "The arena you specified does not exist.");
                        }
                    } else {
                        helpMessage(sender);
                    }
                    break;
                case "settype":
                    if(args.length > 2) {
                        Arena arena = am.get(args[1]);
                        if(arena != null) {
                            Kit.Type type = null;
                            for(Kit.Type t : Kit.Type.values()) {
                                if(t.name().equalsIgnoreCase(args[2].toLowerCase())) {
                                    type = t;
                                }
                            }

                            if(type != null) {
                                sender.sendMessage(ChatColor.GREEN + "Arena " + ChatColor.WHITE + arena.getName() + ChatColor.GREEN + " type has been set to " + ChatColor.WHITE + type.name().toLowerCase() + ChatColor.GREEN + ".");
                                arena.setType(type);
                                am.push(true, arena);
                            } else {
                                sender.sendMessage(ChatColor.RED + "The kit type you specified is invalid.");
                            }
                        } else {
                            sender.sendMessage(ChatColor.RED + "The kit you specified does not exist.");
                        }
                    } else {
                        helpMessage(sender);
                    }
                    break;
                case "setenabled":
                    if(args.length > 2) {
                        Arena arena = am.get(args[1]);
                        if(arena != null) {
                            boolean b = Boolean.parseBoolean(args[2]);

                            sender.sendMessage(ChatColor.GREEN + "Arena " + ChatColor.WHITE + arena.getName() + ChatColor.GREEN + " type has been set to " + ChatColor.WHITE + b + ChatColor.GREEN + ".");
                            arena.setEnabled(b);
                            am.push(true, arena);
                        } else {
                            sender.sendMessage(ChatColor.RED + "The kit you specified does not exist.");
                        }
                    } else {
                        helpMessage(sender);
                    }
                default:
                    helpMessage(sender);
            }
        } else {
            helpMessage(sender);
        }
    }

    private void helpMessage(CommandSender sender) {
        sender.sendMessage(Color.translate("&e/arena list &7- &fLists all existing arenas."));
        sender.sendMessage(Color.translate("&e/arena create <arena> &7- &fCreates a new arena."));
        sender.sendMessage(Color.translate("&e/arena delete <arena> &7- &fDeletes an existing arena."));
        sender.sendMessage(Color.translate("&e/arena rename <arena> <new name> &7- &fRenames an existing arena."));
        sender.sendMessage(Color.translate("&e/arena setdisplayname <arena> <display name> &7- &fSets the display name for an arena."));
        sender.sendMessage(Color.translate("&e/arena setpos <arena> <1, 2, 3, c1, c2> &7- &fSets a position for an arena."));
        sender.sendMessage(Color.translate("&e/arena settype <arena> <type> &7- &fSets the type for the arena."));
        sender.sendMessage(Color.translate("&e/arena setenabled <arena> <boolean> &7- &fEnables this map to be played on."));
        sender.sendMessage(Color.translate("&e/arena setbuildlimit <arena> <min, max> <int> &7- &fSets the build limit."));
    }
}
