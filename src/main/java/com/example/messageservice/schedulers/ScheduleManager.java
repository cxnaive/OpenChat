package com.example.messageservice.schedulers;

import com.example.messageservice.managers.AnnouncementManager;
import com.example.messageservice.models.Announcement;
import com.example.messageservice.services.AnnouncementService;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import lombok.RequiredArgsConstructor;
import com.example.messageservice.MessageServicePlugin;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public class ScheduleManager {

    private final MessageServicePlugin plugin;
    private final AnnouncementManager announcementManager;
    private final AnnouncementService announcementService;
    private final SchedulerAdapter schedulerAdapter;

    private final Map<String, ScheduledTask> scheduledTasks = new ConcurrentHashMap<>();
    private final Map<String, CronTask> cronTasks = new ConcurrentHashMap<>();
    
    // Cron 表达式解析器
    private final CronExpressionParser cronParser = new CronExpressionParser();

    public void initialize() {
        loadScheduledAnnouncements();
        
        // 启动 Cron 任务检查器（每分钟检查一次）
        startCronChecker();
    }

    public void shutdown() {
        // 取消所有定时任务
        scheduledTasks.values().forEach(ScheduledTask::cancel);
        scheduledTasks.clear();
        cronTasks.clear();
    }

    private void loadScheduledAnnouncements() {
        List<Announcement> announcements = announcementManager.getEnabledAnnouncements();
        
        for (Announcement announcement : announcements) {
            if (announcement.getTrigger() == null) {
                continue;
            }

            if (announcement.getTrigger().getType() == Announcement.TriggerType.SCHEDULE) {
                scheduleAnnouncement(announcement);
            }
        }
    }

    private void scheduleAnnouncement(Announcement announcement) {
        String schedule = announcement.getTrigger().getSchedule();
        if (schedule == null || schedule.isEmpty()) {
            return;
        }

        // 检查是否是 Cron 表达式（包含空格或特殊字符）
        if (isCronExpression(schedule)) {
            registerCronSchedule(announcement, schedule);
        } else {
            // 简单的间隔调度（以tick为单位）
            try {
                long interval = parseInterval(schedule);
                if (interval > 0) {
                    ScheduledTask task = schedulerAdapter.runTimerOnGlobal(() -> {
                        announcementService.broadcastAnnouncement(announcement.getId());
                    }, interval, interval);

                    scheduledTasks.put(announcement.getId(), task);
                    plugin.getLogger().info("已调度公告 '" + announcement.getId() + "'，间隔: " + interval + " ticks");
                }
            } catch (Exception e) {
                plugin.getLogger().warning("无法解析公告 '" + announcement.getId() + "' 的调度表达式: " + schedule);
            }
        }
    }
    
    /**
     * 判断是否是 Cron 表达式
     * Cron 表达式通常包含空格（5-6个字段）
     */
    private boolean isCronExpression(String schedule) {
        if (schedule == null || schedule.isEmpty()) {
            return false;
        }
        // 如果包含空格，认为是 Cron 表达式
        return schedule.trim().split("\\s+").length >= 5;
    }
    
    /**
     * 注册 Cron 表达式调度
     */
    private void registerCronSchedule(Announcement announcement, String cronExpression) {
        try {
            // 验证 Cron 表达式
            if (!cronParser.validate(cronExpression)) {
                plugin.getLogger().warning("无效的 Cron 表达式 '" + cronExpression + "' 用于公告 '" + announcement.getId() + "'");
                return;
            }
            
            cronTasks.put(announcement.getId(), new CronTask(announcement.getId(), cronExpression));
            plugin.getLogger().info("已注册 Cron 调度 '" + announcement.getId() + "'，表达式: " + cronExpression);
        } catch (Exception e) {
            plugin.getLogger().warning("注册 Cron 调度失败 '" + announcement.getId() + "': " + e.getMessage());
        }
    }
    
    /**
     * 启动 Cron 任务检查器
     * 每分钟检查一次是否有 Cron 任务需要执行
     */
    private void startCronChecker() {
        // 每分钟检查一次（1200 ticks）
        schedulerAdapter.runTimerOnGlobal(() -> {
            checkCronTasks();
        }, 1200L, 1200L);
        
        plugin.getLogger().fine("Cron 任务检查器已启动");
    }
    
    /**
     * 检查所有 Cron 任务是否应该执行
     */
    private void checkCronTasks() {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
        
        for (CronTask cronTask : cronTasks.values()) {
            try {
                if (cronParser.shouldExecute(cronTask.getExpression(), now)) {
                    Optional<Announcement> optional = announcementManager.getAnnouncement(cronTask.getAnnouncementId());
                    if (optional.isPresent() && optional.get().isEnabled()) {
                        announcementService.broadcastAnnouncement(cronTask.getAnnouncementId());
                        plugin.getLogger().fine("执行 Cron 任务 '" + cronTask.getAnnouncementId() + "'");
                    }
                }
            } catch (Exception e) {
                plugin.getLogger().warning("检查 Cron 任务失败 '" + cronTask.getAnnouncementId() + "': " + e.getMessage());
            }
        }
    }

    private long parseInterval(String schedule) {
        // 支持简单的秒/分钟/小时格式
        // 例如: "30s", "5m", "1h"
        schedule = schedule.trim().toLowerCase();
        
        try {
            if (schedule.endsWith("s")) {
                long seconds = Long.parseLong(schedule.substring(0, schedule.length() - 1));
                return seconds * 20; // 转换为ticks
            } else if (schedule.endsWith("m")) {
                long minutes = Long.parseLong(schedule.substring(0, schedule.length() - 1));
                return minutes * 20 * 60;
            } else if (schedule.endsWith("h")) {
                long hours = Long.parseLong(schedule.substring(0, schedule.length() - 1));
                return hours * 20 * 60 * 60;
            } else {
                // 默认为ticks
                return Long.parseLong(schedule);
            }
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public void reload() {
        shutdown();
        loadScheduledAnnouncements();
    }

    public boolean addSchedule(String announcementId, String schedule) {
        Optional<Announcement> optional = announcementManager.getAnnouncement(announcementId);
        if (optional.isEmpty()) {
            return false;
        }

        Announcement announcement = optional.get();
        
        // 取消现有任务
        ScheduledTask existingTask = scheduledTasks.remove(announcementId);
        if (existingTask != null) {
            existingTask.cancel();
        }
        // 移除 Cron 任务
        cronTasks.remove(announcementId);

        // 更新公告的调度设置
        if (announcement.getTrigger() == null) {
            announcement.setTrigger(Announcement.TriggerSettings.builder()
                    .type(Announcement.TriggerType.SCHEDULE)
                    .schedule(schedule)
                    .events(new ArrayList<>())
                    .build());
        } else {
            announcement.getTrigger().setType(Announcement.TriggerType.SCHEDULE);
            announcement.getTrigger().setSchedule(schedule);
        }

        announcementManager.updateAnnouncement(announcement);
        scheduleAnnouncement(announcement);
        
        return true;
    }

    public boolean removeSchedule(String announcementId) {
        ScheduledTask task = scheduledTasks.remove(announcementId);
        if (task != null) {
            task.cancel();
        }
        // 移除 Cron 任务
        cronTasks.remove(announcementId);

        Optional<Announcement> optional = announcementManager.getAnnouncement(announcementId);
        if (optional.isPresent()) {
            Announcement announcement = optional.get();
            if (announcement.getTrigger() != null) {
                announcement.getTrigger().setType(Announcement.TriggerType.MANUAL);
                announcement.getTrigger().setSchedule(null);
                announcementManager.updateAnnouncement(announcement);
            }
        }

        return task != null || cronTasks.containsKey(announcementId);
    }

    public Map<String, String> getActiveSchedules() {
        Map<String, String> result = new HashMap<>();
        for (Map.Entry<String, ScheduledTask> entry : scheduledTasks.entrySet()) {
            Optional<Announcement> optional = announcementManager.getAnnouncement(entry.getKey());
            if (optional.isPresent() && optional.get().getTrigger() != null) {
                result.put(entry.getKey(), optional.get().getTrigger().getSchedule());
            }
        }
        // 添加 Cron 任务
        for (Map.Entry<String, CronTask> entry : cronTasks.entrySet()) {
            result.put(entry.getKey(), entry.getValue().getExpression());
        }
        return result;
    }
    
    /**
     * 验证 Cron 表达式是否有效
     */
    public boolean validateCronExpression(String expression) {
        return cronParser.validate(expression);
    }
    
    /**
     * 获取 Cron 表达式的下一次执行时间描述
     */
    public String getCronDescription(String expression) {
        if (!cronParser.validate(expression)) {
            return "无效的 Cron 表达式";
        }
        
        String[] parts = expression.trim().split("\\s+");
        if (parts.length == 5) {
            return String.format("分: %s, 时: %s, 日: %s, 月: %s, 周: %s", 
                parts[0], parts[1], parts[2], parts[3], parts[4]);
        } else if (parts.length == 6) {
            return String.format("秒: %s, 分: %s, 时: %s, 日: %s, 月: %s, 周: %s", 
                parts[0], parts[1], parts[2], parts[3], parts[4], parts[5]);
        }
        return "未知格式";
    }

    private static class CronTask {
        private final String announcementId;
        private final String expression;

        public CronTask(String announcementId, String expression) {
            this.announcementId = announcementId;
            this.expression = expression;
        }

        public String getAnnouncementId() {
            return announcementId;
        }

        public String getExpression() {
            return expression;
        }
    }
    
    /**
     * Cron 表达式解析器
     * 支持标准 Unix Cron 格式（5个字段：分 时 日 月 周）
     * 或扩展格式（6个字段：秒 分 时 日 月 周）
     */
    public static class CronExpressionParser {
        
        /**
         * 验证 Cron 表达式是否有效
         */
        public boolean validate(String expression) {
            if (expression == null || expression.isEmpty()) {
                return false;
            }
            
            String[] parts = expression.trim().split("\\s+");
            // 支持 5 或 6 个字段
            if (parts.length != 5 && parts.length != 6) {
                return false;
            }
            
            try {
                // 简单验证每个字段
                for (String part : parts) {
                    if (!isValidCronField(part)) {
                        return false;
                    }
                }
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        
        /**
         * 检查单个 Cron 字段是否有效
         */
        private boolean isValidCronField(String field) {
            if (field == null || field.isEmpty()) {
                return false;
            }
            
            // 支持的特殊字符: * , - /
            return field.matches("^[0-9*,/-]+$");
        }
        
        /**
         * 检查在当前时间是否应该执行
         * 
         * @param expression Cron 表达式
         * @param dateTime 当前时间
         * @return 是否应该执行
         */
        public boolean shouldExecute(String expression, ZonedDateTime dateTime) {
            if (!validate(expression)) {
                return false;
            }
            
            String[] parts = expression.trim().split("\\s+");
            
            try {
                if (parts.length == 6) {
                    // 6 字段格式: 秒 分 时 日 月 周
                    return matchesField(parts[0], dateTime.getSecond()) &&
                           matchesField(parts[1], dateTime.getMinute()) &&
                           matchesField(parts[2], dateTime.getHour()) &&
                           matchesField(parts[3], dateTime.getDayOfMonth()) &&
                           matchesField(parts[4], dateTime.getMonthValue()) &&
                           matchesDayOfWeek(parts[5], dateTime);
                } else {
                    // 5 字段格式: 分 时 日 月 周
                    return matchesField(parts[0], dateTime.getMinute()) &&
                           matchesField(parts[1], dateTime.getHour()) &&
                           matchesField(parts[2], dateTime.getDayOfMonth()) &&
                           matchesField(parts[3], dateTime.getMonthValue()) &&
                           matchesDayOfWeek(parts[4], dateTime);
                }
            } catch (Exception e) {
                return false;
            }
        }
        
        /**
         * 检查字段是否匹配
         * 支持: * (任意), 数字, 列表 (1,2,3), 范围 (1-5), 步长 (星号/5)
         */
        private boolean matchesField(String field, int value) {
            if (field.equals("*")) {
                return true;
            }
            
            // 处理步长格式: 星号/5 或 1-10/2
            if (field.contains("/")) {
                String[] parts = field.split("/");
                int step = Integer.parseInt(parts[1]);
                
                if (parts[0].equals("*")) {
                    return value % step == 0;
                } else if (parts[0].contains("-")) {
                    String[] range = parts[0].split("-");
                    int start = Integer.parseInt(range[0]);
                    int end = Integer.parseInt(range[1]);
                    return value >= start && value <= end && (value - start) % step == 0;
                }
            }
            
            // 处理范围格式: 1-5
            if (field.contains("-")) {
                String[] parts = field.split("-");
                int start = Integer.parseInt(parts[0]);
                int end = Integer.parseInt(parts[1]);
                return value >= start && value <= end;
            }
            
            // 处理列表格式: 1,2,3
            if (field.contains(",")) {
                String[] values = field.split(",");
                for (String v : values) {
                    if (Integer.parseInt(v.trim()) == value) {
                        return true;
                    }
                }
                return false;
            }
            
            // 单个数字
            return Integer.parseInt(field) == value;
        }
        
        /**
         * 匹配星期几
         * 支持: 0-6 或 1-7 (周日可以是0或7)
         */
        private boolean matchesDayOfWeek(String field, ZonedDateTime dateTime) {
            int dayOfWeek = dateTime.getDayOfWeek().getValue() % 7; // 转换为 0-6 (周日=0)
            
            if (field.equals("*")) {
                return true;
            }
            
            // 处理步长、范围、列表
            if (field.contains("/")) {
                String[] parts = field.split("/");
                int step = Integer.parseInt(parts[1]);
                
                if (parts[0].equals("*")) {
                    return dayOfWeek % step == 0;
                }
            }
            
            if (field.contains("-")) {
                String[] parts = field.split("-");
                int start = Integer.parseInt(parts[0]);
                int end = Integer.parseInt(parts[1]);
                return dayOfWeek >= start && dayOfWeek <= end;
            }
            
            if (field.contains(",")) {
                String[] values = field.split(",");
                for (String v : values) {
                    int val = Integer.parseInt(v.trim());
                    if (val == 7) val = 0; // 周日处理
                    if (val == dayOfWeek) {
                        return true;
                    }
                }
                return false;
            }
            
            int val = Integer.parseInt(field);
            if (val == 7) val = 0;
            return val == dayOfWeek;
        }
    }
}
