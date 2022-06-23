package cc.kitpvp.kitpvp.duels;

import lombok.Data;
import org.bukkit.Material;
import org.bukkit.block.Block;

public @Data class BrokenBlock {

    private final Block block;
    private final Material material;
    private final byte data;
}
