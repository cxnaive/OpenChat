package cn.handyplus.chat.listener;

import cn.handyplus.chat.PlayerChat;
import cn.handyplus.chat.constants.ChatConstants;
import cn.handyplus.chat.core.ChatUtil;
import cn.handyplus.chat.core.HornUtil;
import cn.handyplus.chat.util.ConfigUtil;
import cn.handyplus.lib.constants.BaseConstants;
import cn.handyplus.lib.core.CollUtil;
import cn.handyplus.lib.core.DateUtil;
import cn.handyplus.lib.core.JsonUtil;
import cn.handyplus.lib.util.BcUtil;
import cn.handyplus.lib.util.MessageUtil;
import com.example.messageservice.managers.CrossServerSyncManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * BC消息处理
 *
 * @author handy
 */
public class ChatPluginMessageListener implements PluginMessageListener {

    private static final ChatPluginMessageListener INSTANCE = new ChatPluginMessageListener();

    public static ChatPluginMessageListener getInstance() {
        return INSTANCE;
    }

    public void register() {
        Bukkit.getMessenger().registerIncomingPluginChannel(PlayerChat.INSTANCE, BcUtil.BUNGEE_CORD_CHANNEL, INSTANCE);
    }

    public void unregister() {
        Bukkit.getMessenger().unregisterIncomingPluginChannel(PlayerChat.INSTANCE, BcUtil.BUNGEE_CORD_CHANNEL, INSTANCE);
    }

    /**
     * 处理消息
     *
     * @param channel 频道
     * @param player  玩家
     * @param message 消息
     */
    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte[] message) {
        // ====== 处理 BungeeCord 原始响应（如 GetServers） ======
        handleBungeeCordRawResponse(channel, message);

        // 自定义消息处理
        // 优先使用 BungeeCord 自动发现的服务器名，回退到配置文件
        String server = getEffectiveServerName();
        MessageUtil.sendConsoleDebugMessage("子服:" + server + "收到消息");
        // 设置玩家列表
        List<String> playerList = BcUtil.getPlayerList(message);
        MessageUtil.sendConsoleDebugMessage("当前BC在线玩家列表:" + playerList);
        if (CollUtil.isNotEmpty(playerList)) {
            ChatConstants.PLAYER_LIST.addAll(playerList);
            ChatConstants.PLAYER_LIST = ChatConstants.PLAYER_LIST.stream().distinct().collect(Collectors.toList());
            MessageUtil.sendConsoleDebugMessage("聚合在线玩家数据列表:" + ChatConstants.PLAYER_LIST);
        }
        Optional<BcUtil.BcMessageParam> paramOptional = BcUtil.getParamByForward(message);
        if (!paramOptional.isPresent()) {
            return;
        }
        BcUtil.BcMessageParam bcMessageParam = paramOptional.get();

        // ====== 公告模块消息拦截 ======
        if (CrossServerSyncManager.isAnnouncementType(bcMessageParam.getType())) {
            CrossServerSyncManager syncManager = getCrossServerSyncManager();
            if (syncManager != null) {
                syncManager.handleIncomingMessage(bcMessageParam);
            }
            return;
        }
        // 判断时间太久的不发送
        long between = DateUtil.between(new Date(bcMessageParam.getTimestamp()), new Date(), ChronoUnit.MINUTES);
        if (between > 1) {
            return;
        }

        // ====== 3.4.0: 跨服拒绝通知处理 ======
        if (ChatConstants.REJECT_TYPE.equals(bcMessageParam.getType())) {
            handleRejectNotification(bcMessageParam);
            return;
        }

        // 群组聊天消息
        if (ChatConstants.CHAT_TYPE.equals(bcMessageParam.getType()) || ChatConstants.ITEM_TYPE.equals(bcMessageParam.getType())) {
            ChatUtil.asyncSendMsg(bcMessageParam, false);
            return;
        }
        // 获取喇叭配置
        List<String> serverList = ConfigUtil.LB_CONFIG.getStringList("lb." + bcMessageParam.getType() + ".server");
        if (CollUtil.isEmpty(serverList)) {
            MessageUtil.sendConsoleDebugMessage(bcMessageParam.getType() + "的server配置错误");
            return;
        }
        // 判断是否包含该子服
        if (!serverList.contains(server)) {
            MessageUtil.sendConsoleDebugMessage(server + "子服不发消息");
            return;
        }
        // 发送消息
        HornUtil.sendMsg(player, bcMessageParam);
    }

    /**
     * 处理跨服拒绝通知。
     * <p>当其他子服上的过滤器拒绝了消息投递时，会通过 Forward 回传拒绝通知。
     * 本方法在本服查找到发送者并展示拒绝原因。</p>
     *
     * @param param 拒绝通知参数
     * @since 3.4.0
     */
    private void handleRejectNotification(BcUtil.BcMessageParam param) {
        String senderName = param.getSenderName();
        String rejectedPlayerName = param.getPlayerName();
        String reason = param.getMessage();

        if (senderName == null || senderName.isEmpty()) {
            return;
        }
        Player sender = Bukkit.getPlayer(senderName);
        if (sender != null && sender.isOnline()) {
            MessageUtil.sendMessage(sender, "§c发送给 §e" + rejectedPlayerName + " §c的消息被拒绝: §7" + reason);
        }
    }

    /**
     * 获取有效的服务器名称
     * 优先使用 BungeeCord 自动发现的服务器名，回退到配置文件中的 server 字段
     */
    private String getEffectiveServerName() {
        try {
            CrossServerSyncManager syncManager = getCrossServerSyncManager();
            if (syncManager != null) {
                return syncManager.getServerName();
            }
        } catch (Exception ignored) {
        }
        return BaseConstants.CONFIG.getString("server");
    }

    /**
     * 获取 CrossServerSyncManager 实例
     * 通过 PlayerChat 中注入的 MessageServicePlugin 获取
     */
    private CrossServerSyncManager getCrossServerSyncManager() {
        try {
            return com.example.messageservice.MessageServicePlugin.getInstance().getCrossServerSyncManager();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 处理 BungeeCord 原始响应（非 Forward 格式）
     * 例如 GetServers 响应
     */
    private void handleBungeeCordRawResponse(String channel, byte[] message) {
        if (!BcUtil.BUNGEE_CORD_CHANNEL.equals(channel)) return;
        try {
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));
            String subChannel = in.readUTF();

            if ("GetServers".equals(subChannel)) {
                String serverListStr = in.readUTF();
                List<String> servers = Arrays.asList(serverListStr.split(", "));
                CrossServerSyncManager syncManager = getCrossServerSyncManager();
                if (syncManager != null) {
                    syncManager.setDiscoveredServers(servers);
                }
            } else if ("GetServer".equals(subChannel)) {
                String serverName = in.readUTF();
                CrossServerSyncManager syncManager = getCrossServerSyncManager();
                if (syncManager != null) {
                    syncManager.setDiscoveredServerName(serverName);
                }
            }
        } catch (Exception ignored) {
            // 非标准 BungeeCord 响应格式，忽略
        }
    }

}