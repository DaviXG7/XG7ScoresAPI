package com.xg7plugins.xg7entities.api.scores;

import com.xg7plugins.xg7entities.api.Score;
import com.xg7plugins.xg7entities.api.ScoreManager;
import com.xg7plugins.xg7entities.api.utils.ScoreCondition;
import com.xg7plugins.xg7entities.api.utils.Text;
import org.bukkit.entity.Player;

public class XPBar extends Score {

    public XPBar(long delay, String[] numbers, String id, ScoreCondition condition) {
        super(delay, numbers, id, condition);
        ScoreManager.registerScore(this);
    }

    @Override
    public void update() {
        for (Player player : super.getPlayers()) {
            player.setLevel(Integer.parseInt(Text.format(getToUpdate()[getIndexUpdating()].split(", ")[0]).setPlaceholders(player).getText()));
            player.setExp(Float.parseFloat(Text.format(getToUpdate()[getIndexUpdating()].split(", ")[1]).setPlaceholders(player).getText()));
        }
    }
}
