package cc.kitpvp.kitpvp.listeners.bukkit.player;

import cc.kitpvp.kitpvp.KitPvPPlugin;
import cc.kitpvp.kitpvp.duels.Occupation;
import cc.kitpvp.kitpvp.duels.Participant;
import cc.kitpvp.kitpvp.player.Cooldown;
import cc.kitpvp.kitpvp.player.Profile;
import cc.kitpvp.kitpvp.util.MathUtil;
import cc.kitpvp.kitpvp.util.MathUtil2;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.*;



public class PlayerInteractListener implements Listener {

    private KitPvPPlugin plugin;
    public PlayerInteractListener(KitPvPPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Profile profile = plugin.getPlayerManager().getProfile(player.getUniqueId());
        Action action = event.getAction();
        Block block = event.getClickedBlock();

        if(action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)) {

            Occupation occupation = profile.getDuelOccupation();
            if (occupation != null) {
                Participant participant = occupation.getAlive().get(player.getUniqueId());
                if (player.getItemInHand().getType() == Material.ENDER_PEARL) {
                    Cooldown cooldown = profile.getCooldowns().get(Cooldown.Type.ENDER_PEARL);
                    if (cooldown != null) {
                        if (!cooldown.isExpired()) {
                            player.sendMessage(cooldown.getBlockedMessage());
                            player.getInventory().addItem(new ItemStack(Material.ENDER_PEARL, 1));
                        }
                    }
                }

                if(profile.getDuelOccupation().getCurrentPlaying().contains(player)) {
                    if(block != null) {
                        BlockState state = block.getState();
                        MaterialData data = state.getData();
                        if (data instanceof Door) {
                            event.setCancelled(true);
                        } else if (data instanceof TrapDoor) {
                            event.setCancelled(true);
                        } else if (data instanceof Gate) {
                            event.setCancelled(true);
                        } else if(data instanceof Lever) {
                            event.setCancelled(true);
                        }
                    }
                } else {
                    event.setCancelled(true);
                }
            }
        } else if (action.equals(Action.LEFT_CLICK_AIR)) {
            Occupation occupation = profile.getDuelOccupation();
            if (occupation != null) {
                Participant participant = occupation.getAlive().get(player.getUniqueId());

                if (occupation.isBowSpleef() && 
                player.getItemInHand().getType() == Material.BOW && 
                occupation.getState() == Occupation.State.ACTIVE &&
                profile.getTripleShots() > 0
                ) {
                    profile.setTripleShots(profile.getTripleShots() - 1);

                    Arrow a1 = (Arrow)player.launchProjectile(Arrow.class);
                    
                    a1.setShooter(player);
                    a1.setVelocity(a1.getVelocity().multiply(2));
                    a1.setFireTicks(9999);

                    Arrow a2 = (Arrow)player.launchProjectile(Arrow.class);
                    
                    a2.setShooter(player);
                    a2.setVelocity(MathUtil2.rotateVector(a2.getVelocity().multiply(2), (double)10.0));
                    a2.setFireTicks(9999);

                    Arrow a3 = (Arrow)player.launchProjectile(Arrow.class);
                    
                    a3.setShooter(player);
                    a3.setVelocity(MathUtil2.rotateVector(a3.getVelocity().multiply(2), (double)-10.0));
                    a3.setFireTicks(9999);
                }
            }
        }
    }
}
