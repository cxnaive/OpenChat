package cn.handyplus.chat;

import cn.handyplus.chat.hook.PlaceholderUtil;
import cn.handyplus.chat.listener.ChatPluginMessageListener;
import cn.handyplus.chat.util.ClearItemJob;
import cn.handyplus.chat.util.ConfigUtil;
import cn.handyplus.chat.command.ChannelMainCommand;
import cn.handyplus.chat.listener.ChannelGuiListener;
import cn.handyplus.lib.InitApi;
import cn.handyplus.lib.constants.HookPluginEnum;
import cn.handyplus.lib.util.BcUtil;
import cn.handyplus.lib.util.HookPluginUtil;
import cn.handyplus.lib.util.MessageUtil;
import com.example.messageservice.MessageServicePlugin;
import com.example.messageservice.commands.AnnounceCommand;
import com.example.messageservice.config.ConfigManager;
import com.example.messageservice.database.DatabaseManager;
import com.example.messageservice.gui.GuiManager;
import com.example.messageservice.listeners.FirstJoinListener;
import com.example.messageservice.managers.AnnouncementManager;
import com.example.messageservice.schedulers.ScheduleManager;
import com.example.messageservice.schedulers.SchedulerAdapter;
import com.example.messageservice.services.AnnouncementService;
import com.example.messageservice.utils.PlaceholderManager;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * 主类
 *
 * @author handy
 */
public class PlayerChat extends JavaPlugin {
    public static PlayerChat INSTANCE;
    public static boolean USE_PAPI;
    public static boolean USE_DISCORD_SRV;

    // 公告模块管理器
    private ConfigManager msConfigManager;
    private DatabaseManager msDatabaseManager;
    private AnnouncementManager msAnnouncementManager;
    private AnnouncementService msAnnouncementService;
    private ScheduleManager msScheduleManager;
    private GuiManager msGuiManager;
    private AnnounceCommand msAnnounceCommand;

    @Override
    public void onEnable() {
        INSTANCE = this;
        InitApi initApi = InitApi.getInstance(this);
        ConfigUtil.init();
        // 加载PlaceholderApi
        USE_PAPI = HookPluginUtil.hook(HookPluginEnum.PLACEHOLDER_API);
        if (USE_PAPI) {
            new PlaceholderUtil(this).register();
        }
        // 加载DiscordSRV
        USE_DISCORD_SRV = HookPluginUtil.hook(HookPluginEnum.DISCORD_SRV);
        // 加载主数据
        initApi.initCommand("cn.handyplus.chat.command")
                .initListener("cn.handyplus.chat.listener")
                .enableSql("cn.handyplus.chat.enter")
                .initClickEvent("cn.handyplus.chat.listener.gui")
                .addMetrics(18860)
                .enableBc()
                .checkVersion();
        ChatPluginMessageListener.getInstance().register();
        // 注册 /channel 独立命令
        getCommand("channel").setExecutor(new ChannelMainCommand());
        // 注册频道 GUI 监听器
        getServer().getPluginManager().registerEvents(new ChannelGuiListener(), this);
        // 定时任务启动
        ClearItemJob.init();

        // ==================== 公告模块初始化（原 MessageService） ====================
        initializeAnnouncementModule();

        MessageUtil.sendConsoleMessage(ChatColor.GREEN + "已成功载入服务器!");
        MessageUtil.sendConsoleMessage(ChatColor.GREEN + "Author:handy WIKI: https://ricedoc.handyplus.cn/wiki/PlayerChat/README/");
    }

    @Override
    public void onDisable() {
        // 关闭公告模块
        shutdownAnnouncementModule();

        InitApi.disable();
        BcUtil.unregisterOut();
        ChatPluginMessageListener.getInstance().unregister();
    }

