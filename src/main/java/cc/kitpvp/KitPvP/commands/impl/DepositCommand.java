package cc.kitpvp.KitPvP.commands.impl;

import cc.kitpvp.KitPvP.KitPvPPlugin;
import cc.kitpvp.KitPvP.commands.BaseCommand;
import cc.kitpvp.KitPvP.player.PlayerState;
import cc.kitpvp.KitPvP.player.Profile;
import cc.kitpvp.KitPvP.util.item.ItemBuilder;
import cc.kitpvp.KitPvP.util.message.CC;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class DepositCommand extends BaseCommand {
    private final KitPvPPlugin plugin;

    public DepositCommand(KitPvPPlugin plugin) {
        super("deposit");
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
            player.sendMessage(CC.RED + "You can't deposit while you aren't in spawn!");
            return;
        }

        ItemStack goldItem = new ItemBuilder(Material.GOLD_INGOT).name(CC.GOLD + CC.B + "Gold").lore(CC.GRAY + "Deposit gold in spawn using /deposit!").build();

        int gold = 0;
        for (ItemStack itemStack : player.getInventory().getContents()) {
            if (itemStack == null) continue;

            if (itemStack.isSimilar(goldItem)) {
                gold = gold + itemStack.getAmount();
            }
        }

        player.getInventory().remove(Material.GOLD_INGOT);

        int bonus = profile.getLevel();

        profile.getStatistics().setCredits(profile.getStatistics().getCredits() + (gold + bonus));
        player.sendMessage("Successfully deposited: " + gold + " gold (Bonus: " + bonus + ").");
    }
}
