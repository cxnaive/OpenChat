package cn.handyplus.chat.gui;

import cn.handyplus.chat.PlayerChat;
import cn.handyplus.chat.constants.ChatConstants;
import cn.handyplus.chat.core.ChannelUtil;
import cn.handyplus.chat.service.ChatPlayerChannelService;
import cn.handyplus.chat.util.ConfigUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 频道管理 GUI。
 * <ul>
 *   <li>/channel → 玩家视图：切换频道发言 + 开关频道接收</li>
 *   <li>/channel admin → 管理员视图：额外含私聊监控开关</li>
 * </ul>
 *
 * @since 3.4.0
 */
public class ChannelAdminGui {

    public static final String PLAYER_TITLE = "§b§l频道管理";
    public static final String ADMIN_TITLE = "§c§l频道管理 §7(管理员)";

    public static class Holder implements InventoryHolder {
        private Inventory inventory;
        private boolean admin;

        public boolean isAdmin() { return admin; }
        public void setAdmin(boolean admin) { this.admin = admin; }
        @Override
        public Inventory getInventory() { return inventory; }
        public void setInventory(Inventory inventory) { this.inventory = inventory; }
    }

    /**
     * 打开玩家频道管理 GUI
     */
    public static void open(Player player) {
        openInternal(player, false);
    }

    /**
     * 打开管理员频道管理 GUI
     */
    public static void openAdmin(Player player) {
        openInternal(player, true);
    }

    private static void openInternal(Player player, boolean admin) {
        // 获取所有频道
        List<String> channels = new ArrayList<>();
        ConfigurationSection section = ConfigUtil.CHAT_CONFIG.getConfigurationSection("chat");
        if (section != null) {
            channels.addAll(section.getKeys(false));
        }

        // GUI 行数
        int rows = admin ? 6 : Math.max(4, (channels.size() / 7) + 3);
        int size = Math.min(54, rows * 9);

        Holder holder = new Holder();
        holder.setAdmin(admin);
        Inventory gui = Bukkit.createInventory(holder, size, Component.text(admin ? ADMIN_TITLE : PLAYER_TITLE));
        holder.setInventory(gui);

        fillBorder(gui);

        String activeChannel = ChatConstants.PLAYER_CHAT_CHANNEL.getOrDefault(player.getUniqueId(), ChatConstants.DEFAULT);
        Set<String> blocked = ChatConstants.PLAYER_CHANNEL_BLOCKED.get(player.getUniqueId());
        Set<String> systemBlocked = ChatConstants.PLAYER_CHANNEL_SYSTEM_BLOCKED.get(player.getUniqueId());

        // 放置频道项
        int slot = 10;
        for (String channelId : channels) {
            if (ChatConstants.TELL.equals(channelId)) continue;
            if (slot % 9 == 8) slot += 2;
            if (slot >= size - 9) break;

            // 可见性过滤：插件频道用成员注册制，非插件频道用权限制
            boolean isPluginChannel = ChatConstants.PLUGIN_CHANNEL.containsKey(channelId);
            if (isPluginChannel) {
                // 插件频道：只有注册了该频道的玩家才能看到（注册本身就是权限）
                List<String> playerChannels = ChatConstants.PLAYER_PLUGIN_CHANNEL
                    .getOrDefault(player.getUniqueId(), java.util.Collections.emptyList());
                if (!playerChannels.contains(channelId)) continue;
            } else {
                // 非插件频道：检查权限
                if (!player.hasPermission(ChatConstants.PLAYER_CHAT_USE + channelId)) continue;
            }

            boolean isActive = channelId.equals(activeChannel);
            boolean isSystemBlocked = systemBlocked != null && systemBlocked.contains(channelId);
            boolean isReceiving = !isSystemBlocked && (blocked == null || !blocked.contains(channelId));
            String channelName = ChannelUtil.getChannelName(channelId);

            Material material = isActive ? Material.LIME_WOOL : (isReceiving ? Material.GREEN_WOOL : (isSystemBlocked ? Material.BARRIER : Material.RED_WOOL));
            ItemStack item = new ItemStack(material);
            item.editMeta(meta -> {
                meta.displayName(Component.text((isActive ? "§a§l" : "§f") + stripColor(channelName))
                    .decoration(TextDecoration.ITALIC, false));
                List<Component> lore = new ArrayList<>();
                if (isActive) {
                    lore.add(Component.text("§a§l◀ 当前发言频道").decoration(TextDecoration.ITALIC, false));
                }
                if (isSystemBlocked) {
                    lore.add(Component.text("§c✘ 系统禁用中").decoration(TextDecoration.ITALIC, false));
                } else {
                    lore.add(Component.text(isReceiving ? "§a✔ 接收已开启" : "§c✘ 接收已关闭")
                        .decoration(TextDecoration.ITALIC, false));
                }
                lore.add(Component.text("§7").decoration(TextDecoration.ITALIC, false));
                if (!isSystemBlocked) {
                    lore.add(Component.text("§e左键 §7切换发言频道").decoration(TextDecoration.ITALIC, false));
                    lore.add(Component.text("§e右键 §7切换接收开关").decoration(TextDecoration.ITALIC, false));
                } else {
                    lore.add(Component.text("§7该频道当前不可操作").decoration(TextDecoration.ITALIC, false));
                }
                meta.lore(lore);
            });
            // 存储频道 ID 到 PersistentDataContainer 无法在 1.13 API 使用，改用 lore 末尾隐藏标识
            // 这里我们通过名称反查频道 ID

            gui.setItem(slot, item);
            slot++;
        }

        // ====== 底部控制按钮 ======
        int bottom = size - 9;

        // 全部开启接收
        ItemStack allOn = new ItemStack(Material.EMERALD_BLOCK);
        allOn.editMeta(meta -> {
            meta.displayName(Component.text("§a§l全部开启接收").decoration(TextDecoration.ITALIC, false));
            meta.lore(java.util.Collections.singletonList(
                Component.text("§7开启所有频道的消息接收").decoration(TextDecoration.ITALIC, false)));
        });
        gui.setItem(bottom + 1, allOn);

        // 仅保留主频道
        ItemStack allOff = new ItemStack(Material.REDSTONE_BLOCK);
        allOff.editMeta(meta -> {
            meta.displayName(Component.text("§c§l仅保留主频道").decoration(TextDecoration.ITALIC, false));
            meta.lore(java.util.Collections.singletonList(
                Component.text("§7关闭除主频道外的所有频道接收").decoration(TextDecoration.ITALIC, false)));
        });
        gui.setItem(bottom + 7, allOff);

        // 管理员：私聊监控
        if (admin) {
            boolean spyOn = Boolean.TRUE.equals(ChatConstants.PLAYER_SOCIAL_SPY.get(player.getUniqueId()));
            ItemStack spy = new ItemStack(spyOn ? Material.SPYGLASS : Material.ENDER_EYE);
            spy.editMeta(meta -> {
                meta.displayName(Component.text((spyOn ? "§a§l" : "§7§l") + "私聊监控")
                    .decoration(TextDecoration.ITALIC, false));
                List<Component> spyLore = new ArrayList<>();
                spyLore.add(Component.text(spyOn ? "§a✔ 已开启" : "§c✘ 已关闭").decoration(TextDecoration.ITALIC, false));
                spyLore.add(Component.text("§7开启后可查看所有玩家间的私信").decoration(TextDecoration.ITALIC, false));
                spyLore.add(Component.text("§e点击切换").decoration(TextDecoration.ITALIC, false));
                meta.lore(spyLore);
            });
            gui.setItem(bottom + 4, spy);
        }

        player.openInventory(gui);
    }

