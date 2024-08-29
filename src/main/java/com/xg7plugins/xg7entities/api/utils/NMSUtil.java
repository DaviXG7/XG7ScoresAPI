package com.xg7plugins.xg7entities.api.utils;

import lombok.Getter;
import org.bukkit.Bukkit;

/**
 * This class is used to get NMS and CraftBukkit classes
 */
public class NMSUtil {
    static String packageName = Bukkit.getServer().getClass().getPackage().getName();
    @Getter
    static String version = packageName.substring(packageName.lastIndexOf('.') + 1);


    public static Class<?> getNMSClass(String className) throws ClassNotFoundException {
        String fullName = "net.minecraft.server." + version + "." + className;
        return Class.forName(fullName);
    }

    public static Class<?> getCraftBukkitClass(String className) throws ClassNotFoundException {
        String fullName = "org.bukkit.craftbukkit." + version + "." + className;
        return Class.forName(fullName);
    }


}
