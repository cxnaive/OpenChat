package com.example.messageservice.services;

import com.example.messageservice.config.ConfigManager;
import com.example.messageservice.managers.AnnouncementManager;
import com.example.messageservice.models.Announcement;
import com.example.messageservice.schedulers.SchedulerAdapter;
import com.example.messageservice.utils.PlaceholderManager;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import com.example.messageservice.MessageServicePlugin;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class AnnouncementService {

    private final MessageServicePlugin plugin;
    private final ConfigManager configManager;
    private final AnnouncementManager announcementManager;
    private final PlaceholderManager placeholderManager;
    private final SchedulerAdapter schedulerAdapter;

    private final Queue<AnnouncementTask> announcementQueue = new ConcurrentLinkedQueue<>();
    private final Map<UUID, BossBar> activeBossBars = new HashMap<>();

    public AnnouncementService(MessageServicePlugin plugin, ConfigManager configManager, 
                              AnnouncementManager announcementManager, 
                              PlaceholderManager placeholderManager,
                              SchedulerAdapter schedulerAdapter) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.announcementManager = announcementManager;
        this.placeholderManager = placeholderManager;
        this.schedulerAdapter = schedulerAdapter;
        
        startQueueProcessor();
    }

    public void broadcastAnnouncement(String announcementId) {
        Optional<Announcement> optional = announcementManager.getAnnouncementWithRefresh(announcementId);
        if (optional.isEmpty()) {
            plugin.getLogger().warning("公告 '" + announcementId + "' 不存在");
            return;
        }

        Announcement announcement = optional.get();
        if (!announcement.isEnabled()) {
            plugin.getLogger().warning("公告 '" + announcementId + "' 已禁用");
            return;
        }

        // 检查公告是否对当前服务器生效
        String currentServer = plugin.getConfig().getString("cross-server.server-name", "unknown");
        List<String> servers = announcement.getServers();
        if (servers != null && !servers.isEmpty() && !servers.contains("*")) {
            if (!servers.contains(currentServer)) {
                return;
            }
        }

        // 检查全局冷却
        if (announcement.getCooldown() != null && announcement.getCooldown().getGlobal() > 0) {
            if (!announcementManager.checkGlobalCooldown(announcementId, announcement.getCooldown().getGlobal())) {
                plugin.getLogger().fine("公告 '" + announcementId + "' 处于全局冷却中");
                return;
            }
        }

        // 获取目标玩家
        List<Player> targets = getTargetPlayers(announcement);
        if (targets.isEmpty()) {
            plugin.getLogger().fine("公告 '" + announcementId + "' 没有目标玩家");
            return;
        }

        // 添加到队列
        boolean added = announcementQueue.offer(new AnnouncementTask(announcement, targets));
        if (!added) {
            plugin.getLogger().warning("公告 '" + announcementId + "' 添加到队列失败！");
        }

        // 设置全局冷却
        if (announcement.getCooldown() != null && announcement.getCooldown().getGlobal() > 0) {
            announcementManager.setGlobalCooldown(announcementId);
        }
    }

    /**
     * 执行公告（用于临时公告）
     * 不检查冷却和服务器列表，直接发送
     */
    public void executeAnnouncement(Announcement announcement) {
        executeAnnouncement(announcement, null);
    }

    /**
     * 执行公告（用于事件触发）
     * 支持传入触发玩家，用于 TRIGGER_PLAYER 目标类型
     */
    public void executeAnnouncement(Announcement announcement, Player triggerPlayer) {
        if (announcement == null) {
            return;
        }

        // 获取目标玩家（传入触发玩家）
        List<Player> targets = getTargetPlayers(announcement, triggerPlayer);
        if (targets.isEmpty()) {
            plugin.getLogger().fine("公告 '" + announcement.getId() + "' 没有目标玩家");
            return;
        }

        // 添加到队列
        boolean added = announcementQueue.offer(new AnnouncementTask(announcement, targets));
        if (!added) {
            plugin.getLogger().warning("公告 '" + announcement.getId() + "' 添加到队列失败！");
        }
    }

    public void sendAnnouncementToPlayer(String announcementId, Player player) {
        if (player == null || !player.isOnline()) {
            return;
        }

        Optional<Announcement> optional = announcementManager.getAnnouncementWithRefresh(announcementId);
        if (optional.isEmpty()) {
            plugin.getLogger().warning("公告 '" + announcementId + "' 不存在");
            return;
        }

        Announcement announcement = optional.get();
        if (!announcement.isEnabled()) {
            plugin.getLogger().warning("公告 '" + announcementId + "' 已禁用");
            return;
        }

        // 检查玩家冷却
        if (announcement.getCooldown() != null && announcement.getCooldown().getPerPlayer() > 0) {
            if (!announcementManager.checkPlayerCooldown(player.getUniqueId(), announcementId, announcement.getCooldown().getPerPlayer())) {
                plugin.getLogger().fine("公告 '" + announcementId + "' 对玩家 " + player.getName() + " 处于冷却中");
                return;
            }
        }

        // 使用玩家调度器确保在正确的线程上执行
        schedulerAdapter.runOnPlayer(player, () -> {
            sendToPlayer(announcement, player);
        });

        // 设置玩家冷却
        if (announcement.getCooldown() != null && announcement.getCooldown().getPerPlayer() > 0) {
            announcementManager.setPlayerCooldown(player.getUniqueId(), announcementId);
        }
    }

    public void sendImmediateMessage(String message, List<Player> targets, Announcement.DisplayType displayType) {
        if (targets == null || targets.isEmpty()) {
            return;
        }

        Announcement announcement = Announcement.builder()
                .id("immediate_" + System.currentTimeMillis())
                .enabled(true)
                .content(Collections.singletonList(message))
                .display(Announcement.DisplaySettings.builder()
                        .type(displayType != null ? displayType : Announcement.DisplayType.CHAT)
                        .build())
                .build();

        announcementQueue.offer(new AnnouncementTask(announcement, new ArrayList<>(targets)));
    }

    private void startQueueProcessor() {
        // 延迟1tick启动，然后每1tick检查一次队列（20 ticks = 1秒太慢了）
        schedulerAdapter.runTimerOnGlobal(() -> {
            processQueue();
        }, 1L, 1L);  // 改为每tick检查，减少延迟
    }

    private void processQueue() {
        int processed = 0;
        int maxPerTick = configManager.getMaxPerTick();

        while (!announcementQueue.isEmpty() && processed < maxPerTick) {
            AnnouncementTask task = announcementQueue.poll();
            if (task == null) continue;

            processAnnouncementTask(task);
            processed++;
        }
    }

    private void processAnnouncementTask(AnnouncementTask task) {
        Announcement announcement = task.announcement;
        List<Player> targets = task.targets;

        // 为每个玩家单独调度，确保在正确的区域线程上执行
        int onlineCount = 0;
        for (Player player : targets) {
            if (player.isOnline()) {
                onlineCount++;
                schedulerAdapter.runOnPlayer(player, () -> {
                    sendToPlayer(announcement, player);
                });
            }
        }
    }

    private void sendToPlayer(Announcement announcement, Player player) {
        try {
            List<String> content = announcement.getContent();
            if (content == null || content.isEmpty()) {
                plugin.getLogger().warning("公告 '" + announcement.getId() + "' 内容为空，无法发送");
                return;
            }

            // 解析占位符
            List<Component> parsedContent = content.stream()
                    .map(line -> placeholderManager.parseToComponent(line, player))
                    .collect(Collectors.toList());

            // 处理关键词（如 %click_able(显示文本,"命令")%）
            parsedContent = parsedContent.stream()
                    .map(this::processKeywords)
                    .collect(Collectors.toList());

            // 应用点击和悬停事件
            if (announcement.getActions() != null) {
                parsedContent = applyActions(parsedContent, announcement.getActions(), player);
            }

            // 根据显示类型发送
            Announcement.DisplayType displayType = announcement.getDisplay() != null ? 
                    announcement.getDisplay().getType() : Announcement.DisplayType.CHAT;

            switch (displayType) {
                case CHAT -> sendChatMessage(player, parsedContent);
                case TITLE -> sendTitle(player, parsedContent, announcement.getDisplay());
                case ACTIONBAR -> sendActionBar(player, parsedContent);
                case BOSSBAR -> sendBossBar(player, parsedContent, announcement.getDisplay());
                case TOAST -> sendToast(player, parsedContent, announcement.getDisplay());
                case COMBINED -> sendCombined(player, parsedContent, announcement.getDisplay());
            }

            // 播放音效
            if (announcement.getSound() != null && announcement.getSound().getSound() != null) {
                playSoundForPlayer(player, announcement.getSound());
            }

        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "发送公告给玩家 " + player.getName() + " 时出错", e);
        }
    }

    private void playSoundForPlayer(Player player, Announcement.SoundSettings soundSettings) {
        try {
            org.bukkit.Sound sound = org.bukkit.Sound.valueOf(soundSettings.getSound());
            float volume = soundSettings.getVolume();
            float pitch = soundSettings.getPitch();
            player.playSound(player.getLocation(), sound, volume, pitch);
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("无效的音效名称: " + soundSettings.getSound());
        }
    }

    private void sendChatMessage(Player player, List<Component> content) {
        for (Component line : content) {
            player.sendMessage(line);
        }
    }

    private void sendTitle(Player player, List<Component> content, Announcement.DisplaySettings settings) {
        if (content.isEmpty()) return;

        Component title = content.get(0);
        Component subtitle = content.size() > 1 ? content.get(1) : Component.empty();

        int fadeIn = settings != null ? settings.getFadeIn() : 10;
        int stay = settings != null ? settings.getStay() : 70;
        int fadeOut = settings != null ? settings.getFadeOut() : 20;

        Title.Times times = Title.Times.times(
                Duration.ofMillis(fadeIn * 50L),
                Duration.ofMillis(stay * 50L),
                Duration.ofMillis(fadeOut * 50L)
        );

        Title titleObj = Title.title(title, subtitle, times);
        player.showTitle(titleObj);
    }

    private void sendActionBar(Player player, List<Component> content) {
        if (content.isEmpty()) return;

        Component message = content.get(0);
        player.sendActionBar(message);
    }

    private void sendBossBar(Player player, List<Component> content, Announcement.DisplaySettings settings) {
        if (content.isEmpty()) return;

        Component message = content.get(0);
        
        String colorStr = settings != null ? settings.getColor() : "WHITE";
        BossBar.Color color = parseBossBarColor(colorStr);
        
        String styleStr = settings != null ? settings.getStyle() : "SOLID";
        BossBar.Overlay overlay = parseBossBarOverlay(styleStr);

        // 是否启用血量进度显示
        boolean showProgress = settings == null || settings.isBossbarProgress();

        BossBar bossBar = BossBar.bossBar(message, 1.0f, color, overlay);
        
        // 移除旧的BossBar
        BossBar oldBar = activeBossBars.remove(player.getUniqueId());
        if (oldBar != null) {
            player.hideBossBar(oldBar);
        }

        player.showBossBar(bossBar);
        activeBossBars.put(player.getUniqueId(), bossBar);

        // 自动隐藏
        int stay = settings != null ? settings.getStay() : 70;
        
        // 如果启用了进度显示，创建进度更新任务
        if (showProgress && stay > 0) {
            int updateInterval = 1; // 每tick更新一次
            int totalTicks = stay;
            
            for (int i = 1; i <= totalTicks; i++) {
                final int tick = i;
                schedulerAdapter.runDelayedOnPlayer(player, () -> {
                    float progress = 1.0f - ((float) tick / totalTicks);
                    if (progress < 0) progress = 0;
                    if (progress > 1) progress = 1;
                    bossBar.progress(progress);
                }, i);
            }
        }
        
        schedulerAdapter.runDelayedOnPlayer(player, () -> {
            player.hideBossBar(bossBar);
            activeBossBars.remove(player.getUniqueId());
        }, stay);
    }

    private void sendToast(Player player, List<Component> content, Announcement.DisplaySettings settings) {
        if (content.isEmpty()) return;

        Component title = content.get(0);
        Component description = content.size() > 1 ? content.get(1) : Component.empty();

        // 获取Toast类型配置
        String toastTypeStr = (settings != null && settings.getColor() != null) ? settings.getColor() : "TASK";
        ToastType toastType = ToastType.fromString(toastTypeStr);

        // 获取自定义图标
        String customIcon = (settings != null && settings.getToastIcon() != null) ? settings.getToastIcon() : null;

        // 使用进度系统发送Toast通知
        sendAdvancementToast(player, title, description, toastType, customIcon);
    }

    private void sendAdvancementToast(Player player, Component title, Component description, ToastType type, String customIcon) {
        try {
            // 创建临时命名空间ID - 使用更短的名称避免潜在问题
            String uniqueId = String.valueOf(System.currentTimeMillis()).substring(8) + 
                             player.getUniqueId().toString().substring(0, 4);
            org.bukkit.NamespacedKey key = new org.bukkit.NamespacedKey(cn.handyplus.chat.PlayerChat.INSTANCE, "toast_" + uniqueId);

            // 检查是否已存在同名进度
            org.bukkit.advancement.Advancement existing = Bukkit.getAdvancement(key);
            if (existing != null) {
                // 使用现有进度
                grantAndRevokeAdvancement(player, existing);
                return;
            }

            // 构建进度JSON
            String json = buildAdvancementJson(title, description, type, customIcon);
            
            plugin.getLogger().fine("创建Toast进度: " + key + " JSON: " + json);

            // 使用UnsafeValues加载进度
            org.bukkit.advancement.Advancement advancement = Bukkit.getUnsafe().loadAdvancement(key, json);
            
            if (advancement != null) {
                grantAndRevokeAdvancement(player, advancement);
                
                // 延迟清理进度定义
                schedulerAdapter.runDelayedOnGlobal(() -> {
                    try {
                        Bukkit.getUnsafe().removeAdvancement(key);
                    } catch (Exception e) {
                        plugin.getLogger().fine("清理Toast进度失败: " + e.getMessage());
                    }
                }, 40L); // 2秒后清理
            }

        } catch (Exception e) {
            // 如果Toast发送失败，记录错误并回退到发送聊天消息
            plugin.getLogger().warning("发送Toast失败: " + e.getMessage());
            e.printStackTrace();
            player.sendMessage(title);
        }
    }

    private void grantAndRevokeAdvancement(Player player, org.bukkit.advancement.Advancement advancement) {
        org.bukkit.advancement.AdvancementProgress progress = player.getAdvancementProgress(advancement);
        
        // 授予所有未完成的条件
        for (String criteria : progress.getRemainingCriteria()) {
            progress.awardCriteria(criteria);
        }

        // 延迟后撤销进度（隐藏Toast并清理）
        schedulerAdapter.runDelayedOnPlayer(player, () -> {
            try {
                for (String criteria : progress.getAwardedCriteria()) {
                    progress.revokeCriteria(criteria);
                }
            } catch (Exception e) {
                plugin.getLogger().fine("撤销Toast进度失败: " + e.getMessage());
            }
        }, 20L); // 1秒后清理
    }

    private String buildAdvancementJson(Component title, Component description, ToastType type, String customIcon) {
        // 将Component转换为JSON字符串
        String titleJson = net.kyori.adventure.text.serializer.json.JSONComponentSerializer.json().serialize(title);
        String descJson = net.kyori.adventure.text.serializer.json.JSONComponentSerializer.json().serialize(description);

        // 使用自定义图标或默认图标
        String iconId = customIcon != null ? customIcon : type.getIcon();

        // 构建进度JSON - 使用正确的Minecraft进度格式
        return "{" +
                "\"display\":{" +
                "\"icon\":{" +
                "\"id\":\"" + iconId + "\"" +
                "}," +
                "\"title\":" + titleJson + "," +
                "\"description\":" + descJson + "," +
                "\"frame\":\"" + type.getFrame() + "\"," +
                "\"show_toast\":true," +
                "\"announce_to_chat\":false," +
                "\"hidden\":true" +
                "}," +
                "\"criteria\":{" +
                "\"impossible\":{" +
                "\"trigger\":\"minecraft:impossible\"" +
                "}" +
                "}," +
                "\"requirements\":[[\"impossible\"]]" +
                "}";
    }

    public enum ToastType {
        TASK("minecraft:book", "task"),
        CHALLENGE("minecraft:diamond", "challenge"),
        GOAL("minecraft:emerald", "goal");

        private final String icon;
        private final String frame;

        ToastType(String icon, String frame) {
            this.icon = icon;
            this.frame = frame;
        }

        public String getIcon() {
            return icon;
        }

        public String getFrame() {
            return frame;
        }

        public static ToastType fromString(String str) {
            if (str == null || str.isEmpty()) {
                return TASK;
            }
            try {
                return valueOf(str.toUpperCase());
            } catch (IllegalArgumentException e) {
                return TASK;
            }
        }
    }

    private void sendCombined(Player player, List<Component> content, Announcement.DisplaySettings settings) {
        // 同时发送多种类型
        if (content.size() >= 1) {
            player.sendMessage(content.get(0));
        }
        if (content.size() >= 2) {
            player.sendActionBar(content.get(1));
        }
    }

    private List<Component> applyActions(List<Component> content, Announcement.ActionSettings actions, Player player) {
        if (actions == null) {
            return content;
        }

        List<Component> result = new ArrayList<>();
        for (Component line : content) {
            Component modified = line;

            // 应用点击事件
            if (actions.getOnClickCommand() != null && !actions.getOnClickCommand().isEmpty()) {
                String command = placeholderManager.parse(actions.getOnClickCommand(), player);
                if (!command.startsWith("/")) {
                    command = "/" + command;
                }
                modified = modified.clickEvent(ClickEvent.runCommand(command));
            }

            // 应用悬停事件
            if (actions.getOnHoverText() != null && !actions.getOnHoverText().isEmpty()) {
                Component hoverText = placeholderManager.parseToComponent(actions.getOnHoverText(), player);
                modified = modified.hoverEvent(HoverEvent.showText(hoverText));
            }

            result.add(modified);
        }

        return result;
    }

    private List<Player> getTargetPlayers(Announcement announcement) {
        return getTargetPlayers(announcement, null);
    }

    private List<Player> getTargetPlayers(Announcement announcement, Player triggerPlayer) {
        Announcement.TargetSettings target = announcement.getTarget();
        if (target == null) {
            return new ArrayList<>(Bukkit.getOnlinePlayers());
        }

        return switch (target.getType()) {
            case ALL -> new ArrayList<>(Bukkit.getOnlinePlayers());
            case WORLD -> getPlayersInWorld(target.getValue());
            case PERMISSION -> getPlayersWithPermission(target.getValue());
            case RANGE -> getPlayersInRange(target.getValue());
            case TRIGGER_PLAYER -> triggerPlayer != null && triggerPlayer.isOnline() 
                    ? Collections.singletonList(triggerPlayer) 
                    : new ArrayList<>();
            default -> new ArrayList<>(Bukkit.getOnlinePlayers());
        };
    }

    private List<Player> getPlayersInWorld(String worldName) {
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(world.getPlayers());
    }

    private List<Player> getPlayersWithPermission(String permission) {
        return Bukkit.getOnlinePlayers().stream()
                .filter(p -> p.hasPermission(permission))
                .collect(Collectors.toList());
    }

    private List<Player> getPlayersInRange(String rangeStr) {
        // 简化实现，返回所有在线玩家
        // 实际实现需要根据具体需求解析范围参数
        return new ArrayList<>(Bukkit.getOnlinePlayers());
    }

    private BossBar.Color parseBossBarColor(String color) {
        try {
            return BossBar.Color.valueOf(color.toUpperCase());
        } catch (IllegalArgumentException e) {
            return BossBar.Color.WHITE;
        }
    }

    private BossBar.Overlay parseBossBarOverlay(String style) {
        try {
            return BossBar.Overlay.valueOf(style.toUpperCase());
        } catch (IllegalArgumentException e) {
            return BossBar.Overlay.PROGRESS;
        }
    }

    public void cleanup() {
        // 清理所有BossBar
        for (Map.Entry<UUID, BossBar> entry : activeBossBars.entrySet()) {
            Player player = Bukkit.getPlayer(entry.getKey());
            if (player != null && player.isOnline()) {
                player.hideBossBar(entry.getValue());
            }
        }
        activeBossBars.clear();
        announcementQueue.clear();
    }

    /**
     * 获取公告缓存管理器
     */
    public AnnouncementManager getAnnouncementManager() {
        return announcementManager;
    }

    private record AnnouncementTask(Announcement announcement, List<Player> targets) {}

    /**
     * 处理消息中的关键词
     * 支持：%click_able(显示文本,"命令")% - 可点击按钮
     * 保留原始消息的颜色格式
     */
    private Component processKeywords(Component component) {
        // 使用 LegacyComponentSerializer 将 Component 转换为带颜色代码的字符串
        String text = LegacyComponentSerializer.legacySection().serialize(component);

        // 处理 %click_able(显示文本,"命令")% 格式
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(
            "%click_able\\(([^,]+),\\s*\"([^\"]+)\"\\)%"
        );
        java.util.regex.Matcher matcher = pattern.matcher(text);

        if (!matcher.find()) {
            // 没有关键词，返回原组件
            return component;
        }

        // 构建新的Component，处理关键词
        net.kyori.adventure.text.TextComponent.Builder builder = net.kyori.adventure.text.Component.text();
        int lastEnd = 0;

        matcher.reset();
        while (matcher.find()) {
            // 添加关键词前的文本（保留颜色代码）
            if (matcher.start() > lastEnd) {
                String beforeText = text.substring(lastEnd, matcher.start());
                // 使用 LegacyComponentSerializer 解析带 § 颜色代码的文本
                builder.append(LegacyComponentSerializer.legacySection().deserialize(beforeText));
            }

            String displayText = matcher.group(1);
            String command = matcher.group(2);

            // 解析显示文本中的颜色代码（如 &a, &l 等）
            Component clickableText = parseColorCodes(displayText)
                .clickEvent(ClickEvent.runCommand("/" + command))
                .hoverEvent(HoverEvent.showText(net.kyori.adventure.text.Component.text("§a点击执行: /" + command)));

            builder.append(clickableText);
            lastEnd = matcher.end();
        }

        // 添加剩余文本（保留颜色代码）
        if (lastEnd < text.length()) {
            String afterText = text.substring(lastEnd);
            builder.append(LegacyComponentSerializer.legacySection().deserialize(afterText));
        }

        return builder.build();
    }

    /**
     * 解析颜色代码（&a, &b, &l 等）为Component
     * 使用 LegacyComponentSerializer 正确解析 § 颜色代码
     */
    private Component parseColorCodes(String text) {
        // 先将 & 颜色代码转换为 § 颜色代码
        String coloredText = text
            .replace("&0", "§0").replace("&1", "§1").replace("&2", "§2").replace("&3", "§3")
            .replace("&4", "§4").replace("&5", "§5").replace("&6", "§6").replace("&7", "§7")
            .replace("&8", "§8").replace("&9", "§9").replace("&a", "§a").replace("&b", "§b")
            .replace("&c", "§c").replace("&d", "§d").replace("&e", "§e").replace("&f", "§f")
            .replace("&k", "§k").replace("&l", "§l").replace("&m", "§m").replace("&n", "§n")
            .replace("&o", "§o").replace("&r", "§r");

        // 使用 LegacyComponentSerializer 正确解析 § 颜色代码
        return LegacyComponentSerializer.legacySection().deserialize(coloredText);
    }
}