    /**
     * 处理左键点击：切换发言频道 / 功能按钮
     */
    public static void handleLeftClick(Player player, ItemStack clicked, Holder holder) {
        if (clicked == null || clicked.getType() == Material.GRAY_STAINED_GLASS_PANE) return;

        Material mat = clicked.getType();

        // 一键全部开启
        if (mat == Material.EMERALD_BLOCK) {
            Set<String> sysBlocked = ChatConstants.PLAYER_CHANNEL_SYSTEM_BLOCKED.get(player.getUniqueId());
            if (sysBlocked != null && !sysBlocked.isEmpty()) {
                // 保留系统级屏蔽
                Set<String> newBlocked = ConcurrentHashMap.newKeySet();
                newBlocked.addAll(sysBlocked);
                ChatConstants.PLAYER_CHANNEL_BLOCKED.put(player.getUniqueId(), newBlocked);
            } else {
                ChatConstants.PLAYER_CHANNEL_BLOCKED.remove(player.getUniqueId());
            }
            player.sendMessage("§a已开启所有频道的消息接收");
            refresh(player, holder);
            return;
        }

        // 仅保留主频道
        if (mat == Material.REDSTONE_BLOCK) {
            Set<String> blocked = ChatConstants.PLAYER_CHANNEL_BLOCKED.computeIfAbsent(
                player.getUniqueId(), k -> ConcurrentHashMap.newKeySet());
            blocked.clear();
            ConfigurationSection section = ConfigUtil.CHAT_CONFIG.getConfigurationSection("chat");
            if (section != null) {
                for (String ch : section.getKeys(false)) {
                    if (!ch.equals(ChatConstants.DEFAULT) && !ch.equals(ChatConstants.TELL)) {
                        // 只屏蔽玩家可见的频道（插件频道用注册制，非插件频道用权限）
                        boolean isPluginCh = ChatConstants.PLUGIN_CHANNEL.containsKey(ch);
                        if (isPluginCh) {
                            List<String> pc = ChatConstants.PLAYER_PLUGIN_CHANNEL
                                .getOrDefault(player.getUniqueId(), java.util.Collections.emptyList());
                            if (!pc.contains(ch)) continue;
                        } else {
                            if (!player.hasPermission(ChatConstants.PLAYER_CHAT_USE + ch)) continue;
                        }
                        blocked.add(ch);
                    }
                }
            }
            // 保留系统级屏蔽
            Set<String> sysBlocked = ChatConstants.PLAYER_CHANNEL_SYSTEM_BLOCKED.get(player.getUniqueId());
            if (sysBlocked != null) {
                blocked.addAll(sysBlocked);
            }
            player.sendMessage("§c已关闭除主频道外的所有频道接收");
            refresh(player, holder);
            return;
        }

        // 私聊监控
        if (mat == Material.SPYGLASS || mat == Material.ENDER_EYE) {
            boolean current = Boolean.TRUE.equals(ChatConstants.PLAYER_SOCIAL_SPY.get(player.getUniqueId()));
            ChatConstants.PLAYER_SOCIAL_SPY.put(player.getUniqueId(), !current);
            player.sendMessage(!current ? "§a私聊监控已开启" : "§c私聊监控已关闭");
            refresh(player, holder);
            return;
        }

        // 频道项 → 切换发言频道
        if (mat.name().endsWith("_WOOL") || mat == Material.BARRIER) {
            String channelId = resolveChannelFromItem(clicked);
            if (channelId == null) return;
            // 系统级屏蔽检查
            Set<String> sysBlocked = ChatConstants.PLAYER_CHANNEL_SYSTEM_BLOCKED.get(player.getUniqueId());
            if (sysBlocked != null && sysBlocked.contains(channelId)) {
                player.sendMessage("§c该频道当前已被系统禁用，无法切换");
                return;
            }
            // 插件频道用成员注册制替代权限检查
            if (!ChatConstants.PLUGIN_CHANNEL.containsKey(channelId)) {
                if (!player.hasPermission(ChatConstants.PLAYER_CHAT_USE + channelId)) {
                    player.sendMessage("§c你没有切换到该频道的权限");
                    return;
                }
            }
            ChatPlayerChannelService.getInstance().setChannel(player.getUniqueId(), channelId);
            player.sendMessage("§a已切换到频道 " + translateColor(ChannelUtil.getChannelName(channelId)));
            refresh(player, holder);
        }
    }

