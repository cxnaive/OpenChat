package cn.handyplus.lib.inventory;

import org.bukkit.event.inventory.InventoryClickEvent;

public interface IHandyClickEvent {
   String guiType();

   void rawSlotClick(HandyInventory var1, InventoryClickEvent var2);

   default boolean isAsync() {
      return false;
   }
}
