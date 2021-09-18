package cc.kitpvp.KitPvP.commands.impl.staff;

import cc.kitpvp.KitPvP.KitPvPPlugin;
import cc.kitpvp.KitPvP.commands.BaseCommand;
import cc.kitpvp.KitPvP.player.Profile;
import cc.kitpvp.KitPvP.storage.mongo.MongoUpdate;
import cc.kitpvp.KitPvP.util.ThreadUtil;
import cc.kitpvp.KitPvP.util.WebPlayer;
import cc.kitpvp.KitPvP.util.message.Color;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CreditsCommand extends BaseCommand {
    private final KitPvPPlugin plugin;

    public CreditsCommand(KitPvPPlugin plugin) {
        super("credits");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        ThreadUtil.runTask(true, plugin, () -> {
            if (!sender.hasPermission("kitpvp.admin")) {
                sender.sendMessage("No permission!");
                return;
            }

            if (args.length < 2) {
                sendUsage(sender);
                return;
            }

            Player target;
            int credits;

            switch (args[0]) {
                case "view":
                    target = Bukkit.getPlayer(args[1]);

                    if (target != null && target.isOnline()) {
                        Profile targetProfile = plugin.getPlayerManager().getProfile(target);
                        int previousCredits = targetProfile.getStatistics().getCredits();
                        sender.sendMessage(target.getDisplayName() + "'s balance: " + previousCredits);
                    } else {
                        WebPlayer webPlayer;
                        try {
                            webPlayer = new WebPlayer(args[1]);
                        } catch (Exception e) {
                            sender.sendMessage("Invalid Player!");
                            return;
                        }

                        if (!webPlayer.isValid()) {
                            sender.sendMessage("Invalid Player!");
                            return;
                        }

                        UUID uuid = webPlayer.getUuid();

                        plugin.getMongo().getDocument(false, "profiles", uuid, document -> {
                            if (document != null) {
                                int previousCredits = document.getInteger("credits");
                                sender.sendMessage(webPlayer.getName() + "'s balance: " + previousCredits);
                            }
                        });
                    }
                    break;
                case "set":
                    if (args.length < 3) {
                        sendUsage(sender);
                        return;
                    }

                    target = Bukkit.getPlayer(args[1]);

                    try {
                        credits = Integer.parseInt(args[2]);
                    } catch (Exception e) {
                        sender.sendMessage("Invalid amount!");
                        return;
                    }

                    if (target != null && target.isOnline()) {
                        Profile targetProfile = plugin.getPlayerManager().getProfile(target);
                        targetProfile.getStatistics().setCredits(credits);
                        sender.sendMessage("Successfully set " + target.getDisplayName() + "'s balance to " + credits);
                    } else {
                        WebPlayer webPlayer;
                        try {
                            webPlayer = new WebPlayer(args[1]);
                        } catch (Exception e) {
                            sender.sendMessage("Invalid Player!");
                            return;
                        }

                        if (!webPlayer.isValid()) {
                            sender.sendMessage("Invalid Player!");
                            return;
                        }

                        UUID uuid = webPlayer.getUuid();

                        Map<String, Object> map = new HashMap<>();
                        map.put("credits", credits);

                        MongoUpdate mu = new MongoUpdate("profiles", uuid);
                        mu.setUpdate(map);
                        plugin.getMongo().massUpdate(false, mu);
                        sender.sendMessage("Successfully set " + webPlayer.getName() + "'s balance to " + credits);
                    }
                    break;
                case "add":
                    if (args.length < 3) {
                        sendUsage(sender);
                        return;
                    }

                    target = Bukkit.getPlayer(args[1]);

                    try {
                        credits = Integer.parseInt(args[2]);
                    } catch (Exception e) {
                        sender.sendMessage("Invalid amount!");
                        return;
                    }

                    if (target != null && target.isOnline()) {
                        Profile targetProfile = plugin.getPlayerManager().getProfile(target);
                        int previousCredits = targetProfile.getStatistics().getCredits();
                        targetProfile.getStatistics().setCredits(previousCredits + credits);
                        sender.sendMessage("Successfuly added " + credits + "to " + target.getDisplayName() + ", new balance: " + (previousCredits + credits));
                    } else {
                        WebPlayer webPlayer;
                        try {
                            webPlayer = new WebPlayer(args[1]);
                        } catch (Exception e) {
                            sender.sendMessage("Invalid Player!");
                            return;
                        }

                        if (!webPlayer.isValid()) {
                            sender.sendMessage("Invalid Player!");
                            return;
                        }

                        UUID uuid = webPlayer.getUuid();

                        plugin.getMongo().getDocument(false, "profiles", uuid, document -> {
                            if (document != null) {
                                int previousCredits = document.getInteger("credits");

                                Map<String, Object> map = new HashMap<>();
                                map.put("credits", (previousCredits + credits));

                                MongoUpdate mu = new MongoUpdate("profiles", uuid);
                                mu.setUpdate(map);
                                plugin.getMongo().massUpdate(false, mu);

                                sender.sendMessage("Successfuly added " + credits + "to " + webPlayer.getName() + ", new balance: " + (previousCredits + credits));
                            }
                        });
                    }
                    break;
                case "remove":
                    if (args.length < 3) {
                        sendUsage(sender);
                        return;
                    }

                    target = Bukkit.getPlayer(args[1]);

                    try {
                        credits = Integer.parseInt(args[2]);
                    } catch (Exception e) {
                        sender.sendMessage("Invalid amount!");
                        return;
                    }

                    if (target != null && target.isOnline()) {
                        Profile targetProfile = plugin.getPlayerManager().getProfile(target);
                        int previousCredits = targetProfile.getStatistics().getCredits();
                        targetProfile.getStatistics().setCredits(previousCredits - credits);
                        sender.sendMessage("Successfuly removed " + credits + "from " + target.getDisplayName() + ", new balance: " + (previousCredits - credits));
                    } else {
                        WebPlayer webPlayer;
                        try {
                            webPlayer = new WebPlayer(args[1]);
                        } catch (Exception e) {
                            sender.sendMessage("Invalid Player!");
                            return;
                        }

                        if (!webPlayer.isValid()) {
                            sender.sendMessage("Invalid Player!");
                            return;
                        }

                        UUID uuid = webPlayer.getUuid();

                        plugin.getMongo().getDocument(false, "profiles", uuid, document -> {
                            if (document != null) {
                                int previousCredits = document.getInteger("credits");

                                Map<String, Object> map = new HashMap<>();
                                map.put("credits", (previousCredits - credits));

                                MongoUpdate mu = new MongoUpdate("profiles", uuid);
                                mu.setUpdate(map);
                                plugin.getMongo().massUpdate(false, mu);

                                sender.sendMessage("Successfuly removed " + credits + "from " + webPlayer.getName() + ", new balance: " + (previousCredits - credits));
                            }
                        });
                    }
                    break;
                default:
                    sendUsage(sender);
                    break;
            }
        });
    }

    public void sendUsage(CommandSender sender) {
        sender.sendMessage(Color.translate("&6&lCredits Management.") );
        sender.sendMessage(Color.translate("&f- /credits view <player>"));
        sender.sendMessage(Color.translate("&f- /credits set <player> <amount>"));
        sender.sendMessage(Color.translate("&f- /credits add <player> <amount>"));
        sender.sendMessage(Color.translate("&f- /credits remove <player> <amount>"));
    }
}