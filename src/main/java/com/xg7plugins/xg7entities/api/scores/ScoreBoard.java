package com.xg7plugins.xg7entities.api.scores;

import com.xg7plugins.xg7entities.api.Score;
import com.xg7plugins.xg7entities.api.ScoreManager;
import com.xg7plugins.xg7entities.api.XG7Scores;
import com.xg7plugins.xg7entities.api.utils.ScoreCondition;
import com.xg7plugins.xg7entities.api.utils.Text;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Getter
@Setter
public class ScoreBoard extends Score {

    private String[] lines;
    private String id;

    private Scoreboard scoreboard;

    public ScoreBoard(String title, String[] lines, String id, ScoreCondition condition, long delay) {
        super(delay, new String[]{title},id, condition);
        this.lines = lines;
        this.id = id;

        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

        Objective objective = scoreboard.registerNewObjective(id, "dummy");
        objective.setDisplayName(title);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        int index = lines.length + 1;

        for (String s : lines) {
            index--;

            String entry = IntStream.range(0, index).mapToObj(i -> "§r").collect(Collectors.joining());

            Team team = scoreboard.registerNewTeam(id + ":Team=" + index);

            s = Text.format(s).getText();

            String prefix = s.substring(0, Math.min(s.length(), 16));
            String suffix = null;
            if (s.length() > 16) {
                suffix = XG7Scores.getVersion() > 12 ? s.substring(16) : s.substring(16, Math.min(s.length(), 32));
                suffix = ChatColor.getLastColors(prefix) + suffix;
                if (suffix.length() > 16) suffix = s.substring(0,16);
            }

            team.setPrefix(prefix);
            if (suffix != null) team.setSuffix(suffix);

            team.addEntry(entry);

            objective.getScore(entry).setScore(index);

        }

        ScoreManager.registerScore(this);

    }

    public ScoreBoard(String[] title, String[] lines, String id, ScoreCondition condition, long taskDelay) {
        super(taskDelay, title,id,condition);
        this.lines = lines;
        this.id = id;
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

        Objective objective = scoreboard.registerNewObjective(id, "dummy");
        objective.setDisplayName(title[0]);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        int index = lines.length + 1;

        for (String s : lines) {
            index--;

            String entry = IntStream.range(0, index).mapToObj(i -> "§r").collect(Collectors.joining());

            Team team = scoreboard.registerNewTeam(id + ":Team=" + index);

            s = Text.format(s).getText();

            String prefix = s.substring(0, Math.min(s.length(), 16));
            String suffix = null;
            if (s.length() > 16) {
                suffix = XG7Scores.getVersion() > 12 ? s.substring(16) : s.substring(16, Math.min(s.length(), 32));
                suffix = ChatColor.getLastColors(prefix) + suffix;
                if (suffix.length() > 16) suffix = s.substring(0,16);
            }

            team.setPrefix(prefix);
            if (suffix != null) team.setSuffix(suffix);

            team.addEntry(entry);


            objective.getScore(entry).setScore(index);

        }

        ScoreManager.registerScore(this);

    }

    @Override
    public void update() {


        for (Player player : super.getPlayers()) {

            Objective objective = scoreboard.getObjective(id);

            objective.setDisplayName(Text.format(super.getToUpdate()[super.getIndexUpdating()]).setPlaceholders(player).getText());

            int index = lines.length + 1;

            for (String s : lines) {

                index--;

                String entry = IntStream.range(0, index).mapToObj(i -> "§r").collect(Collectors.joining());

                Team team = scoreboard.getTeam(id + ":Team=" + index);

                s = Text.format(s).setPlaceholders(player).getText();

                String prefix = s.substring(0, Math.min(s.length(), 16));
                String suffix = null;
                if (s.length() > 16) {
                    suffix = XG7Scores.getVersion() > 12 ? s.substring(16) : s.substring(16, Math.min(s.length(), 32));
                    suffix = ChatColor.getLastColors(prefix) + suffix;
                    if (suffix.length() > 16) suffix = s.substring(0,16);
                }

                team.setPrefix(prefix);
                if (suffix != null) team.setSuffix(suffix);
                else team.setSuffix("");
            }

            player.setScoreboard(scoreboard);

        }

    }

    @Override
    public void removePlayer(Player player) {
        super.removePlayer(player);
        Bukkit.getScheduler().runTask(XG7Scores.getPlugin(), () -> player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard()));
    }
}
