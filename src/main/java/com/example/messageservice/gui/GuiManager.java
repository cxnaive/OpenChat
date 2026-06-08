package com.example.messageservice.gui;

import com.example.messageservice.managers.AnnouncementManager;
import com.example.messageservice.models.Announcement;
import com.example.messageservice.schedulers.SchedulerAdapter;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import com.example.messageservice.MessageServicePlugin;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public class GuiManager implements Listener {

    private final MessageServicePlugin plugin;
    private final AnnouncementManager announcementManager;
    private SchedulerAdapter schedulerAdapter;

    private final Map<UUID, GuiContext> playerContexts = new ConcurrentHashMap<>();
    private final Map<UUID, ChatInputContext> chatInputContexts = new ConcurrentHashMap<>();
    
    // GUI 上下文时间戳，用于过期清理
    private final Map<UUID, Long> contextTimestamps = new ConcurrentHashMap<>();
    
    // PersistentDataContainer 的 NamespacedKey
    private static final String ANNOUNCEMENT_ID_KEY = "announcement_id";
    
    // 上下文过期时间（毫秒）：10分钟
    private static final long CONTEXT_EXPIRY_TIME = 10 * 60 * 1000;
    // 清理间隔（tick）：5分钟 = 6000 tick
    private static final long CLEANUP_INTERVAL = 6000;

    public enum GuiType {
        MAIN_MENU,
        ANNOUNCEMENT_LIST,
        EDIT_ANNOUNCEMENT,
        SELECT_DISPLAY_TYPE,
        SELECT_TARGET_TYPE,
        SELECT_TRIGGER_TYPE,
        EDIT_CONTENT,
        EDIT_PRIORITY,
        EDIT_COOLDOWN,
        CREATE_ANNOUNCEMENT,
        DELETE_CONFIRM,
        EDIT_BOSSBAR_SETTINGS,
        EDIT_TITLE_SETTINGS,
        EDIT_TOAST_SETTINGS,
        EDIT_TOAST_ICON,
        EDIT_SOUND_SETTINGS,
        SERVER_SELECTOR,
        EDIT_SCHEDULE_SETTINGS,
        EDIT_COMMAND_SETTINGS,
        CRON_PRESETS
    }

    public enum ChatInputType {
        ANNOUNCEMENT_ID,
        ANNOUNCEMENT_ID_EDIT,
        CONTENT_LINE,
        PRIORITY,
        GLOBAL_COOLDOWN,
        PLAYER_COOLDOWN,
        TARGET_VALUE,
        BOSSBAR_STAY,
        TITLE_FADEIN,
        TITLE_STAY,
        TITLE_FADEOUT,
        TOAST_ICON,
        SERVER_LIST,
        SOUND_NAME,
        SCHEDULE_TIME,
        CRON_EXPRESSION,
        TRIGGER_COMMAND
    }

    @RequiredArgsConstructor
    public static class GuiContext {
        private final GuiType type;
        private String announcementId;
        private Announcement editingAnnouncement;
        private int page = 0;
        private int contentLineIndex = -1;
    }

    public static class ChatInputContext {
        private final ChatInputType type;
        private final GuiType returnGui;
        private Announcement editingAnnouncement;
        private int lineIndex = -1;

        public ChatInputContext(ChatInputType type, GuiType returnGui) {
            this.type = type;
            this.returnGui = returnGui;
        }

        public ChatInputContext(ChatInputType type, GuiType returnGui, Announcement editingAnnouncement) {
            this.type = type;
            this.returnGui = returnGui;
            this.editingAnnouncement = editingAnnouncement;
        }
    }

    public void initialize(SchedulerAdapter schedulerAdapter) {
        this.schedulerAdapter = schedulerAdapter;
        Bukkit.getPluginManager().registerEvents(this, cn.handyplus.chat.PlayerChat.INSTANCE);
        
        // 启动定期清理任务
        startCleanupTask();
    }
    
    /**
     * 启动 GUI 上下文定期清理任务
     * 参照 FoliaShop 的资源管理实现
     */
    private void startCleanupTask() {
        if (schedulerAdapter != null) {
            schedulerAdapter.runTimerOnGlobal(() -> {
                cleanupExpiredContexts();
            }, CLEANUP_INTERVAL, CLEANUP_INTERVAL);
            plugin.getLogger().info("GUI 上下文清理任务已启动");
        }
    }
    
    /**
     * 清理过期的 GUI 上下文
     */
    private void cleanupExpiredContexts() {
        long now = System.currentTimeMillis();
        int cleanedCount = 0;
        
        // 清理过期的 GUI 上下文
        Iterator<Map.Entry<UUID, Long>> iterator = contextTimestamps.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, Long> entry = iterator.next();
            UUID playerId = entry.getKey();
            long timestamp = entry.getValue();
            
            // 检查是否过期
            if (now - timestamp > CONTEXT_EXPIRY_TIME) {
                // 检查玩家是否在线
                Player player = Bukkit.getPlayer(playerId);
                if (player == null || !player.isOnline()) {
                    // 玩家不在线，清理上下文
                    playerContexts.remove(playerId);
                    chatInputContexts.remove(playerId);
                    iterator.remove();
                    cleanedCount++;
                }
            }
        }
        
        if (cleanedCount > 0) {
            plugin.getLogger().fine("已清理 " + cleanedCount + " 个过期的 GUI 上下文");
        }
    }
    
    /**
     * 更新上下文时间戳
     */
    private void updateContextTimestamp(UUID playerId) {
        contextTimestamps.put(playerId, System.currentTimeMillis());
    }
    
    /**
     * 清理指定玩家的所有上下文
     */
    private void cleanupPlayerContexts(UUID playerId) {
        playerContexts.remove(playerId);
        chatInputContexts.remove(playerId);
        contextTimestamps.remove(playerId);
    }

    public void openMainMenu(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, 
                Component.text("§2MessageService - 主菜单"));

        gui.setItem(11, createItem(Material.BOOK, "§a公告列表", 
                "§7查看和管理所有公告", "", "§e点击打开公告列表"));

        gui.setItem(13, createItem(Material.WRITABLE_BOOK, "§e新建公告", 
                "§7创建一个新的公告", "", "§e点击开始创建"));

        gui.setItem(15, createItem(Material.CLOCK, "§b重载配置", 
                "§7重新加载所有配置", "", "§e点击重载"));

        gui.setItem(26, createItem(Material.BARRIER, "§c关闭", 
                "§7关闭菜单"));

        fillEmptySlots(gui, Material.GRAY_STAINED_GLASS_PANE);
        player.openInventory(gui);
        playerContexts.put(player.getUniqueId(), new GuiContext(GuiType.MAIN_MENU));
        updateContextTimestamp(player.getUniqueId());
    }

    public void openAnnouncementList(Player player, int page) {
        List<Announcement> announcements = announcementManager.getAllAnnouncements();
        int totalPages = (int) Math.ceil(announcements.size() / 45.0);

        Inventory gui = Bukkit.createInventory(null, 54,
                Component.text("§2公告列表 - 页 " + (page + 1) + "/" + Math.max(1, totalPages)));

        int startIndex = page * 45;
        int endIndex = Math.min(startIndex + 45, announcements.size());

        for (int i = startIndex; i < endIndex; i++) {
            Announcement announcement = announcements.get(i);
            Material material = announcement.isEnabled() ? Material.LIME_WOOL : Material.RED_WOOL;
            String status = announcement.isEnabled() ? "§a已启用" : "§c已禁用";

            List<String> lore = new ArrayList<>();
            lore.add("§7状态: " + status);
            lore.add("§7类型: §f" + getDisplayTypeName(announcement));
            lore.add("§7优先级: §f" + announcement.getPriority());
            lore.add("§7内容行数: §f" + (announcement.getContent() != null ? announcement.getContent().size() : 0));
            lore.add("");
            lore.add("§e左键 §7编辑");
            lore.add("§e右键 §7切换启用/禁用");
            lore.add("§eShift+右键 §7删除");
            lore.add("§e中键 §7预览");

            gui.setItem(i - startIndex, createAnnouncementItem(material, announcement.getId(), lore.toArray(new String[0])));
        }

        if (page > 0) {
            gui.setItem(45, createItem(Material.ARROW, "§e上一页", "§7点击返回上一页"));
        }

        gui.setItem(49, createItem(Material.BOOK, "§a返回主菜单", "§7点击返回"));

        if (page < totalPages - 1) {
            gui.setItem(53, createItem(Material.ARROW, "§e下一页", "§7点击前往下一页"));
        }

        fillEmptySlots(gui, Material.GRAY_STAINED_GLASS_PANE);
        player.openInventory(gui);
        GuiContext context = new GuiContext(GuiType.ANNOUNCEMENT_LIST);
        context.page = page;
        playerContexts.put(player.getUniqueId(), context);
        updateContextTimestamp(player.getUniqueId());
        playOpenSound(player);
    }

    public void openEditAnnouncement(Player player, Announcement announcement) {
        Inventory gui = Bukkit.createInventory(null, 54,
                Component.text("§2编辑公告: " + announcement.getId()));

        gui.setItem(10, createItem(Material.NAME_TAG, "§e公告ID",
                "§7当前ID: §f" + announcement.getId(), "", "§e点击修改ID"));

        Material statusMaterial = announcement.isEnabled() ? Material.LIME_DYE : Material.GRAY_DYE;
        String statusText = announcement.isEnabled() ? "§a已启用" : "§c已禁用";
        gui.setItem(11, createItem(statusMaterial, "§e启用状态", 
                "§7当前: " + statusText, "", "§e点击切换"));

        gui.setItem(12, createItem(Material.EXPERIENCE_BOTTLE, "§e优先级", 
                "§7当前: §f" + announcement.getPriority(), "", "§e点击修改"));

        String displayType = getDisplayTypeName(announcement);
        gui.setItem(13, createItem(Material.ITEM_FRAME, "§e显示类型", 
                "§7当前: §f" + displayType, "", "§e点击修改"));

        String targetType = getTargetTypeName(announcement);
        String targetValue = announcement.getTarget() != null ? announcement.getTarget().getValue() : "*";
        gui.setItem(14, createItem(Material.COMPASS, "§e目标设置", 
                "§7类型: §f" + targetType, "§7值: §f" + targetValue, "", "§e点击修改"));

        int contentLines = announcement.getContent() != null ? announcement.getContent().size() : 0;
        gui.setItem(15, createItem(Material.WRITABLE_BOOK, "§e编辑内容", 
                "§7当前行数: §f" + contentLines, "", "§e点击编辑公告内容"));

        String triggerType = getTriggerTypeName(announcement);
        gui.setItem(16, createItem(Material.CLOCK, "§e触发方式", 
                "§7当前: §f" + triggerType, "", "§e点击修改"));

        int globalCooldown = announcement.getCooldown() != null ? announcement.getCooldown().getGlobal() : 0;
        int playerCooldown = announcement.getCooldown() != null ? announcement.getCooldown().getPerPlayer() : 0;
        gui.setItem(19, createItem(Material.CLOCK, "§e冷却设置",
                "§7全局冷却: §f" + globalCooldown + "秒",
                "§7玩家冷却: §f" + playerCooldown + "秒",
                "", "§e点击修改"));

        // 生效区服设置
        List<String> servers = announcement.getServers();
        String serversText;
        if (servers == null || servers.isEmpty() || servers.contains("*")) {
            serversText = "§a所有区服";
        } else {
            serversText = "§f" + String.join(", ", servers);
        }
        gui.setItem(21, createItem(Material.COMPASS, "§e生效区服",
                "§7当前: " + serversText,
                "", "§e点击修改生效区服"));

        // 声音设置
        Announcement.SoundSettings sound = announcement.getSound();
        String soundText;
        if (sound == null || sound.getSound() == null || sound.getSound().isEmpty()) {
            soundText = "§c未设置";
        } else {
            soundText = "§a" + sound.getSound();
        }
        gui.setItem(22, createItem(Material.NOTE_BLOCK, "§e声音设置",
                "§7当前: " + soundText,
                sound != null ? "§7音量: §f" + sound.getVolume() : "",
                sound != null ? "§7音调: §f" + sound.getPitch() : "",
                "", "§e点击修改声音设置"));

        // 根据触发类型添加参数设置按钮（放在声音设置后面）
        Announcement.TriggerType currentTriggerType = announcement.getTrigger() != null ? 
                announcement.getTrigger().getType() : Announcement.TriggerType.MANUAL;
        
        switch (currentTriggerType) {
            case SCHEDULE -> {
                String schedule = announcement.getTrigger().getSchedule() != null && !announcement.getTrigger().getSchedule().isEmpty() ?
                        announcement.getTrigger().getSchedule() : "未设置";
                gui.setItem(23, createItem(Material.CLOCK, "§6定时参数",
                        "§7时间间隔: §f" + schedule,
                        "",
                        "§e点击修改参数"));
            }
            case COMMAND -> {
                String cmd = announcement.getTrigger().getCommand() != null && !announcement.getTrigger().getCommand().isEmpty() ?
                        announcement.getTrigger().getCommand() : "未设置";
                gui.setItem(23, createItem(Material.COMMAND_BLOCK, "§6命令参数",
                        "§7触发命令: §f" + cmd,
                        "",
                        "§e点击修改参数"));
            }
            case EVENT -> {
                List<String> events = announcement.getTrigger().getEvents() != null && !announcement.getTrigger().getEvents().isEmpty() ?
                        announcement.getTrigger().getEvents() : Collections.singletonList("未设置");
                gui.setItem(23, createItem(Material.TRIPWIRE_HOOK, "§6事件参数",
                        "§7触发事件: §f" + String.join(", ", events),
                        "",
                        "§e点击修改参数"));
            }
            case FIRST_JOIN -> {
                gui.setItem(23, createItem(Material.PLAYER_HEAD, "§6首次登录",
                        "§7触发条件: §f新玩家首次加入服务器",
                        "§7无需额外配置",
                        "",
                        "§a已启用"));
            }
            default -> gui.setItem(23, createItem(Material.BARRIER, "§7无参数", "§7手动触发无需配置参数"));
        }

        // 根据显示类型添加参数设置按钮
        Announcement.DisplayType currentDisplayType = announcement.getDisplay() != null ? 
                announcement.getDisplay().getType() : Announcement.DisplayType.CHAT;
        
        switch (currentDisplayType) {
            case BOSSBAR -> {
                String bbColor = announcement.getDisplay().getColor() != null ? announcement.getDisplay().getColor() : "WHITE";
                String bbStyle = announcement.getDisplay().getStyle() != null ? announcement.getDisplay().getStyle() : "SOLID";
                int bbStay = announcement.getDisplay().getStay() > 0 ? announcement.getDisplay().getStay() : 70;
                boolean bbProgress = announcement.getDisplay() == null || announcement.getDisplay().isBossbarProgress();
                String progressText = bbProgress ? "§a启用" : "§c禁用";
                gui.setItem(20, createItem(Material.DRAGON_HEAD, "§6BossBar参数",
                        "§7颜色: §f" + bbColor,
                        "§7样式: §f" + bbStyle,
                        "§7时长: §f" + bbStay + " tick",
                        "§7进度: " + progressText,
                        "", "§e点击修改参数"));
            }
            case TITLE, COMBINED -> {
                int fadeIn = announcement.getDisplay().getFadeIn() > 0 ? announcement.getDisplay().getFadeIn() : 10;
                int stay = announcement.getDisplay().getStay() > 0 ? announcement.getDisplay().getStay() : 70;
                int fadeOut = announcement.getDisplay().getFadeOut() > 0 ? announcement.getDisplay().getFadeOut() : 20;
                gui.setItem(20, createItem(Material.OAK_SIGN, "§6Title参数",
                        "§7淡入: §f" + fadeIn + " tick",
                        "§7停留: §f" + stay + " tick",
                        "§7淡出: §f" + fadeOut + " tick",
                        "", "§e点击修改参数"));
            }
            case TOAST -> {
                String toastType = announcement.getDisplay().getColor() != null ? announcement.getDisplay().getColor() : "TASK";
                String toastIcon = announcement.getDisplay().getToastIcon() != null ? announcement.getDisplay().getToastIcon() : "默认";
                gui.setItem(20, createItem(Material.BREAD, "§6Toast参数",
                        "§7类型: §f" + toastType,
                        "§7图标: §f" + toastIcon,
                        "", "§e点击修改参数"));
            }
            default -> gui.setItem(20, createItem(Material.BARRIER, "§7无参数", "§7当前类型无需配置参数"));
        }

        gui.setItem(47, createItem(Material.LIME_WOOL, "§a保存更改", 
                "§7点击保存所有修改"));

        gui.setItem(49, createItem(Material.ENDER_EYE, "§b预览公告", 
                "§7点击预览效果"));

        gui.setItem(51, createItem(Material.ARROW, "§c返回列表", 
                "§7点击返回公告列表"));

        fillEmptySlots(gui, Material.GRAY_STAINED_GLASS_PANE);
        player.openInventory(gui);
        GuiContext context = new GuiContext(GuiType.EDIT_ANNOUNCEMENT);
        context.announcementId = announcement.getId();
        context.editingAnnouncement = announcement;
        playerContexts.put(player.getUniqueId(), context);
    }

    public void openCreateAnnouncement(Player player) {
        player.closeInventory();
        player.sendMessage(Component.text("§e请在聊天栏输入新公告的ID"));
        player.sendMessage(Component.text("§7输入 §ccancel §7取消创建"));
        
        chatInputContexts.put(player.getUniqueId(), new ChatInputContext(
                ChatInputType.ANNOUNCEMENT_ID, 
                GuiType.MAIN_MENU, 
                null
        ));
    }

    public void openDisplayTypeSelector(Player player, Announcement announcement) {
        Inventory gui = Bukkit.createInventory(null, 27,
                Component.text("§2选择显示类型"));

        Announcement.DisplayType currentType = announcement.getDisplay() != null ?
                announcement.getDisplay().getType() : Announcement.DisplayType.CHAT;

        gui.setItem(10, createTypeItem(Material.PAPER, "CHAT", currentType == Announcement.DisplayType.CHAT,
                "§7普通聊天消息", "§7显示在聊天栏"));

        gui.setItem(11, createTypeItem(Material.OAK_SIGN, "TITLE", currentType == Announcement.DisplayType.TITLE,
                "§7屏幕中央标题", "§7大标题显示"));

        gui.setItem(12, createTypeItem(Material.EXPERIENCE_BOTTLE, "ACTIONBAR", currentType == Announcement.DisplayType.ACTIONBAR,
                "§7快捷栏上方", "§7显示在经验条上方"));

        gui.setItem(13, createTypeItem(Material.DRAGON_HEAD, "BOSSBAR", currentType == Announcement.DisplayType.BOSSBAR,
                "§7Boss血条", "§7顶部血条显示"));

        gui.setItem(14, createTypeItem(Material.BOOK, "TOAST", currentType == Announcement.DisplayType.TOAST,
                "§7右上角弹窗", "§7类似进度达成提示"));

        gui.setItem(15, createTypeItem(Material.CHEST, "COMBINED", currentType == Announcement.DisplayType.COMBINED,
                "§7组合显示", "§7同时显示多种类型"));

        gui.setItem(26, createItem(Material.ARROW, "§c返回", "§7返回编辑界面"));

        fillEmptySlots(gui, Material.GRAY_STAINED_GLASS_PANE);
        player.openInventory(gui);
        GuiContext context = new GuiContext(GuiType.SELECT_DISPLAY_TYPE);
        context.editingAnnouncement = announcement;
        playerContexts.put(player.getUniqueId(), context);
    }

    public void openTargetTypeSelector(Player player, Announcement announcement) {
        Inventory gui = Bukkit.createInventory(null, 36,
                Component.text("§2选择目标类型"));

        Announcement.TargetType currentType = announcement.getTarget() != null ?
                announcement.getTarget().getType() : Announcement.TargetType.ALL;

        gui.setItem(10, createTypeItem(Material.GLOBE_BANNER_PATTERN, "ALL", currentType == Announcement.TargetType.ALL,
                "§7所有玩家", "§7发送给所有在线玩家"));

        gui.setItem(11, createTypeItem(Material.GRASS_BLOCK, "WORLD", currentType == Announcement.TargetType.WORLD,
                "§7指定世界", "§7只在特定世界显示"));

        gui.setItem(12, createTypeItem(Material.GOLDEN_HELMET, "PERMISSION", currentType == Announcement.TargetType.PERMISSION,
                "§7权限组", "§7需要特定权限"));

        gui.setItem(13, createTypeItem(Material.COMPASS, "RANGE", currentType == Announcement.TargetType.RANGE,
                "§7范围", "§7指定范围内的玩家"));

        gui.setItem(14, createTypeItem(Material.PLAYER_HEAD, "TRIGGER_PLAYER", currentType == Announcement.TargetType.TRIGGER_PLAYER,
                "§7触发玩家", "§7仅发送给触发事件的玩家", "§7适用于EVENT/FIRST_JOIN触发"));

        gui.setItem(35, createItem(Material.ARROW, "§c返回", "§7返回编辑界面"));

        fillEmptySlots(gui, Material.GRAY_STAINED_GLASS_PANE);
        player.openInventory(gui);
        GuiContext context = new GuiContext(GuiType.SELECT_TARGET_TYPE);
        context.editingAnnouncement = announcement;
        playerContexts.put(player.getUniqueId(), context);
    }

    public void openTriggerTypeSelector(Player player, Announcement announcement) {
        Inventory gui = Bukkit.createInventory(null, 27,
                Component.text("§2选择触发方式"));

        Announcement.TriggerType currentType = announcement.getTrigger() != null ?
                announcement.getTrigger().getType() : Announcement.TriggerType.MANUAL;

        gui.setItem(10, createTypeItem(Material.REDSTONE_BLOCK, "MANUAL", currentType == Announcement.TriggerType.MANUAL,
                "§7手动触发", "§7通过命令手动发送"));

        gui.setItem(11, createTypeItem(Material.CLOCK, "SCHEDULE", currentType == Announcement.TriggerType.SCHEDULE,
                "§7定时触发", "§7按时间间隔或Cron表达式发送"));

        gui.setItem(12, createTypeItem(Material.COMMAND_BLOCK, "COMMAND", currentType == Announcement.TriggerType.COMMAND,
                "§7命令触发", "§7玩家执行命令时发送"));

        gui.setItem(13, createTypeItem(Material.TRIPWIRE_HOOK, "EVENT", currentType == Announcement.TriggerType.EVENT,
                "§7事件触发", "§7游戏事件发生时发送"));

        gui.setItem(14, createTypeItem(Material.PLAYER_HEAD, "FIRST_JOIN", currentType == Announcement.TriggerType.FIRST_JOIN,
                "§7首次登录触发", "§7新玩家首次加入时发送"));

        gui.setItem(26, createItem(Material.ARROW, "§c返回", "§7返回编辑界面"));

        fillEmptySlots(gui, Material.GRAY_STAINED_GLASS_PANE);
        player.openInventory(gui);
        GuiContext context = new GuiContext(GuiType.SELECT_TRIGGER_TYPE);
        context.editingAnnouncement = announcement;
        playerContexts.put(player.getUniqueId(), context);
    }

    public void openContentEditor(Player player, Announcement announcement, int page) {
        List<String> content = announcement.getContent() != null ? announcement.getContent() : new ArrayList<>();
        int linesPerPage = 18; // slot 0~17
        int totalPages = Math.max(1, (int) Math.ceil(content.size() / (double) linesPerPage));

        Inventory gui = Bukkit.createInventory(null, 36,
                Component.text("§2编辑内容 - 页 " + (page + 1) + "/" + totalPages));

        int startIndex = page * linesPerPage;
        int endIndex = Math.min(startIndex + linesPerPage, content.size());

        for (int i = startIndex; i < endIndex; i++) {
            String line = content.get(i);
            String preview = line.length() > 30 ? line.substring(0, 30) + "..." : line;

            gui.setItem(i - startIndex, createItem(Material.PAPER, "§f行 " + (i + 1),
                    "§7" + preview, "", "§e左键 §7编辑", "§e右键 §7删除", "§eShift+左键 §7上移", "§eShift+右键 §7下移"));
        }

        gui.setItem(27, createItem(Material.EMERALD_BLOCK, "§a添加新行", "§7点击添加新内容行"));

        if (page > 0) {
            gui.setItem(28, createItem(Material.ARROW, "§e上一页", "§7点击返回上一页"));
        }

        gui.setItem(31, createItem(Material.BOOK, "§a返回编辑", "§7返回公告编辑界面"));

        if (page < totalPages - 1) {
            gui.setItem(34, createItem(Material.ARROW, "§e下一页", "§7点击前往下一页"));
        }

        fillEmptySlots(gui, Material.GRAY_STAINED_GLASS_PANE);
        player.openInventory(gui);
        GuiContext context = new GuiContext(GuiType.EDIT_CONTENT);
        context.editingAnnouncement = announcement;
        context.page = page;
        playerContexts.put(player.getUniqueId(), context);
        updateContextTimestamp(player.getUniqueId());
    }

    public void openPriorityEditor(Player player, Announcement announcement) {
        player.closeInventory();
        player.sendMessage(Component.text("§e请在聊天栏输入新的优先级 (整数, 数值越大优先级越高)"));
        player.sendMessage(Component.text("§7当前优先级: §f" + announcement.getPriority()));
        player.sendMessage(Component.text("§7输入 §ccancel §7取消修改"));
        
        chatInputContexts.put(player.getUniqueId(), new ChatInputContext(
                ChatInputType.PRIORITY, 
                GuiType.EDIT_ANNOUNCEMENT, 
                announcement
        ));
    }

    public void openCooldownEditor(Player player, Announcement announcement) {
        Inventory gui = Bukkit.createInventory(null, 27,
                Component.text("§2编辑冷却设置"));

        int globalCooldown = announcement.getCooldown() != null ? announcement.getCooldown().getGlobal() : 0;
        int playerCooldown = announcement.getCooldown() != null ? announcement.getCooldown().getPerPlayer() : 0;

        gui.setItem(11, createItem(Material.CLOCK, "§e全局冷却", 
                "§7当前: §f" + globalCooldown + "秒", 
                "§7所有玩家共享的冷却时间", "", "§e点击修改"));

        gui.setItem(13, createItem(Material.PLAYER_HEAD, "§e玩家冷却", 
                "§7当前: §f" + playerCooldown + "秒",
                "§7每个玩家独立的冷却时间", "", "§e点击修改"));

        gui.setItem(26, createItem(Material.ARROW, "§c返回", "§7返回编辑界面"));

        fillEmptySlots(gui, Material.GRAY_STAINED_GLASS_PANE);
        player.openInventory(gui);
        GuiContext context = new GuiContext(GuiType.EDIT_COOLDOWN);
        context.editingAnnouncement = announcement;
        playerContexts.put(player.getUniqueId(), context);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        GuiContext context = playerContexts.get(player.getUniqueId());
        if (context == null) return;

        // 检查点击的是否是插件GUI的顶部库存
        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory == null) return;
        
        // 获取玩家当前打开的顶部库存标题
        InventoryView openInventory = player.getOpenInventory();
        if (openInventory == null) return;
        
        Component titleComponent = openInventory.title();
        if (titleComponent == null) return;
        
        String title = PlainTextComponentSerializer.plainText().serialize(titleComponent);
        
        // 只处理插件GUI的点击（标题包含特定前缀）
        if (!title.contains("§2") && !title.contains("§c")) {
            return;
        }
        
        // 只有在点击顶部库存（GUI）时才取消事件
        if (clickedInventory.equals(openInventory.getTopInventory())) {
            event.setCancelled(true);
        } else {
            // 点击的是玩家背包，不处理
            return;
        }

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        ItemMeta meta = clickedItem.getItemMeta();
        if (meta == null) return;

        Component displayNameComponent = meta.displayName();
        if (displayNameComponent == null) return;

        String displayName = PlainTextComponentSerializer.plainText().serialize(displayNameComponent);

        switch (context.type) {
            case MAIN_MENU -> handleMainMenuClick(player, displayName);
            case ANNOUNCEMENT_LIST -> handleAnnouncementListClick(player, displayName, event.getClick(), context, clickedItem);
            case EDIT_ANNOUNCEMENT -> handleEditAnnouncementClick(player, displayName, context);
            case SELECT_DISPLAY_TYPE -> handleDisplayTypeClick(player, displayName, context);
            case SELECT_TARGET_TYPE -> handleTargetTypeClick(player, displayName, context);
            case SELECT_TRIGGER_TYPE -> handleTriggerTypeClick(player, displayName, context);
            case EDIT_CONTENT -> handleContentEditClick(player, displayName, event.getClick(), context, event.getSlot());
            case EDIT_COOLDOWN -> handleCooldownEditClick(player, displayName, context);
            case DELETE_CONFIRM -> handleDeleteConfirmClick(player, displayName, context);
            case EDIT_BOSSBAR_SETTINGS -> handleBossBarSettingsClick(player, displayName, context);
            case EDIT_TITLE_SETTINGS -> handleTitleSettingsClick(player, displayName, context);
            case EDIT_TOAST_SETTINGS -> handleToastSettingsClick(player, displayName, context);
            case EDIT_TOAST_ICON -> handleToastIconClick(player, displayName, context);
            case EDIT_SOUND_SETTINGS -> handleSoundSettingsClick(player, displayName, context);
            case SERVER_SELECTOR -> handleServerSelectorClick(player, displayName, context);
            case EDIT_SCHEDULE_SETTINGS -> handleScheduleSettingsClick(player, displayName, context);
            case EDIT_COMMAND_SETTINGS -> handleCommandSettingsClick(player, displayName, context);
            case CRON_PRESETS -> handleCronPresetsClick(player, displayName, context);
        }

        if (context.type != GuiType.DELETE_CONFIRM) {
            playClickSound(player);
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        ChatInputContext context = chatInputContexts.get(player.getUniqueId());
        if (context == null) return;

        event.setCancelled(true);
        String message = event.getMessage();

        if (message.equalsIgnoreCase("cancel")) {
            player.sendMessage(Component.text("§c已取消操作"));
            chatInputContexts.remove(player.getUniqueId());
            
            if (schedulerAdapter != null) {
                schedulerAdapter.runOnPlayer(player, () -> {
                    if (context.returnGui == GuiType.EDIT_ANNOUNCEMENT && context.editingAnnouncement != null) {
                        openEditAnnouncement(player, context.editingAnnouncement);
                    } else if (context.returnGui == GuiType.EDIT_CONTENT && context.editingAnnouncement != null) {
                        openContentEditor(player, context.editingAnnouncement, 0);
                    } else {
                        openMainMenu(player);
                    }
                });
            }
            return;
        }

        switch (context.type) {
            case ANNOUNCEMENT_ID -> handleAnnouncementIdInput(player, message, context);
            case ANNOUNCEMENT_ID_EDIT -> handleAnnouncementIdEditInput(player, message, context);
            case PRIORITY -> handlePriorityInput(player, message, context);
            case CONTENT_LINE -> handleContentLineInput(player, message, context);
            case GLOBAL_COOLDOWN -> handleGlobalCooldownInput(player, message, context);
            case PLAYER_COOLDOWN -> handlePlayerCooldownInput(player, message, context);
            case BOSSBAR_STAY -> handleBossBarStayInput(player, message, context);
            case TITLE_FADEIN -> handleTitleFadeInInput(player, message, context);
            case TITLE_STAY -> handleTitleStayInput(player, message, context);
            case TITLE_FADEOUT -> handleTitleFadeOutInput(player, message, context);
            case TOAST_ICON -> handleToastIconInput(player, message, context);
            case SERVER_LIST -> handleServerListInput(player, message, context);
            case SOUND_NAME -> handleSoundNameInput(player, message, context);
            case SCHEDULE_TIME -> handleScheduleTimeInput(player, message, context);
            case CRON_EXPRESSION -> handleCronExpressionInput(player, message, context);
            case TRIGGER_COMMAND -> handleTriggerCommandInput(player, message, context);
        }
    }

    private void handleMainMenuClick(Player player, String displayName) {
        if (displayName.contains("公告列表")) {
            openAnnouncementList(player, 0);
        } else if (displayName.contains("新建公告")) {
            openCreateAnnouncement(player);
        } else if (displayName.contains("重载配置")) {
            announcementManager.reload();
            player.sendMessage(Component.text("§a配置已重载!"));
            player.closeInventory();
        } else if (displayName.contains("关闭")) {
            player.closeInventory();
        }
    }

    private void handleAnnouncementListClick(Player player, String displayName, ClickType clickType, GuiContext context, ItemStack clickedItem) {
        if (displayName.contains("上一页")) {
            openAnnouncementList(player, context.page - 1);
            return;
        }

        if (displayName.contains("下一页")) {
            openAnnouncementList(player, context.page + 1);
            return;
        }

        if (displayName.contains("返回主菜单")) {
            openMainMenu(player);
            return;
        }

        String announcementId = displayName.replace("§f", "").replace("§r", "").trim();
        Optional<Announcement> optional = announcementManager.getAnnouncement(announcementId);

        if (optional.isEmpty()) {
            player.sendMessage(Component.text("§c找不到公告: " + announcementId));
            return;
        }

        Announcement announcement = optional.get();

        if (clickType.isShiftClick() && clickType.isRightClick()) {
            openDeleteConfirm(player, announcement, context.page);
        } else if (clickType.isRightClick()) {
            announcement.setEnabled(!announcement.isEnabled());
            announcementManager.updateAnnouncement(announcement);
            player.sendMessage(Component.text("§a公告 '" + announcementId + "' 状态已切换!"));
            openAnnouncementList(player, context.page);
        } else if (clickType == ClickType.MIDDLE) {
            player.closeInventory();
            if (player.hasPermission("messageservice.preview")) {
                player.performCommand("broadcast preview " + announcementId + " " + player.getName());
            } else {
                player.sendMessage(Component.text("§c你没有权限预览公告!"));
            }
        } else {
            openEditAnnouncement(player, announcement);
        }
    }

    private void handleEditAnnouncementClick(Player player, String displayName, GuiContext context) {
        if (context.editingAnnouncement == null) return;

        if (displayName.contains("公告ID")) {
            player.closeInventory();
            player.sendMessage(Component.text("§e请在聊天栏输入新的公告ID"));
            player.sendMessage(Component.text("§7当前ID: §f" + context.editingAnnouncement.getId()));
            player.sendMessage(Component.text("§7只能包含字母、数字、下划线和连字符"));
            player.sendMessage(Component.text("§7输入 §ccancel §7取消"));

            ChatInputContext chatContext = new ChatInputContext(
                    ChatInputType.ANNOUNCEMENT_ID_EDIT,
                    GuiType.EDIT_ANNOUNCEMENT,
                    context.editingAnnouncement
            );
            chatInputContexts.put(player.getUniqueId(), chatContext);
        } else if (displayName.contains("启用状态")) {
            context.editingAnnouncement.setEnabled(!context.editingAnnouncement.isEnabled());
            openEditAnnouncement(player, context.editingAnnouncement);
        } else if (displayName.contains("优先级")) {
            openPriorityEditor(player, context.editingAnnouncement);
        } else if (displayName.contains("显示类型")) {
            openDisplayTypeSelector(player, context.editingAnnouncement);
        } else if (displayName.contains("目标设置")) {
            openTargetTypeSelector(player, context.editingAnnouncement);
        } else if (displayName.contains("编辑内容")) {
            openContentEditor(player, context.editingAnnouncement, 0);
        } else if (displayName.contains("触发方式")) {
            openTriggerTypeSelector(player, context.editingAnnouncement);
        } else if (displayName.contains("冷却设置")) {
            openCooldownEditor(player, context.editingAnnouncement);
        } else if (displayName.contains("生效区服")) {
            openServerSelector(player, context.editingAnnouncement);
        } else if (displayName.contains("声音设置")) {
            openSoundSettings(player, context.editingAnnouncement);
        } else if (displayName.contains("保存更改")) {
            announcementManager.updateAnnouncement(context.editingAnnouncement);
            player.sendMessage(Component.text("§a公告 '" + context.editingAnnouncement.getId() + "' 已保存!"));
            playSuccessSound(player);
            openAnnouncementList(player, 0);
        } else if (displayName.contains("预览公告")) {
            player.closeInventory();
            Bukkit.dispatchCommand(player, "broadcast preview " + context.editingAnnouncement.getId() + " " + player.getName());
        } else if (displayName.contains("返回列表")) {
            openAnnouncementList(player, 0);
        } else if (displayName.contains("BossBar参数") || displayName.contains("Title参数") || displayName.contains("Toast参数")) {
            // 打开对应的参数设置界面
            Announcement.DisplayType type = context.editingAnnouncement.getDisplay() != null ? 
                    context.editingAnnouncement.getDisplay().getType() : Announcement.DisplayType.CHAT;
            switch (type) {
                case BOSSBAR -> openBossBarSettings(player, context.editingAnnouncement);
                case TITLE, COMBINED -> openTitleSettings(player, context.editingAnnouncement);
                case TOAST -> openToastSettings(player, context.editingAnnouncement);
            }
        } else if (displayName.contains("定时参数")) {
            openScheduleSettings(player, context.editingAnnouncement);
        } else if (displayName.contains("命令参数")) {
            openCommandSettings(player, context.editingAnnouncement);
        }
    }

    private void handleDisplayTypeClick(Player player, String displayName, GuiContext context) {
        if (displayName.contains("返回")) {
            if (context.editingAnnouncement != null) {
                openEditAnnouncement(player, context.editingAnnouncement);
            }
            return;
        }

        Announcement.DisplayType newType = null;
        if (displayName.contains("CHAT")) newType = Announcement.DisplayType.CHAT;
        else if (displayName.contains("TITLE")) newType = Announcement.DisplayType.TITLE;
        else if (displayName.contains("ACTIONBAR")) newType = Announcement.DisplayType.ACTIONBAR;
        else if (displayName.contains("BOSSBAR")) newType = Announcement.DisplayType.BOSSBAR;
        else if (displayName.contains("TOAST")) newType = Announcement.DisplayType.TOAST;
        else if (displayName.contains("COMBINED")) newType = Announcement.DisplayType.COMBINED;

        if (newType != null && context.editingAnnouncement != null) {
            Announcement.DisplaySettings displaySettings = context.editingAnnouncement.getDisplay();
            if (displaySettings == null) {
                displaySettings = createDefaultDisplaySettings(newType);
                context.editingAnnouncement.setDisplay(displaySettings);
            } else {
                displaySettings.setType(newType);
                // 确保各类型参数有默认值
                ensureDefaultSettings(displaySettings, newType);
            }
            player.sendMessage(Component.text("§a显示类型已更改为: " + newType));
            
            // 根据类型打开对应的设置界面
            switch (newType) {
                case BOSSBAR -> openBossBarSettings(player, context.editingAnnouncement);
                case TITLE, COMBINED -> openTitleSettings(player, context.editingAnnouncement);
                case TOAST -> openToastSettings(player, context.editingAnnouncement);
                default -> openEditAnnouncement(player, context.editingAnnouncement);
            }
        }
    }

    private Announcement.DisplaySettings createDefaultDisplaySettings(Announcement.DisplayType type) {
        Announcement.DisplaySettings.DisplaySettingsBuilder builder = Announcement.DisplaySettings.builder()
                .type(type);
        
        switch (type) {
            case BOSSBAR -> builder.color("WHITE").style("SOLID").stay(70);
            case TITLE, COMBINED -> builder.fadeIn(10).stay(70).fadeOut(20);
            case TOAST -> builder.color("TASK");
            default -> {}
        }
        
        return builder.build();
    }

    private void ensureDefaultSettings(Announcement.DisplaySettings settings, Announcement.DisplayType type) {
        switch (type) {
            case BOSSBAR -> {
                if (settings.getColor() == null) settings.setColor("WHITE");
                if (settings.getStyle() == null) settings.setStyle("SOLID");
                if (settings.getStay() == 0) settings.setStay(70);
            }
            case TITLE, COMBINED -> {
                if (settings.getFadeIn() == 0) settings.setFadeIn(10);
                if (settings.getStay() == 0) settings.setStay(70);
                if (settings.getFadeOut() == 0) settings.setFadeOut(20);
            }
            case TOAST -> {
                if (settings.getColor() == null) settings.setColor("TASK");
            }
            default -> {}
        }
    }

    private void handleTargetTypeClick(Player player, String displayName, GuiContext context) {
        if (displayName.contains("返回")) {
            if (context.editingAnnouncement != null) {
                openEditAnnouncement(player, context.editingAnnouncement);
            }
            return;
        }

        Announcement.TargetType newType = null;
        if (displayName.contains("ALL")) newType = Announcement.TargetType.ALL;
        else if (displayName.contains("WORLD")) newType = Announcement.TargetType.WORLD;
        else if (displayName.contains("PERMISSION")) newType = Announcement.TargetType.PERMISSION;
        else if (displayName.contains("RANGE")) newType = Announcement.TargetType.RANGE;
        else if (displayName.contains("TRIGGER_PLAYER")) newType = Announcement.TargetType.TRIGGER_PLAYER;

        if (newType != null && context.editingAnnouncement != null) {
            if (context.editingAnnouncement.getTarget() == null) {
                String value = newType == Announcement.TargetType.TRIGGER_PLAYER ? "trigger_player" : "*";
                context.editingAnnouncement.setTarget(Announcement.TargetSettings.builder().type(newType).value(value).build());
            } else {
                context.editingAnnouncement.getTarget().setType(newType);
                if (newType == Announcement.TargetType.TRIGGER_PLAYER) {
                    context.editingAnnouncement.getTarget().setValue("trigger_player");
                }
            }
            player.sendMessage(Component.text("§a目标类型已更改为: " + newType));
            openEditAnnouncement(player, context.editingAnnouncement);
        }
    }

    private void handleTriggerTypeClick(Player player, String displayName, GuiContext context) {
        if (displayName.contains("返回")) {
            if (context.editingAnnouncement != null) {
                openEditAnnouncement(player, context.editingAnnouncement);
            }
            return;
        }

        Announcement.TriggerType newType = null;
        if (displayName.contains("MANUAL")) newType = Announcement.TriggerType.MANUAL;
        else if (displayName.contains("SCHEDULE")) newType = Announcement.TriggerType.SCHEDULE;
        else if (displayName.contains("COMMAND")) newType = Announcement.TriggerType.COMMAND;
        else if (displayName.contains("EVENT")) newType = Announcement.TriggerType.EVENT;
        else if (displayName.contains("FIRST_JOIN")) newType = Announcement.TriggerType.FIRST_JOIN;

        if (newType != null && context.editingAnnouncement != null) {
            if (context.editingAnnouncement.getTrigger() == null) {
                context.editingAnnouncement.setTrigger(Announcement.TriggerSettings.builder().type(newType).build());
            } else {
                context.editingAnnouncement.getTrigger().setType(newType);
            }
            player.sendMessage(Component.text("§a触发方式已更改为: " + newType));
            
            switch (newType) {
                case SCHEDULE -> openScheduleSettings(player, context.editingAnnouncement);
                case COMMAND -> openCommandSettings(player, context.editingAnnouncement);
                case EVENT -> openEventSettings(player, context.editingAnnouncement);
                case FIRST_JOIN -> {
                    // 首次登录触发不需要额外配置，直接返回编辑界面
                    player.sendMessage(Component.text("§a首次登录触发已设置，无需额外配置"));
                    openEditAnnouncement(player, context.editingAnnouncement);
                }
                default -> openEditAnnouncement(player, context.editingAnnouncement);
            }
        }
    }

    private void handleContentEditClick(Player player, String displayName, ClickType clickType, GuiContext context, int slot) {
        if (context.editingAnnouncement == null) return;

        if (displayName.contains("添加新行")) {
            player.closeInventory();
            player.sendMessage(Component.text("§e请在聊天栏输入新行的内容"));
            player.sendMessage(Component.text("§7支持颜色代码 (&a, &b, &c 等)"));
            player.sendMessage(Component.text("§7输入 §ccancel §7取消"));
            
            ChatInputContext inputContext = new ChatInputContext(
                    ChatInputType.CONTENT_LINE, 
                    GuiType.EDIT_CONTENT, 
                    context.editingAnnouncement
            );
            inputContext.lineIndex = -1;
            chatInputContexts.put(player.getUniqueId(), inputContext);
            return;
        }

        if (displayName.contains("上一页")) {
            openContentEditor(player, context.editingAnnouncement, context.page - 1);
            return;
        }

        if (displayName.contains("下一页")) {
            openContentEditor(player, context.editingAnnouncement, context.page + 1);
            return;
        }

        if (displayName.contains("返回编辑")) {
            openEditAnnouncement(player, context.editingAnnouncement);
            return;
        }

        if (displayName.startsWith("§f行 ")) {
            // 检查槽位范围，确保只在内容区域 (0-17) 内响应
            if (slot < 0 || slot >= 18) {
                playErrorSound(player);
                return;
            }

            int lineIndex = context.page * 18 + slot;
            List<String> content = context.editingAnnouncement.getContent();
            
            if (lineIndex < 0 || lineIndex >= content.size()) {
                playErrorSound(player);
                return;
            }

            if (clickType.isShiftClick() && clickType.isLeftClick() && lineIndex > 0) {
                Collections.swap(content, lineIndex, lineIndex - 1);
                openContentEditor(player, context.editingAnnouncement, context.page);
            } else if (clickType.isShiftClick() && clickType.isRightClick() && lineIndex < content.size() - 1) {
                Collections.swap(content, lineIndex, lineIndex + 1);
                openContentEditor(player, context.editingAnnouncement, context.page);
            } else if (clickType.isRightClick()) {
                content.remove(lineIndex);
                player.sendMessage(Component.text("§a该行已删除"));
                openContentEditor(player, context.editingAnnouncement, context.page);
            } else {
                player.closeInventory();
                player.sendMessage(Component.text("§e正在编辑第 " + (lineIndex + 1) + " 行"));
                player.sendMessage(Component.text("§7当前内容: §f" + content.get(lineIndex)));
                player.sendMessage(Component.text("§7输入新内容 (输入 §ccancel §7取消):"));
                
                ChatInputContext inputContext = new ChatInputContext(
                        ChatInputType.CONTENT_LINE, 
                        GuiType.EDIT_CONTENT, 
                        context.editingAnnouncement
                );
                inputContext.lineIndex = lineIndex;
                chatInputContexts.put(player.getUniqueId(), inputContext);
            }
        }
    }

    private void handleCooldownEditClick(Player player, String displayName, GuiContext context) {
        if (context.editingAnnouncement == null) return;

        if (displayName.contains("全局冷却")) {
            player.closeInventory();
            player.sendMessage(Component.text("§e请在聊天栏输入全局冷却时间(秒)"));
            player.sendMessage(Component.text("§7输入 0 表示无冷却"));
            player.sendMessage(Component.text("§7输入 §ccancel §7取消"));
            
            chatInputContexts.put(player.getUniqueId(), new ChatInputContext(
                    ChatInputType.GLOBAL_COOLDOWN, 
                    GuiType.EDIT_COOLDOWN, 
                    context.editingAnnouncement
            ));
        } else if (displayName.contains("玩家冷却")) {
            player.closeInventory();
            player.sendMessage(Component.text("§e请在聊天栏输入玩家冷却时间(秒)"));
            player.sendMessage(Component.text("§7输入 0 表示无冷却"));
            player.sendMessage(Component.text("§7输入 §ccancel §7取消"));
            
            chatInputContexts.put(player.getUniqueId(), new ChatInputContext(
                    ChatInputType.PLAYER_COOLDOWN, 
                    GuiType.EDIT_COOLDOWN, 
                    context.editingAnnouncement
            ));
        } else if (displayName.contains("返回")) {
            openEditAnnouncement(player, context.editingAnnouncement);
        }
    }

    private void handleAnnouncementIdInput(Player player, String message, ChatInputContext context) {
        if (!message.matches("^[a-zA-Z0-9_-]+$")) {
            player.sendMessage(Component.text("§c公告ID只能包含字母、数字、下划线和连字符!"));
            return;
        }

        if (announcementManager.exists(message)) {
            player.sendMessage(Component.text("§c公告 '" + message + "' 已存在!"));
            return;
        }

        Announcement newAnnouncement = Announcement.builder()
                .id(message)
                .enabled(true)
                .priority(1)
                .content(new ArrayList<>())
                .display(Announcement.DisplaySettings.builder()
                        .type(Announcement.DisplayType.CHAT)
                        .build())
                .target(Announcement.TargetSettings.builder()
                        .type(Announcement.TargetType.ALL)
                        .value("*")
                        .build())
                .trigger(Announcement.TriggerSettings.builder()
                        .type(Announcement.TriggerType.MANUAL)
                        .build())
                .cooldown(Announcement.CooldownSettings.builder()
                        .global(0)
                        .perPlayer(0)
                        .build())
                .build();

        if (announcementManager.createAnnouncement(newAnnouncement)) {
            player.sendMessage(Component.text("§a公告 '" + message + "' 创建成功!"));
            chatInputContexts.remove(player.getUniqueId());
            
            if (schedulerAdapter != null) {
                schedulerAdapter.runOnPlayer(player, () -> {
                    openEditAnnouncement(player, newAnnouncement);
                    playSuccessSound(player);
                });
            }
        } else {
            player.sendMessage(Component.text("§c创建公告失败!"));
            playErrorSound(player);
        }
    }

    private void handleAnnouncementIdEditInput(Player player, String message, ChatInputContext context) {
        if (!message.matches("^[a-zA-Z0-9_-]+$")) {
            player.sendMessage(Component.text("§c公告ID只能包含字母、数字、下划线和连字符!"));
            return;
        }

        String oldId = context.editingAnnouncement.getId();
        if (message.equals(oldId)) {
            player.sendMessage(Component.text("§7ID未变化，已取消"));
            chatInputContexts.remove(player.getUniqueId());
            if (schedulerAdapter != null) {
                schedulerAdapter.runOnPlayer(player, () -> openEditAnnouncement(player, context.editingAnnouncement));
            }
            return;
        }

        if (announcementManager.exists(message)) {
            player.sendMessage(Component.text("§c公告 '" + message + "' 已存在!"));
            return;
        }

        if (announcementManager.renameAnnouncement(oldId, message)) {
            context.editingAnnouncement.setId(message);
            player.sendMessage(Component.text("§a公告ID已从 '" + oldId + "' 改为 '" + message + "'"));
            chatInputContexts.remove(player.getUniqueId());

            if (schedulerAdapter != null) {
                schedulerAdapter.runOnPlayer(player, () -> {
                    openEditAnnouncement(player, context.editingAnnouncement);
                    playSuccessSound(player);
                });
            }
        } else {
            player.sendMessage(Component.text("§c修改ID失败!"));
            playErrorSound(player);
        }
    }

    private void handlePriorityInput(Player player, String message, ChatInputContext context) {
        try {
            int priority = Integer.parseInt(message);
            context.editingAnnouncement.setPriority(priority);
            player.sendMessage(Component.text("§a优先级已设置为: " + priority));
            chatInputContexts.remove(player.getUniqueId());
            
            if (schedulerAdapter != null) {
                schedulerAdapter.runOnPlayer(player, () -> {
                    openEditAnnouncement(player, context.editingAnnouncement);
                    playSuccessSound(player);
                });
            }
        } catch (NumberFormatException e) {
            player.sendMessage(Component.text("§c请输入有效的整数!"));
            playErrorSound(player);
        }
    }

    private void handleContentLineInput(Player player, String message, ChatInputContext context) {
        List<String> content = context.editingAnnouncement.getContent();
        if (content == null) {
            content = new ArrayList<>();
            context.editingAnnouncement.setContent(content);
        }

        if (context.lineIndex == -1) {
            content.add(message);
            player.sendMessage(Component.text("§a新行已添加"));
        } else if (context.lineIndex >= 0 && context.lineIndex < content.size()) {
            content.set(context.lineIndex, message);
            player.sendMessage(Component.text("§a第 " + (context.lineIndex + 1) + " 行已更新"));
        } else {
            player.sendMessage(Component.text("§c错误: 行索引超出范围!"));
            playErrorSound(player);
            return;
        }

        chatInputContexts.remove(player.getUniqueId());
        if (schedulerAdapter != null) {
            schedulerAdapter.runOnPlayer(player, () -> {
                openContentEditor(player, context.editingAnnouncement, 0);
                playSuccessSound(player);
            });
        }
    }

    private void handleGlobalCooldownInput(Player player, String message, ChatInputContext context) {
        try {
            int cooldown = Integer.parseInt(message);
            if (cooldown < 0) cooldown = 0;

            if (context.editingAnnouncement.getCooldown() == null) {
                context.editingAnnouncement.setCooldown(Announcement.CooldownSettings.builder().global(cooldown).perPlayer(0).build());
            } else {
                context.editingAnnouncement.getCooldown().setGlobal(cooldown);
            }

            player.sendMessage(Component.text("§a全局冷却已设置为: " + cooldown + "秒"));
            chatInputContexts.remove(player.getUniqueId());

            if (schedulerAdapter != null) {
                schedulerAdapter.runOnPlayer(player, () -> {
                    openCooldownEditor(player, context.editingAnnouncement);
                    playSuccessSound(player);
                });
            }
        } catch (NumberFormatException e) {
            player.sendMessage(Component.text("§c请输入有效的整数!"));
            playErrorSound(player);
        }
    }

    private void handlePlayerCooldownInput(Player player, String message, ChatInputContext context) {
        try {
            int cooldown = Integer.parseInt(message);
            if (cooldown < 0) cooldown = 0;

            if (context.editingAnnouncement.getCooldown() == null) {
                context.editingAnnouncement.setCooldown(Announcement.CooldownSettings.builder().global(0).perPlayer(cooldown).build());
            } else {
                context.editingAnnouncement.getCooldown().setPerPlayer(cooldown);
            }

            player.sendMessage(Component.text("§a玩家冷却已设置为: " + cooldown + "秒"));
            chatInputContexts.remove(player.getUniqueId());

            if (schedulerAdapter != null) {
                schedulerAdapter.runOnPlayer(player, () -> {
                    openCooldownEditor(player, context.editingAnnouncement);
                    playSuccessSound(player);
                });
            }
        } catch (NumberFormatException e) {
            player.sendMessage(Component.text("§c请输入有效的整数!"));
            playErrorSound(player);
        }
    }

    private void handleBossBarStayInput(Player player, String message, ChatInputContext context) {
        try {
            int stay = Integer.parseInt(message);
            if (stay < 20) stay = 20; // 最少1秒
            if (stay > 6000) stay = 6000; // 最多5分钟

            if (context.editingAnnouncement.getDisplay() == null) {
                context.editingAnnouncement.setDisplay(Announcement.DisplaySettings.builder()
                        .type(Announcement.DisplayType.BOSSBAR)
                        .stay(stay)
                        .color("WHITE")
                        .style("SOLID")
                        .build());
            } else {
                context.editingAnnouncement.getDisplay().setStay(stay);
            }

            player.sendMessage(Component.text("§aBossBar显示时间已设置为: " + stay + " tick (约 " + (stay / 20) + " 秒)"));
            chatInputContexts.remove(player.getUniqueId());

            if (schedulerAdapter != null) {
                schedulerAdapter.runOnPlayer(player, () -> {
                    openBossBarSettings(player, context.editingAnnouncement);
                    playSuccessSound(player);
                });
            }
        } catch (NumberFormatException e) {
            player.sendMessage(Component.text("§c请输入有效的整数!"));
            playErrorSound(player);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;

        if (schedulerAdapter != null) {
            schedulerAdapter.runDelayedOnPlayer(player, () -> {
                if (player.getOpenInventory().getTopInventory().getSize() == 0) {
                    // 玩家关闭了所有 GUI，清理上下文
                    cleanupPlayerContexts(player.getUniqueId());
                }
            }, 1L);
        }
    }
    
    /**
     * 玩家下线时清理上下文
     * 参照 FoliaShop 的资源管理实现
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        cleanupPlayerContexts(player.getUniqueId());
    }

    private ItemStack createItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(Component.text(name));

        if (lore.length > 0) {
            List<Component> loreComponents = new ArrayList<>();
            for (String line : lore) {
                loreComponents.add(Component.text(line));
            }
            meta.lore(loreComponents);
        }

        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createTypeItem(Material material, String typeName, boolean isSelected, String... descriptions) {
        List<String> lore = new ArrayList<>();
        for (String desc : descriptions) {
            lore.add(desc);
        }
        lore.add("");
        if (isSelected) {
            lore.add("§a✓ 当前选择");
        } else {
            lore.add("§e点击选择");
        }

        Material displayMaterial = isSelected ? Material.LIME_WOOL : material;
        return createItem(displayMaterial, "§f" + typeName, lore.toArray(new String[0]));
    }

    private void fillEmptySlots(Inventory gui, Material material) {
        ItemStack filler = new ItemStack(material);
        ItemMeta meta = filler.getItemMeta();
        meta.displayName(Component.text(" "));
        filler.setItemMeta(meta);

        for (int i = 0; i < gui.getSize(); i++) {
            if (gui.getItem(i) == null) {
                gui.setItem(i, filler);
            }
        }
    }

    /**
     * 创建带有公告ID的PersistentDataContainer的物品
     */
    private ItemStack createAnnouncementItem(Material material, String announcementId, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(Component.text("§f" + announcementId));

        if (lore.length > 0) {
            List<Component> loreComponents = new ArrayList<>();
            for (String line : lore) {
                loreComponents.add(Component.text(line));
            }
            meta.lore(loreComponents);
        }

        // 使用PersistentDataContainer存储公告ID
        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(new org.bukkit.NamespacedKey(cn.handyplus.chat.PlayerChat.INSTANCE, ANNOUNCEMENT_ID_KEY), PersistentDataType.STRING, announcementId);

        item.setItemMeta(meta);
        return item;
    }

    /**
     * 从物品的PersistentDataContainer获取公告ID
     */
    private String getAnnouncementIdFromItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return null;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return null;
        }

        PersistentDataContainer container = meta.getPersistentDataContainer();
        return container.get(new org.bukkit.NamespacedKey(cn.handyplus.chat.PlayerChat.INSTANCE, ANNOUNCEMENT_ID_KEY), PersistentDataType.STRING);
    }

    private String getDisplayTypeName(Announcement announcement) {
        if (announcement.getDisplay() == null) return "CHAT";
        return announcement.getDisplay().getType().toString();
    }

    private String getTargetTypeName(Announcement announcement) {
        if (announcement.getTarget() == null) return "ALL";
        return announcement.getTarget().getType().toString();
    }

    private String getTriggerTypeName(Announcement announcement) {
        if (announcement.getTrigger() == null) return "MANUAL";
        return announcement.getTrigger().getType().toString();
    }

    public void openDeleteConfirm(Player player, Announcement announcement, int returnPage) {
        Inventory gui = Bukkit.createInventory(null, 27,
                Component.text("§c§l确认删除: " + announcement.getId()));

        gui.setItem(11, createItem(Material.RED_WOOL, "§c§l确认删除",
                "§7你确定要删除公告 '", "§7" + announcement.getId() + "'§7?",
                "",
                "§c§l此操作不可撤销!",
                "",
                "§e点击确认删除"));

        gui.setItem(15, createItem(Material.LIME_WOOL, "§a取消删除",
                "§7保留公告 '", "§7" + announcement.getId() + "'",
                "",
                "§e点击取消"));

        fillEmptySlots(gui, Material.GRAY_STAINED_GLASS_PANE);
        player.openInventory(gui);

        GuiContext context = new GuiContext(GuiType.DELETE_CONFIRM);
        context.editingAnnouncement = announcement;
        context.page = returnPage;
        playerContexts.put(player.getUniqueId(), context);

        playSound(player, Sound.BLOCK_NOTE_BLOCK_BASS, 0.8f, 0.5f);
    }

    private void handleDeleteConfirmClick(Player player, String displayName, GuiContext context) {
        if (context.editingAnnouncement == null) return;

        if (displayName.contains("确认删除")) {
            String announcementId = context.editingAnnouncement.getId();
            announcementManager.deleteAnnouncement(announcementId);
            player.sendMessage(Component.text("§c公告 '" + announcementId + "' 已删除!"));
            playSound(player, Sound.BLOCK_ANVIL_DESTROY, 0.8f, 0.8f);
            openAnnouncementList(player, 0);
        } else if (displayName.contains("取消删除")) {
            playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 0.8f, 1.2f);
            openAnnouncementList(player, context.page);
        }
    }

    private void playSound(Player player, Sound sound, float volume, float pitch) {
        try {
            player.playSound(player.getLocation(), sound, SoundCategory.MASTER, volume, pitch);
        } catch (Exception e) {
        }
    }

    private void playClickSound(Player player) {
        playSound(player, Sound.UI_BUTTON_CLICK, 0.6f, 1.0f);
    }

    private void playOpenSound(Player player) {
        playSound(player, Sound.BLOCK_CHEST_OPEN, 0.6f, 1.0f);
    }

    private void playCloseSound(Player player) {
        playSound(player, Sound.BLOCK_CHEST_CLOSE, 0.6f, 1.0f);
    }

    private void playSuccessSound(Player player) {
        playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 0.8f, 2.0f);
    }

    private void playErrorSound(Player player) {
        playSound(player, Sound.BLOCK_NOTE_BLOCK_BASS, 0.8f, 0.5f);
    }

    public void cleanup() {
        playerContexts.clear();
        chatInputContexts.clear();
    }

    /**
     * 打开BossBar设置界面
     */
    public void openBossBarSettings(Player player, Announcement announcement) {
        Inventory gui = Bukkit.createInventory(null, 36,
                Component.text("§2BossBar设置: " + announcement.getId()));

        Announcement.DisplaySettings display = announcement.getDisplay();
        String currentColor = display != null && display.getColor() != null ? display.getColor() : "WHITE";
        String currentStyle = display != null && display.getStyle() != null ? display.getStyle() : "SOLID";
        int currentStay = display != null && display.getStay() > 0 ? display.getStay() : 70;
        boolean progressEnabled = display == null || display.isBossbarProgress();

        // 颜色选项
        gui.setItem(10, createBossBarColorItem(Material.RED_WOOL, "RED", currentColor));
        gui.setItem(11, createBossBarColorItem(Material.GREEN_WOOL, "GREEN", currentColor));
        gui.setItem(12, createBossBarColorItem(Material.BLUE_WOOL, "BLUE", currentColor));
        gui.setItem(13, createBossBarColorItem(Material.YELLOW_WOOL, "YELLOW", currentColor));
        gui.setItem(14, createBossBarColorItem(Material.PURPLE_WOOL, "PURPLE", currentColor));
        gui.setItem(15, createBossBarColorItem(Material.WHITE_WOOL, "WHITE", currentColor));
        gui.setItem(16, createBossBarColorItem(Material.PINK_WOOL, "PINK", currentColor));

        // 样式选项
        gui.setItem(19, createBossBarStyleItem(Material.BOOK, "SOLID", currentStyle));
        gui.setItem(20, createBossBarStyleItem(Material.BOOK, "SEGMENTED_6", currentStyle));
        gui.setItem(21, createBossBarStyleItem(Material.BOOK, "SEGMENTED_10", currentStyle));
        gui.setItem(22, createBossBarStyleItem(Material.BOOK, "SEGMENTED_12", currentStyle));
        gui.setItem(23, createBossBarStyleItem(Material.BOOK, "SEGMENTED_20", currentStyle));

        // 显示时间
        gui.setItem(28, createItem(Material.CLOCK, "§e显示时间", 
                "§7当前: §f" + currentStay + " tick",
                "§7约 " + (currentStay / 20) + " 秒",
                "", "§e点击修改"));

        // 血量进度显示
        Material progressMaterial = progressEnabled ? Material.LIME_DYE : Material.GRAY_DYE;
        String progressStatus = progressEnabled ? "§a已启用" : "§c已禁用";
        gui.setItem(30, createItem(progressMaterial, "§e血量进度显示", 
                "§7状态: " + progressStatus,
                "",
                "§7控制BossBar血量是否随",
                "§7时间递减",
                "",
                "§e点击切换"));

        // 返回按钮
        gui.setItem(35, createItem(Material.ARROW, "§c返回", "§7返回编辑界面"));

        fillEmptySlots(gui, Material.GRAY_STAINED_GLASS_PANE);
        player.openInventory(gui);

        GuiContext context = new GuiContext(GuiType.EDIT_BOSSBAR_SETTINGS);
        context.editingAnnouncement = announcement;
        playerContexts.put(player.getUniqueId(), context);
    }

    private ItemStack createBossBarColorItem(Material material, String colorName, String currentColor) {
        boolean isSelected = colorName.equalsIgnoreCase(currentColor);
        List<String> lore = new ArrayList<>();
        lore.add("§7颜色: " + colorName);
        if (isSelected) {
            lore.add("§a§l当前已选择");
        } else {
            lore.add("§e点击选择");
        }
        return createItem(material, (isSelected ? "§a§l" : "§f") + colorName, lore.toArray(new String[0]));
    }

    private ItemStack createBossBarStyleItem(Material material, String styleName, String currentStyle) {
        boolean isSelected = styleName.equalsIgnoreCase(currentStyle);
        List<String> lore = new ArrayList<>();
        lore.add("§7样式: " + styleName);
        if (isSelected) {
            lore.add("§a§l当前已选择");
        } else {
            lore.add("§e点击选择");
        }
        return createItem(material, (isSelected ? "§a§l" : "§f") + styleName, lore.toArray(new String[0]));
    }

    private void handleBossBarSettingsClick(Player player, String displayName, GuiContext context) {
        if (context.editingAnnouncement == null) return;

        if (displayName.contains("返回")) {
            openEditAnnouncement(player, context.editingAnnouncement);
            return;
        }

        Announcement.DisplaySettings display = context.editingAnnouncement.getDisplay();
        if (display == null) {
            display = Announcement.DisplaySettings.builder()
                    .type(Announcement.DisplayType.BOSSBAR)
                    .color("WHITE")
                    .style("SOLID")
                    .stay(70)
                    .build();
            context.editingAnnouncement.setDisplay(display);
        }

        // 处理颜色选择
        String[] colors = {"RED", "GREEN", "BLUE", "YELLOW", "PURPLE", "WHITE", "PINK"};
        for (String color : colors) {
            if (displayName.contains(color)) {
                display.setColor(color);
                player.sendMessage(Component.text("§aBossBar颜色已设置为: " + color));
                openBossBarSettings(player, context.editingAnnouncement);
                return;
            }
        }

        // 处理样式选择
        String[] styles = {"SOLID", "SEGMENTED_6", "SEGMENTED_10", "SEGMENTED_12", "SEGMENTED_20"};
        for (String style : styles) {
            if (displayName.contains(style)) {
                display.setStyle(style);
                player.sendMessage(Component.text("§aBossBar样式已设置为: " + style));
                openBossBarSettings(player, context.editingAnnouncement);
                return;
            }
        }

        // 处理显示时间修改
        if (displayName.contains("显示时间")) {
            player.sendMessage(Component.text("§e请在聊天栏输入显示时间（tick，1秒=20tick），输入 cancel 取消"));
            player.closeInventory();
            ChatInputContext chatContext = new ChatInputContext(ChatInputType.BOSSBAR_STAY, GuiType.EDIT_BOSSBAR_SETTINGS, context.editingAnnouncement);
            chatInputContexts.put(player.getUniqueId(), chatContext);
        }

        // 处理血量进度显示切换
        if (displayName.contains("血量进度显示")) {
            boolean newState = !display.isBossbarProgress();
            display.setBossbarProgress(newState);
            player.sendMessage(Component.text("§aBossBar血量进度显示已" + (newState ? "§a启用" : "§c禁用")));
            openBossBarSettings(player, context.editingAnnouncement);
        }
    }

    /**
     * 打开Title设置界面
     */
    public void openTitleSettings(Player player, Announcement announcement) {
        Inventory gui = Bukkit.createInventory(null, 36,
                Component.text("§2Title设置: " + announcement.getId()));

        Announcement.DisplaySettings display = announcement.getDisplay();
        int fadeIn = display != null && display.getFadeIn() > 0 ? display.getFadeIn() : 10;
        int stay = display != null && display.getStay() > 0 ? display.getStay() : 70;
        int fadeOut = display != null && display.getFadeOut() > 0 ? display.getFadeOut() : 20;

        // 淡入时间
        gui.setItem(11, createItem(Material.CLOCK, "§e淡入时间", 
                "§7当前: §f" + fadeIn + " tick",
                "§7约 " + (fadeIn / 20.0) + " 秒",
                "", "§e点击修改"));

        // 停留时间
        gui.setItem(13, createItem(Material.CLOCK, "§e停留时间", 
                "§7当前: §f" + stay + " tick",
                "§7约 " + (stay / 20.0) + " 秒",
                "", "§e点击修改"));

        // 淡出时间
        gui.setItem(15, createItem(Material.CLOCK, "§e淡出时间", 
                "§7当前: §f" + fadeOut + " tick",
                "§7约 " + (fadeOut / 20.0) + " 秒",
                "", "§e点击修改"));

        // 返回按钮
        gui.setItem(35, createItem(Material.ARROW, "§c返回", "§7返回编辑界面"));

        fillEmptySlots(gui, Material.GRAY_STAINED_GLASS_PANE);
        player.openInventory(gui);

        GuiContext context = new GuiContext(GuiType.EDIT_TITLE_SETTINGS);
        context.editingAnnouncement = announcement;
        playerContexts.put(player.getUniqueId(), context);
    }

    private void handleTitleSettingsClick(Player player, String displayName, GuiContext context) {
        if (context.editingAnnouncement == null) return;

        if (displayName.contains("返回")) {
            openEditAnnouncement(player, context.editingAnnouncement);
            return;
        }

        Announcement.DisplaySettings display = context.editingAnnouncement.getDisplay();
        if (display == null) {
            display = Announcement.DisplaySettings.builder()
                    .type(Announcement.DisplayType.TITLE)
                    .fadeIn(10)
                    .stay(70)
                    .fadeOut(20)
                    .build();
            context.editingAnnouncement.setDisplay(display);
        }

        // 处理淡入时间修改
        if (displayName.contains("淡入时间")) {
            player.sendMessage(Component.text("§e请在聊天栏输入淡入时间（tick，1秒=20tick），输入 cancel 取消"));
            player.closeInventory();
            ChatInputContext chatContext = new ChatInputContext(ChatInputType.TITLE_FADEIN, GuiType.EDIT_TITLE_SETTINGS, context.editingAnnouncement);
            chatInputContexts.put(player.getUniqueId(), chatContext);
        }
        // 处理停留时间修改
        else if (displayName.contains("停留时间")) {
            player.sendMessage(Component.text("§e请在聊天栏输入停留时间（tick，1秒=20tick），输入 cancel 取消"));
            player.closeInventory();
            ChatInputContext chatContext = new ChatInputContext(ChatInputType.TITLE_STAY, GuiType.EDIT_TITLE_SETTINGS, context.editingAnnouncement);
            chatInputContexts.put(player.getUniqueId(), chatContext);
        }
        // 处理淡出时间修改
        else if (displayName.contains("淡出时间")) {
            player.sendMessage(Component.text("§e请在聊天栏输入淡出时间（tick，1秒=20tick），输入 cancel 取消"));
            player.closeInventory();
            ChatInputContext chatContext = new ChatInputContext(ChatInputType.TITLE_FADEOUT, GuiType.EDIT_TITLE_SETTINGS, context.editingAnnouncement);
            chatInputContexts.put(player.getUniqueId(), chatContext);
        }
    }

    private void handleTitleFadeInInput(Player player, String message, ChatInputContext context) {
        try {
            int fadeIn = Integer.parseInt(message);
            if (fadeIn < 0) fadeIn = 0;
            if (fadeIn > 100) fadeIn = 100;

            if (context.editingAnnouncement.getDisplay() == null) {
                context.editingAnnouncement.setDisplay(Announcement.DisplaySettings.builder()
                        .type(Announcement.DisplayType.TITLE)
                        .fadeIn(fadeIn)
                        .stay(70)
                        .fadeOut(20)
                        .build());
            } else {
                context.editingAnnouncement.getDisplay().setFadeIn(fadeIn);
            }

            player.sendMessage(Component.text("§aTitle淡入时间已设置为: " + fadeIn + " tick (约 " + (fadeIn / 20.0) + " 秒)"));
            chatInputContexts.remove(player.getUniqueId());

            if (schedulerAdapter != null) {
                schedulerAdapter.runOnPlayer(player, () -> {
                    openTitleSettings(player, context.editingAnnouncement);
                    playSuccessSound(player);
                });
            }
        } catch (NumberFormatException e) {
            player.sendMessage(Component.text("§c请输入有效的整数!"));
            playErrorSound(player);
        }
    }

    private void handleTitleStayInput(Player player, String message, ChatInputContext context) {
        try {
            int stay = Integer.parseInt(message);
            if (stay < 20) stay = 20;
            if (stay > 6000) stay = 6000;

            if (context.editingAnnouncement.getDisplay() == null) {
                context.editingAnnouncement.setDisplay(Announcement.DisplaySettings.builder()
                        .type(Announcement.DisplayType.TITLE)
                        .fadeIn(10)
                        .stay(stay)
                        .fadeOut(20)
                        .build());
            } else {
                context.editingAnnouncement.getDisplay().setStay(stay);
            }

            player.sendMessage(Component.text("§aTitle停留时间已设置为: " + stay + " tick (约 " + (stay / 20.0) + " 秒)"));
            chatInputContexts.remove(player.getUniqueId());

            if (schedulerAdapter != null) {
                schedulerAdapter.runOnPlayer(player, () -> {
                    openTitleSettings(player, context.editingAnnouncement);
                    playSuccessSound(player);
                });
            }
        } catch (NumberFormatException e) {
            player.sendMessage(Component.text("§c请输入有效的整数!"));
            playErrorSound(player);
        }
    }

    private void handleTitleFadeOutInput(Player player, String message, ChatInputContext context) {
        try {
            int fadeOut = Integer.parseInt(message);
            if (fadeOut < 0) fadeOut = 0;
            if (fadeOut > 100) fadeOut = 100;

            if (context.editingAnnouncement.getDisplay() == null) {
                context.editingAnnouncement.setDisplay(Announcement.DisplaySettings.builder()
                        .type(Announcement.DisplayType.TITLE)
                        .fadeIn(10)
                        .stay(70)
                        .fadeOut(fadeOut)
                        .build());
            } else {
                context.editingAnnouncement.getDisplay().setFadeOut(fadeOut);
            }

            player.sendMessage(Component.text("§aTitle淡出时间已设置为: " + fadeOut + " tick (约 " + (fadeOut / 20.0) + " 秒)"));
            chatInputContexts.remove(player.getUniqueId());

            if (schedulerAdapter != null) {
                schedulerAdapter.runOnPlayer(player, () -> {
                    openTitleSettings(player, context.editingAnnouncement);
                    playSuccessSound(player);
                });
            }
        } catch (NumberFormatException e) {
            player.sendMessage(Component.text("§c请输入有效的整数!"));
            playErrorSound(player);
        }
    }

    /**
     * 打开Toast设置界面
     */
    public void openToastSettings(Player player, Announcement announcement) {
        Inventory gui = Bukkit.createInventory(null, 36,
                Component.text("§2Toast设置: " + announcement.getId()));

        Announcement.DisplaySettings display = announcement.getDisplay();
        String currentType = display != null && display.getColor() != null ? display.getColor() : "TASK";
        String currentIcon = display != null && display.getToastIcon() != null ? display.getToastIcon() : "默认";

        // Toast类型选项
        gui.setItem(11, createToastTypeItem(Material.GRASS_BLOCK, "TASK", currentType, "普通任务"));
        gui.setItem(13, createToastTypeItem(Material.GOLD_BLOCK, "GOAL", currentType, "目标达成"));
        gui.setItem(15, createToastTypeItem(Material.NETHERITE_BLOCK, "CHALLENGE", currentType, "挑战完成"));

        // 图标设置
        Material iconMaterial = currentIcon.equals("默认") ? Material.ITEM_FRAME : Material.valueOf(currentIcon.replace("minecraft:", "").toUpperCase());
        gui.setItem(21, createItem(iconMaterial, "§e自定义图标", 
                "§7当前: §f" + currentIcon,
                "",
                "§7支持格式:",
                "§7- minecraft:book (原版物品ID)",
                "§7- diamond (简写)",
                "",
                "§e点击修改图标"));

        // 返回按钮
        gui.setItem(35, createItem(Material.ARROW, "§c返回", "§7返回编辑界面"));

        fillEmptySlots(gui, Material.GRAY_STAINED_GLASS_PANE);
        player.openInventory(gui);

        GuiContext context = new GuiContext(GuiType.EDIT_TOAST_SETTINGS);
        context.editingAnnouncement = announcement;
        playerContexts.put(player.getUniqueId(), context);
    }

    private ItemStack createToastTypeItem(Material material, String typeName, String currentType, String description) {
        boolean isSelected = typeName.equalsIgnoreCase(currentType);
        List<String> lore = new ArrayList<>();
        lore.add("§7类型: " + typeName);
        lore.add("§7描述: " + description);
        if (isSelected) {
            lore.add("§a§l当前已选择");
        } else {
            lore.add("§e点击选择");
        }
        return createItem(material, (isSelected ? "§a§l" : "§f") + typeName, lore.toArray(new String[0]));
    }

    private void handleToastSettingsClick(Player player, String displayName, GuiContext context) {
        if (context.editingAnnouncement == null) return;

        if (displayName.contains("返回")) {
            openEditAnnouncement(player, context.editingAnnouncement);
            return;
        }

        Announcement.DisplaySettings display = context.editingAnnouncement.getDisplay();
        if (display == null) {
            display = Announcement.DisplaySettings.builder()
                    .type(Announcement.DisplayType.TOAST)
                    .color("TASK")
                    .build();
            context.editingAnnouncement.setDisplay(display);
        }

        // 处理类型选择
        String[] types = {"TASK", "GOAL", "CHALLENGE"};
        for (String type : types) {
            if (displayName.contains(type)) {
                display.setColor(type);
                player.sendMessage(Component.text("§aToast类型已设置为: " + type));
                openToastSettings(player, context.editingAnnouncement);
                return;
            }
        }

        // 处理图标设置
        if (displayName.contains("自定义图标")) {
            openToastIconEditor(player, context.editingAnnouncement);
        }
    }

    /**
     * 打开Toast图标编辑器
     */
    public void openToastIconEditor(Player player, Announcement announcement) {
        Inventory gui = Bukkit.createInventory(null, 36,
                Component.text("§2Toast图标: " + announcement.getId()));

        Announcement.DisplaySettings display = announcement.getDisplay();
        String currentIcon = display != null && display.getToastIcon() != null ? display.getToastIcon() : "默认";

        // 常用图标预设
        gui.setItem(10, createItem(Material.BOOK, "§e书", "§7minecraft:book", "", "§e点击选择"));
        gui.setItem(11, createItem(Material.DIAMOND, "§e钻石", "§7minecraft:diamond", "", "§e点击选择"));
        gui.setItem(12, createItem(Material.EMERALD, "§e绿宝石", "§7minecraft:emerald", "", "§e点击选择"));
        gui.setItem(13, createItem(Material.GOLD_INGOT, "§e金锭", "§7minecraft:gold_ingot", "", "§e点击选择"));
        gui.setItem(14, createItem(Material.IRON_SWORD, "§e铁剑", "§7minecraft:iron_sword", "", "§e点击选择"));
        gui.setItem(15, createItem(Material.CHEST, "§e箱子", "§7minecraft:chest", "", "§e点击选择"));
        gui.setItem(16, createItem(Material.NETHER_STAR, "§e下界之星", "§7minecraft:nether_star", "", "§e点击选择"));

        gui.setItem(19, createItem(Material.GRASS_BLOCK, "§e草方块", "§7minecraft:grass_block", "", "§e点击选择"));
        gui.setItem(20, createItem(Material.DIAMOND_SWORD, "§e钻石剑", "§7minecraft:diamond_sword", "", "§e点击选择"));
        gui.setItem(21, createItem(Material.BOW, "§e弓", "§7minecraft:bow", "", "§e点击选择"));
        gui.setItem(22, createItem(Material.POTION, "§e药水", "§7minecraft:potion", "", "§e点击选择"));
        gui.setItem(23, createItem(Material.ENCHANTED_BOOK, "§e附魔书", "§7minecraft:enchanted_book", "", "§e点击选择"));
        gui.setItem(24, createItem(Material.ENDER_PEARL, "§e末影珍珠", "§7minecraft:ender_pearl", "", "§e点击选择"));
        gui.setItem(25, createItem(Material.BLAZE_POWDER, "§e烈焰粉", "§7minecraft:blaze_powder", "", "§e点击选择"));

        // 自定义输入
        gui.setItem(29, createItem(Material.WRITABLE_BOOK, "§e自定义图标", 
                "§7当前: §f" + currentIcon,
                "",
                "§7输入格式:",
                "§7- minecraft:diamond",
                "§7- diamond",
                "",
                "§e点击输入自定义ID"));

        // 重置为默认
        gui.setItem(31, createItem(Material.BARRIER, "§c恢复默认", "§7使用默认图标", "", "§e点击恢复"));

        // 返回按钮
        gui.setItem(35, createItem(Material.ARROW, "§c返回", "§7返回Toast设置"));

        fillEmptySlots(gui, Material.GRAY_STAINED_GLASS_PANE);
        player.openInventory(gui);

        GuiContext context = new GuiContext(GuiType.EDIT_TOAST_ICON);
        context.editingAnnouncement = announcement;
        playerContexts.put(player.getUniqueId(), context);
    }

    private void handleToastIconClick(Player player, String displayName, GuiContext context) {
        if (context.editingAnnouncement == null) return;

        if (displayName.contains("返回")) {
            openToastSettings(player, context.editingAnnouncement);
            return;
        }

        Announcement.DisplaySettings display = context.editingAnnouncement.getDisplay();
        if (display == null) {
            display = Announcement.DisplaySettings.builder()
                    .type(Announcement.DisplayType.TOAST)
                    .color("TASK")
                    .build();
            context.editingAnnouncement.setDisplay(display);
        }

        // 处理预设图标选择
        String[] iconMappings = {
            "书:minecraft:book",
            "钻石:minecraft:diamond",
            "绿宝石:minecraft:emerald",
            "金锭:minecraft:gold_ingot",
            "铁剑:minecraft:iron_sword",
            "箱子:minecraft:chest",
            "下界之星:minecraft:nether_star",
            "草方块:minecraft:grass_block",
            "钻石剑:minecraft:diamond_sword",
            "弓:minecraft:bow",
            "药水:minecraft:potion",
            "附魔书:minecraft:enchanted_book",
            "末影珍珠:minecraft:ender_pearl",
            "烈焰粉:minecraft:blaze_powder"
        };

        for (String mapping : iconMappings) {
            String[] parts = mapping.split(":");
            String name = parts[0];
            String iconId = parts[1] + ":" + parts[2];
            
            if (displayName.contains(name)) {
                display.setToastIcon(iconId);
                player.sendMessage(Component.text("§aToast图标已设置为: " + iconId));
                openToastIconEditor(player, context.editingAnnouncement);
                return;
            }
        }

        // 处理自定义输入
        if (displayName.contains("自定义图标")) {
            player.sendMessage(Component.text("§e请在聊天栏输入物品ID（如 minecraft:diamond 或 diamond），输入 cancel 取消"));
            player.closeInventory();
            ChatInputContext chatContext = new ChatInputContext(ChatInputType.TOAST_ICON, GuiType.EDIT_TOAST_ICON, context.editingAnnouncement);
            chatInputContexts.put(player.getUniqueId(), chatContext);
        }

        // 处理恢复默认
        if (displayName.contains("恢复默认")) {
            display.setToastIcon(null);
            player.sendMessage(Component.text("§aToast图标已恢复为默认值"));
            openToastIconEditor(player, context.editingAnnouncement);
        }
    }

    private void handleToastIconInput(Player player, String message, ChatInputContext context) {
        if (message.equalsIgnoreCase("cancel")) {
            player.sendMessage(Component.text("§c已取消操作"));
            chatInputContexts.remove(player.getUniqueId());
            
            if (schedulerAdapter != null) {
                schedulerAdapter.runOnPlayer(player, () -> {
                    openToastIconEditor(player, context.editingAnnouncement);
                });
            }
            return;
        }

        // 格式化物品ID
        String iconId = message.trim();
        if (!iconId.contains(":")) {
            iconId = "minecraft:" + iconId.toLowerCase();
        }

        if (context.editingAnnouncement.getDisplay() == null) {
            context.editingAnnouncement.setDisplay(Announcement.DisplaySettings.builder()
                    .type(Announcement.DisplayType.TOAST)
                    .color("TASK")
                    .toastIcon(iconId)
                    .build());
        } else {
            context.editingAnnouncement.getDisplay().setToastIcon(iconId);
        }

        player.sendMessage(Component.text("§aToast图标已设置为: " + iconId));
        chatInputContexts.remove(player.getUniqueId());

        if (schedulerAdapter != null) {
            schedulerAdapter.runOnPlayer(player, () -> {
                openToastIconEditor(player, context.editingAnnouncement);
                playSuccessSound(player);
            });
        }
    }

    /**
     * 打开发布服务器选择界面（36格，支持多选）
     * 区服列表优先使用 BungeeCord 自动发现，回退到配置文件
     */
    public void openServerSelector(Player player, Announcement announcement) {
        // 获取可用区服列表：优先自动发现，回退到配置
        List<String> availableServers = getAvailableServers();
        if (availableServers.isEmpty()) {
            availableServers = Arrays.asList("server1", "server2");
        }

        Inventory gui = Bukkit.createInventory(null, 36,
                Component.text("§2选择生效区服"));

        List<String> currentServers = announcement.getServers();
        boolean allServers = currentServers == null || currentServers.isEmpty() || currentServers.contains("*");

        // 信息栏：显示已选区服数量 (slot 4)
        int selectedCount = allServers ? availableServers.size() :
                (int) currentServers.stream().filter(availableServers::contains).count();
        String selectedInfo = allServers ? "§a所有区服" : "§f" + selectedCount + " §7/ §f" + availableServers.size();
        gui.setItem(4, createItem(Material.BOOK, "§b已选区服",
                "§7当前选择: " + selectedInfo,
                "§7来源: " + (hasDiscoveredServers() ? "§a自动发现" : "§e配置文件"),
                "",
                "§e左键点击区服按钮可多选切换"));

        // 区服按钮区 (slot 9~26，最多18个区服)
        int slot = 9;
        for (String server : availableServers) {
            if (slot >= 27) break;

            boolean selected = !allServers && currentServers != null && currentServers.contains(server);
            Material material = selected ? Material.LIME_WOOL : Material.WHITE_WOOL;
            String prefix = selected ? "§a✓ " : "§f";

            gui.setItem(slot, createItem(material, prefix + server,
                    selected ? "§a已选中" : "§7未选中",
                    "",
                    "§e点击切换选择"));
            slot++;
        }

        // 底部操作栏 (slot 27~35)
        // slot 27: 所有区服 toggle
        Material allMaterial = allServers ? Material.LIME_WOOL : Material.WHITE_WOOL;
        gui.setItem(27, createItem(allMaterial, "§a所有区服",
                allServers ? "§a✓ 已选中所有区服" : "§7点击选中所有区服",
                "",
                "§e点击切换"));

        // slot 29: 刷新区服列表（从 BungeeCord 重新获取）
        gui.setItem(29, createItem(Material.COMPASS, "§b刷新区服",
                "§7从 BungeeCord 重新获取区服列表",
                "",
                "§e点击刷新"));

        // slot 31: 自定义区服
        gui.setItem(31, createItem(Material.WRITABLE_BOOK, "§b自定义区服",
                "§7手动输入区服名称",
                "",
                "§e点击输入"));

        // slot 33: 确认选择
        gui.setItem(33, createItem(Material.LIME_WOOL, "§a确认选择",
                "§7保存当前选择并返回",
                "",
                "§e点击确认"));

        // slot 35: 返回
        gui.setItem(35, createItem(Material.BARRIER, "§c返回",
                "§7返回编辑界面"));

        fillEmptySlots(gui, Material.GRAY_STAINED_GLASS_PANE);
        player.openInventory(gui);

        GuiContext context = new GuiContext(GuiType.SERVER_SELECTOR);
        context.announcementId = announcement.getId();
        context.editingAnnouncement = announcement;
        playerContexts.put(player.getUniqueId(), context);
        updateContextTimestamp(player.getUniqueId());
    }

    /**
     * 获取可用区服列表：优先 BungeeCord 自动发现，回退到配置文件
     */
    private List<String> getAvailableServers() {
        // 优先使用自动发现的区服列表
        try {
            com.example.messageservice.managers.CrossServerSyncManager syncManager =
                    plugin.getCrossServerSyncManager();
            if (syncManager != null) {
                List<String> discovered = syncManager.getDiscoveredServers();
                if (discovered != null && !discovered.isEmpty()) {
                    return discovered;
                }
            }
        } catch (Exception ignored) {
        }
        // 回退到配置文件
        return plugin.getConfig().getStringList("cross-server.available-servers");
    }

    /**
     * 是否已有自动发现的区服列表
     */
    private boolean hasDiscoveredServers() {
        try {
            com.example.messageservice.managers.CrossServerSyncManager syncManager =
                    plugin.getCrossServerSyncManager();
            return syncManager != null && syncManager.getDiscoveredServers() != null
                    && !syncManager.getDiscoveredServers().isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    private void handleServerSelectorClick(Player player, String displayName, GuiContext context) {
        if (displayName.contains("返回")) {
            openEditAnnouncement(player, context.editingAnnouncement);
            return;
        }

        if (displayName.contains("确认选择")) {
            List<String> servers = context.editingAnnouncement.getServers();
            if (servers == null || servers.isEmpty()) {
                servers = new ArrayList<>();
                servers.add("*");
                context.editingAnnouncement.setServers(servers);
            }
            player.sendMessage(Component.text("§a已保存生效区服设置"));
            playSuccessSound(player);
            openEditAnnouncement(player, context.editingAnnouncement);
            return;
        }

        if (displayName.contains("所有区服")) {
            List<String> currentServers = context.editingAnnouncement.getServers();
            boolean isAll = currentServers == null || currentServers.isEmpty() || currentServers.contains("*");

            if (isAll) {
                context.editingAnnouncement.setServers(new ArrayList<>());
                player.sendMessage(Component.text("§c已取消全选，请选择特定区服"));
            } else {
                List<String> servers = new ArrayList<>();
                servers.add("*");
                context.editingAnnouncement.setServers(servers);
                player.sendMessage(Component.text("§a已设置为在所有区服生效"));
            }
            openServerSelector(player, context.editingAnnouncement);
            return;
        }

        if (displayName.contains("刷新区服")) {
            requestServerListFromBungeeCord(player);
            player.sendMessage(Component.text("§e已请求 BungeeCord 区服列表，请稍后重新打开..."));
            playClickSound(player);
            return;
        }

        if (displayName.contains("自定义区服")) {
            player.closeInventory();
            player.sendMessage(Component.text("§e请在聊天栏输入区服名称，多个区服用逗号分隔"));
            player.sendMessage(Component.text("§7例如: server1,server2,server3"));
            player.sendMessage(Component.text("§7输入 §ccancel §7取消"));

            ChatInputContext chatContext = new ChatInputContext(ChatInputType.SERVER_LIST, GuiType.SERVER_SELECTOR, context.editingAnnouncement);
            chatInputContexts.put(player.getUniqueId(), chatContext);
            return;
        }

        // 处理特定区服多选 toggle
        String serverName = displayName.replace("§a✓ ", "").replace("§f", "").replace("§e", "").trim();
        if (serverName.isEmpty()) return;

        List<String> availableServers = getAvailableServers();
        if (!availableServers.contains(serverName)) return;

        List<String> currentServers = context.editingAnnouncement.getServers();
        if (currentServers == null) {
            currentServers = new ArrayList<>();
        }

        if (currentServers.contains("*")) {
            currentServers.clear();
        }

        if (currentServers.contains(serverName)) {
            currentServers.remove(serverName);
            player.sendMessage(Component.text("§c已取消选择区服: " + serverName));
        } else {
            currentServers.add(serverName);
            player.sendMessage(Component.text("§a已选择区服: " + serverName));
        }

        context.editingAnnouncement.setServers(currentServers);
        openServerSelector(player, context.editingAnnouncement);
    }

    /**
     * 向 BungeeCord 请求区服列表
     */
    private void requestServerListFromBungeeCord(Player player) {
        try {
            com.example.messageservice.managers.CrossServerSyncManager syncManager =
                    plugin.getCrossServerSyncManager();
            if (syncManager != null) {
                syncManager.requestServerList(player);
            }
        } catch (Exception e) {
            plugin.getLogger().warning("请求 BungeeCord 区服列表失败: " + e.getMessage());
        }
    }

    private void handleServerListInput(Player player, String message, ChatInputContext context) {
        if (message.equalsIgnoreCase("cancel")) {
            player.sendMessage(Component.text("§c已取消操作"));
            chatInputContexts.remove(player.getUniqueId());
            
            if (schedulerAdapter != null) {
                schedulerAdapter.runOnPlayer(player, () -> {
                    openServerSelector(player, context.editingAnnouncement);
                });
            }
            return;
        }

        // 解析区服列表
        List<String> servers = new ArrayList<>();
        String[] parts = message.split(",");
        for (String part : parts) {
            String server = part.trim();
            if (!server.isEmpty()) {
                servers.add(server);
            }
        }

        if (servers.isEmpty()) {
            player.sendMessage(Component.text("§c请输入至少一个区服名称"));
            return;
        }

        context.editingAnnouncement.setServers(servers);
        player.sendMessage(Component.text("§a已设置生效区服: " + String.join(", ", servers)));
        chatInputContexts.remove(player.getUniqueId());

        if (schedulerAdapter != null) {
            schedulerAdapter.runOnPlayer(player, () -> {
                openServerSelector(player, context.editingAnnouncement);
                playSuccessSound(player);
            });
        }
    }

    public void openSoundSettings(Player player, Announcement announcement) {
        Inventory gui = Bukkit.createInventory(null, 27,
                Component.text("§2声音设置"));

        Announcement.SoundSettings currentSound = announcement.getSound();
        String currentSoundName = (currentSound != null && currentSound.getSound() != null) ? currentSound.getSound() : "未设置";
        float currentVolume = currentSound != null ? currentSound.getVolume() : 1.0f;
        float currentPitch = currentSound != null ? currentSound.getPitch() : 1.0f;

        gui.setItem(10, createItem(Material.NOTE_BLOCK, "§e当前音效",
                "§7音效: §f" + currentSoundName,
                "§7音量: §f" + currentVolume,
                "§7音调: §f" + currentPitch));

        gui.setItem(11, createItem(Material.LIME_DYE, "§a设置音效",
                "§7点击输入音效名称",
                "§7预设: pling, anvil, lvlup, bell",
                "§7或输入完整的音效名称"));

        gui.setItem(12, createItem(Material.GOLD_NUGGET, "§e音量: §f" + currentVolume,
                "§7当前音量: " + currentVolume,
                "§7范围: 0.0 - 2.0",
                "§e点击修改音量"));

        gui.setItem(13, createItem(Material.IRON_NUGGET, "§e音调: §f" + currentPitch,
                "§7当前音调: " + currentPitch,
                "§7范围: 0.5 - 2.0",
                "§e点击修改音调"));

        gui.setItem(14, createItem(Material.BARRIER, "§c禁用音效",
                "§7点击禁用声音"));

        gui.setItem(15, createItem(Material.MUSIC_DISC_CAT, "§b试听音效",
                "§7点击试听当前音效"));

        gui.setItem(26, createItem(Material.ARROW, "§c返回", "§7返回编辑界面"));

        fillEmptySlots(gui, Material.GRAY_STAINED_GLASS_PANE);
        player.openInventory(gui);
        GuiContext context = new GuiContext(GuiType.EDIT_SOUND_SETTINGS);
        context.editingAnnouncement = announcement;
        playerContexts.put(player.getUniqueId(), context);
    }

    private void handleSoundSettingsClick(Player player, String displayName, GuiContext context) {
        if (displayName.contains("返回")) {
            if (context.editingAnnouncement != null) {
                openEditAnnouncement(player, context.editingAnnouncement);
            }
            return;
        }

        if (displayName.contains("设置音效")) {
            player.closeInventory();
            player.sendMessage(Component.text("§e请输入音效名称"));
            player.sendMessage(Component.text("§7预设: pling, anvil, lvlup, bell"));
            player.sendMessage(Component.text("§7或输入完整的音效名称 (如: BLOCK_NOTE_BLOCK_PLING)"));
            player.sendMessage(Component.text("§7输入 §ccancel §7取消"));

            chatInputContexts.put(player.getUniqueId(), new ChatInputContext(
                    ChatInputType.SOUND_NAME,
                    GuiType.EDIT_SOUND_SETTINGS,
                    context.editingAnnouncement
            ));
        } else if (displayName.contains("音量")) {
            player.closeInventory();
            player.sendMessage(Component.text("§e请输入音量 (0.0 - 2.0)"));
            player.sendMessage(Component.text("§7输入 §ccancel §7取消"));

            chatInputContexts.put(player.getUniqueId(), new ChatInputContext(
                    ChatInputType.SOUND_NAME,
                    GuiType.EDIT_SOUND_SETTINGS,
                    context.editingAnnouncement
            ));
            chatInputContexts.get(player.getUniqueId()).lineIndex = 1; // 标记为音量设置
        } else if (displayName.contains("音调")) {
            player.closeInventory();
            player.sendMessage(Component.text("§e请输入音调 (0.5 - 2.0)"));
            player.sendMessage(Component.text("§7输入 §ccancel §7取消"));

            chatInputContexts.put(player.getUniqueId(), new ChatInputContext(
                    ChatInputType.SOUND_NAME,
                    GuiType.EDIT_SOUND_SETTINGS,
                    context.editingAnnouncement
            ));
            chatInputContexts.get(player.getUniqueId()).lineIndex = 2; // 标记为音调设置
        } else if (displayName.contains("禁用音效")) {
            context.editingAnnouncement.setSound(null);
            player.sendMessage(Component.text("§a已禁用音效"));
            openSoundSettings(player, context.editingAnnouncement);
        } else if (displayName.contains("试听音效")) {
            Announcement.SoundSettings sound = context.editingAnnouncement.getSound();
            if (sound != null && sound.getSound() != null) {
                try {
                    Sound bukkitSound = Sound.valueOf(sound.getSound());
                    player.playSound(player.getLocation(), bukkitSound, SoundCategory.MASTER, sound.getVolume(), sound.getPitch());
                    player.sendMessage(Component.text("§a正在试听: " + sound.getSound()));
                } catch (IllegalArgumentException e) {
                    player.sendMessage(Component.text("§c无效的音效名称: " + sound.getSound()));
                }
            } else {
                player.sendMessage(Component.text("§c当前未设置音效"));
            }
        }
    }

    private void handleSoundNameInput(Player player, String message, ChatInputContext context) {
        if (message.equalsIgnoreCase("cancel")) {
            player.sendMessage(Component.text("§c已取消操作"));
            chatInputContexts.remove(player.getUniqueId());

            if (schedulerAdapter != null) {
                schedulerAdapter.runOnPlayer(player, () -> {
                    openSoundSettings(player, context.editingAnnouncement);
                });
            }
            return;
        }

        // 检查是设置音效名称、音量还是音调
        if (context.lineIndex == 1) {
            // 设置音量
            try {
                float volume = Float.parseFloat(message);
                volume = Math.max(0.0f, Math.min(2.0f, volume));

                if (context.editingAnnouncement.getSound() == null) {
                    context.editingAnnouncement.setSound(Announcement.SoundSettings.builder()
                            .sound("BLOCK_NOTE_BLOCK_PLING")
                            .volume(volume)
                            .pitch(1.0f)
                            .build());
                } else {
                    context.editingAnnouncement.getSound().setVolume(volume);
                }

                player.sendMessage(Component.text("§a音量已设置为: " + volume));
            } catch (NumberFormatException e) {
                player.sendMessage(Component.text("§c请输入有效的数字 (0.0 - 2.0)"));
                playErrorSound(player);
                return;
            }
        } else if (context.lineIndex == 2) {
            // 设置音调
            try {
                float pitch = Float.parseFloat(message);
                pitch = Math.max(0.5f, Math.min(2.0f, pitch));

                if (context.editingAnnouncement.getSound() == null) {
                    context.editingAnnouncement.setSound(Announcement.SoundSettings.builder()
                            .sound("BLOCK_NOTE_BLOCK_PLING")
                            .volume(1.0f)
                            .pitch(pitch)
                            .build());
                } else {
                    context.editingAnnouncement.getSound().setPitch(pitch);
                }

                player.sendMessage(Component.text("§a音调已设置为: " + pitch));
            } catch (NumberFormatException e) {
                player.sendMessage(Component.text("§c请输入有效的数字 (0.5 - 2.0)"));
                playErrorSound(player);
                return;
            }
        } else {
            // 设置音效名称
            String soundName = message.trim().toUpperCase();
            
            // 检查是否是预设名称
            String fullSoundName = switch (soundName.toLowerCase()) {
                case "pling" -> "BLOCK_NOTE_BLOCK_PLING";
                case "anvil" -> "BLOCK_ANVIL_LAND";
                case "lvlup" -> "ENTITY_PLAYER_LEVELUP";
                case "bell" -> "BLOCK_NOTE_BLOCK_BELL";
                default -> soundName;
            };

            // 验证音效名称是否有效
            try {
                Sound.valueOf(fullSoundName);
            } catch (IllegalArgumentException e) {
                player.sendMessage(Component.text("§c无效的音效名称: " + fullSoundName));
                playErrorSound(player);
                return;
            }

            if (context.editingAnnouncement.getSound() == null) {
                context.editingAnnouncement.setSound(Announcement.SoundSettings.builder()
                        .sound(fullSoundName)
                        .volume(1.0f)
                        .pitch(1.0f)
                        .build());
            } else {
                context.editingAnnouncement.getSound().setSound(fullSoundName);
            }

            player.sendMessage(Component.text("§a音效已设置为: " + fullSoundName));
        }

        chatInputContexts.remove(player.getUniqueId());

        if (schedulerAdapter != null) {
            schedulerAdapter.runOnPlayer(player, () -> {
                openSoundSettings(player, context.editingAnnouncement);
                playSuccessSound(player);
            });
        }
    }

    /**
     * 处理定时时间输入
     */
    private void handleScheduleTimeInput(Player player, String message, ChatInputContext context) {
        if (context.editingAnnouncement == null) return;

        String schedule = message.trim().toLowerCase();
        
        if (!schedule.matches("\\d+[smh]")) {
            player.sendMessage(Component.text("§c格式错误! 请使用: 数字+单位"));
            player.sendMessage(Component.text("§7例如: 30s, 5m, 1h"));
            playErrorSound(player);
            return;
        }

        if (context.editingAnnouncement.getTrigger() == null) {
            context.editingAnnouncement.setTrigger(
                    Announcement.TriggerSettings.builder()
                            .type(Announcement.TriggerType.SCHEDULE)
                            .schedule(schedule)
                            .build()
            );
        } else {
            context.editingAnnouncement.getTrigger().setSchedule(schedule);
        }

        player.sendMessage(Component.text("§a时间间隔已设置为: " + schedule));
        chatInputContexts.remove(player.getUniqueId());

        if (schedulerAdapter != null) {
            schedulerAdapter.runOnPlayer(player, () -> {
                openScheduleSettings(player, context.editingAnnouncement);
                playSuccessSound(player);
            });
        }
    }

    /**
     * 处理 Cron 表达式输入
     */
    private void handleCronExpressionInput(Player player, String message, ChatInputContext context) {
        if (context.editingAnnouncement == null) return;

        String cronExpression = message.trim();
        
        // 验证 Cron 表达式格式
        String[] parts = cronExpression.split("\\s+");
        if (parts.length != 5 && parts.length != 6) {
            player.sendMessage(Component.text("§c格式错误! Cron表达式需要5或6个字段"));
            player.sendMessage(Component.text("§7格式: 分 时 日 月 周"));
            player.sendMessage(Component.text("§7例如: 0 * * * * (每小时整点)"));
            playErrorSound(player);
            return;
        }

        if (context.editingAnnouncement.getTrigger() == null) {
            context.editingAnnouncement.setTrigger(
                    Announcement.TriggerSettings.builder()
                            .type(Announcement.TriggerType.SCHEDULE)
                            .schedule(cronExpression)
                            .build()
            );
        } else {
            context.editingAnnouncement.getTrigger().setSchedule(cronExpression);
        }

        player.sendMessage(Component.text("§aCron表达式已设置为: " + cronExpression));
        chatInputContexts.remove(player.getUniqueId());

        if (schedulerAdapter != null) {
            schedulerAdapter.runOnPlayer(player, () -> {
                openScheduleSettings(player, context.editingAnnouncement);
                playSuccessSound(player);
            });
        }
    }

    /**
     * 打开定时触发设置界面
     */
    public void openScheduleSettings(Player player, Announcement announcement) {
        Inventory gui = Bukkit.createInventory(null, 36,
                Component.text("§2定时触发设置"));

        String currentSchedule = announcement.getTrigger() != null && announcement.getTrigger().getSchedule() != null ? 
                announcement.getTrigger().getSchedule() : "";
        
        // 判断当前是否是 Cron 表达式
        boolean isCron = isCronExpression(currentSchedule);
        String scheduleType = isCron ? "Cron表达式" : "时间间隔";

        // 时间间隔设置
        gui.setItem(10, createItem(Material.CLOCK, "§e设置时间间隔",
                "§7当前: " + (currentSchedule.isEmpty() ? "未设置" : currentSchedule),
                "",
                "§7格式说明:",
                "§f  30s §7- 30秒",
                "§f  5m §7- 5分钟",
                "§f  1h §7- 1小时",
                "",
                "§e点击设置"));

        // Cron 表达式设置
        gui.setItem(12, createItem(Material.COMPASS, "§6设置Cron表达式",
                "§7当前: " + (currentSchedule.isEmpty() ? "未设置" : currentSchedule),
                "",
                "§7Cron表达式格式:",
                "§f  分 时 日 月 周",
                "§7示例:",
                "§f  0 * * * * §7- 每小时整点",
                "§f  */5 * * * * §7- 每5分钟",
                "§f  0 9 * * 1 §7- 每周一9点",
                "",
                "§e点击设置"));

        // 当前配置信息
        List<String> configLore = new ArrayList<>();
        configLore.add("§7触发类型: §f定时触发");
        configLore.add("§7配置类型: §f" + scheduleType);
        if (!currentSchedule.isEmpty()) {
            configLore.add("§7当前值: §f" + currentSchedule);
            if (isCron) {
                configLore.add("§7解析: §f" + getCronDescription(currentSchedule));
            }
        }
        configLore.add("");
        configLore.add("§7公告将按设定时间自动发送");
        
        gui.setItem(14, createItem(Material.BOOK, "§b当前配置", configLore.toArray(new String[0])));

        // 快速预设
        gui.setItem(16, createItem(Material.CHEST, "§a快速预设",
                "§7点击选择常用设置",
                "",
                "§e点击打开"));

        // 清除设置
        gui.setItem(28, createItem(Material.REDSTONE_BLOCK, "§c清除设置",
                "§7清除定时触发设置",
                "",
                "§e点击清除"));

        gui.setItem(35, createItem(Material.ARROW, "§c返回", "§7返回触发方式选择"));

        fillEmptySlots(gui, Material.GRAY_STAINED_GLASS_PANE);
        player.openInventory(gui);

        GuiContext context = new GuiContext(GuiType.EDIT_SCHEDULE_SETTINGS);
        context.editingAnnouncement = announcement;
        playerContexts.put(player.getUniqueId(), context);
    }
    
    /**
     * 判断是否是 Cron 表达式
     */
    private boolean isCronExpression(String schedule) {
        if (schedule == null || schedule.isEmpty()) {
            return false;
        }
        return schedule.trim().split("\\s+").length >= 5;
    }
    
    /**
     * 获取 Cron 表达式描述
     */
    private String getCronDescription(String expression) {
        String[] parts = expression.trim().split("\\s+");
        if (parts.length == 5) {
            return String.format("分:%s 时:%s 日:%s 月:%s 周:%s", 
                parts[0], parts[1], parts[2], parts[3], parts[4]);
        } else if (parts.length == 6) {
            return String.format("秒:%s 分:%s 时:%s 日:%s 月:%s 周:%s", 
                parts[0], parts[1], parts[2], parts[3], parts[4], parts[5]);
        }
        return "未知格式";
    }

    /**
     * 处理定时触发设置点击
     */
    private void handleScheduleSettingsClick(Player player, String displayName, GuiContext context) {
        if (context.editingAnnouncement == null) return;

        if (displayName.contains("返回")) {
            openTriggerTypeSelector(player, context.editingAnnouncement);
            return;
        }

        if (displayName.contains("设置时间间隔")) {
            player.closeInventory();
            player.sendMessage(Component.text("§e请在聊天栏输入时间间隔"));
            player.sendMessage(Component.text("§7格式: 数字+单位 (如 30s, 5m, 1h)"));
            player.sendMessage(Component.text("§7输入 §ccancel §7取消"));
            
            ChatInputContext inputContext = new ChatInputContext(
                    ChatInputType.SCHEDULE_TIME,
                    GuiType.EDIT_SCHEDULE_SETTINGS,
                    context.editingAnnouncement
            );
            chatInputContexts.put(player.getUniqueId(), inputContext);
            return;
        }
        
        if (displayName.contains("设置Cron表达式")) {
            player.closeInventory();
            player.sendMessage(Component.text("§e请在聊天栏输入Cron表达式"));
            player.sendMessage(Component.text("§7格式: 分 时 日 月 周"));
            player.sendMessage(Component.text("§7示例:"));
            player.sendMessage(Component.text("§f  0 * * * * §7- 每小时整点"));
            player.sendMessage(Component.text("§f  */5 * * * * §7- 每5分钟"));
            player.sendMessage(Component.text("§f  0 9 * * 1 §7- 每周一9点"));
            player.sendMessage(Component.text("§7输入 §ccancel §7取消"));
            
            ChatInputContext inputContext = new ChatInputContext(
                    ChatInputType.CRON_EXPRESSION,
                    GuiType.EDIT_SCHEDULE_SETTINGS,
                    context.editingAnnouncement
            );
            chatInputContexts.put(player.getUniqueId(), inputContext);
            return;
        }
        
        if (displayName.contains("快速预设")) {
            openCronPresets(player, context.editingAnnouncement);
            return;
        }

        if (displayName.contains("清除设置")) {
            if (context.editingAnnouncement.getTrigger() != null) {
                context.editingAnnouncement.getTrigger().setSchedule("");
            }
            player.sendMessage(Component.text("§a已清除定时触发设置"));
            openScheduleSettings(player, context.editingAnnouncement);
            playSuccessSound(player);
        }
    }
    
    /**
     * 打开 Cron 表达式快速预设界面
     */
    public void openCronPresets(Player player, Announcement announcement) {
        Inventory gui = Bukkit.createInventory(null, 36,
                Component.text("§2Cron表达式预设"));

        // 常用预设
        gui.setItem(10, createItem(Material.CLOCK, "§e每分钟",
                "§7Cron: §f* * * * *",
                "§7每分钟执行一次",
                "",
                "§e点击选择"));

        gui.setItem(11, createItem(Material.CLOCK, "§e每5分钟",
                "§7Cron: §f*/5 * * * *",
                "§7每5分钟执行一次",
                "",
                "§e点击选择"));

        gui.setItem(12, createItem(Material.CLOCK, "§e每15分钟",
                "§7Cron: §f*/15 * * * *",
                "§7每15分钟执行一次",
                "",
                "§e点击选择"));

        gui.setItem(13, createItem(Material.CLOCK, "§e每小时整点",
                "§7Cron: §f0 * * * *",
                "§7每小时整点执行",
                "",
                "§e点击选择"));

        gui.setItem(14, createItem(Material.CLOCK, "§e每天凌晨",
                "§7Cron: §f0 0 * * *",
                "§7每天0点执行",
                "",
                "§e点击选择"));

        gui.setItem(15, createItem(Material.CLOCK, "§e每天早上9点",
                "§7Cron: §f0 9 * * *",
                "§7每天早上9点执行",
                "",
                "§e点击选择"));

        gui.setItem(16, createItem(Material.CLOCK, "§e每周一9点",
                "§7Cron: §f0 9 * * 1",
                "§7每周一早上9点执行",
                "",
                "§e点击选择"));

        gui.setItem(19, createItem(Material.CLOCK, "§e每周五18点",
                "§7Cron: §f0 18 * * 5",
                "§7每周五晚上6点执行",
                "",
                "§e点击选择"));

        gui.setItem(20, createItem(Material.CLOCK, "§e每月1号",
                "§7Cron: §f0 0 1 * *",
                "§7每月1号0点执行",
                "",
                "§e点击选择"));

        gui.setItem(21, createItem(Material.CLOCK, "§e每月15号",
                "§7Cron: §f0 0 15 * *",
                "§7每月15号0点执行",
                "",
                "§e点击选择"));

        gui.setItem(35, createItem(Material.ARROW, "§c返回", "§7返回定时设置"));

        fillEmptySlots(gui, Material.GRAY_STAINED_GLASS_PANE);
        player.openInventory(gui);

        GuiContext context = new GuiContext(GuiType.CRON_PRESETS);
        context.editingAnnouncement = announcement;
        playerContexts.put(player.getUniqueId(), context);
    }

    /**
     * 处理事件触发设置点击
     */
    private void handleEventSettingsClick(Player player, String displayName, int slot, GuiContext context) {
        if (context.editingAnnouncement == null) return;

        if (displayName.contains("返回")) {
            openTriggerTypeSelector(player, context.editingAnnouncement);
            return;
        }
    }

    /**
     * 打开命令触发设置界面
     */
    public void openCommandSettings(Player player, Announcement announcement) {
        Inventory gui = Bukkit.createInventory(null, 27,
                Component.text("§2命令触发设置"));

        String currentCommand = announcement.getTrigger() != null && announcement.getTrigger().getCommand() != null ? 
                announcement.getTrigger().getCommand() : "";

        gui.setItem(11, createItem(Material.COMMAND_BLOCK, "§e设置触发命令",
                "§7当前: " + (currentCommand.isEmpty() ? "未设置" : currentCommand),
                "",
                "§7当玩家执行此命令时触发公告",
                "§7命令不需要带斜杠 /",
                "",
                "§e点击设置"));

        gui.setItem(13, createItem(Material.BOOK, "§b当前配置",
                "§7触发类型: §f命令触发",
                "§7触发命令: §f" + (currentCommand.isEmpty() ? "未设置" : currentCommand),
                "",
                "§7玩家执行该命令时公告将自动发送"));

        gui.setItem(15, createItem(Material.REDSTONE_BLOCK, "§c清除设置",
                "§7清除命令触发设置",
                "",
                "§e点击清除"));

        gui.setItem(26, createItem(Material.ARROW, "§c返回", "§7返回触发方式选择"));

        fillEmptySlots(gui, Material.GRAY_STAINED_GLASS_PANE);
        player.openInventory(gui);

        GuiContext context = new GuiContext(GuiType.EDIT_COMMAND_SETTINGS);
        context.editingAnnouncement = announcement;
        playerContexts.put(player.getUniqueId(), context);
    }

    /**
     * 处理命令触发设置点击
     */
    private void handleCommandSettingsClick(Player player, String displayName, GuiContext context) {
        if (context.editingAnnouncement == null) return;

        if (displayName.contains("返回")) {
            openTriggerTypeSelector(player, context.editingAnnouncement);
            return;
        }

        if (displayName.contains("设置触发命令")) {
            player.closeInventory();
            player.sendMessage(Component.text("§e请在聊天栏输入触发命令"));
            player.sendMessage(Component.text("§7命令不需要带斜杠 /"));
            player.sendMessage(Component.text("§7例如: help, info, rules"));
            player.sendMessage(Component.text("§7输入 §ccancel §7取消"));
            
            ChatInputContext inputContext = new ChatInputContext(
                    ChatInputType.TRIGGER_COMMAND,
                    GuiType.EDIT_COMMAND_SETTINGS,
                    context.editingAnnouncement
            );
            chatInputContexts.put(player.getUniqueId(), inputContext);
            return;
        }

        if (displayName.contains("清除设置")) {
            if (context.editingAnnouncement.getTrigger() != null) {
                context.editingAnnouncement.getTrigger().setCommand("");
            }
            player.sendMessage(Component.text("§a已清除命令触发设置"));
            openCommandSettings(player, context.editingAnnouncement);
            playSuccessSound(player);
        }
    }
    
    /**
     * 处理 Cron 预设点击
     */
    private void handleCronPresetsClick(Player player, String displayName, GuiContext context) {
        if (context.editingAnnouncement == null) return;

        if (displayName.contains("返回")) {
            openScheduleSettings(player, context.editingAnnouncement);
            return;
        }

        // Cron 表达式映射
        Map<String, String> cronPresets = new HashMap<>();
        cronPresets.put("每分钟", "* * * * *");
        cronPresets.put("每5分钟", "*/5 * * * *");
        cronPresets.put("每15分钟", "*/15 * * * *");
        cronPresets.put("每小时整点", "0 * * * *");
        cronPresets.put("每天凌晨", "0 0 * * *");
        cronPresets.put("每天早上9点", "0 9 * * *");
        cronPresets.put("每周一9点", "0 9 * * 1");
        cronPresets.put("每周五18点", "0 18 * * 5");
        cronPresets.put("每月1号", "0 0 1 * *");
        cronPresets.put("每月15号", "0 0 15 * *");

        String cronExpression = cronPresets.get(displayName.replace("§e", ""));
        if (cronExpression != null) {
            if (context.editingAnnouncement.getTrigger() == null) {
                context.editingAnnouncement.setTrigger(
                        Announcement.TriggerSettings.builder()
                                .type(Announcement.TriggerType.SCHEDULE)
                                .schedule(cronExpression)
                                .build()
                );
            } else {
                context.editingAnnouncement.getTrigger().setSchedule(cronExpression);
            }

            player.sendMessage(Component.text("§a已设置 Cron 表达式: " + cronExpression));
            openScheduleSettings(player, context.editingAnnouncement);
            playSuccessSound(player);
        }
    }

    /**
     * 处理命令触发输入
     */
    private void handleTriggerCommandInput(Player player, String message, ChatInputContext context) {
        if (context.editingAnnouncement == null) return;

        String command = message.trim().toLowerCase();
        
        if (command.isEmpty()) {
            player.sendMessage(Component.text("§c命令不能为空"));
            playErrorSound(player);
            return;
        }

        if (command.startsWith("/")) {
            command = command.substring(1);
        }

        if (context.editingAnnouncement.getTrigger() == null) {
            context.editingAnnouncement.setTrigger(
                    Announcement.TriggerSettings.builder()
                            .type(Announcement.TriggerType.COMMAND)
                            .command(command)
                            .build()
            );
        } else {
            context.editingAnnouncement.getTrigger().setCommand(command);
        }

        player.sendMessage(Component.text("§a触发命令已设置为: " + command));
        chatInputContexts.remove(player.getUniqueId());

        if (schedulerAdapter != null) {
            schedulerAdapter.runOnPlayer(player, () -> {
                openCommandSettings(player, context.editingAnnouncement);
                playSuccessSound(player);
            });
        }
    }

    /**
     * 打开事件触发设置界面
     */
    public void openEventSettings(Player player, Announcement announcement) {
        Inventory gui = Bukkit.createInventory(null, 27,
                Component.text("§2事件触发设置"));

        List<String> currentEvents = announcement.getTrigger() != null && announcement.getTrigger().getEvents() != null ?
                announcement.getTrigger().getEvents() : new ArrayList<>();

        // 常用事件列表
        String[] commonEvents = {"PlayerJoinEvent", "PlayerQuitEvent", "PlayerDeathEvent", "PlayerRespawnEvent"};
        Material[] eventMaterials = {Material.PLAYER_HEAD, Material.BARRIER, Material.SKELETON_SKULL, Material.TOTEM_OF_UNDYING};

        for (int i = 0; i < commonEvents.length; i++) {
            String eventName = commonEvents[i];
            boolean isSelected = currentEvents.contains(eventName);
            gui.setItem(10 + i, createTypeItem(eventMaterials[i], eventName, isSelected,
                    "§7" + eventName, isSelected ? "§a已选择" : "§7点击选择"));
        }

        gui.setItem(22, createItem(Material.BOOK, "§b当前配置",
                "§7触发类型: §f事件触发",
                "§7已选事件: §f" + (currentEvents.isEmpty() ? "无" : String.join(", ", currentEvents)),
                "",
                "§7当这些事件发生时公告将自动发送"));

        gui.setItem(26, createItem(Material.ARROW, "§c返回", "§7返回触发方式选择"));

        fillEmptySlots(gui, Material.GRAY_STAINED_GLASS_PANE);
        player.openInventory(gui);

        GuiContext context = new GuiContext(GuiType.SELECT_TRIGGER_TYPE);
        context.editingAnnouncement = announcement;
        playerContexts.put(player.getUniqueId(), context);
    }
}
