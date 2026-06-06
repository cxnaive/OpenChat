package cn.handyplus.chat.command;

import cn.handyplus.chat.constants.ChatConstants;
import cn.handyplus.chat.core.ChannelUtil;
import cn.handyplus.chat.gui.ChannelAdminGui;
import cn.handyplus.chat.service.ChatPlayerChannelService;
import cn.handyplus.chat.util.ConfigUtil;
import cn.handyplus.lib.core.MapUtil;
import cn.handyplus.lib.util.BaseUtil;
import cn.handyplus.lib.util.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * /channel 独立命令。
 * <ul>
 *   <li>/channel — 打开频道管理 GUI</li>
 *   <li>/channel &lt;频道名&gt; — 切换到指定频道</li>
 *   <li>/channel admin — 打开管理员频道管理 GUI</li>
 * </ul>
 *
 * @since 3.4.0
 */
public class ChannelMainCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§c该命令只能由玩家执行。");
            return true;
        }
        Player player = (Player) sender;

        if (args.length == 0) {
            // /channel — 打开玩家频道 GUI
            ChannelAdminGui.open(player);
            return true;
        }

        String sub = args[0].toLowerCase();

        if (sub.equals("admin")) {
            // /channel admin — 管理员 GUI（需要管理员权限）
            if (!player.hasPermission("playerChat.admin")) {
                MessageUtil.sendMessage(player, BaseUtil.getLangMsg("noPermission"));
                return true;
            }
            ChannelAdminGui.openAdmin(player);
            return true;
        }

        // /channel <频道名> — 切换频道
        if (!player.hasPermission("playerChat.channel")) {
            MessageUtil.sendMessage(player, BaseUtil.getLangMsg("noPermission"));
            return true;
        }

        String channel = args[0];
        String chatChannel = ChannelUtil.isChannelEnable(channel);
        if (chatChannel == null) {
            MessageUtil.sendMessage(player, BaseUtil.getLangMsg("channelDoesNotExist"));
            return true;
        }
        if (ChatConstants.TELL.equals(chatChannel)) {
            MessageUtil.sendMessage(player, BaseUtil.getLangMsg("channelDoesNotExist"));
            return true;
        }

        // 权限检查（插件频道用成员注册制，跳过权限检查）
        if (!ChatConstants.PLUGIN_CHANNEL.containsKey(channel)) {
            String channelPermission = ChatConstants.PLAYER_CHAT_USE + chatChannel;
            if (!player.hasPermission(channelPermission)) {
                MessageUtil.sendMessage(player, BaseUtil.getLangMsg("noChannelPermission", MapUtil.of("${permission}", channelPermission)));
                return true;
            }
        }

        ChatPlayerChannelService.getInstance().setChannel(player.getUniqueId(), channel);
        String channelName = org.bukkit.ChatColor.translateAlternateColorCodes('&',
            ChannelUtil.getChannelName(chatChannel));
        MessageUtil.sendMessage(player, BaseUtil.getLangMsg("channelSwitchMsg", MapUtil.of("${channel}", channelName)));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            String input = args[0].toLowerCase();
            if ("admin".startsWith(input) && sender.hasPermission("playerChat.admin")) {
                completions.add("admin");
            }
            // 添加可用频道名
            if (sender instanceof Player) {
                Player p = (Player) sender;
                for (String key : ConfigUtil.CHAT_CONFIG.getConfigurationSection("chat").getKeys(false)) {
                    if (key.equals(ChatConstants.TELL)) continue;
                    if (key.toLowerCase().startsWith(input)) {
                        // 插件频道用成员注册制，非插件频道用权限制
                        if (ChatConstants.PLUGIN_CHANNEL.containsKey(key)) {
                            List<String> playerChannels = ChatConstants.PLAYER_PLUGIN_CHANNEL
                                .getOrDefault(p.getUniqueId(), java.util.Collections.emptyList());
                            if (!playerChannels.contains(key)) continue;
                        } else {
                            if (!p.hasPermission(ChatConstants.PLAYER_CHAT_USE + key)) continue;
                        }
                        completions.add(key);
                    }
                }
            }
        }
        return completions;
    }

}
