package cc.kitpvp.KitPvP.util.inventoryapi;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.function.BiConsumer;

public class PlayerAction implements Action {
    private BiConsumer<Player, ClickType> consumer;
    private boolean autoClose;

    public PlayerAction(BiConsumer<Player, ClickType> biConsumer) {
        this(biConsumer, false);
    }

    public PlayerAction(BiConsumer<Player, ClickType> consumer, boolean autoClose) {
        this.consumer = consumer;
        this.autoClose = autoClose;
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        ClickType clickType = event.getClick();
        Player player = (Player) event.getWhoClicked();
        consumer.accept(player, clickType);

        if (autoClose) {
            player.closeInventory();
        }
    }
}