    /**
     * 处理右键点击：切换频道接收开关
     */
    public static void handleRightClick(Player player, ItemStack clicked, Holder holder) {
        if (clicked == null || clicked.getType() == Material.GRAY_STAINED_GLASS_PANE) return;
        if (!clicked.getType().name().endsWith("_WOOL") && clicked.getType() != Material.BARRIER) return;

        String channelId = resolveChannelFromItem(clicked);
        if (channelId == null) return;

        // 系统级屏蔽检查：不允许操作被系统禁用的频道
        Set<String> sysBlocked = ChatConstants.PLAYER_CHANNEL_SYSTEM_BLOCKED.get(player.getUniqueId());
        if (sysBlocked != null && sysBlocked.contains(channelId)) {
            player.sendMessage("§c该频道当前已被系统禁用，无法操作");
            return;
        }

        Set<String> blocked = ChatConstants.PLAYER_CHANNEL_BLOCKED.computeIfAbsent(
            player.getUniqueId(), k -> ConcurrentHashMap.newKeySet());

        String channelName = translateColor(ChannelUtil.getChannelName(channelId));
        if (blocked.contains(channelId)) {
            blocked.remove(channelId);
            player.sendMessage("§a已开启 §f" + channelName + " §a的消息接收");
        } else {
            blocked.add(channelId);
            player.sendMessage("§c已关闭 §f" + channelName + " §c的消息接收");
        }
        refresh(player, holder);
    }

    private static void refresh(Player player, Holder holder) {
        Bukkit.getScheduler().runTask(PlayerChat.INSTANCE, () -> {
            if (player.isOnline() && player.getOpenInventory().getTopInventory().getHolder() instanceof Holder) {
                openInternal(player, holder.isAdmin());
            }
        });
    }

    private static void fillBorder(Inventory gui) {
        ItemStack border = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        border.editMeta(m -> m.displayName(Component.text(" ")));
        for (int i = 0; i < gui.getSize(); i++) gui.setItem(i, border);
    }

    private static String stripColor(String text) {
        // 先将 & 颜色代码转换为 § 再统一去除
        return org.bukkit.ChatColor.stripColor(
            org.bukkit.ChatColor.translateAlternateColorCodes('&', text));
    }

    /** 将频道名中的 & 颜色代码转换为 §，用于 player.sendMessage() */
    private static String translateColor(String text) {
        return org.bukkit.ChatColor.translateAlternateColorCodes('&', text);
    }

    /**
     * 通过点击物品的显示名反查频道 ID
     */
    private static String resolveChannelFromItem(ItemStack item) {
        if (item.getItemMeta() == null || !item.getItemMeta().hasDisplayName()) return null;
        // 序列化为纯文本
        String display = LegacyComponentSerializer.legacyAmpersand().serialize(item.getItemMeta().displayName());
        display = stripColor(display).trim();

        ConfigurationSection section = ConfigUtil.CHAT_CONFIG.getConfigurationSection("chat");
        if (section == null) return null;

        for (String key : section.getKeys(false)) {
            String name = ConfigUtil.CHAT_CONFIG.getString("chat." + key + ".name", key);
            if (stripColor(name).equalsIgnoreCase(display) || key.equalsIgnoreCase(display)) {
                return key;
            }
        }
        return null;
    }

}
