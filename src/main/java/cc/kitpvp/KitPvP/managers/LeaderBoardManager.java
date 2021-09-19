package cc.kitpvp.KitPvP.managers;

import cc.kitpvp.KitPvP.KitPvPPlugin;
import lombok.Getter;
import org.bson.Document;

import java.util.Iterator;

public class LeaderBoardManager {
    private final KitPvPPlugin plugin;
    @Getter private Iterator<Document> killsSorted;

    public LeaderBoardManager(KitPvPPlugin plugin) {
        this.plugin = plugin;
    }

    public void sortTop() {
        killsSorted = plugin.getMongo().sortNumber(false, "profiles", "kills");
    }
}
