package cn.handyplus.lib.inventory;

import cn.handyplus.lib.core.LockUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class HandyInventoryListener implements Listener {
   @EventHandler
   public void onInventoryClick(InventoryClickEvent event) {
      InventoryCheckParam inventoryCheckParam = HandyClickFactory.getInstance().inventoryCheck(event);
      if (inventoryCheckParam.isCheck()) {
         HandyInventory handyInventory = inventoryCheckParam.getHandyInventory();
         handyInventory.playSound(event.getRawSlot());
         event.setCancelled(handyInventory.isToCancel());
         HandyClickFactory.getInstance().rawSlotClick(handyInventory, event);
      }
   }

   @EventHandler(priority = EventPriority.MONITOR)
   public void onKick(PlayerKickEvent event) {
      this.removeCache(event.getPlayer());
   }

   @EventHandler(priority = EventPriority.MONITOR)
   public void onQuit(PlayerQuitEvent event) {
      this.removeCache(event.getPlayer());
   }

   private void removeCache(Player player) {
      LockUtil.unTimeLock(player.getUniqueId());
   }
}
