package cn.handyplus.chat.core;

import cn.handyplus.chat.PlayerChat;
import cn.handyplus.chat.api.DeliveryContext;
import cn.handyplus.chat.api.MessageFilter;
import cn.handyplus.chat.api.RecipientProvider;
import cn.handyplus.chat.constants.ChatConstants;
import cn.handyplus.chat.hook.PlaceholderApiUtil;
import cn.handyplus.chat.param.ChatChildParam;
import cn.handyplus.chat.param.ChatParam;
import cn.handyplus.chat.util.ConfigUtil;
import cn.handyplus.lib.constants.BaseConstants;
import cn.handyplus.lib.core.CollUtil;
import cn.handyplus.lib.core.JsonUtil;
import cn.handyplus.lib.core.Pair;
import cn.handyplus.lib.core.PatternUtil;
import cn.handyplus.lib.core.StrUtil;
import cn.handyplus.lib.internal.HandySchedulerUtil;
import cn.handyplus.lib.internal.PlayerSchedulerUtil;
import cn.handyplus.lib.util.BaseUtil;
import cn.handyplus.lib.util.BcUtil;
import cn.handyplus.lib.util.HandyConfigUtil;
import cn.handyplus.lib.util.HandyPermissionUtil;
import cn.handyplus.lib.util.ItemStackUtil;
import cn.handyplus.lib.util.MessageUtil;
import cn.handyplus.lib.util.RgbTextUtil;
import cn.handyplus.lib.util.XSeriesUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * 聊天解析工具
 *
 * @author handy
 */
public class ChatUtil {

    /**
     * 异步发送消息
     *
     * @param msg          消息内容
     * @param isConsoleMsg 打印消息
     */
    public static void asyncSendMsg(BcUtil.BcMessageParam msg, boolean isConsoleMsg) {
        HandySchedulerUtil.runTaskAsynchronously(() -> sendTextMsg(msg, isConsoleMsg));
    }

