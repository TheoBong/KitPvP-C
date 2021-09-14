package cc.kitpvp.KitPvP.kits;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
import java.util.stream.IntStream;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class KitContents {
    private final ItemStack[] armor;
    private final HashMap<Integer, ItemStack> contents;

    public static Builder newBuilder() {
        return new Builder();
    }

    ItemStack[] getArmor() {
        return armor.clone();
    }

    void apply(Player player) {
        PlayerInventory inventory = player.getInventory();

        inventory.clear();
        inventory.setArmorContents(null);

        inventory.setArmorContents(armor.clone());
        contents.forEach(inventory::setItem);

        player.updateInventory();
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Builder {
        private final ItemStack[] armor = new ItemStack[4];
        private final HashMap<Integer, ItemStack> contents = new HashMap<>();

        private int getFirstEmptyIndex() {
            return IntStream.range(0, 36).filter(i -> !contents.containsKey(i)).findFirst().orElse(-1);
        }

        public Builder addItem(ItemStack item) {
            int index = getFirstEmptyIndex();

            if (index != -1) {
                contents.put(index, item);
            }

            return this;
        }

        public Builder fill(ItemStack item) {
            while (getFirstEmptyIndex() != -1) {
                addItem(item.clone());
            }

            return this;
        }

        public Builder setItem(int index, ItemStack item) {
            contents.put(index, item);
            return this;
        }

        public Builder addArmor(ItemStack... armor) {
            System.arraycopy(armor, 0, this.armor, 0, armor.length);
            return this;
        }

        private void fillBlank(ItemStack[] items) {
            IntStream.range(0, items.length).filter(i -> items[i] == null).forEach(i -> items[i] = new ItemStack(Material.AIR));
        }

        KitContents build() {
            fillBlank(armor);
            return new KitContents(armor, contents);
        }
    }
}
