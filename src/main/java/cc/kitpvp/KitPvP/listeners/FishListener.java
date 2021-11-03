package cc.kitpvp.KitPvP.listeners;

import cc.kitpvp.KitPvP.KitPvPPlugin;
import cc.kitpvp.KitPvP.player.Profile;
import cc.kitpvp.KitPvP.util.item.ItemBuilder;
import cc.kitpvp.KitPvP.util.message.CC;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Map;
import java.util.Random;

public class FishListener implements Listener {

    private final KitPvPPlugin plugin;

    public FishListener(KitPvPPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onFish(PlayerFishEvent event) {
        Player player = event.getPlayer();

        if (event.getState() == PlayerFishEvent.State.CAUGHT_FISH) {

            //want to replace this method with something to just get rid of the drop
            event.setCancelled(true);

            //add custom drop item to player
            player.getInventory().addItem(getFish());
        }
    }

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();

//        if (event.getAction() != Action.LEFT_CLICK_AIR && event.getAction() != Action.LEFT_CLICK_BLOCK) {
//            return;
//        }

        if (event.getItem() == null) {
            return;
        }

        ItemStack healthFish = new ItemBuilder(Material.RAW_FISH).name(CC.GOLD + "Health fish").build();
        if (event.getItem().isSimilar(healthFish)) {
            if (!player.isDead() && player.getHealth() > 0.0 && player.getHealth() <= 19.0) {
                double health = player.getHealth() + 7.0;

                player.setHealth(health > 20.0 ? 20.0 : health);

                ItemStack removeItem = new ItemBuilder(Material.RAW_FISH).name(CC.GOLD + "Health fish").amount(1).build();
                player.getInventory().removeItem(removeItem);
            }
            return;
        }

        ItemStack resFish = new ItemBuilder(Material.RAW_FISH).name(CC.GOLD + "Resistance fish").build();
        if (event.getItem().isSimilar(resFish)) {
            Profile profile = plugin.getPlayerManager().getProfile(player);
            if (profile.getCurrentKit() == plugin.getKitManager().getFfaKitByName("flash")) {
                PotionEffect potionEffect = new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 200, 3);
                player.addPotionEffect(potionEffect);

                PotionEffect potionEffect2 = new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 200, 2);
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> player.addPotionEffect(potionEffect2), 200);
            } else {
                PotionEffect potionEffect = new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 200, 1);
                player.addPotionEffect(potionEffect);
            }

            ItemStack removeItem = new ItemBuilder(Material.RAW_FISH).name(CC.GOLD + "Resistance fish").amount(1).build();
            player.getInventory().removeItem(removeItem);
            return;
        }

        ItemStack strengthFish = new ItemBuilder(Material.RAW_FISH).name(CC.GOLD + "Strength fish").build();
        if (event.getItem().isSimilar(strengthFish)) {
            PotionEffect potionEffect = new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 100, 1);
            player.addPotionEffect(potionEffect);

            ItemStack removeItem = new ItemBuilder(Material.RAW_FISH).name(CC.GOLD + "Strength fish").amount(1).build();
            player.getInventory().removeItem(removeItem);
            return;
        }

        ItemStack speedFish = new ItemBuilder(Material.RAW_FISH).name(CC.GOLD + "Speed fish").build();
        if (event.getItem().isSimilar(speedFish)) {
            PotionEffect potionEffect = new PotionEffect(PotionEffectType.SPEED, 140, 3);
            player.addPotionEffect(potionEffect);

            ItemStack removeItem = new ItemBuilder(Material.RAW_FISH).name(CC.GOLD + "Speed fish").amount(1).build();
            player.getInventory().removeItem(removeItem);
            return;
        }

        ItemStack regenFish = new ItemBuilder(Material.RAW_FISH).name(CC.GOLD + "Regen fish").build();
        if (event.getItem().isSimilar(regenFish)) {
            PotionEffect potionEffect = new PotionEffect(PotionEffectType.REGENERATION, 300, 1);
            player.addPotionEffect(potionEffect);

            ItemStack removeItem = new ItemBuilder(Material.RAW_FISH).name(CC.GOLD + "Regen fish").amount(1).build();
            player.getInventory().removeItem(removeItem);
        }
    }

    private ItemStack getFish() {
        Random rand = new Random();
        int random = rand.nextInt(100);

        if (random <= 3) {
            return new ItemBuilder(Material.RAW_FISH).name(CC.GOLD + "Resistance fish").build();
        }
        if (random <= 8) {
            return new ItemBuilder(Material.RAW_FISH).name(CC.GOLD + "Strength fish").build();
        }
        if (random <= 23) {
            return new ItemBuilder(Material.RAW_FISH).name(CC.GOLD + "Speed fish").build();
        }
        if (random <= 40) {
            return new ItemBuilder(Material.RAW_FISH).name(CC.GOLD + "Regen fish").build();
        }

        return new ItemBuilder(Material.RAW_FISH).name(CC.GOLD + "Health fish").build();
    }

    public void consumeItem(Player player, int count, ItemStack itemStack) {
        Map<Integer, ? extends ItemStack> ammo = player.getInventory().all(itemStack);

        int found = 0;
        for (ItemStack stack : ammo.values())
            found += stack.getAmount();
        if (count > found)
            return;

        for (Integer index : ammo.keySet()) {
            ItemStack stack = ammo.get(index);

            int removed = Math.min(count, stack.getAmount());
            count -= removed;

            if (stack.getAmount() == removed)
                player.getInventory().setItem(index, null);
            else
                stack.setAmount(stack.getAmount() - removed);

            if (count <= 0)
                break;
        }

        player.updateInventory();
    }
}