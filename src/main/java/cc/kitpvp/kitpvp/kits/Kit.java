package cc.kitpvp.kitpvp.kits;

import cc.kitpvp.kitpvp.KitPvPPlugin;
import cc.kitpvp.kitpvp.player.PlayerState;
import cc.kitpvp.kitpvp.player.Profile;
import cc.kitpvp.kitpvp.util.item.ItemBuilder;
import cc.kitpvp.kitpvp.util.message.CC;
import cc.kitpvp.kitpvp.util.timer.Timer;
import cc.kitpvp.kitpvp.util.timer.impl.IntegerTimer;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.*;
import java.util.concurrent.TimeUnit;

public abstract class Kit implements Listener {
    protected KitPvPPlugin plugin;
    @Getter private final String name;
    @Getter private final ItemStack icon;
    private final KitContents contents;
    private final List<PotionEffect> effects;
    private final Map<String, Integer> cooldownTimers = new HashMap<>();
    private final Map<UUID, Map<String, Timer>> playerTimersById = new HashMap<>();

    public Kit(KitPvPPlugin plugin, String name, ItemStack icon, String... description) {
        this.plugin = plugin;
        this.name = name;

        ItemBuilder builder = ItemBuilder.from(icon);
        String[] coloredDescription = Arrays.stream(description).map(s -> CC.PRIMARY + s).toArray(String[]::new);

        this.icon = builder.name(CC.SECONDARY + name).lore(coloredDescription).build();
        this.contents = contentsBuilder().build();
        this.effects = effects();

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public Kit(KitPvPPlugin plugin, String name, Material icon, String... description) {
        this(plugin, name, new ItemStack(icon), description);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        playerTimersById.remove(event.getPlayer().getUniqueId());
    }

    protected void registerCooldownTimer(String id, int seconds) {
        cooldownTimers.put(id, seconds);
    }

    protected boolean isOnCooldown(Player player, String id) {
        if (!playerTimersById.containsKey(player.getUniqueId())) {
            playerTimersById.put(player.getUniqueId(), new HashMap<>());
        }

        Map<String, Timer> timers = playerTimersById.get(player.getUniqueId());

        if (!timers.containsKey(id)) {
            timers.put(id, new IntegerTimer(TimeUnit.SECONDS, cooldownTimers.get(id)));
        }

        Timer timer = timers.get(id);

        boolean onCooldown = timer.isActive();

        if (onCooldown) {
            player.sendMessage(CC.RED + "You can't do this for another " + timer.formattedExpiration() + ".");
        }

        return onCooldown;
    }

    public void repairKit(Player player) {
        player.getInventory().setArmorContents(null);
        player.getInventory().setArmorContents(contents.getArmor());
    }

    public void purchaseKit(Player player) {
        Profile profile = plugin.getPlayerManager().getProfile(player);

        if (ownsKit(player)) {
            player.sendMessage(CC.PRIMARY + "You already own " + CC.ACCENT + this.getName());
            return;
        }

        if (profile.getStatistics().getCredits() >= 500) {
            profile.addPurchasedKit(this);
            player.sendMessage(CC.PRIMARY + "You successfully purchased " + CC.ACCENT + this.getName() + CC.PRIMARY
                    + " for " + CC.ACCENT + "500 " + CC.PRIMARY + "credits.");
            profile.getStatistics().setCredits(profile.getStatistics().getCredits() - 500);
        } else {
            player.sendMessage(CC.RED + "You can't afford that kit!");
        }
    }

    public boolean ownsKit(Player player) {
        Profile profile = plugin.getPlayerManager().getProfile(player);
        return profile.getPurchasedKits().contains(this.name);
    }

    protected boolean isInvalidKit(Player player) {
        Profile profile = plugin.getPlayerManager().getProfile(player);
        return profile.getCurrentKit() != this;
    }

    public void apply(Player player) {
        Profile profile = plugin.getPlayerManager().getProfile(player);

        if (profile.getState() != PlayerState.SPAWN) {
            player.sendMessage(CC.RED + "You can't choose a kit right now!");
            return;
        }

        profile.setKit(this);
        contents.apply(player);

        player.getActivePotionEffects().stream().map(PotionEffect::getType).forEach(player::removePotionEffect);

        if (effects != null) {
            player.addPotionEffects(effects);
        }

        onEquip(player);
        player.sendMessage(CC.GREEN + "You have equipped the " + name + " kit.");
    }

    protected abstract void onEquip(Player player);

    protected abstract List<PotionEffect> effects();

    protected abstract KitContents.Builder contentsBuilder();

    public Timer getCooldownTimer(Player player, String kitName) {
        Map<String, Timer> timers = playerTimersById.get(player.getUniqueId());

        if (timers == null) {
            return null;
        }

        String cooldownId = kitName.toLowerCase();

        if (!timers.containsKey(cooldownId)) {
            return null;
        }

        Timer timer = timers.get(cooldownId);

        return timer.isActive(false) ? timer : null;
    }
}
