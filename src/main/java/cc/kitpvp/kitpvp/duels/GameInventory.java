package cc.kitpvp.kitpvp.duels;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;
import xyz.leuo.gooey.button.Button;
import xyz.leuo.gooey.gui.GUI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class GameInventory extends GUI {

    private @Getter UUID uuid;
    private @Getter String name;
    private @Getter List<ItemStack> contents;
    //private @Getter @Setter GameInventory opponentInventory;
    private @Getter @Setter GameInventory nextInventory;
    private @Getter @Setter GameInventory previousInventory;
    private @Getter int health, hunger;
    private @Getter boolean placed;

    public GameInventory(Participant participant) {
        super(participant.getName() + "'s Inventory", 54);
        this.uuid = UUID.randomUUID();
        this.name = participant.getName();

        Player player = participant.getPlayer();
        final PlayerInventory inventory = player.getInventory();

        ItemStack[] armor = inventory.getArmorContents(), contents = inventory.getContents();
        this.contents = new ArrayList<>(Arrays.asList(contents));

        for(int i = 0; i < 4; i++) {
            ItemStack item = armor[i];
            int place = i + 36;
            if(item != null) {
                if(!item.getType().equals(Material.AIR)) {
                    Button button = new Button(item, null);
                    this.setButton(place, button);
                }
            }
        }

        for(int i = 0; i < 36; i++) {
            ItemStack item = contents[i];
            int place = i;
            if(item != null) {
                if(!item.getType().equals(Material.AIR)) {
                    if(i < 9) {
                        place += 27;
                    } else {
                        place -= 9;
                    }

                    Button button = new Button(item, null);
                    this.setButton(place, button);
                }
            }
        }

        ItemStack skullItem = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta skullMeta = (SkullMeta) skullItem.getItemMeta();
        skullMeta.setOwner(participant.getName());
        skullItem.setItemMeta(skullMeta);

        Button playerInfo = null;
        if(participant.isAlive()) {
            this.health = (int) Math.round(player.getHealth());
            this.hunger = player.getFoodLevel();
            playerInfo = new Button(skullItem, "&a" + participant.getName());
            playerInfo.setLore(
                    "&bHealth: &f" + health + "/" + player.getMaxHealth(),
                    "&bHunger: &f" + hunger + "/20");
        } else {
            playerInfo = new Button(skullItem, "&c" + participant.getName() + " &7(dead)");
        }

        Potion potion = new Potion(PotionType.INSTANT_HEAL, 2);
        potion.setSplash(true);
        ItemStack healthPot = potion.toItemStack(1);
        int potions = countItems(inventory, healthPot);
        int soups = countItems(inventory, new ItemStack(Material.MUSHROOM_SOUP, 1));
        int gapples = countItems(inventory, new ItemStack(Material.GOLDEN_APPLE, 1));

        setButton(49, playerInfo);

//      do potion effects later
//        List<PotionEffect> effects = new ArrayList<>(player.getActivePotionEffects());
    }

    @Override
    public void open(Player player) {
        if(previousInventory != null && !placed) {
            Button button = new Button(Material.LEVER, "&eView " + previousInventory.getName() + "'s inventory.");
            button.setButtonAction((player1, gui, button1, inventoryClickEvent) -> previousInventory.open(player1));
            setButton(45, button);
            placed = true;
        }

        if(nextInventory != null && !placed) {
            Button button = new Button(Material.LEVER, "&eView " + nextInventory.getName() + "'s inventory.");
            button.setButtonAction((player1, gui, button1, inventoryClickEvent) -> nextInventory.open(player1));
            setButton(53, button);
            placed = true;
        }
        super.open(player);
    }

    public int countItems(PlayerInventory inventory, ItemStack itemStack) {
        int count = 0;
        for(int i = 0; i < 36; i++) {
            ItemStack item = inventory.getItem(i);
            if(item != null){
                if(item.isSimilar(itemStack)) {
                    count += item.getAmount();
                }
            }
        }

        return count;
    }
}
