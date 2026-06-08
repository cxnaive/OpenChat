package com.example.messageservice.cache;

import com.example.messageservice.MessageServicePlugin;
import com.example.messageservice.database.AnnouncementDatabase;
import com.example.messageservice.models.Announcement;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

/**
 * 公告缓存管理器
 * 管理公告的内存缓存和同步状态
 */
public class AnnouncementCache {

    private final MessageServicePlugin plugin;
    private final AnnouncementDatabase announcementDatabase;

    private final Map<String, Announcement> announcements = new ConcurrentHashMap<>();
    private final Map<String, Integer> versions = new ConcurrentHashMap<>();
    private final Map<String, Long> lastUpdateTimes = new ConcurrentHashMap<>();

    private long lastSyncTriggerTime = 0;
    private static final long SYNC_COOLDOWN = 30000; // 30秒冷却

    private boolean isSyncing = false;

    public AnnouncementCache(MessageServicePlugin plugin, AnnouncementDatabase announcementDatabase) {
        this.plugin = plugin;
        this.announcementDatabase = announcementDatabase;
    }

    /**
     * 初始化缓存（从数据库加载）
     */
    public void initialize() {
        plugin.getLogger().info("初始化公告缓存...");
        loadFromDatabase();
        plugin.getLogger().info("公告缓存初始化完成，共 " + announcements.size() + " 条公告");
    }

    /**
     * 从数据库重新加载所有公告
     */
    public void loadFromDatabase() {
        if (isSyncing) {
            plugin.getLogger().fine("正在同步中，跳过数据库加载");
            return;
        }

        isSyncing = true;
        try {
            List<Announcement> dbAnnouncements = announcementDatabase.loadAllAnnouncements();
            
            synchronized (announcements) {
                announcements.clear();
                versions.clear();
                lastUpdateTimes.clear();

                for (Announcement announcement : dbAnnouncements) {
                    announcements.put(announcement.getId(), announcement);
                    versions.put(announcement.getId(), announcement.getVersion());
                    lastUpdateTimes.put(announcement.getId(), System.currentTimeMillis());
                }
            }

            plugin.getLogger().info("已从数据库加载 " + dbAnnouncements.size() + " 条公告到缓存");
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "从数据库加载公告失败", e);
        } finally {
            isSyncing = false;
        }
    }

    /**
     * 获取所有公告
     */
    public List<Announcement> getAllAnnouncements() {
        return new ArrayList<>(announcements.values());
    }

    /**
     * 根据ID获取公告
     */
    public Optional<Announcement> getAnnouncement(String id) {
        return Optional.ofNullable(announcements.get(id));
    }

    /**
     * 添加或更新公告
     */
    public void putAnnouncement(Announcement announcement) {
        String id = announcement.getId();
        announcements.put(id, announcement);
        versions.put(id, announcement.getVersion());
        lastUpdateTimes.put(id, System.currentTimeMillis());
    }

    /**
     * 删除公告
     */
    public void removeAnnouncement(String id) {
        announcements.remove(id);
        versions.remove(id);
        lastUpdateTimes.remove(id);
    }

    /**
     * 获取公告版本
     */
    public int getVersion(String id) {
        return versions.getOrDefault(id, 1);
    }

    /**
     * 检查是否需要同步（服务器级别冷却）
     * @return true 表示可以触发同步
     */
    public boolean shouldTriggerSync() {
        long now = System.currentTimeMillis();
        if (now - lastSyncTriggerTime >= SYNC_COOLDOWN) {
            lastSyncTriggerTime = now;
            return true;
        }
        return false;
    }

    /**
     * 处理版本更新通知
     * @param remoteVersion 远程版本号
     * @param updatedBy 更新者服务器
     * @return true 表示需要重新加载
     */
    public boolean handleVersionUpdate(long remoteVersion, String updatedBy) {
        long now = System.currentTimeMillis();

        for (Map.Entry<String, Integer> entry : versions.entrySet()) {
            String id = entry.getKey();
            int localVersion = entry.getValue();

            if (remoteVersion > localVersion) {
                plugin.getLogger().info("检测到公告版本更新: " + id + 
                    " (本地: " + localVersion + " -> 远程: " + remoteVersion + 
                    ", 更新者: " + updatedBy + ")");
                return true;
            }
        }

        return false;
    }

    /**
     * 更新单个公告（用于增量更新）
     */
    public void updateAnnouncement(Announcement announcement) {
        String id = announcement.getId();
        int oldVersion = versions.getOrDefault(id, 0);

        if (announcement.getVersion() > oldVersion) {
            announcements.put(id, announcement);
            versions.put(id, announcement.getVersion());
            lastUpdateTimes.put(id, System.currentTimeMillis());
            plugin.getLogger().fine("更新缓存中的公告: " + id + 
                " (版本: " + oldVersion + " -> " + announcement.getVersion() + ")");
        }
    }

    /**
     * 获取缓存统计信息
     */
    public String getCacheStats() {
        return String.format("缓存: %d 条公告, 最后同步: %d 秒前",
            announcements.size(),
            (System.currentTimeMillis() - lastSyncTriggerTime) / 1000);
    }

    /**
     * 检查并刷新单个公告（如果数据库中有更新版本）
     * @param id 公告ID
     * @return 最新的公告对象，如果公告不存在则返回null
     */
    public Announcement checkAndRefreshAnnouncement(String id) {
        Announcement cached = announcements.get(id);
        
        AnnouncementDatabase.AnnouncementVersion dbVersion = announcementDatabase.loadAnnouncementVersion(id);
        
        if (dbVersion == null) {
            return cached;
        }
        
        int localVersion = versions.getOrDefault(id, 0);
        
        if (dbVersion.version() > localVersion) {
            plugin.getLogger().info("检测到公告版本更新: " + id + 
                " (本地: " + localVersion + " -> 数据库: " + dbVersion.version() + ")");
            
            Announcement freshAnnouncement = announcementDatabase.loadAnnouncement(id);
            if (freshAnnouncement != null) {
                announcements.put(id, freshAnnouncement);
                versions.put(id, dbVersion.version());
                lastUpdateTimes.put(id, System.currentTimeMillis());
                plugin.getLogger().info("已从数据库刷新公告: " + id);
                return freshAnnouncement;
            }
        }
        
        return cached;
    }

    /**
     * 清空缓存
     */
    public void clear() {
        announcements.clear();
        versions.clear();
        lastUpdateTimes.clear();
        lastSyncTriggerTime = 0;
        plugin.getLogger().info("公告缓存已清空");
    }
}
