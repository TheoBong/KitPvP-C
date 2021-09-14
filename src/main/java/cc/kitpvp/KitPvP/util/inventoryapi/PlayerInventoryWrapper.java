package cc.kitpvp.KitPvP.util.inventoryapi;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class PlayerInventoryWrapper {
    private final Map<UUID, InventoryWrapper> openPlayerWrappers = new HashMap<>();
    private final String name;
    private final int rows;

    public PlayerInventoryWrapper(String name, int rows) {
        this.name = name;
        this.rows = rows;
    }

    public void open(Player player) {
        InventoryWrapper wrapper = new SimpleInventoryWrapper(name, rows) {
            @Override
            public void init() {
            }

            @Override
            public void update() {
            }
        };
        openPlayerWrappers.put(player.getUniqueId(), wrapper);
        init(player, wrapper);
        updateInternal(player, wrapper);
        player.openInventory(wrapper.getInventory());
    }


    public void updateInternal(Player player, InventoryWrapper wrapper) {
        wrapper.clear();
        update(player, wrapper);
    }

    public void close(Player player) {
        openPlayerWrappers.remove(player.getUniqueId());
    }

    public abstract void init(Player player, InventoryWrapper wrapper);

    public abstract void update(Player player, InventoryWrapper wrapper);

    public Map<UUID, InventoryWrapper> getOpenPlayerWrappers() {
        return this.openPlayerWrappers;
    }
}
