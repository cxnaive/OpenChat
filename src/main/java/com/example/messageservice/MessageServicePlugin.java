package com.example.messageservice;

import cn.handyplus.chat.PlayerChat;
import com.example.messageservice.commands.AnnounceCommand;
import com.example.messageservice.config.ConfigManager;
import com.example.messageservice.database.DatabaseManager;
import com.example.messageservice.gui.GuiManager;
import com.example.messageservice.managers.AnnouncementManager;
import com.example.messageservice.managers.BackupManager;
import com.example.messageservice.managers.CrossServerSyncManager;
import com.example.messageservice.schedulers.ScheduleManager;
import com.example.messageservice.services.AnnouncementService;
import com.example.messageservice.utils.PlaceholderManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.InputStream;
import java.util.logging.Logger;

/**
 * MessageService 兼容桥接类。
 * 不继承 JavaPlugin（Bukkit 限制每个 JAR 只能有一个 JavaPlugin 子类），
 * 而是作为纯 POJO 将所有方法委托给 PlayerChat.INSTANCE。
 *
 * 保持包名和类名不变，确保迁移的代码无需修改引用。
 */
public class MessageServicePlugin {

    @Getter
    private static MessageServicePlugin instance;

    // 以下管理器由 PlayerChat.onEnable() 中通过 setter 注入
    @Getter private ConfigManager configManager;
    @Getter private DatabaseManager databaseManager;
    @Getter private AnnouncementManager announcementManager;
    @Getter private PlaceholderManager placeholderManager;
    @Getter private AnnouncementService announcementService;
    @Getter private ScheduleManager scheduleManager;
    @Getter private GuiManager guiManager;
    @Getter private CrossServerSyncManager crossServerSyncManager;
    @Getter private BackupManager backupManager;
    @Getter private AnnounceCommand announceCommand;

    public MessageServicePlugin() {
        if (instance == null) {
            instance = this;
        }
    }

    /**
     * 获取单例实例
     */
    public static MessageServicePlugin getInstance() {
        if (instance == null) {
            instance = new MessageServicePlugin();
        }
        return instance;
    }

    // ==================== Setter（由 PlayerChat.onEnable 调用） ====================

    public void setConfigManager(ConfigManager configManager) { this.configManager = configManager; }
    public void setDatabaseManager(DatabaseManager databaseManager) { this.databaseManager = databaseManager; }
    public void setAnnouncementManager(AnnouncementManager announcementManager) { this.announcementManager = announcementManager; }
    public void setPlaceholderManager(PlaceholderManager placeholderManager) { this.placeholderManager = placeholderManager; }
    public void setAnnouncementService(AnnouncementService announcementService) { this.announcementService = announcementService; }
    public void setScheduleManager(ScheduleManager scheduleManager) { this.scheduleManager = scheduleManager; }
    public void setGuiManager(GuiManager guiManager) { this.guiManager = guiManager; }
    public void setCrossServerSyncManager(CrossServerSyncManager crossServerSyncManager) { this.crossServerSyncManager = crossServerSyncManager; }
    public void setBackupManager(BackupManager backupManager) { this.backupManager = backupManager; }
    public void setAnnounceCommand(AnnounceCommand announceCommand) { this.announceCommand = announceCommand; }

    /**
     * 获取API实例
     */
    public com.example.messageservice.api.MessageServiceApi getApi() {
        return com.example.messageservice.api.MessageServiceApi.getInstance();
    }

    // ==================== JavaPlugin API 兼容（委托给 PlayerChat） ====================

    /**
     * 委托配置读取给 PlayerChat
     */
    public FileConfiguration getConfig() {
        return PlayerChat.INSTANCE.getConfig();
    }

    /**
     * 委托日志给 PlayerChat
     */
    public Logger getLogger() {
        return PlayerChat.INSTANCE.getLogger();
    }

    /**
     * 委托数据目录给 PlayerChat
     */
    public File getDataFolder() {
        return PlayerChat.INSTANCE.getDataFolder();
    }

    /**
     * 委托 getResource 给 PlayerChat
     */
    public InputStream getResource(String filename) {
        return PlayerChat.INSTANCE.getResource(filename);
    }

    /**
     * 委托 getServer 给 Bukkit
     */
    public Server getServer() {
        return Bukkit.getServer();
    }

    /**
     * 检查插件是否已启用（通过 PlayerChat 判断）
     */
    public boolean isEnabled() {
        return PlayerChat.INSTANCE != null && PlayerChat.INSTANCE.isEnabled();
    }

    /**
     * 委托插件描述给 PlayerChat
     */
    public org.bukkit.plugin.PluginDescriptionFile getDescription() {
        return PlayerChat.INSTANCE.getDescription();
    }

    /**
     * 获取插件名称
     */
    public String getName() {
        return PlayerChat.INSTANCE.getName();
    }

    /**
     * 重新加载配置
     */
    public void reloadConfig() {
        PlayerChat.INSTANCE.reloadConfig();
    }

    /**
     * 保存配置
     */
    public void saveConfig() {
        PlayerChat.INSTANCE.saveConfig();
    }

    /**
     * 保存默认配置
     */
    public void saveDefaultConfig() {
        PlayerChat.INSTANCE.saveDefaultConfig();
    }

    /**
     * 保存资源文件
     */
    public void saveResource(String resourcePath, boolean replace) {
        PlayerChat.INSTANCE.saveResource(resourcePath, replace);
    }
}
