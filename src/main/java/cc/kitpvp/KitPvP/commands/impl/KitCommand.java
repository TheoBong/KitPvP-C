package cc.kitpvp.KitPvP.commands.impl;

import cc.kitpvp.KitPvP.KitPvPPlugin;
import cc.kitpvp.KitPvP.commands.BaseCommand;
import cc.kitpvp.KitPvP.inventories.KitSelectorPlayerWrapper;
import cc.kitpvp.KitPvP.kits.Kit;
import cc.kitpvp.KitPvP.player.PlayerState;
import cc.kitpvp.KitPvP.player.Profile;
import cc.kitpvp.KitPvP.util.message.CC;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KitCommand extends BaseCommand {
    private final KitPvPPlugin plugin;

    public KitCommand(KitPvPPlugin plugin) {
        super("kit");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Players only!");
            return;
        }

        Player player = (Player) sender;
        Profile profile = plugin.getPlayerManager().getProfile(player);

        if (profile.getState() != PlayerState.SPAWN) {
            player.sendMessage(CC.RED + "You can't choose a kit right now!");
            return;
        }

        if (args.length < 1) {
            plugin.getInventoryManager().getPlayerWrapper(KitSelectorPlayerWrapper.class).open(player);
            return;
        }

        Kit kit = plugin.getKitManager().getFfaKitByName(args[0]);

        if (kit == null) {
            plugin.getInventoryManager().getPlayerWrapper(KitSelectorPlayerWrapper.class).open(player);
            return;
        }

        kit.apply(player);
    }
}
