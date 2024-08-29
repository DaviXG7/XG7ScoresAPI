package com.xg7plugins.xg7entities.api.utils;

import com.xg7plugins.xg7entities.api.Score;
import com.xg7plugins.xg7entities.api.ScoreManager;
import com.xg7plugins.xg7entities.api.XG7Scores;
import com.xg7plugins.xg7entities.api.scores.ActionBar;
import lombok.Getter;
import lombok.SneakyThrows;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
public class Text {

    private Component component;
    private String rawText;
    private boolean isAction;
    private boolean isCenter;

    public Text(String text, PixelsSize centerSize) {

        this.rawText = ChatColor.translateAlternateColorCodes('&', text);

        this.isAction = rawText.startsWith("[ACTION] ");
        this.isCenter = rawText.startsWith("[CENTER] ");

        if (isAction || isCenter) this.rawText = rawText.substring(9);
        if (isAction && isCenter) this.rawText = rawText.substring(9);

        this.component = MiniMessage.miniMessage().deserialize(rawText);

        if (isCenter) {
            this.rawText = getCentralizedText(centerSize.getPixels(), rawText) + PlainTextComponentSerializer.plainText().serialize(component);
            this.component = Component.text(rawText).append(component);
        }


    }
    public String getText() {
        return PlainTextComponentSerializer.plainText().serialize(component);
    }
    @Contract("_, _ -> new")
    public static @NotNull Text format(String text, PixelsSize sizeIfIsCenter) {
        return new Text(text, sizeIfIsCenter);
    }
    @Contract("_ -> new")
    public static @NotNull Text format(String text) {
        return new Text(text, PixelsSize.CHAT);
    }

    public void send(CommandSender sender) {

        if (sender instanceof Player && isAction) {
            sendActionBar(((Player) sender));
            return;
        }
        sender.spigot().sendMessage((BaseComponent) component);
    }
    public Text setPlaceholders(Player player) {
        this.component = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null ? MiniMessage.miniMessage().deserialize(PlaceholderAPI.setPlaceholders(player, rawText)) : component;
        return this;
    }
    @SneakyThrows
    public void sendScoreActionBar(Player player) {
        if (XG7Scores.getVersion() < 8) return;

        if (Integer.parseInt(Bukkit.getServer().getVersion().split("\\.")[1].replace(")", "")) >= 9) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(getText()));
            return;
        }

        Class<?> craftPlayerClass = NMSUtil.getCraftBukkitClass("entity.CraftPlayer");
        Object craftPlayer = craftPlayerClass.cast(player);

        Class<?> packetPlayOutChatClass = NMSUtil.getNMSClass("PacketPlayOutChat");
        Class<?> iChatBaseComponentClass = NMSUtil.getNMSClass("IChatBaseComponent");
        Class<?> chatComponentTextClass = NMSUtil.getNMSClass("ChatComponentText");

        Object chatComponent = chatComponentTextClass.getConstructor(String.class).newInstance(PlainTextComponentSerializer.plainText().serialize(component));
        Object packet = packetPlayOutChatClass.getConstructor(iChatBaseComponentClass, byte.class)
                .newInstance(chatComponent, (byte) 2);

        Object craftPlayerHandle = craftPlayerClass.getMethod("getHandle").invoke(craftPlayer);
        Object playerConnection = craftPlayerHandle.getClass().getField("playerConnection").get(craftPlayerHandle);
        playerConnection.getClass().getMethod("sendPacket", NMSUtil.getNMSClass("Packet")).invoke(playerConnection, packet);

    }

    @SneakyThrows
    public void sendActionBar(Player player) {

        if (XG7Scores.getVersion() < 8) return;

        ScoreManager.getSendActionBlackList().add(player.getUniqueId());

        sendScoreActionBar(player);

        Bukkit.getScheduler().runTaskLater(XG7Scores.getPlugin(), () -> ScoreManager.getSendActionBlackList().remove(player.getUniqueId()),60L);

    }

    public static long convertToMilliseconds(String timeStr) {
        long milliseconds = 0;
        Pattern pattern = Pattern.compile("(\\d+)([SMHD])");
        Matcher matcher = pattern.matcher(timeStr.toUpperCase());

        while (matcher.find()) {
            long value = Long.parseLong(matcher.group(1));
            String unit = matcher.group(2);

            switch (unit) {
                case "S":
                    milliseconds += value * 1000;
                    break;
                case "M":
                    milliseconds += value * 60000;
                    break;
                case "H":
                    milliseconds += value * 3600000;
                    break;
                case "D":
                    milliseconds += value * 86400000;
                    break;
                default:
                    Log.severe("Invalid time unit: " + unit);
            }
        }

        return milliseconds;
    }

    public static String getCentralizedText(int pixels, @NotNull String text) {

        int textWidht = 0;
        boolean cCode = false;
        boolean isBold = false;
        boolean isrgb = false;
        int rgbCount = 0;
        int cCodeCount = 0;
        int rgbToAdd = 0;
        for (char c : text.toCharArray()) {
            if (isrgb) {
                if (rgbCount == 6) {
                    isrgb = false;
                    continue;
                }
                if ("0123456789aAbBcCdDeEfF".contains(String.valueOf(c))) {
                    rgbToAdd = getCharSize(c, isBold);
                    rgbCount++;
                    continue;
                }
                rgbCount = 0;
                textWidht += rgbToAdd;
                continue;
            }
            if (c == '&') {
                cCode = true;
                cCodeCount++;
                continue;
            }
            if (cCode && net.md_5.bungee.api.ChatColor.ALL_CODES.contains(c + "")) {
                cCode = false;
                cCodeCount = 0;
                isBold = c == 'l' || c == 'L';
                continue;
            }
            if (cCode) {
                if (c == '#') {
                    cCode = false;
                    isrgb = true;
                    continue;
                }
                while (cCodeCount != 0) {
                    cCodeCount--;
                    textWidht += getCharSize('&', isBold);
                }
            }
            textWidht += getCharSize(c, isBold);
        }

        textWidht /= 2;

        if (textWidht > pixels) return text;

        StringBuilder builder = new StringBuilder();

        int compensated = 0;

        while (compensated < pixels - textWidht) {
            builder.append(ChatColor.COLOR_CHAR + "r ");
            compensated += 4;
        }

        return builder.toString();

    }

    private static int getCharSize(char c, boolean isBold) {
        String[] chars = new String[]{"~@", "1234567890ABCDEFGHJKLMNOPQRSTUVWXYZabcedjhmnopqrsuvxwyz/\\+=-_^?&%$#", "{}fk*\"<>()", "It[] ", "'l`", "!|:;,.i", "¨´"};
        for (int i = 0; i < chars.length; i++) {
            if (chars[i].contains(String.valueOf(c))) return isBold && c != ' ' ? 8 - i : 7 - i;
        }

        return 4;
    }

    @Getter
    public enum PixelsSize {

        CHAT(157),
        MOTD(127),
        INV(75);

        private final int pixels;

        PixelsSize (int pixels) {
            this.pixels = pixels;
        }


    }

}
