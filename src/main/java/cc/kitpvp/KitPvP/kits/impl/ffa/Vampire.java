package cc.kitpvp.KitPvP.kits.impl.ffa;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import cc.kitpvp.KitPvP.util.item.ItemBuilder;
import cc.kitpvp.KitPvP.KitPvPPlugin;
import cc.kitpvp.KitPvP.kits.Kit;
import cc.kitpvp.KitPvP.kits.KitContents;
import cc.kitpvp.KitPvP.player.Profile;
import cc.kitpvp.KitPvP.player.PlayerState;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Vampire extends Kit {
    public Vampire(KitPvPPlugin plugin) {
        super(plugin, "Vampire", Material.GHAST_TEAR, "Suck the blood from your opponents.");
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
        builder.addItem(new ItemBuilder(Material.FISHING_ROD).unbreakable(true).build());
        builder.addArmor(
                new ItemBuilder(Material.LEATHER_BOOTS).color(Color.BLACK).unbreakable(true).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4).build(),
                new ItemBuilder(Material.LEATHER_LEGGINGS).color(Color.BLACK).unbreakable(true).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4).build(),
                new ItemBuilder(Material.LEATHER_CHESTPLATE).color(Color.RED).unbreakable(true).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4).build(),
                new ItemBuilder(Material.LEATHER_HELMET).color(Color.BLACK).unbreakable(true).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4).build()

        );

        return builder;
    }

    private int randomPercent() {
        return ThreadLocalRandom.current().nextInt(0, 101);
    }

    @EventHandler
    public void onDrain(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player) || !(event.getEntity() instanceof Player)) {
            return;
        }

        Player vampire = (Player) event.getDamager();
        Player victim = (Player) event.getEntity();

        if (isInvalidKit(vampire)) {
            return;
        }

        Profile victimProfile = plugin.getPlayerManager().getProfile(victim);

        if (victimProfile.getState() != PlayerState.FFA) {
            return;
        }

        if (randomPercent() <= 25) {
            double health = vampire.getHealth() + 2;

            vampire.setHealth(health > 20.0 ? 20.0 : health);
            victim.damage(2, vampire);
        }
    }
}
