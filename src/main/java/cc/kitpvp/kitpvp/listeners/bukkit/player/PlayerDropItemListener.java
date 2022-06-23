package cc.kitpvp.kitpvp.listeners.bukkit.player;

import cc.kitpvp.kitpvp.KitPvPPlugin;
import cc.kitpvp.kitpvp.duels.Occupation;
import cc.kitpvp.kitpvp.player.Profile;
import cc.kitpvp.kitpvp.util.EntityHider;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.List;

public class PlayerDropItemListener implements Listener {

    private KitPvPPlugin plugin;
    private EntityHider entityHider;
    public PlayerDropItemListener(KitPvPPlugin plugin) {
        this.plugin = plugin;
        this.entityHider = plugin.getEntityHider();
    }

    private List<Material> items = Arrays.asList(
            Material.DIAMOND_SWORD,
            Material.IRON_SWORD,
            Material.STONE_SWORD,
            Material.GOLD_SWORD,
            Material.WOOD_SWORD
    );

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        Profile profile = plugin.getPlayerManager().getProfile(player.getUniqueId());
        Occupation occupation = profile.getDuelOccupation();

        if(occupation != null && occupation.getAlivePlayers().contains(player)) {

            Item item = event.getItemDrop();

            if (items.contains(item.getItemStack().getType())) {
                player.sendMessage(ChatColor.RED + "You cannot drop your sword.");
                event.setCancelled(true);
                return;
            }

            occupation.addEntity(item);

            if (item.getItemStack().getType().equals(Material.GLASS_BOTTLE) || item.getItemStack().getType().equals(Material.BOWL)) {
                item.remove();
            }

            new BukkitRunnable() {
                public void run() {
                    item.remove();
                }
            }.runTaskLater(plugin, 400L);
        }
    }
}
