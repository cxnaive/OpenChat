package com.example.messageservice.managers;

import com.example.messageservice.MessageServicePlugin;
import com.example.messageservice.cache.AnnouncementCache;
import com.example.messageservice.config.ConfigManager;
import com.example.messageservice.database.AnnouncementDatabase;
import com.example.messageservice.database.DatabaseManager;
import com.example.messageservice.models.Announcement;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import com.example.messageservice.MessageServicePlugin;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class AnnouncementManager {

    private final MessageServicePlugin plugin;
    private final ConfigManager configManager;
    private final DatabaseManager databaseManager;
    private AnnouncementDatabase announcementDatabase;

    private final Map<String, Announcement> announcements = new ConcurrentHashMap<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    @Getter
    private final Map<String, Long> globalCooldowns = new ConcurrentHashMap<>();
    @Getter
    private final Map<UUID, Map<String, Long>> playerCooldowns = new ConcurrentHashMap<>();

    // 公告缓存管理器
    private AnnouncementCache announcementCache;

    // 跨服同步管理器
    private CrossServerSyncManager crossServerSyncManager;
    private boolean syncEnabled = false;

    public AnnouncementManager(MessageServicePlugin plugin, ConfigManager configManager) {
        this(plugin, configManager, null);
    }

    public AnnouncementManager(MessageServicePlugin plugin, ConfigManager configManager, DatabaseManager databaseManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.databaseManager = databaseManager;

        if (databaseManager != null && databaseManager.isConnected()) {
            this.announcementDatabase = new AnnouncementDatabase(plugin, databaseManager);
            this.announcementCache = new AnnouncementCache((MessageServicePlugin) plugin, announcementDatabase);
        }

        // 加载公告（从缓存）
        loadAnnouncements();
    }
    
    /**
     * 设置跨服同步管理器
     */
    public void setCrossServerSyncManager(CrossServerSyncManager manager) {
        this.crossServerSyncManager = manager;
    }

    /**
     * 启用/禁用跨服同步
     */
    public void setSyncEnabled(boolean enabled) {
        this.syncEnabled = enabled;
    }

    /**
     * 检查跨服同步是否启用
     */
    public boolean isSyncEnabled() {
        return syncEnabled && crossServerSyncManager != null;
    }

    public void loadAnnouncements() {
        lock.writeLock().lock();
        try {
            announcements.clear();

            // 使用缓存管理器加载
            if (announcementCache != null) {
                announcementCache.initialize();
                
                String currentServer = getCurrentServerName();
                int filteredCount = 0;
                
                for (Announcement announcement : announcementCache.getAllAnnouncements()) {
                    if (announcement != null && announcement.getId() != null) {
                        if (isAnnouncementValidForCurrentServer(announcement)) {
                            announcements.put(announcement.getId(), announcement);
                        } else {
                            filteredCount++;
                        }
                    }
                }
                
                plugin.getLogger().info("从缓存加载了 " + announcements.size() + " 个公告，过滤了 " + filteredCount + " 个");
            } else {
                // 数据库不可用，从配置文件加载
                loadFromConfig();
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * 从配置文件加载公告（用于数据库不可用时的回退）
     */
    private void loadFromConfig() {
        ConfigurationSection section = configManager.getAnnouncementsConfig().getConfigurationSection("announcements");

        if (section == null) {
            plugin.getLogger().warning("没有找到公告配置");
            return;
        }

        String currentServer = getCurrentServerName();
        int filteredCount = 0;
        for (String key : section.getKeys(false)) {
            try {
                ConfigurationSection announcementSection = section.getConfigurationSection(key);
                if (announcementSection == null) continue;

                Announcement announcement = Announcement.fromConfig(key, announcementSection);
                if (announcement != null) {
                    // 检查公告是否对当前服务器生效
                    if (isAnnouncementValidForCurrentServer(announcement)) {
                        announcements.put(key, announcement);
                    } else {
                        filteredCount++;
                    }
                }
            } catch (Exception e) {
                plugin.getLogger().log(Level.WARNING, "加载公告 '" + key + "' 时出错", e);
            }
        }

        plugin.getLogger().info("从配置文件加载了 " + announcements.size() + " 个公告，过滤了 " + filteredCount + " 个");
    }

    /**
     * 从配置文件导入公告到数据库
     */
    public int importFromConfig() {
        if (announcementDatabase == null || !databaseManager.isConnected()) {
            plugin.getLogger().warning("数据库未连接，无法导入");
            return 0;
        }

        ConfigurationSection section = configManager.getAnnouncementsConfig().getConfigurationSection("announcements");
        if (section == null) {
            plugin.getLogger().warning("配置文件中没有找到公告");
            return 0;
        }

        int count = 0;
        String serverId = getCurrentServerName();

        for (String key : section.getKeys(false)) {
            try {
                ConfigurationSection announcementSection = section.getConfigurationSection(key);
                if (announcementSection == null) continue;

                Announcement announcement = Announcement.fromConfig(key, announcementSection);
                if (announcement != null) {
                    // 设置初始版本号
                    if (announcement.getVersion() == null) {
                        announcement.setVersion(1);
                    }
                    announcementDatabase.saveAnnouncement(announcement, serverId);
                    count++;
                }
            } catch (Exception e) {
                plugin.getLogger().log(Level.WARNING, "导入公告 '" + key + "' 时出错", e);
            }
        }

        plugin.getLogger().info("从配置文件导入了 " + count + " 个公告到数据库");

        // 重新加载
        loadAnnouncements();

        return count;
    }

    /**
     * 导出公告到配置文件（备份）
     */
    public int exportToConfig() {
        lock.readLock().lock();
        try {
            // 清空现有配置
            configManager.getAnnouncementsConfig().set("announcements", null);

            for (Announcement announcement : announcements.values()) {
                saveToConfigOnly(announcement);
            }

            configManager.saveAnnouncementsConfig();
            plugin.getLogger().info("已导出 " + announcements.size() + " 个公告到配置文件");
            return announcements.size();
        } finally {
            lock.readLock().unlock();
        }
    }

    public Optional<Announcement> getAnnouncement(String id) {
        lock.readLock().lock();
        try {
            return Optional.ofNullable(announcements.get(id));
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * 获取公告（检查并刷新到最新版本）
     * 在发送公告前调用此方法确保公告是最新的
     */
    public Optional<Announcement> getAnnouncementWithRefresh(String id) {
        lock.writeLock().lock();
        try {
            if (announcementCache != null) {
                Announcement fresh = announcementCache.checkAndRefreshAnnouncement(id);
                if (fresh != null) {
                    announcements.put(id, fresh);
                    return Optional.of(fresh);
                }
            }
            return Optional.ofNullable(announcements.get(id));
        } finally {
            lock.writeLock().unlock();
        }
    }

    public List<Announcement> getAllAnnouncements() {
        lock.readLock().lock();
        try {
            return new ArrayList<>(announcements.values());
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<Announcement> getEnabledAnnouncements() {
        lock.readLock().lock();
        try {
            return announcements.values().stream()
                    .filter(Announcement::isEnabled)
                    .filter(this::isAnnouncementValidForCurrentServer)
                    .sorted(Comparator.comparingInt(Announcement::getPriority).reversed())
                    .collect(Collectors.toList());
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * 获取所有公告（包含跨服同步的）
     */
    public List<Announcement> getAllAnnouncementsIncludingCrossServer() {
        lock.readLock().lock();
        try {
            return new ArrayList<>(announcements.values());
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * 检查公告是否对当前服务器生效
     */
    private boolean isAnnouncementValidForCurrentServer(Announcement announcement) {
        List<String> servers = announcement.getServers();
        if (servers == null || servers.isEmpty()) {
            return true; // 没有设置则对所有服务器生效
        }
        if (servers.contains("*")) {
            return true; // * 表示所有服务器
        }
        String currentServer = getCurrentServerName();
        return servers.contains(currentServer);
    }

    /**
     * 获取当前服务器名称
     */
    private String getCurrentServerName() {
        return plugin.getConfig().getString("cross-server.server-name", "unknown");
    }

    public boolean createAnnouncement(Announcement announcement) {
        return createAnnouncement(announcement, true);
    }
    
    public boolean createAnnouncement(Announcement announcement, boolean sync) {
        if (announcement == null || announcement.getId() == null) {
            return false;
        }

        lock.writeLock().lock();
        try {
            if (announcements.containsKey(announcement.getId())) {
                return false;
            }
            
            announcements.put(announcement.getId(), announcement);
            saveToConfig(announcement);
            
            // 跨服同步
            if (sync && isSyncEnabled()) {
                crossServerSyncManager.saveAndBroadcast(announcement);
            }

            return true;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public boolean updateAnnouncement(Announcement announcement) {
        return updateAnnouncement(announcement, true);
    }
    
    public boolean updateAnnouncement(Announcement announcement, boolean sync) {
        if (announcement == null || announcement.getId() == null) {
            return false;
        }

        String currentServer = getCurrentServerName();

        lock.writeLock().lock();
        try {
            if (!announcements.containsKey(announcement.getId())) {
                return false;
            }
            
            // 增加版本号
            int currentVersion = announcement.getVersion() != null ? announcement.getVersion() : 1;
            announcement.setVersion(currentVersion + 1);
            
            announcements.put(announcement.getId(), announcement);
            
            // 使用缓存管理器
            if (announcementCache != null) {
                announcementCache.putAnnouncement(announcement);
            }
            
            // 保存到数据库
            if (announcementDatabase != null && databaseManager.isConnected()) {
                databaseManager.getDatabaseQueue().submit("saveAnnouncement", conn -> {
                    announcementDatabase.saveAnnouncement(announcement, currentServer);
                    return null;
                }, null, e -> plugin.getLogger().log(Level.WARNING, "从数据库保存公告失败", e));
            }
            
            // 跨服同步 - 发送版本更新通知
            if (sync && isSyncEnabled()) {
                crossServerSyncManager.broadcastAnnouncementUpdate(announcement.getVersion(), currentServer);
            }

            return true;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public boolean deleteAnnouncement(String id) {
        return deleteAnnouncement(id, true);
    }

    public boolean deleteAnnouncement(String id, boolean sync) {
        lock.writeLock().lock();
        try {
            if (!announcements.containsKey(id)) {
                return false;
            }

            announcements.remove(id);
            
            // 使用缓存管理器
            if (announcementCache != null) {
                announcementCache.removeAnnouncement(id);
            }
            
            // 从数据库删除
            if (announcementDatabase != null && databaseManager.isConnected()) {
                databaseManager.getDatabaseQueue().submit("deleteAnnouncement", conn -> {
                    announcementDatabase.deleteAnnouncement(id);
                    return null;
                }, null, e -> plugin.getLogger().log(Level.WARNING, "从数据库删除公告失败", e));
            }

            // 跨服同步
            if (sync && isSyncEnabled()) {
                crossServerSyncManager.deleteAndBroadcast(id);
            }

            return true;
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * 重命名公告ID
     * 本质上是：旧ID删除 + 新ID创建
     *
     * @param oldId 旧ID
     * @param newId 新ID
     * @return 是否成功
     */
    public boolean renameAnnouncement(String oldId, String newId) {
        lock.writeLock().lock();
        try {
            if (!announcements.containsKey(oldId)) return false;
            if (announcements.containsKey(newId)) return false;

            Announcement announcement = announcements.remove(oldId);
            String oldIdForDelete = oldId;
            announcement.setId(newId);
            announcements.put(newId, announcement);

            // 更新缓存
            if (announcementCache != null) {
                announcementCache.removeAnnouncement(oldIdForDelete);
                announcementCache.putAnnouncement(announcement);
            }

            // 删除旧ID的数据库记录，保存新ID
            if (announcementDatabase != null && databaseManager.isConnected()) {
                databaseManager.getDatabaseQueue().submit("renameAnnouncement", conn -> {
                    announcementDatabase.deleteAnnouncement(oldIdForDelete);
                    String currentServer = plugin.getConfig().getString("cross-server.server-name", "unknown");
                    announcementDatabase.saveAnnouncement(announcement, currentServer);
                    return null;
                }, null, e -> plugin.getLogger().log(Level.WARNING, "重命名公告数据库记录失败", e));
            }

            // 保存到配置
            saveToConfig(announcement);

            // 跨服同步
            if (isSyncEnabled()) {
                crossServerSyncManager.deleteAndBroadcast(oldIdForDelete);
                crossServerSyncManager.saveAndBroadcast(announcement);
            }

            plugin.getLogger().info("公告ID已从 '" + oldIdForDelete + "' 改为 '" + newId + "'");
            return true;
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * 添加公告（用于跨服同步接收）
     * 会检查公告是否对当前服务器生效
     */
    public void addAnnouncement(Announcement announcement, boolean sync) {
        if (announcement == null || announcement.getId() == null) {
            return;
        }

        // 检查公告是否对当前服务器生效
        String currentServer = getCurrentServerName();
        List<String> servers = announcement.getServers();
        if (!isAnnouncementValidForCurrentServer(announcement)) {
            return;
        }

        lock.writeLock().lock();
        try {
            announcements.put(announcement.getId(), announcement);
            
            // 使用缓存管理器
            if (announcementCache != null) {
                announcementCache.putAnnouncement(announcement);
            }
            
            // 保存到数据库
            if (sync && announcementDatabase != null && databaseManager.isConnected()) {
                databaseManager.getDatabaseQueue().submit("saveAnnouncement", conn -> {
                    announcementDatabase.saveAnnouncement(announcement, currentServer);
                    return null;
                }, null, e -> plugin.getLogger().log(Level.WARNING, "从数据库保存公告失败", e));
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public boolean exists(String id) {
        lock.readLock().lock();
        try {
            return announcements.containsKey(id);
        } finally {
            lock.readLock().unlock();
        }
    }

    public boolean checkGlobalCooldown(String announcementId, int cooldownSeconds) {
        if (cooldownSeconds <= 0) {
            return true;
        }

        Long lastSent = globalCooldowns.get(announcementId);
        if (lastSent == null) {
            return true;
        }

        long elapsed = (System.currentTimeMillis() - lastSent) / 1000;
        return elapsed >= cooldownSeconds;
    }

    public void setGlobalCooldown(String announcementId) {
        globalCooldowns.put(announcementId, System.currentTimeMillis());
    }

    public boolean checkPlayerCooldown(UUID playerId, String announcementId, int cooldownSeconds) {
        if (cooldownSeconds <= 0) {
            return true;
        }

        Map<String, Long> playerCooldownMap = playerCooldowns.get(playerId);
        if (playerCooldownMap == null) {
            return true;
        }

        Long lastSent = playerCooldownMap.get(announcementId);
        if (lastSent == null) {
            return true;
        }

        long elapsed = (System.currentTimeMillis() - lastSent) / 1000;
        return elapsed >= cooldownSeconds;
    }

    public void setPlayerCooldown(UUID playerId, String announcementId) {
        playerCooldowns.computeIfAbsent(playerId, k -> new ConcurrentHashMap<>())
                .put(announcementId, System.currentTimeMillis());
    }

    public void cleanupPlayerCooldowns(UUID playerId) {
        playerCooldowns.remove(playerId);
    }

    private void saveToConfig(Announcement announcement) {
        // 如果数据库可用，优先保存到数据库（主要存储）
        if (announcementDatabase != null && databaseManager.isConnected()) {
            String serverId = getCurrentServerName();
            // 增加版本号
            Integer currentVersion = announcement.getVersion();
            announcement.setVersion(currentVersion != null ? currentVersion + 1 : 1);

            databaseManager.getDatabaseQueue().submit("saveAnnouncement", conn -> {
                announcementDatabase.saveAnnouncement(announcement, serverId);
                return null;
            }, null, e -> plugin.getLogger().log(Level.WARNING, "保存公告到数据库失败", e));
        } else {
            // 数据库不可用，保存到配置文件（回退）
            saveToConfigOnly(announcement);
            configManager.saveAnnouncementsConfig();
        }
    }

    /**
     * 仅保存到配置文件（不操作数据库，用于导出功能）
     */
    private void saveToConfigOnly(Announcement announcement) {
        String path = "announcements." + announcement.getId();
        configManager.getAnnouncementsConfig().set(path + ".enabled", announcement.isEnabled());
        configManager.getAnnouncementsConfig().set(path + ".priority", announcement.getPriority());
        configManager.getAnnouncementsConfig().set(path + ".content", announcement.getContent());

        // 保存生效区服列表
        if (announcement.getServers() != null && !announcement.getServers().isEmpty()) {
            configManager.getAnnouncementsConfig().set(path + ".servers", announcement.getServers());
        }

        if (announcement.getDisplay() != null) {
            configManager.getAnnouncementsConfig().set(path + ".display.type",
                    announcement.getDisplay().getType().toString().toLowerCase());
            configManager.getAnnouncementsConfig().set(path + ".display.settings.fade-in",
                    announcement.getDisplay().getFadeIn());
            configManager.getAnnouncementsConfig().set(path + ".display.settings.stay",
                    announcement.getDisplay().getStay());
            configManager.getAnnouncementsConfig().set(path + ".display.settings.fade-out",
                    announcement.getDisplay().getFadeOut());
            configManager.getAnnouncementsConfig().set(path + ".display.settings.color",
                    announcement.getDisplay().getColor());
            configManager.getAnnouncementsConfig().set(path + ".display.settings.style",
                    announcement.getDisplay().getStyle());
            configManager.getAnnouncementsConfig().set(path + ".display.settings.toast-icon",
                    announcement.getDisplay().getToastIcon());
            configManager.getAnnouncementsConfig().set(path + ".display.settings.bossbar-progress",
                    announcement.getDisplay().isBossbarProgress());
        }

        if (announcement.getTarget() != null) {
            configManager.getAnnouncementsConfig().set(path + ".target.type",
                    announcement.getTarget().getType().toString().toLowerCase());
            configManager.getAnnouncementsConfig().set(path + ".target.value",
                    announcement.getTarget().getValue());
        }

        if (announcement.getCooldown() != null) {
            configManager.getAnnouncementsConfig().set(path + ".cooldown.global",
                    announcement.getCooldown().getGlobal());
            configManager.getAnnouncementsConfig().set(path + ".cooldown.per-player",
                    announcement.getCooldown().getPerPlayer());
        }

        // 保存版本号
        configManager.getAnnouncementsConfig().set(path + ".version", announcement.getVersion());
    }

    private void removeFromConfig(String id) {
        // 从数据库删除
        if (announcementDatabase != null && databaseManager.isConnected()) {
            databaseManager.getDatabaseQueue().submit("deleteAnnouncement", conn -> {
                announcementDatabase.deleteAnnouncement(id);
                return null;
            }, null, e -> plugin.getLogger().log(Level.WARNING, "从数据库删除公告失败", e));
        }

        // 从配置文件删除
        configManager.getAnnouncementsConfig().set("announcements." + id, null);
        configManager.saveAnnouncementsConfig();
    }

    public void reload() {
        loadAnnouncements();
    }

    /**
     * 获取公告缓存管理器
     */
    public AnnouncementCache getAnnouncementCache() {
        return announcementCache;
    }
}
