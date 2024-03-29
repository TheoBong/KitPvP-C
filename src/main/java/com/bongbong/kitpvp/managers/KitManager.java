package com.bongbong.kitpvp.managers;

import com.bongbong.kitpvp.KitPvPPlugin;
import com.bongbong.kitpvp.kits.Kit;
import com.bongbong.kitpvp.kits.impl.ffa.*;
import lombok.Getter;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class KitManager {
    private final Map<String, Kit> FfaKitNames = new LinkedHashMap<>();
    private final Map<Class<? extends Kit>, Kit> FfaKitClasses = new LinkedHashMap<>();
    @Getter private final Kit defaultKit;

    public KitManager(KitPvPPlugin plugin) {
        registerFFAKits(
                new PvP(plugin),
                new Archer(plugin),
                new Berserker(plugin),
                new Chemist(plugin),
                new Clout(plugin),
                new Flash(plugin),
                new Mineman(plugin),
                new Ninja(plugin),
                new Turtle(plugin),
                new Unholy(plugin),
                new Vampire(plugin)
        );

        defaultKit = getFfaKitByClass(PvP.class);
    }

    private void registerFFAKits(Kit... kits) {
        for (Kit kit : kits) {
            FfaKitNames.put(kit.getName().toLowerCase(), kit);
            FfaKitClasses.put(kit.getClass(), kit);
        }
    }

    public Kit getFfaKitByName(String kitName) {
        return FfaKitNames.get(kitName.toLowerCase());
    }

    public Kit getFfaKitByClass(Class<? extends Kit> clazz) {
        return FfaKitClasses.get(clazz);
    }

    public Collection<Kit> getKits() {
        return FfaKitNames.values();
    }
}
