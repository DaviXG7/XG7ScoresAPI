package com.xg7plugins.xg7entities.api.utils;

import org.bukkit.entity.Player;

@FunctionalInterface
public interface ScoreCondition {

    boolean condition(Player player);

}