    /**
     * 初始化公告模块（原 MessageService 功能）
     */
    private void initializeAnnouncementModule() {
        try {
            MessageServicePlugin msPlugin = MessageServicePlugin.getInstance();

            // 初始化配置管理器
            msConfigManager = new ConfigManager(msPlugin);

            // 初始化数据库管理器
            msDatabaseManager = new DatabaseManager(msPlugin);
            if (!msDatabaseManager.initialize()) {
                getLogger().warning("[公告模块] 数据库初始化失败，将使用文件存储模式");
            }

            // 初始化占位符管理器
            PlaceholderManager placeholderManager = new PlaceholderManager(msPlugin);

            // 初始化调度器适配器
            SchedulerAdapter schedulerAdapter = new SchedulerAdapter(msPlugin);

            // 初始化公告管理器
            msAnnouncementManager = new AnnouncementManager(msPlugin, msConfigManager, msDatabaseManager);

            // 初始化公告服务
            msAnnouncementService = new AnnouncementService(
                    msPlugin, msConfigManager, msAnnouncementManager, placeholderManager, schedulerAdapter
            );

            // 初始化定时任务管理器
            msScheduleManager = new ScheduleManager(
                    msPlugin, msAnnouncementManager, msAnnouncementService, schedulerAdapter
            );
            msScheduleManager.initialize();

            // 初始化GUI管理器
            msGuiManager = new GuiManager(msPlugin, msAnnouncementManager);
            msGuiManager.initialize(schedulerAdapter);

            // 注入到 MessageServicePlugin 桥接类
            msPlugin.setConfigManager(msConfigManager);
            msPlugin.setDatabaseManager(msDatabaseManager);
            msPlugin.setAnnouncementManager(msAnnouncementManager);
            msPlugin.setPlaceholderManager(placeholderManager);
            msPlugin.setAnnouncementService(msAnnouncementService);
            msPlugin.setScheduleManager(msScheduleManager);
            msPlugin.setGuiManager(msGuiManager);

            // 注册公告命令
            msAnnounceCommand = new AnnounceCommand(
                    msConfigManager, msAnnouncementManager, msAnnouncementService, placeholderManager, msGuiManager
            );
            msAnnounceCommand.setPlugin(msPlugin);
            msPlugin.setAnnounceCommand(msAnnounceCommand);
            getCommand("broadcast").setExecutor(msAnnounceCommand);
            getCommand("broadcast").setTabCompleter(msAnnounceCommand);

            // 注册首次登录监听器
            if (msDatabaseManager != null && msDatabaseManager.isConnected()) {
                FirstJoinListener firstJoinListener = new FirstJoinListener(
                        msPlugin, msAnnouncementManager, msAnnouncementService, msDatabaseManager
                );
                getServer().getPluginManager().registerEvents(firstJoinListener, this);
            }

            // 初始化跨服同步
            com.example.messageservice.managers.CrossServerSyncManager crossServerSyncManager =
                    new com.example.messageservice.managers.CrossServerSyncManager(
                            msPlugin, msAnnouncementManager, msAnnouncementService, msDatabaseManager
                    );
            crossServerSyncManager.initialize();
            msPlugin.setCrossServerSyncManager(crossServerSyncManager);

            // 注入跨服同步管理器到公告命令处理器
            msAnnounceCommand.setCrossServerSyncManager(crossServerSyncManager);

            // 注入跨服同步管理器到公告管理器
            msAnnouncementManager.setCrossServerSyncManager(crossServerSyncManager);
            msAnnouncementManager.setSyncEnabled(crossServerSyncManager.isEnabled());

            getLogger().info("[公告模块] 初始化完成 (Folia支持: " + schedulerAdapter.isFolia() + ")");
        } catch (Exception e) {
            getLogger().severe("[公告模块] 初始化失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 关闭公告模块
     */
    private void shutdownAnnouncementModule() {
        try {
            MessageServicePlugin msPlugin = MessageServicePlugin.getInstance();

            if (msPlugin.getCrossServerSyncManager() != null) {
                msPlugin.getCrossServerSyncManager().shutdown();
            }
            if (msDatabaseManager != null) {
                msDatabaseManager.shutdown();
            }
            if (msScheduleManager != null) {
                msScheduleManager.shutdown();
            }
            if (msAnnouncementService != null) {
                msAnnouncementService.cleanup();
            }
            if (msGuiManager != null) {
                msGuiManager.cleanup();
            }
            getLogger().info("[公告模块] 已关闭");
        } catch (Exception e) {
            getLogger().severe("[公告模块] 关闭时出错: " + e.getMessage());
        }
    }

}