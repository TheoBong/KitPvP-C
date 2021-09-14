package cc.kitpvp.KitPvP.player;

import org.bukkit.Bukkit;

import java.util.*;
import java.util.stream.Collectors;

public class PlayerDamageData {
    private final Map<UUID, Double> attackerDamage = new HashMap<>();

    public void put(UUID attackerId, double dmg) {
        attackerDamage.put(attackerId, attackerDamage.getOrDefault(attackerId, 0.0) + dmg);
    }

    public void clear() {
        attackerDamage.clear();
    }

    public double total() {
        return attackerDamage.entrySet().stream().filter(entry -> Bukkit.getPlayer(entry.getKey()) != null).mapToDouble(Map.Entry::getValue).sum();
    }

    public Map<UUID, Double> sortedMap() {
        return attackerDamage.entrySet().stream()
                .filter(entry -> Bukkit.getPlayer(entry.getKey()) != null)
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
    }

    public Map<UUID, Double> getAttackerDamage() {
        return attackerDamage;
    }
}
