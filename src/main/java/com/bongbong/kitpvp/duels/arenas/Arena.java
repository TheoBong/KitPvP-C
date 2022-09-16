package com.bongbong.kitpvp.duels.arenas;

import com.bongbong.kitpvp.duels.kits.Kit;
import com.bongbong.kitpvp.util.LocationUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Data;
import org.bson.Document;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.lang.reflect.Modifier;
import java.util.*;

public @Data class Arena {

    private final UUID uuid;
    private String name, displayName;
    private Location pos1, pos2, pos3, corner1, corner2;
    private Kit.Type type;
    private boolean subArena, enabled;
    //private int buildMin, buildMax;
    private UUID parentArena;
    private List<UUID> subArenas;
    private boolean inUse;

    public Arena(UUID uuid) {
        this.uuid = uuid;
        this.type = Kit.Type.NORMAL;
        this.subArenas = new ArrayList<>();
    }

    public int getBuildMax() {
        if (pos1 != null){
            if (pos2 != null) {
                if (pos3 != null) {
                    return (int)Math.max(Math.max(pos1.getY(), pos2.getY()), pos3.getY()) + 20;
                } else{
                    return (int)Math.max(pos1.getY(), pos2.getY()) + 20;
                }
            } else {
                return (int)pos1.getY() + 20;
            }
        } else{
            return 0;
        }
    }

    public boolean isInBounds(Location l) {
        int x1, x2, y1, y2, z1, z2;
        x1 = Math.min(corner1.getBlockX(), corner2.getBlockX());
        y1 = Math.min(corner1.getBlockY(), corner2.getBlockY());
        z1 = Math.min(corner1.getBlockZ(), corner2.getBlockZ());
        x2 = Math.max(corner1.getBlockX(), corner2.getBlockX());
        y2 = Math.max(corner1.getBlockY(), corner2.getBlockY());
        z2 = Math.max(corner1.getBlockZ(), corner2.getBlockZ());
        return (l.getBlockX() >= x1 && l.getBlockX() <= x2 && l.getBlockY() >= y1 && l.getBlockY() <= y2 && l.getBlockZ() >= z1 && l.getBlockZ() <= z2);
    }

    public void copy(Location l) {
        int xMin, xMax, yMin, yMax, zMin, zMax, xDif, yDif, zDif;
        xMin = Math.min(corner1.getBlockX(), corner2.getBlockX());
        yMin = Math.min(corner1.getBlockY(), corner2.getBlockY());
        zMin = Math.min(corner1.getBlockZ(), corner2.getBlockZ());
        xMax = Math.max(corner1.getBlockX(), corner2.getBlockX());
        yMax = Math.max(corner1.getBlockY(), corner2.getBlockY());
        zMax = Math.max(corner1.getBlockZ(), corner2.getBlockZ());

        xDif = l.getBlockX() - corner1.getBlockX();
        yDif = l.getBlockY() - corner1.getBlockY();
        zDif = l.getBlockZ() - corner1.getBlockZ();

        for(int x = xMin; x < xMax; x++) {
            for(int y = yMin; y < yMax; y++) {
                for(int z = zMin; z < zMax; z++) {

                    Location location = new Location(l.getWorld(), x, y, z);
                    Block block = location.getBlock();

                    Location newLocation = new Location(l.getWorld(), x + xDif, y + yDif, z + zDif);
                    Block newBlock = newLocation.getBlock();

                    if(!block.getType().equals(newBlock.getType())) {
                        newBlock.setType(block.getType());
                        newBlock.setData(block.getData());
                    }
                }
            }
        }
    }

    public void updateAll() {
        // TODO: Update all sub arenas;
    }

    public void importFromDocument(Document d) {
        setName(d.getString("name"));
        setDisplayName(d.getString("display_name"));
        setPos1(LocationUtils.convert(d.getString("pos1")));
        setPos2(LocationUtils.convert(d.getString("pos2")));
        setPos3(LocationUtils.convert(d.getString("pos3")));
        setCorner1(LocationUtils.convert(d.getString("corner1")));
        setCorner2(LocationUtils.convert(d.getString("corner2")));
        setType(Kit.Type.valueOf(d.getString("type")));
        setSubArena(d.getBoolean("sub_arena"));
        setEnabled(d.getBoolean("enabled"));
        //setBuildMin(d.getInteger("build_min"));
        //setBuildMax(d.getInteger("build_max"));
        setParentArena(d.get("parent_arena", UUID.class));
        setSubArenas(d.getList("sub_arenas", UUID.class));
    }

    public Map<String, Object> export() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", getName());
        map.put("display_name", getDisplayName());
        map.put("pos1", LocationUtils.convert(getPos1()));
        map.put("pos2", LocationUtils.convert(getPos2()));
        map.put("pos3", LocationUtils.convert(getPos3()));
        map.put("corner1", LocationUtils.convert(getCorner1()));
        map.put("corner2", LocationUtils.convert(getCorner2()));
        map.put("type", getType().toString());
        map.put("sub_arena", isSubArena());
        map.put("enabled", isEnabled());
        //map.put("build_min", getBuildMin());
        //map.put("build_max", getBuildMax());
        map.put("parent_arena", getParentArena());
        map.put("sub_arenas", getSubArenas());
        return map;
    }

    public String serialize() {
        Gson gson = new GsonBuilder().excludeFieldsWithModifiers(Modifier.STATIC).create();
        return gson.toJson(this);
    }
}
