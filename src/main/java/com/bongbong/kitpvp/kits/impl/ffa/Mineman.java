package com.bongbong.kitpvp.kits.impl.ffa;

import com.bongbong.kitpvp.KitPvPPlugin;
import com.bongbong.kitpvp.kits.Kit;
import com.bongbong.kitpvp.kits.KitContents;
import com.bongbong.kitpvp.player.PlayerState;
import com.bongbong.kitpvp.player.Profile;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import com.bongbong.kitpvp.util.item.ItemBuilder;
import com.bongbong.kitpvp.util.message.CC;

import java.util.*;
import java.util.concurrent.TimeUnit;


public class Mineman extends Kit {

    public Set<BlockData> blockDataSet = new HashSet<>();

    public Mineman(KitPvPPlugin plugin) {
        super(plugin, "Mineman", Material.DIAMOND_PICKAXE, "Can build and break blocks placed by themself and other Minemen.");
        plugin.getServer().getScheduler().runTaskTimer(plugin, new MinemanTask(), 20 * 3, 20 * 3);
    }

    @Override
    protected void onEquip(Player player) {
        // NO-OP
    }

    @Override
    protected List<PotionEffect> effects() {
        return null;
    }

    @Override
    protected KitContents.Builder contentsBuilder() {
        KitContents.Builder builder = KitContents.newBuilder();

        builder.addItem(new ItemBuilder(Material.IRON_SWORD).enchant(Enchantment.DAMAGE_ALL, 1).unbreakable(true).build());
        builder.addItem(new ItemBuilder(Material.DIAMOND_PICKAXE).name(CC.GOLD + "Mineman's Pickaxe").enchant(Enchantment.DIG_SPEED, 5).build());
        builder.addItem(new ItemBuilder(Material.COBBLESTONE).amount(8).build());
        builder.addItem(new ItemBuilder(Material.FISHING_ROD).unbreakable(true).build());
        builder.addArmor(
                new ItemStack(Material.DIAMOND_BOOTS),
                new ItemStack(Material.IRON_LEGGINGS),
                new ItemStack(Material.IRON_CHESTPLATE)
        );

        return builder;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlace(BlockPlaceEvent event) {
        Player mineman = event.getPlayer();

        if (isInvalidKit(mineman)) {
            return;
        }
        if (event.getBlock().getType() != Material.COBBLESTONE) {
            return;
        }

        Profile profile = plugin.getPlayerManager().getProfile(mineman);
        if (profile.getState() == PlayerState.SPAWN) {
            event.setCancelled(true);
            return;
        }

        if (plugin.getSpawnCuboid().contains(event.getBlock().getLocation())) {
            event.setCancelled(true);
            return;
        }
        Material material = event.getBlock().getLocation().getBlock().getType();
        if (material == Material.WATER || material == Material.STATIONARY_WATER || material == Material.STATIONARY_LAVA || material == Material.LAVA) {
            event.setCancelled(true);
            return;
        }

        event.setCancelled(false);
        BlockData blockData = new BlockData(mineman, event.getBlock().getLocation());
        blockDataSet.add(blockData);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBreak(BlockBreakEvent event) {
        Player mineman = event.getPlayer();

        if (isInvalidKit(mineman)) {
            return;
        }
        if (event.getBlock().getType() != Material.COBBLESTONE) {
            return;
        }

        Profile profile = plugin.getPlayerManager().getProfile(mineman);
        if (profile.getState() == PlayerState.SPAWN) {
            event.setCancelled(true);
            return;
        }
        event.setCancelled(true);
        boolean breakBlock = false;
        Iterator<BlockData> iterator = blockDataSet.iterator();
        while (iterator.hasNext()) {
            BlockData blockData = iterator.next();
            if (blockData.getLocation().equals(event.getBlock().getLocation())) {
                breakBlock = true;
                Player placedBy = blockData.getPlacedBy();
                if (placedBy.isOnline()) {
                    blockData.placedBy.getInventory().addItem(new ItemStack(Material.COBBLESTONE));
                }
                iterator.remove();
                break;
            }
        }

        if (breakBlock) {
            event.getBlock().setType(Material.AIR);
        }
    }

    class BlockData {
        @Getter
        private Player placedBy;
        @Getter
        private long placedAt;
        @Getter
        private Location location;

        BlockData(Player placedBy, Location blockLocation) {
            this.placedBy = placedBy;
            this.placedAt = System.currentTimeMillis();
            this.location = blockLocation;
        }
    }

    class MinemanTask implements Runnable {


        @Override
        public void run() {
            if (blockDataSet.isEmpty()) {
                return;
            }

            long currentTime = System.currentTimeMillis();

            Iterator<BlockData> iterator = blockDataSet.iterator();
            while (iterator.hasNext()) {
                BlockData blockData = iterator.next();

                long secondsExisted = TimeUnit.MILLISECONDS.toSeconds(currentTime - blockData.getPlacedAt());

                if (secondsExisted >= 15) {
                    plugin.getServer().getWorld("world").getBlockAt(blockData.getLocation()).setType(Material.AIR);
                    Player placedBy = blockData.getPlacedBy();
                    Profile profile = plugin.getPlayerManager().getProfile(placedBy);
                    if (placedBy.isOnline() && profile.getCurrentKit() == plugin.getKitManager().getFfaKitByName("Mineman")) {
                        if (!placedBy.getInventory().contains(Material.COBBLESTONE, 8)) {
                            placedBy.getInventory().addItem(new ItemStack(Material.COBBLESTONE));
                        }

                    }
                    iterator.remove();
                }
            }
        }
    }
}

