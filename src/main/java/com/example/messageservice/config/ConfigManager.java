package com.example.messageservice.config;

import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import com.example.messageservice.MessageServicePlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.logging.Level;

@Getter
public class ConfigManager {

    private final MessageServicePlugin plugin;
    private FileConfiguration config;
    private FileConfiguration announcementsConfig;
    
    private int maxPerTick;
    private int batchSize;
    private int queueCheckInterval;
    private String prefix;
    private String defaultColor;
    private String dateFormat;
    private boolean regionCheckEnabled;
    private String fallbackScheduler;
    private boolean debugEnabled;

    public ConfigManager(MessageServicePlugin plugin) {
        this.plugin = plugin;
        loadConfigs();
    }

    public void loadConfigs() {
        plugin.saveDefaultConfig();
        config = plugin.getConfig();
        
        loadValues();
        loadAnnouncementsConfig();
    }

    private void loadValues() {
        debugEnabled = config.getBoolean("debug.enabled", false);
        
        maxPerTick = config.getInt("performance.max-per-tick", 50);
        batchSize = config.getInt("performance.batch-size", 100);
        queueCheckInterval = config.getInt("performance.queue-check-interval", 20);
        
        prefix = config.getString("format.prefix", "&6[公告] &f");
        defaultColor = config.getString("format.default-color", "&e");
        dateFormat = config.getString("format.date-format", "yyyy-MM-dd HH:mm");
        
        regionCheckEnabled = config.getBoolean("threading.region-check-enabled", true);
        fallbackScheduler = config.getString("threading.fallback-scheduler", "async");
    }

    private void loadAnnouncementsConfig() {
        File announcementsFile = new File(cn.handyplus.chat.PlayerChat.INSTANCE.getDataFolder(), "announcements.yml");
        if (!announcementsFile.exists()) {
            createDefaultAnnouncementsFile(announcementsFile);
        }
        announcementsConfig = YamlConfiguration.loadConfiguration(announcementsFile);
    }

    private void createDefaultAnnouncementsFile(File file) {
        try (InputStream in = plugin.getResource("announcements.yml")) {
            if (in != null) {
                Files.copy(in, file.toPath());
            } else {
                file.createNewFile();
            }
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "无法创建公告配置文件", e);
        }
    }

    public void reloadConfigs() {
        plugin.reloadConfig();
        loadConfigs();
        plugin.getLogger().info("配置已重新加载");
    }

    public void saveAnnouncementsConfig() {
        File announcementsFile = new File(cn.handyplus.chat.PlayerChat.INSTANCE.getDataFolder(), "announcements.yml");
        try {
            announcementsConfig.save(announcementsFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "无法保存公告配置", e);
        }
    }

    public FileConfiguration getAnnouncementsConfig() {
        return announcementsConfig;
    }
    
    public void setPrefix(String newPrefix) {
        this.prefix = newPrefix;
        config.set("format.prefix", newPrefix);
        plugin.saveConfig();
    }
}
