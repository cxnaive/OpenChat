package cn.handyplus.chat.listener;

import cn.handyplus.chat.constants.ChatConstants;
import cn.handyplus.chat.enter.ChatPlayerChannelEnter;
import cn.handyplus.chat.service.ChatPlayerChannelService;
import cn.handyplus.lib.annotation.HandyListener;
import cn.handyplus.lib.util.BcUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Optional;
import java.util.UUID;

/**
 * 玩家上下线事件
 * 清理缓存 / 恢复私聊模式
 *
 * @author handy
 */
@HandyListener
public class PlayerQuitEventListener implements Listener {

    /**
     * 玩家加入服务器事件.
     * <p>从数据库恢复私聊模式目标。</p>
     *
     * @param event 事件
     */
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        restoreTellTarget(player);
    }

    /**
     * 玩家被服务器踢出事件.
     *
     * @param event 事件
     */
    @EventHandler
    public void onKick(PlayerKickEvent event) {
        removeCache(event.getPlayer());
    }

    /**
     * 玩家离开服务器事件.
     *
     * @param event 事件
     */
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        removeCache(event.getPlayer());
    }

    /**
     * 从数据库恢复私聊模式
     *
     * @param player 玩家
     * @since 3.4.0
     */
    private void restoreTellTarget(Player player) {
        try {
            Optional<ChatPlayerChannelEnter> data = ChatPlayerChannelService.getInstance().findByUid(player.getUniqueId());
            if (!data.isPresent()) return;

            String tellTargetStr = data.get().getTellTarget();
            if (tellTargetStr == null || tellTargetStr.isEmpty()) return;

            UUID targetUuid = UUID.fromString(tellTargetStr);
            ChatConstants.PLAYER_TELL_TARGET.put(player.getUniqueId(), targetUuid);

            // 提示玩家私聊模式已恢复
            Player target = Bukkit.getPlayer(targetUuid);
            String targetName = target != null && target.isOnline()
                ? target.getName()
                : tellTargetStr.substring(0, 8) + "...";
            player.sendMessage("§7私聊模式已恢复，正在与 §f" + targetName + " §7私聊");
        } catch (Exception e) {
            // UUID 解析失败等异常，静默忽略
        }
    }

    /**
     * 清理缓存
     *
     * @param player 事件
     */
    private void removeCache(Player player) {
        ChatConstants.PLAYER_CHAT_CHANNEL.remove(player.getUniqueId());
        ChatConstants.PLAYER_PLUGIN_CHANNEL.remove(player.getUniqueId());
        ChatConstants.PLAYER_CHAT_TIME.remove(player.getUniqueId());
        ChatConstants.PLAYER_LIST.remove(player.getName());
        ChatConstants.PLAYER_IGNORE_MAP.remove(player.getUniqueId());
        ChatConstants.PLAYER_VOTE_MAP.remove(player.getUniqueId());
        ChatConstants.PLAYER_CHAT_NICK.remove(player.getUniqueId());
        ChatConstants.PLAYER_MUTE_CACHE.remove(player.getUniqueId());
        // 3.4.0: 清理私聊模式（数据库中保留，下次上线恢复）
        ChatConstants.PLAYER_TELL_TARGET.remove(player.getUniqueId());
        // 3.4.0: 清理频道屏蔽和监控缓存
        ChatConstants.PLAYER_CHANNEL_BLOCKED.remove(player.getUniqueId());
        ChatConstants.PLAYER_SOCIAL_SPY.remove(player.getUniqueId());
        BcUtil.sendPlayerList();
    }

}
