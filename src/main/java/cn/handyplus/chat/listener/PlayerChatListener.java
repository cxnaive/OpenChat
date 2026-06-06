package cn.handyplus.chat.listener;

import cn.handyplus.chat.PlayerChat;
import cn.handyplus.chat.constants.ChatConstants;
import cn.handyplus.chat.core.ChannelUtil;
import cn.handyplus.chat.core.ChatUtil;
import cn.handyplus.chat.core.ShortcutUtil;
import cn.handyplus.chat.event.PlayerChannelChatEvent;
import cn.handyplus.chat.event.PlayerChannelTellEvent;
import cn.handyplus.chat.param.ChatParam;
import cn.handyplus.chat.service.ChatPlayerChannelService;
import cn.handyplus.lib.annotation.HandyListener;
import cn.handyplus.lib.core.JsonUtil;
import cn.handyplus.lib.core.StrUtil;
import cn.handyplus.lib.util.BcUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 玩家聊天监听器
 *
 * @author handy
 */
@HandyListener
public class PlayerChatListener implements Listener {

    /**
     * 聊天信息处理
     *
     * @param event 事件
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent event) {
        if (event.isCancelled()) {
            return;
        }
        Player player = event.getPlayer();
        // 取消事件
        event.setCancelled(true);

        // ====== 3.4.0 私聊模式检查 ======
        UUID tellTargetUuid = ChatConstants.PLAYER_TELL_TARGET.get(player.getUniqueId());
        if (tellTargetUuid != null) {
            Player tellTarget = Bukkit.getPlayer(tellTargetUuid);
            if (tellTarget != null && tellTarget.isOnline()) {
                // 私聊模式：路由到私聊目标
                sendMsg(player, event.getMessage(), ChatConstants.TELL, tellTarget.getName());
                // 发送者回显
                String rawMsg = event.getMessage();
                if (!player.hasPermission(ChatConstants.CHAT_COLOR)) {
                    rawMsg = cn.handyplus.lib.util.BaseUtil.stripColor(rawMsg);
                }
                player.sendMessage("§7[§f你 §7-> §f" + tellTarget.getName() + "§7] §f" + rawMsg);
                return;
            }
            // 目标不在线，退出私聊模式并回退到普通频道
            ChatConstants.PLAYER_TELL_TARGET.remove(player.getUniqueId());
            ChatPlayerChannelService.getInstance().setTellTarget(player.getUniqueId(), null);
            player.sendMessage("§c私聊目标已下线，已退出私聊模式");
        }

        // 正常频道发送
        sendMsg(player, event.getMessage(), ChatUtil.getChannel(player), null);
    }

    /**
     * 发送消息
     *
     * @param player         玩家
     * @param message        消息
     * @param channel        渠道
     * @param tellPlayerName 接收人
     * @since 1.1.5
     */
    public static void sendMsg(Player player, String message, String channel, String tellPlayerName) {
        // 聊天校验处理
        if (ChatUtil.chatCheck(player, message)) {
            return;
        }
        // @处理
        List<String> mentionedPlayers = new ArrayList<>();
        message = ChatUtil.at(mentionedPlayers, message);
        // 参数构建
        BcUtil.BcMessageParam param = new BcUtil.BcMessageParam();
        param.setPluginName(PlayerChat.INSTANCE.getName());
        param.setPlayerName(player.getName());
        param.setTimestamp(System.currentTimeMillis());
        // 构建消息参数
        ChatParam chatParam = ChatUtil.buildChatParam(player, channel);
        if (chatParam == null) {
            return;
        }
        // 添加私信接收人 1.1.5
        chatParam.setTellPlayerName(tellPlayerName);
        // 添加附近的人 2.1.0
        chatParam.setNearbyPlayers(ChannelUtil.getNearbyPlayers(channel, player));
        // 原消息内容
        chatParam.setMessage(message);
        // 快捷键节点替换
        ShortcutUtil.convert(player, channel, chatParam);
        // @玩家处理
        chatParam.setMentionedPlayers(mentionedPlayers);
        // 有权限进行颜色代码处理
        chatParam.setHasColor(player.hasPermission(ChatConstants.CHAT_COLOR));
        chatParam.setSource(param.getPluginName());
        param.setType(ChatConstants.CHAT_TYPE);
        param.setMessage(JsonUtil.toJson(chatParam));
        // 发送事件
        if (StrUtil.isEmpty(tellPlayerName)) {
            Bukkit.getServer().getPluginManager().callEvent(new PlayerChannelChatEvent(player, param));
        } else {
            Bukkit.getServer().getPluginManager().callEvent(new PlayerChannelTellEvent(player, param));
        }
    }

}
