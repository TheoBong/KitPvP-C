package cc.kitpvp.KitPvP.kits.impl.ffa;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import cc.kitpvp.KitPvP.util.item.ItemBuilder;
import cc.kitpvp.KitPvP.util.player.PlayerUtil;
import cc.kitpvp.KitPvP.KitPvPPlugin;
import cc.kitpvp.KitPvP.kits.Kit;
import cc.kitpvp.KitPvP.kits.KitContents;
import cc.kitpvp.KitPvP.player.Profile;
import cc.kitpvp.KitPvP.player.PlayerState;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Unholy extends Kit {
    public Unholy(KitPvPPlugin plugin) {
        super(plugin, "Unholy", Material.SULPHUR, "Wither the life away from your enemies.");
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
                new ItemBuilder(Material.LEATHER_BOOTS).color(Color.GRAY).enchant(Enchantment.DURABILITY, 50).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4).build(),
                new ItemBuilder(Material.CHAINMAIL_LEGGINGS).enchant(Enchantment.DURABILITY, 4).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).build(),
                new ItemBuilder(Material.CHAINMAIL_CHESTPLATE).enchant(Enchantment.DURABILITY, 4).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).build(),
                new ItemBuilder(Material.LEATHER_HELMET).color(Color.GRAY).enchant(Enchantment.DURABILITY, 50).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4).build()
        );

        return builder;
    }

    private int randomPercent() {
        return ThreadLocalRandom.current().nextInt(0, 101);
    }

    @EventHandler
    public void onWither(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player) || !(event.getEntity() instanceof Player)) {
            return;
        }

        Player unholy = (Player) event.getDamager();
        Player victim = (Player) event.getEntity();

        if (isInvalidKit(unholy)) {
            return;
        }

        Profile victimProfile = plugin.getPlayerManager().getProfile(victim);

        if (victimProfile.getState() != PlayerState.FFA) {
            return;
        }


        if (randomPercent() <= 25) {
            if (PlayerUtil.hasEffect(victim, PotionEffectType.WITHER)) {
                return;
            }
            PotionEffect wither = new PotionEffect(PotionEffectType.WITHER, 60, 2);

            victim.addPotionEffect(wither);
        }
    }
}
