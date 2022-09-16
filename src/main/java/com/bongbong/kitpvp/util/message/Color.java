package com.bongbong.kitpvp.util.message;

import org.bukkit.ChatColor;

import java.util.List;
import java.util.stream.Collectors;

public class Color {

    public static String translate(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static String strip(String s) {
        return ChatColor.stripColor(translate(s));
    }

    public static List<String> translate(List<String> text) {
        return text.stream().map(Color::translate).collect(Collectors.toList());
    }
}
