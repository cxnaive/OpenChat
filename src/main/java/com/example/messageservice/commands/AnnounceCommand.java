package com.example.messageservice.commands;

import com.example.messageservice.config.ConfigManager;
import com.example.messageservice.managers.AnnouncementManager;
import com.example.messageservice.managers.CrossServerSyncManager;
import com.example.messageservice.models.Announcement;
import com.example.messageservice.services.AnnouncementService;
import com.example.messageservice.utils.PlaceholderManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class AnnounceCommand implements TabExecutor {

    private final ConfigManager configManager;
    private final AnnouncementManager announcementManager;
    private final AnnouncementService announcementService;
    private final PlaceholderManager placeholderManager;
    private final com.example.messageservice.gui.GuiManager guiManager;
    private CrossServerSyncManager crossServerSyncManager;

    public AnnounceCommand(ConfigManager configManager,
                          AnnouncementManager announcementManager,
                          AnnouncementService announcementService,
                          PlaceholderManager placeholderManager,
                          com.example.messageservice.gui.GuiManager guiManager) {
        this.configManager = configManager;
        this.announcementManager = announcementManager;
        this.announcementService = announcementService;
        this.placeholderManager = placeholderManager;
        this.guiManager = guiManager;
    }

    public void setCrossServerSyncManager(CrossServerSyncManager crossServerSyncManager) {
        this.crossServerSyncManager = crossServerSyncManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        return switch (subCommand) {
            case "create" -> handleCreate(sender, args);
            case "edit" -> handleEdit(sender, args);
            case "delete" -> handleDelete(sender, args);
            case "list" -> handleList(sender, args);
            case "send" -> handleSend(sender, args);
            case "sendnow" -> handleSendNow(sender, args);
            case "msg" -> handleBroadcast(sender, args);
            case "prefix" -> handlePrefix(sender, args);
            case "preview" -> handlePreview(sender, args);
            case "reload" -> handleReload(sender);
            case "gui" -> handleGui(sender);
            case "import" -> handleImport(sender);
            case "export" -> handleExport(sender);
            default -> {
                sendHelp(sender);
                yield true;
            }
        };
    }

    private boolean handleCreate(CommandSender sender, String[] args) {
        if (!sender.hasPermission("messageservice.create")) {
            sender.sendMessage(Component.text("你没有权限使用此命令").color(NamedTextColor.RED));
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage(Component.text("用法: /broadcast create <id> <内容>").color(NamedTextColor.RED));
            return true;
        }

        String id = args[1];
        if (announcementManager.exists(id)) {
            sender.sendMessage(Component.text("公告 '" + id + "' 已存在").color(NamedTextColor.RED));
            return true;
        }

        String content = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
        List<String> contentList = new ArrayList<>();
        contentList.add(content);

        Announcement announcement = Announcement.builder()
                .id(id)
                .enabled(true)
                .priority(1)
                .content(contentList)
                .display(Announcement.DisplaySettings.builder()
                        .type(Announcement.DisplayType.CHAT)
                        .build())
                .target(Announcement.TargetSettings.builder()
                        .type(Announcement.TargetType.ALL)
                        .value("*")
                        .build())
                .build();

        if (announcementManager.createAnnouncement(announcement)) {
            sender.sendMessage(Component.text("公告 '" + id + "' 创建成功").color(NamedTextColor.GREEN));
        } else {
            sender.sendMessage(Component.text("创建公告失败").color(NamedTextColor.RED));
        }

        return true;
    }

    private boolean handleEdit(CommandSender sender, String[] args) {
        if (!sender.hasPermission("messageservice.edit")) {
            sender.sendMessage(Component.text("你没有权限使用此命令").color(NamedTextColor.RED));
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage(Component.text("用法: /broadcast edit <id> <新内容>").color(NamedTextColor.RED));
            return true;
        }

        String id = args[1];
        Optional<Announcement> optional = announcementManager.getAnnouncement(id);
        if (optional.isEmpty()) {
            sender.sendMessage(Component.text("公告 '" + id + "' 不存在").color(NamedTextColor.RED));
            return true;
        }

        String content = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
        Announcement announcement = optional.get();
        announcement.getContent().clear();
        announcement.getContent().add(content);

        if (announcementManager.updateAnnouncement(announcement)) {
            sender.sendMessage(Component.text("公告 '" + id + "' 编辑成功").color(NamedTextColor.GREEN));
        } else {
            sender.sendMessage(Component.text("编辑公告失败").color(NamedTextColor.RED));
        }

        return true;
    }

    private boolean handleDelete(CommandSender sender, String[] args) {
        if (!sender.hasPermission("messageservice.delete")) {
            sender.sendMessage(Component.text("你没有权限使用此命令").color(NamedTextColor.RED));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(Component.text("用法: /announce delete <id>").color(NamedTextColor.RED));
            return true;
        }

        String id = args[1];
        if (!announcementManager.exists(id)) {
            sender.sendMessage(Component.text("公告 '" + id + "' 不存在").color(NamedTextColor.RED));
            return true;
        }

        if (announcementManager.deleteAnnouncement(id)) {
            sender.sendMessage(Component.text("公告 '" + id + "' 删除成功").color(NamedTextColor.GREEN));
        } else {
            sender.sendMessage(Component.text("删除公告失败").color(NamedTextColor.RED));
        }

        return true;
    }

    private boolean handleList(CommandSender sender, String[] args) {
        if (!sender.hasPermission("messageservice.list")) {
            sender.sendMessage(Component.text("你没有权限使用此命令").color(NamedTextColor.RED));
            return true;
        }

        List<Announcement> announcements = announcementManager.getAllAnnouncements();
        if (announcements.isEmpty()) {
            sender.sendMessage(Component.text("暂无公告").color(NamedTextColor.YELLOW));
            return true;
        }

        sender.sendMessage(Component.text("=== 公告列表 ===").color(NamedTextColor.GOLD));
        for (Announcement announcement : announcements) {
            String status = announcement.isEnabled() ? "§a启用" : "§c禁用";
            sender.sendMessage(Component.text("- " + announcement.getId() + " " + status)
                    .color(NamedTextColor.WHITE));
        }
        sender.sendMessage(Component.text("共 " + announcements.size() + " 个公告").color(NamedTextColor.GRAY));

        return true;
    }

    private boolean handleSend(CommandSender sender, String[] args) {
        if (!sender.hasPermission("messageservice.send")) {
            sender.sendMessage(Component.text("你没有权限使用此命令").color(NamedTextColor.RED));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(Component.text("用法: /announce send <id> [-server <服务器>]").color(NamedTextColor.RED));
            sender.sendMessage(Component.text("  -server all    发送到所有服务器").color(NamedTextColor.GRAY));
            sender.sendMessage(Component.text("  -server <服务器>  发送到指定服务器(多个用逗号分隔)").color(NamedTextColor.GRAY));
            sender.sendMessage(Component.text("  不加 -server    遵从公告配置的生效区服").color(NamedTextColor.GRAY));
            return true;
        }

        String id = args[1];
        if (!announcementManager.exists(id)) {
            sender.sendMessage(Component.text("公告 '" + id + "' 不存在").color(NamedTextColor.RED));
            return true;
        }

        // 解析参数
        List<String> targetServers = null;
        boolean hasServerParam = false;
        
        for (int i = 2; i < args.length; i++) {
            String arg = args[i];
            if (arg.equalsIgnoreCase("-server")) {
                hasServerParam = true;
                if (i + 1 < args.length) {
                    String serverArg = args[i + 1];
                    if (serverArg.equalsIgnoreCase("all")) {
                        targetServers = null;
                    } else {
                        targetServers = Arrays.asList(serverArg.split(","));
                    }
                    i++;
                }
            }
        }

        if (crossServerSyncManager != null && crossServerSyncManager.isEnabled()) {
            if (hasServerParam) {
                // 用户指定了 -server 参数
                if (targetServers == null) {
                    // -server all，发送到所有服务器
                    crossServerSyncManager.broadcastAnnouncementExecute(id);
                    sender.sendMessage(Component.text("公告 '" + id + "' 已广播到所有服务器").color(NamedTextColor.GREEN));
                } else {
                    // 发送到指定服务器
                    crossServerSyncManager.broadcastAnnouncementExecuteToServers(id, targetServers);
                    sender.sendMessage(Component.text("公告 '" + id + "' 已发送到指定服务器: " + String.join(", ", targetServers)).color(NamedTextColor.GREEN));
                }
            } else {
                // 没有 -server 参数，遵从公告配置的生效区服
                // 获取公告配置的生效区服
                Announcement announcement = announcementManager.getAnnouncement(id).orElse(null);
                List<String> configServers = announcement != null ? announcement.getServers() : null;
                
                if (configServers != null && !configServers.isEmpty() && !configServers.contains("*")) {
                    // 公告配置了特定区服，只发送到这些区服
                    crossServerSyncManager.broadcastAnnouncementExecuteToServers(id, configServers);
                    sender.sendMessage(Component.text("公告 '" + id + "' 已发送到配置的生效区服: " + String.join(", ", configServers)).color(NamedTextColor.GREEN));
                } else {
                    // 公告配置为所有区服，发送到所有服务器
                    crossServerSyncManager.broadcastAnnouncementExecute(id);
                    sender.sendMessage(Component.text("公告 '" + id + "' 已广播到所有服务器").color(NamedTextColor.GREEN));
                }
            }
        } else {
            announcementService.broadcastAnnouncement(id);
            sender.sendMessage(Component.text("公告 '" + id + "' 已在当前服务器发送").color(NamedTextColor.GREEN));
        }

        return true;
    }

    private static class BroadcastParams {
        String content;
        String soundName;
        List<String> targetServers;
        boolean isPreview;
        boolean isLocalOnly;

        BroadcastParams() {
            this.targetServers = new ArrayList<>();
            this.isPreview = false;
            this.isLocalOnly = true;
        }
    }

    private BroadcastParams parseBroadcastParams(String[] args, int startIndex) {
        BroadcastParams params = new BroadcastParams();
        List<String> contentParts = new ArrayList<>();
        
        for (int i = startIndex; i < args.length; i++) {
            String arg = args[i];
            
            if (arg.equalsIgnoreCase("-sound")) {
                if (i + 1 < args.length) {
                    params.soundName = args[i + 1].toLowerCase();
                    i++;
                }
            } else if (arg.equalsIgnoreCase("-server")) {
                if (i + 1 < args.length) {
                    String serverArg = args[i + 1];
                    if (serverArg.equalsIgnoreCase("all")) {
                        params.isLocalOnly = false;
                        params.targetServers.clear();
                    } else {
                        params.targetServers = Arrays.asList(serverArg.split(","));
                        params.isLocalOnly = false;
                    }
                    i++;
                }
            } else if (arg.equalsIgnoreCase("-preview")) {
                params.isPreview = true;
            } else {
                contentParts.add(arg);
            }
        }
        
        params.content = String.join(" ", contentParts);
        return params;
    }

    private boolean handleSendNow(CommandSender sender, String[] args) {
        if (!sender.hasPermission("messageservice.sendnow")) {
            sender.sendMessage(Component.text("你没有权限使用此命令").color(NamedTextColor.RED));
            return true;
        }

        if (args.length < 2) {
            sendBroadcastHelp(sender, "sendnow");
            return true;
        }

        BroadcastParams params = parseBroadcastParams(args, 1);

        if (params.content.isEmpty()) {
            sender.sendMessage(Component.text("用法: /broadcast sendnow <内容> [-sound <音效>] [-server <服务器>] [-preview]").color(NamedTextColor.RED));
            return true;
        }

        if (params.isPreview) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(Component.text("预览功能只能由玩家使用").color(NamedTextColor.RED));
                return true;
            }
            announcementService.sendImmediateMessage(params.content, Collections.singletonList(player), Announcement.DisplayType.CHAT);
            if (params.soundName != null) {
                SoundData soundData = parseSoundPreset(params.soundName);
                if (soundData != null) {
                    playSoundForPlayer(player, soundData);
                }
            }
            sender.sendMessage(Component.text("预览消息已发送给你").color(NamedTextColor.GREEN));
            return true;
        }

        if (!params.isLocalOnly && crossServerSyncManager != null && crossServerSyncManager.isEnabled()) {
            if (params.targetServers.isEmpty()) {
                crossServerSyncManager.broadcastTemporaryAnnouncement(params.content, Announcement.DisplayType.CHAT, params.soundName);
            } else {
                crossServerSyncManager.broadcastToSpecificServers(params.content, Announcement.DisplayType.CHAT, params.soundName, params.targetServers);
            }
        } else {
            List<Player> targets = new ArrayList<>(Bukkit.getOnlinePlayers());
            announcementService.sendImmediateMessage(params.content, targets, Announcement.DisplayType.CHAT);

            if (params.soundName != null) {
                SoundData soundData = parseSoundPreset(params.soundName);
                if (soundData != null) {
                    for (Player player : targets) {
                        playSoundForPlayer(player, soundData);
                    }
                }
            }
        }

        sender.sendMessage(Component.text("消息已发送").color(NamedTextColor.GREEN));
        if (params.soundName != null) {
            sender.sendMessage(Component.text("已播放音效: " + params.soundName).color(NamedTextColor.YELLOW));
        }
        if (!params.isLocalOnly) {
            if (params.targetServers.isEmpty()) {
                sender.sendMessage(Component.text("已同步到所有服务器").color(NamedTextColor.AQUA));
            } else {
                sender.sendMessage(Component.text("已发送到指定服务器: " + String.join(", ", params.targetServers)).color(NamedTextColor.AQUA));
            }
        }

        return true;
    }

    private boolean handleBroadcast(CommandSender sender, String[] args) {
        if (!sender.hasPermission("messageservice.broadcast")) {
            sender.sendMessage(Component.text("你没有权限使用此命令").color(NamedTextColor.RED));
            return true;
        }

        if (args.length < 2) {
            sendBroadcastHelp(sender, "msg");
            return true;
        }

        BroadcastParams params = parseBroadcastParams(args, 1);

        if (params.content.isEmpty()) {
            sender.sendMessage(Component.text("用法: /broadcast msg <内容> [-sound <音效>] [-server <服务器>] [-preview]").color(NamedTextColor.RED));
            return true;
        }

        String prefix = configManager.getPrefix();
        String fullMessage = prefix + params.content;

        if (params.isPreview) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(Component.text("预览功能只能由玩家使用").color(NamedTextColor.RED));
                return true;
            }
            announcementService.sendImmediateMessage(fullMessage, Collections.singletonList(player), Announcement.DisplayType.CHAT);
            if (params.soundName != null) {
                SoundData soundData = parseSoundPreset(params.soundName);
                if (soundData != null) {
                    playSoundForPlayer(player, soundData);
                }
            }
            sender.sendMessage(Component.text("预览消息已发送给你").color(NamedTextColor.GREEN));
            return true;
        }

        if (!params.isLocalOnly && crossServerSyncManager != null && crossServerSyncManager.isEnabled()) {
            if (params.targetServers.isEmpty()) {
                crossServerSyncManager.broadcastTemporaryAnnouncement(fullMessage, Announcement.DisplayType.CHAT, params.soundName);
            } else {
                crossServerSyncManager.broadcastToSpecificServers(fullMessage, Announcement.DisplayType.CHAT, params.soundName, params.targetServers);
            }
        } else {
            List<Player> targets = new ArrayList<>(Bukkit.getOnlinePlayers());
            announcementService.sendImmediateMessage(fullMessage, targets, Announcement.DisplayType.CHAT);

            if (params.soundName != null) {
                SoundData soundData = parseSoundPreset(params.soundName);
                if (soundData != null) {
                    for (Player player : targets) {
                        playSoundForPlayer(player, soundData);
                    }
                }
            }
        }

        sender.sendMessage(Component.text("广播消息已发送").color(NamedTextColor.GREEN));
        if (params.soundName != null) {
            sender.sendMessage(Component.text("已播放音效: " + params.soundName).color(NamedTextColor.YELLOW));
        }
        if (!params.isLocalOnly) {
            if (params.targetServers.isEmpty()) {
                sender.sendMessage(Component.text("已同步到所有服务器").color(NamedTextColor.AQUA));
            } else {
                sender.sendMessage(Component.text("已发送到指定服务器: " + String.join(", ", params.targetServers)).color(NamedTextColor.AQUA));
            }
        }

        return true;
    }

    private void sendBroadcastHelp(CommandSender sender, String command) {
        sender.sendMessage(Component.text("=== " + command + " 命令帮助 ===").color(NamedTextColor.GOLD));
        sender.sendMessage(Component.text("用法: /broadcast " + command + " <内容> [参数]").color(NamedTextColor.YELLOW));
        sender.sendMessage(Component.text("").color(NamedTextColor.WHITE));
        sender.sendMessage(Component.text("参数:").color(NamedTextColor.AQUA));
        sender.sendMessage(Component.text("  -sound <音效>  - 播放指定音效 (pling, anvil, lvlup, bell)").color(NamedTextColor.WHITE));
        sender.sendMessage(Component.text("  -server <服务器> - 指定目标服务器").color(NamedTextColor.WHITE));
        sender.sendMessage(Component.text("      all - 发送到所有服务器").color(NamedTextColor.GRAY));
        sender.sendMessage(Component.text("      server1,server2 - 发送到指定服务器(逗号分隔)").color(NamedTextColor.GRAY));
        sender.sendMessage(Component.text("      不指定 - 仅发送到当前服务器").color(NamedTextColor.GRAY));
        sender.sendMessage(Component.text("  -preview       - 预览模式，仅发送给自己").color(NamedTextColor.WHITE));
        sender.sendMessage(Component.text("").color(NamedTextColor.WHITE));
        sender.sendMessage(Component.text("示例:").color(NamedTextColor.AQUA));
        sender.sendMessage(Component.text("  /broadcast " + command + " 欢迎来到服务器").color(NamedTextColor.GRAY));
        sender.sendMessage(Component.text("  /broadcast " + command + " 维护通知 -sound pling").color(NamedTextColor.GRAY));
        sender.sendMessage(Component.text("  /broadcast " + command + " 活动开始 -server all").color(NamedTextColor.GRAY));
        sender.sendMessage(Component.text("  /broadcast " + command + " 测试消息 -server server1,server2").color(NamedTextColor.GRAY));
        sender.sendMessage(Component.text("  /broadcast " + command + " 预览测试 -preview").color(NamedTextColor.GRAY));
    }
    
    private SoundData parseSoundPreset(String preset) {
        return switch (preset.toLowerCase()) {
            case "pling" -> new SoundData(Sound.BLOCK_NOTE_BLOCK_PLING, 2.0f);
            case "anvil" -> new SoundData(Sound.BLOCK_ANVIL_LAND, 1.0f);
            case "lvlup" -> new SoundData(Sound.ENTITY_PLAYER_LEVELUP, 1.0f);
            case "bell" -> new SoundData(Sound.BLOCK_NOTE_BLOCK_BELL, 2.0f);
            default -> null;
        };
    }
    
    private void playSoundForPlayer(Player player, SoundData soundData) {
        try {
            Location loc = player.getLocation();
            player.playSound(loc, soundData.sound(), SoundCategory.MASTER, 1.0f, soundData.pitch());
        } catch (Exception e) {
        }
    }
    
    private record SoundData(Sound sound, float pitch) {}
    
    private boolean handlePrefix(CommandSender sender, String[] args) {
        if (!sender.hasPermission("messageservice.admin")) {
            sender.sendMessage(Component.text("你没有权限使用此命令").color(NamedTextColor.RED));
            return true;
        }
        
        if (args.length == 1) {
            String currentPrefix = configManager.getPrefix();
            sender.sendMessage(Component.text("当前前缀: ").color(NamedTextColor.YELLOW)
                    .append(Component.text(currentPrefix).color(NamedTextColor.WHITE)));
            sender.sendMessage(Component.text("用法: /broadcast prefix <新前缀>").color(NamedTextColor.GRAY));
            sender.sendMessage(Component.text("使用 & 作为颜色代码前缀").color(NamedTextColor.GRAY));
            return true;
        }
        
        String newPrefix = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        configManager.setPrefix(newPrefix);
        
        sender.sendMessage(Component.text("前缀已设置为: ").color(NamedTextColor.GREEN)
                .append(Component.text(newPrefix).color(NamedTextColor.WHITE)));
        
        return true;
    }

    private boolean handleReload(CommandSender sender) {
        if (!sender.hasPermission("messageservice.reload")) {
            sender.sendMessage(Component.text("你没有权限使用此命令").color(NamedTextColor.RED));
            return true;
        }

        configManager.reloadConfigs();
        announcementManager.reload();
        sender.sendMessage(Component.text("配置已重新加载").color(NamedTextColor.GREEN));

        return true;
    }

    private boolean handlePreview(CommandSender sender, String[] args) {
        if (!sender.hasPermission("messageservice.preview")) {
            sender.sendMessage(Component.text("你没有权限使用此命令").color(NamedTextColor.RED));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(Component.text("用法: /broadcast preview <id> [玩家]").color(NamedTextColor.RED));
            return true;
        }

        String id = args[1];
        Optional<Announcement> optional = announcementManager.getAnnouncement(id);
        if (optional.isEmpty()) {
            sender.sendMessage(Component.text("公告 '" + id + "' 不存在").color(NamedTextColor.RED));
            return true;
        }

        Player target;
        if (args.length >= 3) {
            target = Bukkit.getPlayer(args[2]);
            if (target == null) {
                sender.sendMessage(Component.text("玩家 '" + args[2] + "' 不在线").color(NamedTextColor.RED));
                return true;
            }
        } else if (sender instanceof Player) {
            target = (Player) sender;
        } else {
            sender.sendMessage(Component.text("控制台必须指定玩家").color(NamedTextColor.RED));
            return true;
        }

        announcementService.sendAnnouncementToPlayer(id, target);
        sender.sendMessage(Component.text("预览已发送给 " + target.getName()).color(NamedTextColor.GREEN));

        return true;
    }

    private boolean handleGui(CommandSender sender) {
        if (!sender.hasPermission("messageservice.gui")) {
            sender.sendMessage(Component.text("你没有权限使用此命令").color(NamedTextColor.RED));
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("此命令只能由玩家执行").color(NamedTextColor.RED));
            return true;
        }

        guiManager.openMainMenu(player);
        return true;
    }

    private boolean handleImport(CommandSender sender) {
        if (!sender.hasPermission("messageservice.admin")) {
            sender.sendMessage(Component.text("你没有权限使用此命令").color(NamedTextColor.RED));
            return true;
        }

        int count = announcementManager.importFromConfig();
        if (count > 0) {
            sender.sendMessage(Component.text("成功从配置文件导入 " + count + " 个公告到数据库").color(NamedTextColor.GREEN));
        } else {
            sender.sendMessage(Component.text("没有导入任何公告（配置文件为空或数据库未连接）").color(NamedTextColor.YELLOW));
        }

        return true;
    }

    private boolean handleExport(CommandSender sender) {
        if (!sender.hasPermission("messageservice.admin")) {
            sender.sendMessage(Component.text("你没有权限使用此命令").color(NamedTextColor.RED));
            return true;
        }

        int count = announcementManager.exportToConfig();
        sender.sendMessage(Component.text("成功导出 " + count + " 个公告到配置文件").color(NamedTextColor.GREEN));

        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(Component.text("=== MessageService 帮助 ===").color(NamedTextColor.GOLD));
        sender.sendMessage(Component.text("/broadcast create <id> <内容> - 创建公告").color(NamedTextColor.YELLOW));
        sender.sendMessage(Component.text("/broadcast edit <id> <新内容> - 编辑公告").color(NamedTextColor.YELLOW));
        sender.sendMessage(Component.text("/broadcast delete <id> - 删除公告").color(NamedTextColor.YELLOW));
        sender.sendMessage(Component.text("/broadcast list - 列出所有公告").color(NamedTextColor.YELLOW));
        sender.sendMessage(Component.text("/broadcast send <id> - 发送公告").color(NamedTextColor.YELLOW));
        sender.sendMessage(Component.text("/broadcast sendnow <内容> [参数] - 立即发送临时消息(无前缀)").color(NamedTextColor.YELLOW));
        sender.sendMessage(Component.text("/broadcast msg <内容> [参数] - 广播消息(带前缀)").color(NamedTextColor.YELLOW));
        sender.sendMessage(Component.text("  参数: -sound <音效> -server <服务器> -preview").color(NamedTextColor.GRAY));
        sender.sendMessage(Component.text("/broadcast prefix [新前缀] - 查看或设置消息前缀").color(NamedTextColor.YELLOW));
        sender.sendMessage(Component.text("/broadcast preview <id> [玩家] - 预览公告").color(NamedTextColor.YELLOW));
        sender.sendMessage(Component.text("/broadcast reload - 重新加载配置").color(NamedTextColor.YELLOW));
        sender.sendMessage(Component.text("/broadcast gui - 打开图形化界面").color(NamedTextColor.YELLOW));
        if (sender.hasPermission("messageservice.admin")) {
            sender.sendMessage(Component.text("/broadcast import - 从配置文件导入公告到数据库").color(NamedTextColor.YELLOW));
            sender.sendMessage(Component.text("/broadcast export - 导出公告到配置文件（备份）").color(NamedTextColor.YELLOW));
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> commands = new ArrayList<>(Arrays.asList("create", "edit", "delete", "list", "send", "sendnow", "msg", "prefix", "preview", "reload", "gui"));
            if (sender.hasPermission("messageservice.admin")) {
                commands.addAll(Arrays.asList("import", "export"));
            }
            return commands.stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (args.length == 2) {
            String subCommand = args[0].toLowerCase();
            if (subCommand.equals("send") || subCommand.equals("edit") || 
                subCommand.equals("delete") || subCommand.equals("preview")) {
                return announcementManager.getAllAnnouncements().stream()
                        .map(Announcement::getId)
                        .filter(id -> id.startsWith(args[1]))
                        .collect(Collectors.toList());
            }
        }
        
        if (args.length >= 2 && (args[0].equalsIgnoreCase("msg") || args[0].equalsIgnoreCase("sendnow"))) {
            return handleBroadcastTabComplete(args);
        }
        
        if (args.length >= 3 && args[0].equalsIgnoreCase("send")) {
            return handleSendTabComplete(args);
        }

        return Collections.emptyList();
    }

    private List<String> handleSendTabComplete(String[] args) {
        String lastArg = args[args.length - 1].toLowerCase();
        
        boolean hasServerFlag = false;
        
        for (int i = 2; i < args.length - 1; i++) {
            if (args[i].equalsIgnoreCase("-server")) {
                hasServerFlag = true;
            }
        }
        
        if (args.length >= 3 && args[args.length - 2].equalsIgnoreCase("-server")) {
            List<String> servers = getAvailableServers();
            if (!servers.contains("all")) {
                servers.add(0, "all");
            }
            return servers.stream()
                    .filter(s -> s.startsWith(lastArg))
                    .collect(Collectors.toList());
        }
        
        List<String> suggestions = new ArrayList<>();
        
        if (!hasServerFlag && crossServerSyncManager != null && crossServerSyncManager.isEnabled()) {
            suggestions.add("-server");
        }
        
        return suggestions.stream()
                .filter(s -> s.startsWith(lastArg))
                .collect(Collectors.toList());
    }

    private List<String> handleBroadcastTabComplete(String[] args) {
        String lastArg = args[args.length - 1].toLowerCase();
        
        boolean hasSoundFlag = false;
        boolean hasServerFlag = false;
        boolean hasPreview = false;
        
        for (int i = 1; i < args.length - 1; i++) {
            if (args[i].equalsIgnoreCase("-sound")) {
                hasSoundFlag = true;
            } else if (args[i].equalsIgnoreCase("-server")) {
                hasServerFlag = true;
            } else if (args[i].equalsIgnoreCase("-preview")) {
                hasPreview = true;
            }
        }
        
        if (args.length >= 2 && args[args.length - 2].equalsIgnoreCase("-sound")) {
            return Arrays.asList("pling", "anvil", "lvlup", "bell").stream()
                    .filter(s -> s.startsWith(lastArg))
                    .collect(Collectors.toList());
        }
        
        if (args.length >= 2 && args[args.length - 2].equalsIgnoreCase("-server")) {
            List<String> servers = getAvailableServers();
            if (!servers.contains("all")) {
                servers.add(0, "all");
            }
            return servers.stream()
                    .filter(s -> s.startsWith(lastArg))
                    .collect(Collectors.toList());
        }
        
        List<String> suggestions = new ArrayList<>();
        
        if (!hasSoundFlag) {
            suggestions.add("-sound");
        }
        if (!hasServerFlag && crossServerSyncManager != null && crossServerSyncManager.isEnabled()) {
            suggestions.add("-server");
        }
        if (!hasPreview) {
            suggestions.add("-preview");
        }
        
        return suggestions.stream()
                .filter(s -> s.startsWith(lastArg))
                .collect(Collectors.toList());
    }

    private List<String> getAvailableServers() {
        List<String> servers = new ArrayList<>();
        if (plugin != null && plugin.getConfig() != null) {
            List<String> configServers = plugin.getConfig().getStringList("cross-server.available-servers");
            if (configServers != null) {
                servers.addAll(configServers);
            }
        }
        return servers;
    }

    private com.example.messageservice.MessageServicePlugin plugin;

    public void setPlugin(com.example.messageservice.MessageServicePlugin plugin) {
        this.plugin = plugin;
    }
}