    /**
     * 发送消息
     *
     * @param param        消息内容
     * @param isConsoleMsg 打印消息
     */
    public synchronized static void sendTextMsg(BcUtil.BcMessageParam param, boolean isConsoleMsg) {
        String chatParamJson = param.getMessage();
        ChatParam chatParam = JsonUtil.toBean(chatParamJson, ChatParam.class);
        RgbTextUtil rgbTextUtil = buildMsg(chatParam, param.getType());
        String channel = chatParam.getChannel();
        // 频道是否开启
        if (StrUtil.isEmpty(ChannelUtil.isChannelEnable(channel))) {
            return;
        }

        boolean isTell = StrUtil.isNotEmpty(chatParam.getTellPlayerName());

        // 获取接收者列表：优先使用 RecipientProvider，否则使用默认权限过滤
        Player senderPlayer = Bukkit.getPlayer(param.getPlayerName());
        List<Player> recipients = resolveRecipients(channel, senderPlayer);

        // ====== 发送者侧检查：频道发送屏蔽 ======
        // 如果发送者的该频道被系统级屏蔽（如游戏进行中），禁止发送
        if (senderPlayer != null) {
            Set<String> senderSystemBlocked = ChatConstants.PLAYER_CHANNEL_SYSTEM_BLOCKED.get(senderPlayer.getUniqueId());
            if (senderSystemBlocked != null && senderSystemBlocked.contains(channel)) {
                MessageUtil.sendMessage(senderPlayer, "§c该频道当前已禁用，无法发送消息");
                return;
            }
            Set<String> senderBlocked = ChatConstants.PLAYER_CHANNEL_BLOCKED.get(senderPlayer.getUniqueId());
            if (senderBlocked != null && senderBlocked.contains(channel)) {
                MessageUtil.sendMessage(senderPlayer, "§c该频道当前已禁用，无法发送消息");
                return;
            }
        }

        // 构建投递上下文（过滤器用）
        String rawMessage = chatParam.getMessage() != null ? BaseUtil.stripColor(chatParam.getMessage()) : "";

        // 根据频道发送消息
        for (Player onlinePlayer : recipients) {
            // 判断是否开启私信
            if (isTell && !onlinePlayer.getName().equals(chatParam.getTellPlayerName())) {
                continue;
            }
            // 判断是否开启附近的人
            Pair<Boolean, List<UUID>> nearbyPlayersPair = chatParam.getNearbyPlayers();
            if (nearbyPlayersPair != null && nearbyPlayersPair.getKey()) {
                if (!nearbyPlayersPair.getValue().contains(onlinePlayer.getUniqueId())) {
                    continue;
                }
            }
            // 判断是否开启屏蔽
            List<String> ignoreList = ChatConstants.PLAYER_IGNORE_MAP.get(onlinePlayer.getUniqueId());
            if (CollUtil.isNotEmpty(ignoreList) && ignoreList.contains(param.getPlayerName())) {
                continue;
            }

            // ====== 3.4.0 新增：频道接收屏蔽检查 ======
            Set<String> blockedChannels = ChatConstants.PLAYER_CHANNEL_BLOCKED.get(onlinePlayer.getUniqueId());
            if (blockedChannels != null && blockedChannels.contains(channel)) {
                continue;
            }
            // 系统级屏蔽（不可被玩家自行覆盖，如游戏进行中）
            Set<String> systemBlocked = ChatConstants.PLAYER_CHANNEL_SYSTEM_BLOCKED.get(onlinePlayer.getUniqueId());
            if (systemBlocked != null && systemBlocked.contains(channel)) {
                continue;
            }

            // ====== 3.4.0 新增：投递前过滤器检查 ======
            if (!ChatConstants.MESSAGE_FILTERS.isEmpty()) {
                DeliveryContext ctx = new DeliveryContext(channel, param.getPlayerName(), rawMessage, isTell, chatParam.getTellPlayerName());
                boolean rejected = false;
                for (MessageFilter filter : ChatConstants.MESSAGE_FILTERS) {
                    String rejectReason = filter.shouldDeliver(onlinePlayer, ctx);
                    if (rejectReason != null) {
                        handleRejection(param.getPlayerName(), onlinePlayer.getName(), rejectReason);
                        rejected = true;
                        break;
                    }
                }
                if (rejected) {
                    continue;
                }
            }

            rgbTextUtil.send(onlinePlayer);
            // 如果开启艾特，发送消息
            if (ChatConstants.CHAT_TYPE.equals(param.getType()) && ConfigUtil.CHAT_CONFIG.getBoolean("at.enable")) {
                // 获取艾特玩家
                if (CollUtil.isNotEmpty(chatParam.getMentionedPlayers()) && chatParam.getMentionedPlayers().contains(onlinePlayer.getName())) {
                    String sound = ConfigUtil.CHAT_CONFIG.getString("at.sound");
                    playSound(onlinePlayer, sound);
                }
            }
            // 播放频道发言音效
            String channelSound = ConfigUtil.CHAT_CONFIG.getString("chat." + channel + ".sound");
            playSound(onlinePlayer, channelSound);
        }
        // 控制台消息
        if (isConsoleMsg) {
            rgbTextUtil.sendConsole();
        }

        // ====== 3.4.0 新增：管理员监控（Social Spy）======
        if (!ChatConstants.PLAYER_SOCIAL_SPY.isEmpty()) {
            // 构建监控消息
            String spyMsg;
            if (isTell) {
                spyMsg = "§7[§cSpy§7] §f" + param.getPlayerName() + " §7→ §f" + chatParam.getTellPlayerName() + "§7: §f" + rawMessage;
            } else {
                // 查找频道描述（如房间号、队伍名）
                String channelDesc = resolveChannelDescription(channel, senderPlayer);
                String channelLabel = channelDesc != null
                    ? "§7[§cSpy§7] §7[" + channelDesc + "§7]"
                    : "§7[§cSpy§7]";
                spyMsg = channelLabel + " §f" + param.getPlayerName() + "§7: §f" + rawMessage;
            }
            for (Map.Entry<UUID, Boolean> entry : ChatConstants.PLAYER_SOCIAL_SPY.entrySet()) {
                if (!Boolean.TRUE.equals(entry.getValue())) continue;
                if (entry.getKey() != null) {
                    Player spy = Bukkit.getPlayer(entry.getKey());
                    // 私聊不发给私聊双方自己
                    if (spy != null && spy.isOnline()
                        && !(isTell && (spy.getName().equals(param.getPlayerName()) || spy.getName().equals(chatParam.getTellPlayerName())))) {
                        spy.sendMessage(spyMsg);
                    }
                }
            }
        }
    }

