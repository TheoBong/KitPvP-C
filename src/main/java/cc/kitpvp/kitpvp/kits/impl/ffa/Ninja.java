package cc.kitpvp.kitpvp.kits.impl.ffa;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import cc.kitpvp.kitpvp.util.item.ItemBuilder;
import cc.kitpvp.kitpvp.util.message.CC;
import cc.kitpvp.kitpvp.KitPvPPlugin;
import cc.kitpvp.kitpvp.events.DeathEvent;
import cc.kitpvp.kitpvp.kits.Kit;
import cc.kitpvp.kitpvp.kits.KitContents;
import cc.kitpvp.kitpvp.player.Profile;
import cc.kitpvp.kitpvp.player.PlayerState;

import java.util.List;

public class Ninja extends Kit {

    public Ninja(KitPvPPlugin plugin) {
        super(plugin, "Ninja", Material.IRON_SWORD, "Teleports behind the last damaged player when sneaking.");
        registerCooldownTimer("ninja", 15);
    }

    @Override
    protected void onEquip(Player player) {
    }

    @Override
    protected List<PotionEffect> effects() {
        return null;
    }

    @Override
    protected KitContents.Builder contentsBuilder() {
        KitContents.Builder builder = KitContents.newBuilder();

        builder.addItem(new ItemBuilder(Material.IRON_SWORD).enchant(Enchantment.DAMAGE_ALL, 1).unbreakable(true).build());
        builder.addItem(new ItemBuilder(Material.FISHING_ROD).unbreakable(true).build());
        builder.addArmor(
                new ItemStack(Material.DIAMOND_BOOTS),
                new ItemStack(Material.IRON_LEGGINGS),
                new ItemStack(Material.IRON_CHESTPLATE)
        );

        return builder;
    }

    @EventHandler
    public void onKill(DeathEvent event) {
        if (event.getKillerProfile().getCurrentKit() == this) {
            event.getKillerProfile().setLastAttacked(null);
        }
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        Player ninja = event.getPlayer();
        Player victim = plugin.getServer().getPlayer(plugin.getPlayerManager().getProfile(ninja).getLastAttacked());

        if (ninja.isSneaking()) {
            return;
        }

        if (victim == null) {
            return;
        }

        Profile kitProfile = plugin.getPlayerManager().getProfile(ninja);

        if (kitProfile.getState() != PlayerState.FFA) {
            return;
        }

        if (isInvalidKit(ninja)) {
            return;
        }

        if (ninja.getLocation().distanceSquared(victim.getLocation()) >= 400) {
            ninja.sendMessage(CC.RED + "Target is too far away.");
            return;
        }

        if (isOnCooldown(ninja, "ninja")) {
            return;
        }
        ninja.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 60, 0));
        ninja.teleport(victim);
    }
}

