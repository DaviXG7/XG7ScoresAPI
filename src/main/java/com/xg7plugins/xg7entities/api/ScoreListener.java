package com.xg7plugins.xg7entities.api;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class ScoreListener implements Listener {

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Score score = ScoreManager.getByPlayer(event.getPlayer());
        if (score == null) return;
        score.removePlayer(event.getPlayer());
    }


}