    /**
     * 解析频道接收者列表。
     * 优先查找已注册的 {@link RecipientProvider}，否则使用默认的权限过滤机制。
     *
     * @param channel 频道 ID
     * @param sender  发送者（可能为 null，跨服消息时发送者不在本服）
     * @return 接收者列表
     * @since 3.4.0
     */
    private static List<Player> resolveRecipients(String channel, Player sender) {
        for (RecipientProvider provider : ChatConstants.RECIPIENT_PROVIDERS) {
            if (channel.startsWith(provider.getChannelPrefix())) {
                List<Player> recipients = provider.getRecipients(channel, sender);
                if (recipients != null) {
                    return recipients;
                }
            }
        }
        return ChannelUtil.getChannelPlayer(channel);
    }

    /**
     * 查找频道描述（用于管理员监控显示）。
     *
     * @param channel 频道 ID
     * @param sender  发送者
     * @return 描述文本，null 表示无描述
     * @since 3.4.0
     */
    private static String resolveChannelDescription(String channel, Player sender) {
        for (RecipientProvider provider : ChatConstants.RECIPIENT_PROVIDERS) {
            if (channel.startsWith(provider.getChannelPrefix())) {
                String desc = provider.getChannelDescription(channel, sender);
                if (desc != null) return desc;
            }
        }
        return null;
    }

    /**
     * 处理消息投递拒绝通知。
     * <p>如果发送者在本服则直接通知；如果发送者在其他子服，通过 Forward 回传拒绝通知。</p>
     *
     * @param senderName          发送者名称
     * @param rejectedPlayerName  被拒绝投递的玩家名称
     * @param reason              拒绝原因
     * @since 3.4.0
     */
    private static void handleRejection(String senderName, String rejectedPlayerName, String reason) {
        // 尝试本服直接通知
        Player sender = Bukkit.getPlayer(senderName);
        if (sender != null && sender.isOnline()) {
            MessageUtil.sendMessage(sender, "§c发送给 §e" + rejectedPlayerName + " §c的消息被拒绝: §7" + reason);
            return;
        }
        // 跨服：发送拒绝通知
        BcUtil.BcMessageParam rejectParam = new BcUtil.BcMessageParam();
        rejectParam.setPluginName(PlayerChat.INSTANCE.getName());
        rejectParam.setType(ChatConstants.REJECT_TYPE);
        rejectParam.setPlayerName(rejectedPlayerName);
        rejectParam.setSenderName(senderName);
        rejectParam.setMessage(reason);
        rejectParam.setTimestamp(System.currentTimeMillis());
        // 使用任意在线玩家发送 Forward
        Player anyPlayer = Bukkit.getOnlinePlayers().iterator().next();
        if (anyPlayer != null) {
            BcUtil.sendParamForward(anyPlayer, rejectParam);
        }
    }

    /**
     * 构建消息
     *
     * @param player  玩家
     * @param channel 频道
     * @return 参数
     */
    public static @Nullable ChatParam buildChatParam(@NotNull Player player, @NotNull String channel) {
        // 频道是否开启
        String channelEnable = ChannelUtil.isChannelEnable(channel);
        if (StrUtil.isEmpty(channelEnable)) {
            return null;
        }
        String channelName = ChannelUtil.getChannelName(channel);
        Set<String> keySet = HandyConfigUtil.getKey(ConfigUtil.CHAT_CONFIG, "chat." + channelEnable + ".format");
        List<ChatChildParam> childList = new ArrayList<>();

        // 已匹配的组（同一组内只显示第一个满足条件的节点）
        Set<String> matchedGroups = new HashSet<>();

        for (String key : keySet) {
            // 节点权限
            String permission = ConfigUtil.CHAT_CONFIG.getString("chat." + channelEnable + ".format." + key + ".permission");
            if (StrUtil.isNotEmpty(permission) && !player.hasPermission(permission)) {
                continue;
            }

            String text = ConfigUtil.CHAT_CONFIG.getString("chat." + channelEnable + ".format." + key + ".text");
            List<String> hover = ConfigUtil.CHAT_CONFIG.getStringList("chat." + channelEnable + ".format." + key + ".hover");
            String click = ConfigUtil.CHAT_CONFIG.getString("chat." + channelEnable + ".format." + key + ".click");
            String clickSuggest = ConfigUtil.CHAT_CONFIG.getString("chat." + channelEnable + ".format." + key + ".clickSuggest");
            String group = ConfigUtil.CHAT_CONFIG.getString("chat." + channelEnable + ".format." + key + ".group");

            // 处理分组：同组内只显示第一个满足条件的
            if (StrUtil.isNotEmpty(group) && !matchedGroups.add(group)) {
                continue;
            }

            // 替换变量
            text = replaceStr(player, channelName, text);
            hover = replaceStr(player, channelName, hover);
            click = replaceStr(player, channelName, click);
            clickSuggest = replaceStr(player, channelName, clickSuggest);
            ChatChildParam chatChildParam = ChatChildParam.builder().text(text).hover(hover).click(click).clickSuggest(clickSuggest).build();
            childList.add(chatChildParam);
        }
        // 构建参数
        return ChatParam.builder().channel(channel).childList(childList).build();
    }

