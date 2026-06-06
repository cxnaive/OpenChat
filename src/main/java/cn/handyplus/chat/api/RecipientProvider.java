package cn.handyplus.chat.api;

import org.bukkit.entity.Player;

import java.util.List;

/**
 * 自定义频道成员解析器。
 * <p>外部插件可实现此接口，为自己的插件频道提供成员列表。
 * 注册后，当消息的目标频道匹配前缀时，OpenChat 将使用此 Provider
 * 返回的玩家列表代替默认的权限过滤机制。</p>
 *
 * <p>使用场景：</p>
 * <ul>
 *   <li>小游戏房间频道：只投递给同一房间的玩家</li>
 *   <li>队伍频道：只投递给同一队伍的玩家</li>
 *   <li>阵营频道：只投递给同一阵营的玩家</li>
 *   <li>派对频道：只投递给同一派对的玩家</li>
 * </ul>
 *
 * @since 3.4.0
 */
public interface RecipientProvider {

    /**
     * 该 Provider 负责的频道 ID 前缀。
     * <p>例如 "MinigameFramework_" 将处理所有以此开头的频道
     * （MinigameFramework_room、MinigameFramework_team 等）。</p>
     *
     * @return 频道前缀
     */
    String getChannelPrefix();

    /**
     * 返回指定频道消息的接收者列表。
     *
     * @param fullChannelId 完整频道 ID（如 "MinigameFramework_room"）
     * @param sender        发送者（可能为 null，如跨服消息时发送者不在本服）
     * @return 应该接收此消息的玩家列表
     */
    List<Player> getRecipients(String fullChannelId, Player sender);

    /**
     * 返回指定频道的描述信息（用于管理员监控显示）。
     * <p>例如房间频道返回 "§b房间#3"、队伍频道返回 "§9队伍-队长名"。</p>
     *
     * @param fullChannelId 完整频道 ID
     * @param sender        发送者（可能为 null）
     * @return 描述文本，null 表示无描述
     * @since 3.4.0
     */
    default String getChannelDescription(String fullChannelId, Player sender) {
        return null;
    }

}
