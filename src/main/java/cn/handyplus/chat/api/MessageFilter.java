package cn.handyplus.chat.api;

import org.bukkit.entity.Player;

/**
 * 消息投递过滤器。
 * <p>外部插件可实现此接口，在 OpenChat 将消息投递给玩家之前进行拦截。
 * 注册后，每次消息即将发送给某位玩家时都会调用 {@link #shouldDeliver}。</p>
 *
 * <p>使用场景：</p>
 * <ul>
 *   <li>小游戏插件阻止特殊房间中的玩家接收全服消息</li>
 *   <li>小游戏插件阻止私聊已禁用的玩家接收私信</li>
 *   <li>任何需要按条件拦截消息投递的场景</li>
 * </ul>
 *
 * @since 3.4.0
 */
public interface MessageFilter {

    /**
     * 消息即将投递给 recipient 时调用。
     *
     * @param recipient 消息接收者
     * @param context   投递上下文（频道、发送者、内容等）
     * @return null 表示允许投递；非 null 表示拒绝，返回值为拒绝原因（会通知发送者）
     */
    String shouldDeliver(Player recipient, DeliveryContext context);

}
