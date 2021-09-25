package cc.kitpvp.KitPvP.managers;


import cc.kitpvp.KitPvP.KitPvPPlugin;
import cc.kitpvp.KitPvP.player.PlayerState;
import cc.kitpvp.KitPvP.player.Profile;
import cc.kitpvp.KitPvP.util.ItemHotbars;
import cc.kitpvp.KitPvP.util.item.ItemBuilder;
import cc.kitpvp.KitPvP.util.message.CC;
import cc.kitpvp.KitPvP.util.player.PlayerUtil;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.IntStream;

@RequiredArgsConstructor
public class PlayerManager {
    private final KitPvPPlugin plugin;
    private final Map<UUID, Profile> profiles = new HashMap<>();

    public void createProfile(UUID id, String name) {
        Profile profile = new Profile(plugin, name, id);
        profiles.put(id, profile);
    }

    public Profile getProfile(Player player) {
        return profiles.get(player.getUniqueId());
    }

    public void removeProfile(Player player) {
        profiles.remove(player.getUniqueId());
    }

    public void saveAllProfiles() {
        profiles.values().forEach(profile -> profile.push(true));
    }

    public void startExaminationTask(Player p, Player personToBeExamined) {
        Inventory inv = Bukkit.createInventory(null, 54, ChatColor.RED + "Examining " + ChatColor.BOLD + personToBeExamined.getName());

        IntStream.range(0, 36).forEach(i -> {
            ItemStack is = personToBeExamined.getInventory().getItem(i);
            inv.setItem(i, is);
        });

        inv.setItem(36, personToBeExamined.getInventory().getHelmet());
        inv.setItem(37, personToBeExamined.getInventory().getChestplate());
        inv.setItem(38, personToBeExamined.getInventory().getLeggings());
        inv.setItem(39, personToBeExamined.getInventory().getBoots());

        inv.setItem(40, personToBeExamined.getItemInHand());


        IntStream.range(0, 3).forEach(i -> inv.setItem(41 + i, new ItemStack(Material.THIN_GLASS, 1)));

        p.openInventory(inv);
    }

    public void giveSpawnItems(Player player) {
        Profile profile = getProfile(player);

        PlayerUtil.clearPlayer(player);
        profile.setCurrentKit(null);
        ItemHotbars.SPAWN_ITEMS.apply(player);


        if (profile.getLastKit() != null) {
            player.getInventory().setItem(1, new ItemBuilder(Material.WATCH).name(CC.YELLOW + "Last Kit " + CC.SECONDARY + "(" + profile.getLastKit().getName() + ")").build());
            player.updateInventory();
        }
    }

    public void loseSpawnProtection(Player player) {
        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        Profile profile = getProfile(player);

        profile.setState(PlayerState.FFA);

        player.setFlying(false);
        player.setAllowFlight(false);

        player.sendMessage(CC.RED + "You no longer have spawn protection!");
    }

    public void acquireSpawnProtection(Player player) {
        Profile profile = getProfile(player);

        profile.setState(PlayerState.SPAWN);

        if (player.hasPermission("kitpvp.donor")) {
            player.setAllowFlight(true);
            player.setFlying(true);
        } else {
            player.setAllowFlight(false);
            player.setFlying(false);
        }

        player.sendMessage(CC.GREEN + "You have acquired spawn protection.");
    }

    public void resetPlayer(Player player, boolean teleport) {
        Profile profile = getProfile(player);

        profile.setState(PlayerState.SPAWN);
        giveSpawnItems(player);

        if (player.hasPermission("kitpvp.donor")) {
            player.setAllowFlight(true);
            player.setFlying(true);
        } else {
            player.setAllowFlight(false);
            player.setFlying(false);
        }

        player.setWalkSpeed(0.2F);
        player.setHealth(20);


        if (teleport) {
            player.teleport(plugin.getSpawnLocation());
        }
    }
}

