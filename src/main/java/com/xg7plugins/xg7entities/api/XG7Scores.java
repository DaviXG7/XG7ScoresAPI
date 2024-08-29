package com.xg7plugins.xg7entities.api;

import com.xg7plugins.xg7entities.api.utils.Log;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class XG7Scores {
    @Getter
    private static JavaPlugin plugin;

    @Getter
    private static int version;

    public static void setDebug(boolean debug) {
        Log.setEnabled(debug);
    }

    public static void inicialize(JavaPlugin javaPlugin) {
        plugin = javaPlugin;
        version = Integer.parseInt(Bukkit.getServer().getVersion().split("\\.")[1].replace(")", ""));

        javaPlugin.getServer().getPluginManager().registerEvents(new ScoreListener(), javaPlugin);
    }
    public static void disable() {
        ScoreManager.removePlayers();
    }


}