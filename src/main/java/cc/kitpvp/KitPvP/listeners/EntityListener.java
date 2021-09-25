package cc.kitpvp.KitPvP.listeners;

import cc.kitpvp.KitPvP.KitPvPPlugin;
import cc.kitpvp.KitPvP.player.PlayerState;
import cc.kitpvp.KitPvP.player.Profile;
import cc.kitpvp.KitPvP.util.message.CC;
import cc.kitpvp.KitPvP.util.timer.Timer;
import lombok.RequiredArgsConstructor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@RequiredArgsConstructor
public class EntityListener implements Listener {
    private static final double DAMAGE_PER_STRENGTH_LEVEL = 1;
    private final KitPvPPlugin plugin;

    @EventHandler
    public void onShoot(ProjectileLaunchEvent event) {
        if (!(event.getEntity().getShooter() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity().getShooter();

        if (player.getGameMode() != GameMode.SURVIVAL) {
            return;
        }

        Profile profile = plugin.getPlayerManager().getProfile(player);

        if (profile.getState() != PlayerState.FFA) {
            return;
        }

        if (event.getEntity() instanceof EnderPearl) {
            Timer timer = profile.getPearlTimer();

            timer.isActive(); // check active
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player victim = (Player) event.getEntity();
        Profile victimProfile = plugin.getPlayerManager().getProfile(victim);

        if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
            victim.teleport(plugin.getSpawnLocation());
        } else if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            event.setCancelled(true);
            return;
        }

        if (victimProfile.getState() == PlayerState.SPAWN) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerDamageByPlayer(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)
                || (!(event.getDamager() instanceof Player) && (!(event.getDamager() instanceof Arrow)
                || !(((Arrow) event.getDamager()).getShooter() instanceof Player)))) {
            return;
        }

        Player damager = event.getDamager() instanceof Player ? (Player) event.getDamager() : (Player) ((Arrow) event.getDamager()).getShooter();

        Profile damagerProfile = plugin.getPlayerManager().getProfile(damager);

        Player victim = (Player) event.getEntity();

        Profile victimProfile = plugin.getPlayerManager().getProfile(victim);

        if (victimProfile.getState() == PlayerState.SPAWN) {
            damager.sendMessage(CC.RED + "That player currently has spawn protection.");
            return;
        }

        if (damager.getAllowFlight() && damager.getGameMode() != GameMode.CREATIVE) {
            if (damagerProfile.getCurrentKit() == null) {
                damager.setAllowFlight(false);
                damager.setFlying(false);
            }
        }

        if (damagerProfile.getState() == PlayerState.SPAWN && victimProfile.getState() == PlayerState.FFA) {
            if (damagerProfile.getLastKit() != null) {
                damagerProfile.getLastKit().apply(damager);
            } else {
                plugin.getKitManager().getDefaultKit().apply(damager);
            }
            
            damagerProfile.setState(PlayerState.FFA);
            damager.sendMessage(CC.RED + "You no longer have spawn protection!");
        }

        if (victimProfile.getState() == PlayerState.FFA) {
            victimProfile.getDamageData().put(event.getDamager().getUniqueId(), event.getFinalDamage());
            damagerProfile.setLastAttacked(victim.getUniqueId());
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDamageStrengthNerf(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getDamager();

        for (PotionEffect effect : player.getActivePotionEffects()) {
            if (effect.getType().equals(PotionEffectType.INCREASE_DAMAGE)) {
                ItemStack heldItem = player.getItemInHand() != null ? player.getItemInHand() : new ItemStack(Material.AIR);

                int sharpnessLevel = heldItem.getEnchantmentLevel(Enchantment.DAMAGE_ALL);
                int strengthLevel = effect.getAmplifier() + 1;

                double totalDamage = event.getDamage();
                double weaponDamage = (totalDamage - 1.25 * sharpnessLevel) / (1.0 + 1.3 * strengthLevel) - 1.0;
                double finalDamage = 1.0 + weaponDamage + 1.25 * sharpnessLevel + (DAMAGE_PER_STRENGTH_LEVEL * 2) * strengthLevel;

                event.setDamage(finalDamage);
                break;
            }
        }
    }

    @EventHandler
    public void onMobDeath(EntityDeathEvent event) {
        event.getDrops().clear();
    }
}

