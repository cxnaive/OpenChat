package cn.handyplus.chat.command.player;

import cn.handyplus.chat.constants.ChatConstants;
import cn.handyplus.chat.core.MuteUtil;
import cn.handyplus.chat.listener.PlayerChatListener;
import cn.handyplus.chat.service.ChatPlayerChannelService;
import cn.handyplus.lib.command.IHandyCommandEvent;
import cn.handyplus.lib.core.MapUtil;
import cn.handyplus.lib.util.AssertUtil;
import cn.handyplus.lib.util.BaseUtil;
import cn.handyplus.lib.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 私信 / 私聊模式
 *
 * <p>用法：</p>
 * <ul>
 *   <li>/tell &lt;玩家&gt; &lt;消息&gt; — 发送一条私信（原有行为）</li>
 *   <li>/tell &lt;玩家&gt; — 进入与该玩家的私聊模式</li>
 *   <li>/tell — 退出私聊模式</li>
 * </ul>
 *
 * @author handy
 * @since 1.0.0 (私聊模式 since 3.4.0)
 */
public class TellCommand implements IHandyCommandEvent {

    @Override
    public String command() {
        return "tell";
    }

    @Override
    public String permission() {
        return "playerChat.tell";
    }

    @Override
    public boolean isAsync() {
        return true;
    }

    @Override
    public void onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        // 是否为玩家
        Player player = AssertUtil.notPlayer(sender, BaseUtil.getLangMsg("noPlayerFailureMsg"));
        if (player == null) return;

        // 检查是否被禁言
        if (MuteUtil.checkMute(player)) {
            return;
        }

        // /tell — 退出私聊模式 / /tell <玩家> — 进入私聊模式
        // 注意：args[0] 是子命令名 "tell"，所以 args.length < 3 表示没有消息内容
        if (args.length < 3) {
            handleModeSwitch(player, args);
            return;
        }

        // /tell <玩家> <消息> — 发送单条私信（原有行为）
        sendSingleMessage(player, args);
    }

    /**
     * 处理私聊模式切换
     * <ul>
     *   <li>无参数 /tell → 退出私聊模式</li>
     *   <li>/tell <玩家> → 进入与该玩家的私聊模式</li>
     * </ul>
     */
    private void handleModeSwitch(Player player, String[] args) {
        // args[0] = "tell"（子命令名），无额外参数时退出私聊模式
        if (args.length <= 1) {
            // /tell — 退出私聊模式
            UUID currentTarget = ChatConstants.PLAYER_TELL_TARGET.remove(player.getUniqueId());
            if (currentTarget != null) {
                ChatPlayerChannelService.getInstance().setTellTarget(player.getUniqueId(), null);
                MessageUtil.sendMessage(player, BaseUtil.getLangMsg("tellModeExit",
                    MapUtil.of("${player}", getPlayerName(currentTarget))));
            } else {
                MessageUtil.sendMessage(player, BaseUtil.getLangMsg("tellModeNotInMode"));
            }
            return;
        }

        // /tell <玩家> — 进入私聊模式
        String targetName = args[1] != null ? args[1] : args[0];
        // 参数检查：/plc tell xxx 的情况下 args[0] 就是玩家名
        // /tell xxx 的情况下 args[1] 是玩家名（args[0]=tell）
        if (args.length == 1) {
            targetName = args[0];
        }

        // 不能和自己私聊
        AssertUtil.notTrue(player.getName().equalsIgnoreCase(targetName),
            BaseUtil.getLangMsg("sendTellErrorMsg"));

        // 目标玩家是否在线
        Optional<Player> onlinePlayer = BaseUtil.getOnlinePlayer(targetName);
        AssertUtil.isTrue(onlinePlayer.isPresent(),
            BaseUtil.getLangMsg("playerOfflineMsg", MapUtil.of("${player}", targetName)));

        // 进入私聊模式
        Player target = onlinePlayer.get();
        ChatPlayerChannelService.getInstance().setTellTarget(player.getUniqueId(), target.getUniqueId());

        MessageUtil.sendMessage(player, BaseUtil.getLangMsg("tellModeEnter",
            MapUtil.of("${player}", target.getName())));
    }

    /**
     * 发送单条私信（原有行为）
     */
    private void sendSingleMessage(Player player, String[] args) {
        String playerName = args[1];
        AssertUtil.notTrue(player.getName().equals(playerName), BaseUtil.getLangMsg("sendTellErrorMsg"));

        // 私信玩家是否在线
        Optional<Player> onlinePlayer = BaseUtil.getOnlinePlayer(playerName);
        AssertUtil.isTrue(onlinePlayer.isPresent(),
            BaseUtil.getLangMsg("playerOfflineMsg", MapUtil.of("${player}", playerName)));

        // 获取消息
        String message = Arrays.stream(args, 2, args.length).collect(Collectors.joining(" "));

        // 发送消息
        PlayerChatListener.sendMsg(player, message, ChatConstants.TELL, playerName);

        // 没有颜色代码权限，移除颜色代码
        if (!player.hasPermission(ChatConstants.CHAT_COLOR)) {
            message = BaseUtil.stripColor(message);
        }
        HashMap<String, String> map = MapUtil.of("${player}", playerName, "${message}", message);
        MessageUtil.sendMessage(player, BaseUtil.getLangMsg("sendTell", map));
    }

    /**
     * 获取玩家名称
     */
    private String getPlayerName(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        return player != null ? player.getName() : uuid.toString().substring(0, 8);
    }

}