    /**
     * 构建消息
     *
     * @param chatParam 入参
     * @param type      类型
     */
    public static @NotNull RgbTextUtil buildMsg(@NotNull ChatParam chatParam, @NotNull String type) {
        // 加载玩家消息的颜色
        chatParam.setMessage(chatParam.isHasColor() ? chatParam.getMessage() : BaseUtil.stripColor(chatParam.getMessage()));
        for (ChatChildParam chatChildParam : chatParam.getChildList()) {
            chatChildParam.setText(StrUtil.replace(chatChildParam.getText(), "message", chatParam.getMessage()));
        }
        // 构建消息
        List<RgbTextUtil> rgbTextUtilList = new ArrayList<>();
        for (ChatChildParam chatChildParam : chatParam.getChildList()) {
            RgbTextUtil textComponent = RgbTextUtil.init(chatChildParam.getText());
            if (ChatConstants.ITEM_TYPE.equals(type) && StrUtil.isNotEmpty(chatChildParam.getHoverItem())) {
                textComponent.addHoverText(ItemStackUtil.itemStackDeserialize(chatChildParam.getHoverItem()));
            } else {
                textComponent.addHoverText(CollUtil.listToStr(chatChildParam.getHover(), "\n"));
            }
            textComponent.addClickSuggestCommand(chatChildParam.getClickSuggest());
            textComponent.addClickCommand(chatChildParam.getClick());
            textComponent.addClickUrl(chatChildParam.getUrl());
            rgbTextUtilList.add(textComponent);
        }
        // 构建消息
        RgbTextUtil first = rgbTextUtilList.get(0);
        for (int i = 1; i < rgbTextUtilList.size(); i++) {
            first.addExtra(rgbTextUtilList.get(i));
        }
        return first;
    }

    /**
     * 解析内部变量
     *
     * @param player      玩家
     * @param channelName 频道名称
     * @param str         内容
     * @return 新内容
     */
    public static String replaceStr(Player player, String channelName, String str) {
        if (StrUtil.isEmpty(str)) {
            return str;
        }
        str = StrUtil.replace(str, "channel", channelName);
        str = StrUtil.replace(str, "player", player.getName());
        str = StrUtil.replace(str, "nickName", ChatConstants.PLAYER_NICK_CACHE.getOrDefault(player.getUniqueId(), player.getName()));
        str = StrUtil.replace(str, "serverName", BaseConstants.CONFIG.getString("serverName", ""));
        // head组件解析
        str = BaseUtil.headComponent(str, player.getName());
        // 解析 papi 变量
        str = PlaceholderApiUtil.set(player, str);
        return str;
    }

    /**
     * 解析内部变量
     *
     * @param player      玩家
     * @param channelName 频道名称
     * @param strList     内容集合
     * @return 新内容
     */
    public static List<String> replaceStr(Player player, String channelName, List<String> strList) {
        if (CollUtil.isEmpty(strList)) {
            return strList;
        }
        List<String> newStrList = new ArrayList<>();
        for (String str : strList) {
            newStrList.add(replaceStr(player, channelName, str));
        }
        return newStrList;
    }

