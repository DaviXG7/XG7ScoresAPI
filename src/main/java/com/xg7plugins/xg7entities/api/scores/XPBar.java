package com.xg7plugins.xg7entities.api.scores;

import com.xg7plugins.xg7entities.api.Score;
import com.xg7plugins.xg7entities.api.ScoreManager;
import com.xg7plugins.xg7entities.api.utils.ScoreCondition;
import org.bukkit.entity.Player;

public class XPBar extends Score {

    public XPBar(long delay, String[] numbers, String id, ScoreCondition condition) {
        super(delay, numbers, id, condition);
        ScoreManager.registerScore(this);
    }

    @Override
    public void update() {
        for (Player player : super.getPlayers()) {
            player.setLevel(Integer.parseInt(getToUpdate()[getIndexUpdating()].split(", ")[0]));
            player.setExp(Float.parseFloat(getToUpdate()[getIndexUpdating()].split(", ")[1]));
        }
    }
}
