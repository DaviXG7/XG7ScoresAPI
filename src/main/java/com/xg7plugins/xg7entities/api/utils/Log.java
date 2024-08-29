package com.xg7plugins.xg7entities.api.utils;

import lombok.Setter;
import org.bukkit.Bukkit;

import java.util.logging.Level;

/**
 * This class is used to debug
 */
public class Log {

    @Setter
    private static boolean isEnabled = false;

    public static void severe(String message) {
        Bukkit.getLogger().severe("[XG7MenusAPI ERROR] " + message);
    }

    public static void fine(String message) {
        if (isEnabled) Bukkit.getLogger().fine("[XG7MenusAPI SUCSESS] " + message);
    }

    public static void info(String message) {
        if (isEnabled) Bukkit.getLogger().info("[XG7MenusAPI DEBUG] " + message);
    }

    public static void warn(String message) {
        Bukkit.getLogger().log(Level.WARNING, "[XG7MenusAPI ALERT] " + message);
    }

    public static void loading(String message) {
        Bukkit.getLogger().info("[XG7MenusAPI] " + message);
    }

}
