package cn.handyplus.chat.constants;

import cn.handyplus.chat.enter.ChatPlayerMuteEnter;

import cn.handyplus.chat.api.MessageFilter;
import cn.handyplus.chat.api.RecipientProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 常量
 *
 * @author handy
 */
public final class ChatConstants {

    private ChatConstants() {
    }

    /**
     * 默认频道
     */
    public final static String DEFAULT = "default";

    /**
     * 私信频道
     */
    public static final String TELL = "tell";

    /**
     * 消息类型
     */
    public final static String CHAT_TYPE = "RICE_CHAT";

    /**
     * 物品类型
     */
    public final static String ITEM_TYPE = "RICE_ITEM";

    /**
     * 玩家当前频道
     */
    public static final Map<UUID, String> PLAYER_CHAT_CHANNEL = new HashMap<>();

    /**
     * 玩家投票记录
     */
    public static final Map<UUID, Integer> PLAYER_VOTE_MAP = new HashMap<>();

    /**
     * 插件频道
     * key 频道 value 插件名
     *
     * @since 1.0.6
     */
    public static final Map<String, String> PLUGIN_CHANNEL = new HashMap<>();

    /**
     * 玩家注册的插件频道
     *
     * @since 1.0.6
     */
    public static final Map<UUID, List<String>> PLAYER_PLUGIN_CHANNEL = new HashMap<>();

    /**
     * 玩家聊天冷却
     *
     * @since 1.0.7
     */
    public static final Map<UUID, Long> PLAYER_CHAT_TIME = new HashMap<>();

    /**
     * 玩家昵称缓存
     *
     * @since 2.0.5
     */
    public static final Map<UUID, String> PLAYER_CHAT_NICK = new HashMap<>();

    /**
     * 频道使用权限
     *
     * @since 1.1.4
     */
    public static final String PLAYER_CHAT_USE = "playerChat.use.";

    /**
     * 频道查看权限
     *
     * @since 1.1.4
     */
    public static final String PLAYER_CHAT_CHAT = "playerChat.chat.";

    /**
     * 玩家列表
     *
     * @since 1.1.5
     */
    public static List<String> PLAYER_LIST = new ArrayList<>();

    /**
     * 玩家忽略列表
     *
     * @since 1.4.3
     */
    public static Map<UUID, List<String>> PLAYER_IGNORE_MAP = new HashMap<>();

    /**
     * 玩家昵称缓存
     * key: 玩家UUID, value: 昵称
     *
     * @since 2.0.6
     */
    public static Map<UUID, String> PLAYER_NICK_CACHE = new HashMap<>();
    /**
     * 违规
     *
     * @since 2.0.0
     */
    public static final String ILLEGAL = "违规";

    /**
     * AI审核
     *
     * @since 2.0.0
     */
    public static final String AI_ENABLE = "ai.enable";
    /**
     * AI审核忽略
     *
     * @since 2.0.0
     */
    public static final String AI_IGNORE = "playerChat.ai.ignore";

    /**
     * 昵称权限
     *
     * @since 2.0.5
     */
    public static final String NICK_OTHER = "playerChat.nick.other";

    /**
     * 全部
     *
     * @since 2.1.0
     */
    public static final String ALL = "[ALL]";

    /**
     * 聊天颜色
     *
     * @since 3.2.4
     */
    public static final String CHAT_COLOR = "playerChat.color";

    /**
     * 玩家禁言缓存
     * key: 玩家UUID, value: 禁言记录
     */
    public static final Map<UUID, Optional<ChatPlayerMuteEnter>> PLAYER_MUTE_CACHE = new HashMap<>();

    /**
     * 命令别名映射
     * key: 别名, value: 实际命令
     *
     * @since 2.0.6
     */
    public static final Map<String, String> COMMAND_ALIAS_MAP = new HashMap<>();

    // ======================== 3.4.0 新增 ========================

    /**
     * 消息投递过滤器列表（线程安全）
     *
     * @since 3.4.0
     */
    public static final List<MessageFilter> MESSAGE_FILTERS = new CopyOnWriteArrayList<>();

    /**
     * 自定义频道成员解析器列表（线程安全）
     *
     * @since 3.4.0
     */
    public static final List<RecipientProvider> RECIPIENT_PROVIDERS = new CopyOnWriteArrayList<>();

    /**
     * 私聊模式目标：玩家UUID → 私聊对象UUID
     * <p>进入私聊模式后，玩家发送的所有聊天消息都会自动路由给此目标。</p>
     *
     * @since 3.4.0
     */
    public static final Map<UUID, UUID> PLAYER_TELL_TARGET = new ConcurrentHashMap<>();

    /**
     * 拒绝通知消息类型（跨服拒绝回传用）
     *
     * @since 3.4.0
     */
    public static final String REJECT_TYPE = "RICE_REJECT";

    /**
     * 玩家频道接收屏蔽集合（玩家自身偏好）。
     * <p>key: 玩家UUID, value: 被屏蔽的频道名集合。
     * 不在 Map 中的玩家 = 接收所有频道（默认行为）。
     * 集合为空 = 接收所有频道。</p>
     *
     * @since 3.4.0
     */
    public static final Map<UUID, Set<String>> PLAYER_CHANNEL_BLOCKED = new ConcurrentHashMap<>();

    /**
     * 系统级频道屏蔽集合（不可被玩家手动覆盖）。
     * <p>由插件（如小游戏框架）设置，用于游戏进行中禁用特定频道。
     * 玩家无法通过 GUI 或命令解除此屏蔽。</p>
     *
     * @since 3.4.0
     */
    public static final Map<UUID, Set<String>> PLAYER_CHANNEL_SYSTEM_BLOCKED = new ConcurrentHashMap<>();

    /**
     * 管理员私聊监控（Social Spy）。
     * <p>启用后管理员可以看到所有玩家间的私信。</p>
     *
     * @since 3.4.0
     */
    public static final Map<UUID, Boolean> PLAYER_SOCIAL_SPY = new ConcurrentHashMap<>();

}