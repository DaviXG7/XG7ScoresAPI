package com.xg7plugins.xg7entities.api.scores;

import com.xg7plugins.xg7entities.api.Score;
import com.xg7plugins.xg7entities.api.ScoreManager;
import com.xg7plugins.xg7entities.api.utils.ScoreCondition;
import com.xg7plugins.xg7entities.api.utils.Text;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

public class PublicBossBar extends Score {
    
    private BossBar bossBar;
    
    public PublicBossBar(long delay, String[] title, String id, ScoreCondition condition, BarColor color, BarStyle style, double progress) {
        super(delay, title, id, condition);

        bossBar = Bukkit.createBossBar(title[0],color,style);
        bossBar.setProgress(progress);
        ScoreManager.registerScore(this);
    }

    @SneakyThrows
    @Override
    public void addPlayer(Player player) {
        super.addPlayer(player);
        if (!bossBar.getPlayers().contains(player)) bossBar.addPlayer(player);

    }

    @SneakyThrows
    @Override
    public void removePlayer(Player player) {
        super.removePlayer(player);
        bossBar.removePlayer(player);
    }

    @Override
    public void update() {
        for (Player player : super.getPlayers()) {
            bossBar.setTitle(Text.format(getToUpdate()[getIndexUpdating()]).getText());
        }
    }
}
