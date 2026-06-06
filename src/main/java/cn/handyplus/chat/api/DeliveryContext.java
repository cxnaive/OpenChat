package cn.handyplus.chat.api;

/**
 * 消息投递上下文。
 * <p>在 {@link MessageFilter#shouldDeliver} 中使用，描述即将投递的消息信息。</p>
 *
 * @since 3.4.0
 */
public class DeliveryContext {

    private final String channel;
    private final String senderName;
    private final String message;
    private final boolean tell;
    private final String tellTarget;

    public DeliveryContext(String channel, String senderName, String message, boolean tell, String tellTarget) {
        this.channel = channel;
        this.senderName = senderName;
        this.message = message;
        this.tell = tell;
        this.tellTarget = tellTarget;
    }

    /** 当前频道 ID（如 "default"、"tell"、"MinigameFramework_room"） */
    public String getChannel() { return channel; }

    /** 发送者名称 */
    public String getSenderName() { return senderName; }

    /** 原始消息内容（已去除颜色代码） */
    public String getMessage() { return message; }

    /** 是否为私聊消息 */
    public boolean isTell() { return tell; }

    /** 私聊目标玩家名（非私聊时为 null） */
    public String getTellTarget() { return tellTarget; }

}
