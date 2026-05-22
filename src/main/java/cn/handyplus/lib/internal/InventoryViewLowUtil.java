package cn.handyplus.lib.internal;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

class InventoryViewLowUtil {
   protected static InventoryHolder getInventoryHolder(@NotNull Player player) {
      return player.getOpenInventory().getTopInventory().getHolder();
   }

   protected static InventoryType getInventoryType(@NotNull Player player) {
      return player.getOpenInventory().getTopInventory().getType();
   }
}
