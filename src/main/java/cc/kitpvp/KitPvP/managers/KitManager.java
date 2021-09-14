package cc.kitpvp.KitPvP.managers;

import cc.kitpvp.KitPvP.KitPvPPlugin;
import cc.kitpvp.KitPvP.kits.Kit;
import cc.kitpvp.KitPvP.kits.impl.ffa.PvP;
import cc.kitpvp.KitPvP.kits.impl.ffa.Rod;
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
                new Rod(plugin)
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
