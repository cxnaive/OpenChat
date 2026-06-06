package cn.handyplus.chat.listener;

import cn.handyplus.chat.gui.ChannelAdminGui;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;

/**
 * 频道管理 GUI 点击监听
 *
 * @since 3.4.0
 */
public class ChannelGuiListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if (!(holder instanceof ChannelAdminGui.Holder)) return;
        ChannelAdminGui.Holder guiHolder = (ChannelAdminGui.Holder) holder;

        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        if (event.getCurrentItem() == null) return;

        boolean rightClick = event.isRightClick();
        if (rightClick) {
            ChannelAdminGui.handleRightClick(player, event.getCurrentItem(), guiHolder);
        } else {
            ChannelAdminGui.handleLeftClick(player, event.getCurrentItem(), guiHolder);
        }
    }

}
