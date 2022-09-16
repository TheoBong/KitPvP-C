package com.bongbong.kitpvp.commands.impl.staff;

import com.bongbong.kitpvp.KitPvPPlugin;
import com.bongbong.kitpvp.commands.BaseCommand;
import com.bongbong.kitpvp.duels.kits.DuelsKitManager;
import com.bongbong.kitpvp.duels.kits.Kit;
import com.bongbong.kitpvp.util.message.Color;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DKitCommand extends BaseCommand {

    private KitPvPPlugin plugin;

    public DKitCommand(KitPvPPlugin plugin) {
        super("dkit");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof Player && !sender.hasPermission("kitpvp.admin")) {
            sender.sendMessage(ChatColor.RED + "I'm sorry, but you do not have permission to perform this command. Please contact the server administrators if you believe that this is in error.");
            return;
        }

        if(args.length > 0) {
            DuelsKitManager km = plugin.getDuelsKitManager();
            switch(args[0].toLowerCase()) {
                case "list":
                    List<Kit> kits = new ArrayList<>(km.getKits().values());
                    if(!kits.isEmpty()) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("&aKits &7(" + kits.size() + ")&a: ");

                        while(!kits.isEmpty()) {
                            final Kit kit = kits.get(0);
                            sb.append(kit.getColor() + kit.getName());
                            kits.remove(kit);
                            if(kits.isEmpty()) {
                                sb.append("&7.");
                            } else {
                                sb.append("&7, ");
                            }
                        }

                        sender.sendMessage(Color.translate(sb.toString()));
                    } else {
                        sender.sendMessage(ChatColor.RED + "There are no kits.");
                    }
                    break;
                case "create":
                    if(args.length > 1) {
                        Kit kit = km.get(args[1]);
                        if(kit == null) {
                            kit = km.createKit(args[1]);
                            sender.sendMessage(ChatColor.GREEN + "Kit " + ChatColor.WHITE + kit.getName() + ChatColor.GREEN + " has been created.");
                        } else {
                            sender.sendMessage(ChatColor.RED + "The kit you specified already exists.");
                        }
                    } else {
                        helpMessage(sender);
                    }
                    break;
                case "delete":
                    if(args.length > 1) {
                        Kit kit = km.get(args[1]);
                        if(kit != null) {
                            km.remove(true, kit);
                            sender.sendMessage(ChatColor.GREEN + "Kit " + ChatColor.WHITE + kit.getName() + ChatColor.GREEN + " has been deleted.");
                        } else {
                            sender.sendMessage(ChatColor.RED + "The kit you specified does not exist.");
                        }
                    } else {
                        helpMessage(sender);
                    }
                    break;
                case "rename":
                    if(args.length > 2) {
                        Kit kit = km.get(args[1]);
                        Kit newName = km.get(args[2]);
                        if(kit != null && newName == null) {
                            sender.sendMessage(ChatColor.GREEN + "Kit " + ChatColor.WHITE + kit.getName() + ChatColor.GREEN + " has been renamed to " + ChatColor.WHITE + args[2].toLowerCase() + ChatColor.GREEN + ".");
                            kit.setName(Color.strip(args[2].toLowerCase()));
                            km.push(true, kit);
                        } else {
                            sender.sendMessage(ChatColor.RED + "The kit you specified either does not exist or the new name you specified belongs to an existing kit.");
                        }
                    } else {
                        helpMessage(sender);
                    }
                    break;
                case "getinventory":
                    if(args.length > 1 && sender instanceof Player) {
                        Kit kit = km.get(args[1]);
                        if(kit != null) {
                            Player player = (Player) sender;
                            kit.apply(player);
                        } else {
                            sender.sendMessage(ChatColor.RED + "The kit you specified does not exist.");
                        }
                    } else {
                        helpMessage(sender);
                    }
                    break;
                case "setinventory":
                    if(args.length > 1 && sender instanceof Player) {
                        Kit kit = km.get(args[1]);
                        if(kit != null) {
                            Player player = (Player) sender;
                            PlayerInventory inventory = player.getInventory();
                            Map<Integer, ItemStack> armor = new HashMap<>(), items = new HashMap<>();

                            for (int i = 0; i < 4; i++) {
                                armor.put(i, inventory.getArmorContents()[i]);
                            }

                            for (int i = 0; i < 36; i++) {
                                items.put(i, inventory.getItem(i));
                            }

                            kit.setArmor(armor);
                            kit.setItems(items);
                            km.push(true, kit);

                            sender.sendMessage(ChatColor.GREEN + "Kit " + ChatColor.WHITE + kit.getName() + "'s " + ChatColor.GREEN + "inventory and armor has been updated.");

                        } else {
                            sender.sendMessage(ChatColor.RED + "The kit you specified does not exist.");
                        }
                    } else {
                        helpMessage(sender);
                    }
                    break;
                case "seticon":
                    if(args.length > 1 && sender instanceof Player) {
                        Player player = (Player) sender;
                        Kit kit = km.get(args[1]);
                        ItemStack item = player.getItemInHand();
                        if(kit != null) {
                            if(item != null && !item.getType().equals(Material.AIR)) {
                                kit.setIcon(item);
                                km.push(true, kit);
                                sender.sendMessage(ChatColor.GREEN + "Kit " + ChatColor.WHITE + kit.getName() + "'s " + ChatColor.GREEN + "icon has been updated to " + ChatColor.WHITE + item.getType().toString() + ChatColor.GREEN + ".");
                            } else {
                                sender.sendMessage(ChatColor.RED + "The item that is in your hand is not valid.");
                            }
                        } else {
                            sender.sendMessage(ChatColor.RED + "The kit you specified does not exist.");
                        }

                    } else {
                        helpMessage(sender);
                    }
                    break;
                case "settype":
                    if(args.length > 2) {
                        Kit kit = km.get(args[1]);
                        if(kit != null) {
                            Kit.Type type = null;
                            for(Kit.Type t : Kit.Type.values()) {
                                if(t.toString().equalsIgnoreCase(args[2])) {
                                    type = t;
                                }
                            }

                            if(type != null) {
                                kit.setType(type);
                                km.push(true, kit);
                                sender.sendMessage(ChatColor.GREEN + "Kit " + ChatColor.WHITE + kit.getName() + "'s " + ChatColor.GREEN + "type has been set to " + ChatColor.WHITE + type.toString().toLowerCase() + ChatColor.GREEN + ".");
                            } else {
                                sender.sendMessage(ChatColor.RED + "The kit type that you specified is invalid.");
                            }
                        } else {
                            sender.sendMessage(ChatColor.RED + "The kit you specified does not exist.");
                        }
                    } else {
                        helpMessage(sender);
                    }
                    break;
                case "setdisplayname":
                    if(args.length > 2) {
                        Kit kit = km.get(args[1]);
                        if(kit != null) {
                            StringBuilder sb = new StringBuilder();
                            for(int i = 2; i < args.length; i++) {
                                sb.append(args[i]);
                                if(i + 1 != args.length) {
                                    sb.append(" ");
                                }
                            }

                            kit.setDisplayName(sb.toString());
                            km.push(true, kit);
                            sender.sendMessage(ChatColor.GREEN + "Kit " + ChatColor.WHITE + kit.getName() + ChatColor.GREEN + " display name has been set to " + ChatColor.RESET + Color.translate(kit.getDisplayName()) + ChatColor.GREEN + ".");
                        } else {
                            sender.sendMessage(ChatColor.RED + "The kit you specified does not exist.");
                        }
                    } else {
                        helpMessage(sender);
                    }
                    break;
                case "setcolor":
                    if(args.length > 2) {
                        Kit kit = km.get(args[1]);
                        if(kit != null) {
                            kit.setColor(args[2]);
                            km.push(true, kit);
                            sender.sendMessage(ChatColor.GREEN + "Kit " + ChatColor.WHITE + kit.getName() + ChatColor.GREEN + " display name has been set to " + Color.translate(kit.getColor() + "color") + ChatColor.GREEN + ".");
                        } else {
                            sender.sendMessage(ChatColor.RED + "The kit you specified does not exist.");
                        }
                    } else {
                        helpMessage(sender);
                    }
                    break;
                case "setmenuposition":
                    if(args.length > 3) {
                        Kit kit = km.get(args[1]);
                        if(kit != null) {
                            String positionName = null;
                            try {
                                int pos = Integer.parseInt(args[3]);
                                switch (args[2].toLowerCase()) {
                                    case "unranked":
                                        positionName = "unranked";
                                        kit.setUnrankedPosition(pos);
                                        break;
                                    case "ranked":
                                        positionName = "ranked";
                                        kit.setRankedPosition(pos);
                                        break;
                                    case "unranked2v2":
                                        positionName = "unranked 2v2";
                                        kit.setUnranked2v2Position(pos);
                                        break;
                                    case "ranked2v2":
                                        positionName = "ranked 2v2";
                                        kit.setRanked2v2Position(pos);
                                        break;
                                    case "edit":
                                        positionName = "edit";
                                        kit.setEditPosition(pos);
                                        break;
                                }

                                km.push(true, kit);
                                sender.sendMessage(ChatColor.GREEN + "Kit " + ChatColor.WHITE + kit.getName() + ChatColor.GREEN + positionName + " menu position has been set to " + ChatColor.WHITE + pos + ChatColor.GREEN + ".");
                            } catch (NumberFormatException e) {
                                sender.sendMessage(ChatColor.RED + "You must specify a valid number.");
                            }
                        } else {
                            sender.sendMessage(ChatColor.RED + "The kit you specified does not exist.");
                        }
                    } else {
                        helpMessage(sender);
                    }
                    break;
                case "queueable":
                    if(args.length > 2) {
                        Kit kit = km.get(args[1]);
                        if(kit != null) {
                            boolean b = Boolean.parseBoolean(args[2]);
                            kit.setQueueable(b);
                            km.push(true, kit);
                            sender.sendMessage(ChatColor.GREEN + "Kit " + ChatColor.WHITE + kit.getName() + ChatColor.GREEN + " queueable has been set to " + ChatColor.WHITE + b + ChatColor.GREEN + ".");
                        } else {
                            sender.sendMessage(ChatColor.RED + "The kit you specified does not exist.");
                        }
                    } else {
                        helpMessage(sender);
                    }
                    break;
                case "allow2v2":
                    if(args.length > 2) {
                        Kit kit = km.get(args[1]);
                        if(kit != null) {
                            boolean b = Boolean.parseBoolean(args[2]);
                            kit.setAllow2v2(b);
                            km.push(true, kit);
                            sender.sendMessage(ChatColor.GREEN + "Kit " + ChatColor.WHITE + kit.getName() + ChatColor.GREEN + " allow 2v2 has been set to " + ChatColor.WHITE + b + ChatColor.GREEN + ".");
                        } else {
                            sender.sendMessage(ChatColor.RED + "The kit you specified does not exist.");
                        }
                    } else {
                        helpMessage(sender);
                    }
                    break;
                case "ranked":
                    if(args.length > 2) {
                        Kit kit = km.get(args[1]);
                        if(kit != null) {
                            boolean b = Boolean.parseBoolean(args[2]);
                            kit.setRanked(b);
                            km.push(true, kit);
                            sender.sendMessage(ChatColor.GREEN + "Kit " + ChatColor.WHITE + kit.getName() + ChatColor.GREEN + " ranked has been set to " + ChatColor.WHITE + b + ChatColor.GREEN + ".");
                        } else {
                            sender.sendMessage(ChatColor.RED + "The kit you specified does not exist.");
                        }
                    } else {
                        helpMessage(sender);
                    }
                    break;
                case "editable":
                    if(args.length > 2) {
                        Kit kit = km.get(args[1]);
                        if(kit != null) {
                            boolean b = Boolean.parseBoolean(args[2]);
                            kit.setEditable(b);
                            km.push(true, kit);
                            sender.sendMessage(ChatColor.GREEN + "Kit " + ChatColor.WHITE + kit.getName() + ChatColor.GREEN + " editable has been set to " + ChatColor.WHITE + b + ChatColor.GREEN + ".");
                        } else {
                            sender.sendMessage(ChatColor.RED + "The kit you specified does not exist.");
                        }
                    } else {
                        helpMessage(sender);
                    }
                    break;
                case "moreitems":
                    if(args.length > 2) {
                        Kit kit = km.get(args[1]);
                        if(kit != null) {
                            boolean b = Boolean.parseBoolean(args[2]);
                            kit.setMoreItems(b);
                            km.push(true, kit);
                            sender.sendMessage(ChatColor.GREEN + "Kit " + ChatColor.WHITE + kit.getName() + ChatColor.GREEN + " editor more items has been set to " + ChatColor.WHITE + b + ChatColor.GREEN + ".");
                        } else {
                            sender.sendMessage(ChatColor.RED + "The kit you specified does not exist.");
                        }
                    } else {
                        helpMessage(sender);
                    }
                    break;
                default:
                    helpMessage(sender);
            }
        } else {
            helpMessage(sender);
        }
    }
    
    private void helpMessage(CommandSender sender) {
         sender.sendMessage(Color.translate("&e/kit list &7- &fView all kits."));
         sender.sendMessage(Color.translate("&e/kit create <kit> &7- &fCreates a new kit."));
         sender.sendMessage(Color.translate("&e/kit delete <kit> &7- &fDeletes an existing kit."));
         sender.sendMessage(Color.translate("&e/kit rename <kit> <new name> &7- &fRenames an existing kit."));
         sender.sendMessage(Color.translate("&e/kit getinventory <kit> &7- Gives you a kit."));
         sender.sendMessage(Color.translate("&e/kit setinventory <kit> &7- &fSets a kit's inventory to whatever you have applied for armor and what is in your inventory."));
         sender.sendMessage(Color.translate("&e/kit seticon <kit> &7- &fSets a kit's icon to whatever is in your hand."));
         sender.sendMessage(Color.translate("&e/kit settype <kit> <type> &7- &fSet a kit's type."));
         sender.sendMessage(Color.translate("&e/kit setdisplayname <kit> <display name> &7- &fSets the display name for a kit."));
         sender.sendMessage(Color.translate("&e/kit setcolor <kit> <color> &7- &fSets the color for a kit."));
         sender.sendMessage(Color.translate("&e/kit setmenuposition <kit> <menu> <number> &7- &fSets the menu position.."));
         sender.sendMessage(Color.translate("&e/kit queueable <kit> <boolean> &7- &fEnabled or disables queueing for this kit."));
         sender.sendMessage(Color.translate("&e/kit allow2v2 <kit> <boolean> &7- &fEnabled or disables 2v2 queueing for this kit."));
         sender.sendMessage(Color.translate("&e/kit ranked <kit> <boolean> &7- &fEnabled or disables ranked queueing for this kit."));
         sender.sendMessage(Color.translate("&e/kit editable <kit> <boolean> &7- &fEnabled or disables editing for this kit."));
         sender.sendMessage(Color.translate("&e/kit moreitems <kit> <boolean> &7- &fEnabled or disables more items during editing for this kit."));
    }
}
