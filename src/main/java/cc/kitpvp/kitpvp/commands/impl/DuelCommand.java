package cc.kitpvp.kitpvp.commands.impl;

import cc.kitpvp.kitpvp.KitPvPPlugin;
import cc.kitpvp.kitpvp.commands.BaseCommand;
import cc.kitpvp.kitpvp.duels.DuelRequest;
import cc.kitpvp.kitpvp.duels.kits.Kit;
import cc.kitpvp.kitpvp.player.PlayerState;
import cc.kitpvp.kitpvp.player.Profile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.leuo.gooey.button.Button;
import xyz.leuo.gooey.gui.GUI;

public class DuelCommand extends BaseCommand {

    private KitPvPPlugin plugin;

    public DuelCommand(KitPvPPlugin plugin) {
        super("duel");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(sender instanceof Player && args.length > 0) {
            Player player = (Player) sender;
            Profile profile = plugin.getPlayerManager().getProfile(player.getUniqueId());
            Player target = Bukkit.getPlayer(args[0]);
            if(target != null) {
                if(target.getUniqueId().equals(player.getUniqueId())) {
                    player.sendMessage(ChatColor.RED + "You can't duel yourself bruh.");
                    return;
                }

                Profile targetProfile = plugin.getPlayerManager().getProfile(target.getUniqueId());
                DuelRequest duelRequest = targetProfile.getDuelRequests().get(player.getUniqueId());

                if(targetProfile.getState().equals(PlayerState.SPAWN) && profile.getState().equals(PlayerState.SPAWN)) {
                    GUI kitGui = new GUI("Duel Request", 9);
                    for(Kit kit : plugin.getDuelsKitManager().getKits().values()) {
                        Button button = new Button(kit.getIcon(), kit.getDisplayName());
                        button.setButtonAction((player1, gui1, button1, event) -> {
                            DuelRequest dr = new DuelRequest(plugin, target.getUniqueId(), player.getUniqueId(), kit, null);
                            dr.send();
                            targetProfile.getDuelRequests().put(player.getUniqueId(), dr);
                        });
                        button.setCloseOnClick(true);
                        kitGui.setButton(kit.getUnrankedPosition(), button);
                    }

                    kitGui.open(player);
                } else {
                    player.sendMessage(ChatColor.RED + "You cannot duel this player right now.");
                }
            } else {
                player.sendMessage(ChatColor.RED + "The target you specified is not on this server.");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Usage: /duel <player>");
        }
    }
}
