package com.bongbong.kitpvp.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class LocationUtils {

    private static JSONParser parser;

    static {
        parser = new JSONParser();
    }

    public static String convert(Location l) {
        if(l != null) {
            JSONObject json = new JSONObject();
            json.put("world", l.getWorld().getName());
            json.put("x", l.getX());
            json.put("y", l.getY());
            json.put("z", l.getZ());
            json.put("yaw", l.getYaw());
            json.put("pitch", l.getPitch());
            return json.toString();
        }

        return null;
    }

    public static Location convert(String s) {
        if(s != null) {
            JSONObject json = null;
            try {
                json = (JSONObject) parser.parse(s);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (json != null) {
                Location location = new Location(
                        Bukkit.getWorld((String) json.get("world")),
                        (double) json.get("x"),
                        (double) json.get("y"),
                        (double) json.get("z"),
                        (float) ((double) json.get("yaw")),
                        (float) ((double) json.get("pitch"))
                );
                return location;
            } else {
                return null;
            }
        }

        return null;
    }
}
