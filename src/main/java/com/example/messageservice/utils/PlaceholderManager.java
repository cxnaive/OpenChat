package com.example.messageservice.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import com.example.messageservice.MessageServicePlugin;

import java.lang.management.ManagementFactory;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlaceholderManager {

    private final MessageServicePlugin plugin;
    private final MiniMessage miniMessage;
    private final LegacyComponentSerializer legacySerializer;
    private final Map<String, Function<Player, String>> placeholders = new HashMap<>();
    private final Map<String, Function<Player, String>> customPlaceholders = new HashMap<>();

    private static final Pattern LEGACY_COLOR_PATTERN = Pattern.compile("&([0-9a-fk-or])");
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("%([a-zA-Z0-9_-]+)%");
    private static final Pattern HEX_COLOR_PATTERN = Pattern.compile("&#([0-9a-fA-F]{6})");

    public PlaceholderManager(MessageServicePlugin plugin) {
        this.plugin = plugin;
        this.miniMessage = MiniMessage.miniMessage();
        this.legacySerializer = LegacyComponentSerializer.builder()
                .character('&')
                .hexColors()
                .build();
        
        registerDefaultPlaceholders();
    }

    private void registerDefaultPlaceholders() {
        // 玩家相关占位符
        placeholders.put("player", player -> player != null ? player.getName() : "Console");
        placeholders.put("displayname", player -> player != null ? player.getDisplayName() : "Console");
        placeholders.put("uuid", player -> player != null ? player.getUniqueId().toString() : "N/A");
        placeholders.put("world", player -> player != null && player.getWorld() != null ? player.getWorld().getName() : "Unknown");
        placeholders.put("x", player -> player != null ? String.valueOf(player.getLocation().getBlockX()) : "0");
        placeholders.put("y", player -> player != null ? String.valueOf(player.getLocation().getBlockY()) : "0");
        placeholders.put("z", player -> player != null ? String.valueOf(player.getLocation().getBlockZ()) : "0");
        placeholders.put("ping", player -> {
            if (player == null) return "0";
            try {
                return String.valueOf(player.getPing());
            } catch (Exception e) {
                return "0";
            }
        });
        placeholders.put("health", player -> player != null ? String.format("%.1f", player.getHealth()) : "0");
        placeholders.put("maxhealth", player -> player != null ? String.format("%.1f", player.getMaxHealth()) : "0");
        placeholders.put("food", player -> player != null ? String.valueOf(player.getFoodLevel()) : "0");
        placeholders.put("level", player -> player != null ? String.valueOf(player.getLevel()) : "0");
        placeholders.put("exp", player -> player != null ? String.valueOf(player.getTotalExperience()) : "0");
        placeholders.put("gamemode", player -> player != null ? player.getGameMode().toString() : "Unknown");

        // 服务器相关占位符
        placeholders.put("online", player -> String.valueOf(Bukkit.getOnlinePlayers().size()));
        placeholders.put("max", player -> String.valueOf(Bukkit.getMaxPlayers()));
        placeholders.put("servername", player -> Bukkit.getServer().getName());
        placeholders.put("version", player -> Bukkit.getServer().getVersion());
        placeholders.put("bukkitversion", player -> Bukkit.getServer().getBukkitVersion());
        placeholders.put("motd", player -> Bukkit.getServer().getMotd());
        placeholders.put("tps", player -> {
            double[] tps = Bukkit.getServer().getTPS();
            return String.format("%.2f", tps[0]);
        });
        placeholders.put("uptime", player -> {
            long uptime = ManagementFactory.getRuntimeMXBean().getUptime();
            long days = TimeUnit.MILLISECONDS.toDays(uptime);
            long hours = TimeUnit.MILLISECONDS.toHours(uptime) % 24;
            long minutes = TimeUnit.MILLISECONDS.toMinutes(uptime) % 60;
            
            if (days > 0) {
                return String.format("%dd %dh %dm", days, hours, minutes);
            } else if (hours > 0) {
                return String.format("%dh %dm", hours, minutes);
            } else {
                return String.format("%dm", minutes);
            }
        });

        // 时间相关占位符
        placeholders.put("time", player -> new SimpleDateFormat("HH:mm:ss").format(new Date()));
        placeholders.put("date", player -> new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        placeholders.put("datetime", player -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
    }

    public String parse(String text, Player player) {
        if (text == null || text.isEmpty()) {
            return "";
        }

        String result = text;

        // 解析内置占位符
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(result);
        StringBuffer sb = new StringBuffer();
        
        while (matcher.find()) {
            String placeholder = matcher.group(1).toLowerCase();
            String replacement = "";

            // 检查自定义占位符
            if (customPlaceholders.containsKey(placeholder)) {
                replacement = customPlaceholders.get(placeholder).apply(player);
            }
            // 检查默认占位符
            else if (placeholders.containsKey(placeholder)) {
                replacement = placeholders.get(placeholder).apply(player);
            }

            // 转义特殊字符
            replacement = Matcher.quoteReplacement(replacement);
            matcher.appendReplacement(sb, replacement);
        }
        matcher.appendTail(sb);
        result = sb.toString();

        return result;
    }

    public Component parseToComponent(String text, Player player) {
        String parsed = parse(text, player);
        
        // 转换传统颜色代码为MiniMessage格式
        parsed = convertLegacyToMiniMessage(parsed);
        
        return miniMessage.deserialize(parsed);
    }

    public Component parseLegacy(String text, Player player) {
        String parsed = parse(text, player);
        
        // 将 &#RRGGBB 格式转换为 &x&R&R&G&G&B&B 格式（LegacyComponentSerializer支持的格式）
        parsed = convertHexToLegacyFormat(parsed);
        
        return legacySerializer.deserialize(parsed);
    }
    
    private String convertHexToLegacyFormat(String text) {
        if (text == null) return "";
        
        Matcher matcher = HEX_COLOR_PATTERN.matcher(text);
        StringBuffer sb = new StringBuffer();
        
        while (matcher.find()) {
            String hex = matcher.group(1);
            StringBuilder legacyFormat = new StringBuilder("&x");
            for (char c : hex.toCharArray()) {
                legacyFormat.append("&").append(c);
            }
            matcher.appendReplacement(sb, Matcher.quoteReplacement(legacyFormat.toString()));
        }
        matcher.appendTail(sb);
        
        return sb.toString();
    }

    public void registerPlaceholder(String key, Function<Player, String> resolver) {
        customPlaceholders.put(key.toLowerCase(), resolver);
    }

    public void unregisterPlaceholder(String key) {
        customPlaceholders.remove(key.toLowerCase());
    }

    private String convertLegacyToMiniMessage(String text) {
        if (text == null) return "";
        
        String result = text;
        
        // 转换十六进制颜色代码 &#RRGGBB -> <#RRGGBB>
        Matcher hexMatcher = HEX_COLOR_PATTERN.matcher(result);
        StringBuffer sb = new StringBuffer();
        while (hexMatcher.find()) {
            String hexColor = hexMatcher.group(1);
            hexMatcher.appendReplacement(sb, "<#" + hexColor + ">");
        }
        hexMatcher.appendTail(sb);
        result = sb.toString();
        
        // 转换 & 颜色代码
        result = result.replace("&0", "<black>")
                .replace("&1", "<dark_blue>")
                .replace("&2", "<dark_green>")
                .replace("&3", "<dark_aqua>")
                .replace("&4", "<dark_red>")
                .replace("&5", "<dark_purple>")
                .replace("&6", "<gold>")
                .replace("&7", "<gray>")
                .replace("&8", "<dark_gray>")
                .replace("&9", "<blue>")
                .replace("&a", "<green>")
                .replace("&b", "<aqua>")
                .replace("&c", "<red>")
                .replace("&d", "<light_purple>")
                .replace("&e", "<yellow>")
                .replace("&f", "<white>")
                .replace("&k", "<obfuscated>")
                .replace("&l", "<bold>")
                .replace("&m", "<strikethrough>")
                .replace("&n", "<underlined>")
                .replace("&o", "<italic>")
                .replace("&r", "<reset>");
        
        // 转换 § 颜色代码（传统颜色代码）
        result = result.replace("§0", "<black>")
                .replace("§1", "<dark_blue>")
                .replace("§2", "<dark_green>")
                .replace("§3", "<dark_aqua>")
                .replace("§4", "<dark_red>")
                .replace("§5", "<dark_purple>")
                .replace("§6", "<gold>")
                .replace("§7", "<gray>")
                .replace("§8", "<dark_gray>")
                .replace("§9", "<blue>")
                .replace("§a", "<green>")
                .replace("§b", "<aqua>")
                .replace("§c", "<red>")
                .replace("§d", "<light_purple>")
                .replace("§e", "<yellow>")
                .replace("§f", "<white>")
                .replace("§k", "<obfuscated>")
                .replace("§l", "<bold>")
                .replace("§m", "<strikethrough>")
                .replace("§n", "<underlined>")
                .replace("§o", "<italic>")
                .replace("§r", "<reset>");
        
        return result;
    }

    public String colorize(String text) {
        if (text == null) return "";
        
        Component component = legacySerializer.deserialize(text);
        return miniMessage.serialize(component);
    }

    public String stripColor(String text) {
        if (text == null) return "";
        
        String result = text;
        // 移除十六进制颜色代码
        result = HEX_COLOR_PATTERN.matcher(result).replaceAll("");
        // 移除传统颜色代码
        result = LEGACY_COLOR_PATTERN.matcher(result).replaceAll("");
        // 移除MiniMessage标签
        result = result.replaceAll("<[^>]+>", "");
        return result;
    }
}
