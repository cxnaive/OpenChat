package cn.handyplus.lib.internal;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class InventoryViewUtil {
   public static InventoryHolder getInventoryHolder(@NotNull Player player) {
      return CompatCore.IS_1_21 ? InventoryViewHighUtil.getInventoryHolder(player) : InventoryViewLowUtil.getInventoryHolder(player);
   }

   public static InventoryType getInventoryType(@NotNull Player player) {
      return CompatCore.IS_1_21 ? InventoryViewHighUtil.getInventoryType(player) : InventoryViewLowUtil.getInventoryType(player);
   }
}