    /**
     * 处理@人
     *
     * @param mentionedPlayers 被@的人
     * @param message          消息
     * @return 新消息
     * @since 1.0.9
     */
    public static String at(List<String> mentionedPlayers, String message) {
        boolean enable = ConfigUtil.CHAT_CONFIG.getBoolean("at.enable");
        if (!enable) {
            return message;
        }
        List<String> messageList = StrUtil.strToStrList(message, " ");
        for (String name : messageList) {
            if (CollUtil.contains(ChatConstants.PLAYER_LIST, name)) {
                message = message.replaceFirst(name, "@" + name);
            }
        }
        // 提取@的玩家名
        mentionedPlayers.addAll(PatternUtil.extractAtTags(message));
        if (CollUtil.isEmpty(mentionedPlayers)) {
            return message;
        }
        // 将 @玩家名 替换为高亮显示
        boolean keepAt = ConfigUtil.CHAT_CONFIG.getBoolean("at.keepAt", false);
        String atColor = ConfigUtil.CHAT_CONFIG.getString("at.atColor", "&9");
        for (String playerName : mentionedPlayers) {
            message = message.replaceAll("@" + playerName, atColor + (keepAt ? "@" : "") + playerName + ChatColor.RESET);
        }
        return message;
    }

    /**
     * 播放声音
     *
     * @param player   玩家
     * @param soundStr 声音
     * @since 1.0.9
     */
    private static void playSound(Player player, String soundStr) {
        if (StrUtil.isEmpty(soundStr)) {
            return;
        }
        Optional<Sound> soundOptional = XSeriesUtil.getSound(soundStr);
        if (!soundOptional.isPresent()) {
            MessageUtil.sendMessage(player, "没有 " + soundStr + " 音效");
            return;
        }
        PlayerSchedulerUtil.playSound(player, soundOptional.get(), 1, 1);
    }

    /**
     * 聊天校验处理
     *
     * @param player  玩家
     * @param message 消息
     * @return true 不满足条件
     */
    public static boolean chatCheck(Player player, String message) {
        // 内容黑名单处理
        if (blackListCheck(message)) {
            MessageUtil.sendMessage(player, BaseUtil.getLangMsg("blacklistMsg"));
            return true;
        }
        // 聊天间隔处理
        int chatTime = HandyPermissionUtil.getReverseIntNumber(player, BaseConstants.CONFIG, "chatTime");
        if (ChatConstants.PLAYER_CHAT_TIME.containsKey(player.getUniqueId())) {
            long keepAlive = (System.currentTimeMillis() - ChatConstants.PLAYER_CHAT_TIME.get(player.getUniqueId())) / 1000L;
            if (keepAlive < chatTime) {
                String waitTimeMsg = BaseUtil.getLangMsg("chatTime").replace("${chatTime}", (chatTime - keepAlive) + "");
                MessageUtil.sendMessage(player, waitTimeMsg);
                return true;
            }
        }
        ChatConstants.PLAYER_CHAT_TIME.put(player.getUniqueId(), System.currentTimeMillis());
        return false;
    }

    /**
     * 黑名单控制
     *
     * @param message 消息
     * @return true 存在黑名单语言
     */
    public static boolean blackListCheck(String message) {
        List<String> blacklist = BaseConstants.CONFIG.getStringList("blacklist");
        String stripColorMessage = BaseUtil.stripColor(message);
        if (CollUtil.isNotEmpty(blacklist)) {
            for (String blackMsg : blacklist) {
                if (StrUtil.isEmpty(blackMsg)) {
                    continue;
                }
                if (stripColorMessage.contains(blackMsg)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 获取所在频道
     *
     * @param player 玩家
     * @return 频道
     */
    public static String getChannel(Player player) {
        String channel = ChatConstants.PLAYER_CHAT_CHANNEL.getOrDefault(player.getUniqueId(), ChatConstants.DEFAULT);
        // 插件频道直接返回
        if (ChatConstants.PLUGIN_CHANNEL.containsKey(channel)) {
            return channel;
        }
        // 是否有对应频道权限 如果没有权限回到默认频道
        if (!ChatConstants.DEFAULT.equals(channel) && !player.hasPermission(ChatConstants.PLAYER_CHAT_USE + channel)) {
            channel = ChatConstants.DEFAULT;
            // 缓存为频道
            ChatConstants.PLAYER_CHAT_CHANNEL.put(player.getUniqueId(), channel);
        }
        // 频道是否开启
        if (StrUtil.isEmpty(ChannelUtil.isChannelEnable(channel))) {
            return ChatConstants.DEFAULT;
        }
        return channel;
    }

}