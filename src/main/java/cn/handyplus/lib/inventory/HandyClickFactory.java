package cn.handyplus.lib.inventory;

import cn.handyplus.lib.InitApi;
import cn.handyplus.lib.core.CollUtil;
import cn.handyplus.lib.core.LockUtil;
import cn.handyplus.lib.internal.HandySchedulerUtil;
import cn.handyplus.lib.util.MessageUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class HandyClickFactory {
   private static final HandyClickFactory INSTANCE = new HandyClickFactory();
   private static final Map<String, IHandyClickEvent> HANDY_CLICK_EVENT_MAP = new HashMap<>();

   private HandyClickFactory() {
   }

   protected static HandyClickFactory getInstance() {
      return INSTANCE;
   }

   protected void init(List<IHandyClickEvent> handyClickEvents) {
      if (!CollUtil.isEmpty(handyClickEvents)) {
         for (IHandyClickEvent handyClickEvent : handyClickEvents) {
            HANDY_CLICK_EVENT_MAP.put(handyClickEvent.guiType(), handyClickEvent);
         }

         InitApi.PLUGIN.getServer().getPluginManager().registerEvents(new HandyInventoryListener(), InitApi.PLUGIN);
      }
   }

   protected InventoryCheckParam inventoryCheck(InventoryClickEvent event) {
      InventoryCheckParam inventoryCheckParam = new InventoryCheckParam();
      inventoryCheckParam.setCheck(false);
      InventoryHolder holder = event.getInventory().getHolder();
      if (!(holder instanceof HandyInventory)) {
         return inventoryCheckParam;
      } else {
         HandyInventory handyInventory = (HandyInventory)holder;
         Optional<Player> optionalPlayer = HandyInventoryUtil.getPlayer(event);
         if (!optionalPlayer.isPresent()) {
            return inventoryCheckParam;
         } else if (!event.getClick().isShiftClick() && !event.getClick().isKeyboardClick() && !ClickType.DOUBLE_CLICK.equals(event.getClick())) {
            ItemStack currentItem = event.getCurrentItem();
            if (currentItem == null || Material.AIR.equals(currentItem.getType())) {
               event.setCancelled(handyInventory.isToCancel());
               return inventoryCheckParam;
            } else if (event.isCancelled()) {
               return inventoryCheckParam;
            } else {
               handyInventory.setPlayer(optionalPlayer.get());
               String lockKey = handyInventory.getGuiType() + "_" + handyInventory.getPlayer().getUniqueId();
               handyInventory.setLockKey(lockKey);
               inventoryCheckParam.setCheck(true);
               inventoryCheckParam.setHandyInventory(handyInventory);
               return inventoryCheckParam;
            }
         } else {
            event.setCancelled(true);
            return inventoryCheckParam;
         }
      }
   }

   protected void rawSlotClick(HandyInventory handyInventory, InventoryClickEvent event) {
      IHandyClickEvent handyClickEvent = HANDY_CLICK_EVENT_MAP.get(handyInventory.getGuiType());
      if (handyClickEvent != null) {
         if (handyClickEvent.isAsync()) {
            this.onAsyncRawSlotClick(handyClickEvent, handyInventory, event);
         } else {
            this.rawSlotClickExecution(handyClickEvent, handyInventory, event);
         }
      }
   }

   private void onAsyncRawSlotClick(IHandyClickEvent handyClickEvent, HandyInventory handyInventory, InventoryClickEvent event) {
      HandySchedulerUtil.runTaskAsynchronously(() -> this.rawSlotClickExecution(handyClickEvent, handyInventory, event));
   }

   private void rawSlotClickExecution(IHandyClickEvent handyClickEvent, HandyInventory handyInventory, InventoryClickEvent event) {
      if (LockUtil.tryPass(handyInventory.getLockKey())) {
         try {
            handyClickEvent.rawSlotClick(handyInventory, event);
         } catch (RuntimeException var9) {
            MessageUtil.sendMessage(handyInventory.getPlayer(), var9.getMessage());
         } catch (Exception var10) {
            InitApi.PLUGIN.getLogger().log(Level.SEVERE, "click exception", (Throwable)var10);
         } finally {
            LockUtil.done(handyInventory.getLockKey());
         }
      }
   }
}
