package cn.handyplus.lib.inventory;

import cn.handyplus.lib.core.StrUtil;
import cn.handyplus.lib.internal.EntitySchedulerUtil;
import cn.handyplus.lib.internal.InventoryViewUtil;
import cn.handyplus.lib.internal.PlayerSchedulerUtil;
import cn.handyplus.lib.util.ItemStackUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public final class HandyInventoryUtil {
   private HandyInventoryUtil() {
   }

   public static void refreshInventory(Inventory inventory) {
      for (int i = 0; i < inventory.getSize(); i++) {
         inventory.setItem(i, new ItemStack(Material.AIR));
      }
   }

   public static int getIndex(FileConfiguration config, String type) {
      return config.getInt(type + ".index", 0);
   }

   public static boolean isIndex(int rawSlot, FileConfiguration config, String type) {
      String indexStrList = config.getString(type + ".index");
      List<Integer> indexList = StrUtil.strToIntList(indexStrList);
      boolean enable = config.getBoolean(type + ".enable", true);
      return enable && indexList.contains(rawSlot);
   }

   public static void setButton(FileConfiguration config, HandyInventory handyInventory, String type) {
      setButton(config, handyInventory, type, null);
   }

   public static void setButton(FileConfiguration config, HandyInventory handyInventory, String type, Map<String, String> map) {
      setButton(config, handyInventory, type, map, null);
   }

   public static void setButton(FileConfiguration config, HandyInventory handyInventory, String type, Map<String, String> map, boolean enchant) {
      setButton(config, handyInventory, type, map, null, enchant);
   }

   public static void setButton(
      FileConfiguration config, HandyInventory handyInventory, String type, Map<String, String> map, Map<String, List<String>> batchMap
   ) {
      setButton(config, handyInventory, type, map, batchMap, false);
   }

   public static void setButton(
      FileConfiguration config, HandyInventory handyInventory, String type, Map<String, String> map, Map<String, List<String>> batchMap, boolean enchant
   ) {
      if (config.getBoolean(type + ".enable", true)) {
         Inventory inventory = handyInventory.getInventory();
         Player player = handyInventory.getPlayer();
         String indexStrList = config.getString(type + ".index");
         List<Integer> indexList = StrUtil.strToIntList(indexStrList);
         String name = config.getString(type + ".name");
         int amount = config.getInt(type + ".amount", 1);
         String material = config.getString(type + ".material");
         List<String> loreList = config.getStringList(type + ".lore");
         int customModelDataId = config.getInt(type + ".custom-model-data");
         boolean enchantFlag = enchant || config.getBoolean(type + ".isEnchant");
         boolean hideFlag = config.getBoolean(type + ".hideFlag", true);
         boolean hideEnchant = config.getBoolean("hideEnchant", true);
         String sound = config.getString(type + ".sound");
         String headBase = config.getString(type + ".headBase");
         String head = config.getString(type + ".head");
         String tooltipStyle = config.getString(type + ".tooltipStyle");
         String itemModel = config.getString(type + ".itemModel");
         if (StrUtil.isNotEmpty(head) && player != null) {
            head = StrUtil.replace(head, "player", player.getName());
         }

         for (Integer index : indexList) {
            loreList = ItemStackUtil.loreBatchReplaceMap(loreList, batchMap, null);
            ItemStack itemStack = ItemStackUtil.getItemStack(
               material, name, loreList, enchantFlag, customModelDataId, hideFlag, map, hideEnchant, null, tooltipStyle, itemModel
            );
            itemStack.setAmount(amount);
            ItemStackUtil.setSkull(itemStack, headBase);
            ItemStackUtil.setOwner(itemStack, head);
            inventory.setItem(index, itemStack);
            if (StrUtil.isNotEmpty(sound)) {
               handyInventory.getSoundMap().put(index, sound);
            }
         }
      }
   }

   public static Optional<Player> getPlayer(InventoryClickEvent event) {
      HumanEntity humanEntity = event.getWhoClicked();
      return humanEntity instanceof Player ? Optional.of((Player)humanEntity) : Optional.empty();
   }

   public static void setCustomButton(FileConfiguration fileConfig, HandyInventory handyInventory, String parent) {
      Inventory inventory = handyInventory.getInventory();
      Map<Integer, String> strMap = handyInventory.getStrMap();
      Map<Integer, String> soundMap = handyInventory.getSoundMap();
      Player player = handyInventory.getPlayer();
      ConfigurationSection configurationSection = fileConfig.getConfigurationSection(parent);
      if (configurationSection != null) {
         Map<String, Object> values = configurationSection.getValues(false);

         for (String key : values.keySet()) {
            MemorySection memorySection = (MemorySection)values.get(key);
            if (memorySection != null) {
               boolean enable = memorySection.getBoolean("enable", true);
               if (enable) {
                  String command = memorySection.getString("command");
                  List<Integer> indexList = StrUtil.strToIntList(memorySection.getString("index"));
                  String material = memorySection.getString("material");
                  String name = memorySection.getString("name");
                  int amount = memorySection.getInt("amount", 1);
                  List<String> loreList = memorySection.getStringList("lore");
                  int customModelDataId = memorySection.getInt("custom-model-data");
                  boolean isEnchant = memorySection.getBoolean("isEnchant", false);
                  boolean hideFlag = memorySection.getBoolean("hideFlag", true);
                  boolean hideEnchant = memorySection.getBoolean("hideEnchant", true);
                  String sound = memorySection.getString("sound");
                  String headBase = memorySection.getString("headBase");
                  String head = memorySection.getString("head");
                  String tooltipStyle = memorySection.getString("tooltipStyle");
                  String itemModel = memorySection.getString("itemModel");
                  if (StrUtil.isNotEmpty(head) && player != null) {
                     head = StrUtil.replace(head, "player", player.getName());
                  }

                  for (Integer index : indexList) {
                     ItemStack itemStack = ItemStackUtil.getItemStack(
                        material, name, loreList, isEnchant, customModelDataId, hideFlag, null, hideEnchant, null, tooltipStyle, itemModel
                     );
                     itemStack.setAmount(amount);
                     ItemStackUtil.setSkull(itemStack, headBase);
                     ItemStackUtil.setOwner(itemStack, head);
                     inventory.setItem(index, itemStack);
                     if (StrUtil.isNotEmpty(sound)) {
                        soundMap.put(index, sound);
                     }

                     if (StrUtil.isNotEmpty(command)) {
                        strMap.put(index, command);
                     }
                  }
               }
            }
         }
      }
   }

   public static Map<Integer, String> getCustomButton(FileConfiguration fileConfig, String parent) {
      Map<Integer, String> map = new HashMap<>();
      ConfigurationSection configurationSection = fileConfig.getConfigurationSection(parent);
      if (configurationSection == null) {
         return map;
      } else {
         Map<String, Object> values = configurationSection.getValues(false);

         for (String key : values.keySet()) {
            MemorySection memorySection = (MemorySection)values.get(key);
            if (memorySection != null) {
               boolean enable = memorySection.getBoolean("enable", true);
               if (enable) {
                  List<Integer> indexList = StrUtil.strToIntList(memorySection.getString("index"));
                  String command = memorySection.getString("command");
                  if (!StrUtil.isEmpty(command)) {
                     for (Integer index : indexList) {
                        map.put(index, command);
                     }
                  }
               }
            }
         }

         return map;
      }
   }

   public static Map<String, String> replacePageMap(HandyInventory handyInventory) {
      Map<String, String> map = new HashMap<>(4);
      Integer pageNum = handyInventory.getPageNum();
      Integer pageCount = handyInventory.getPageCount();
      map.put("count", pageCount == 0 ? "1" : String.valueOf(pageCount));
      map.put("pageNum", String.valueOf(pageNum));
      map.put("nextPage", String.valueOf(pageNum + 1));
      map.put("previousPage", pageNum - 1 < 1 ? "1" : String.valueOf(pageNum - 1));
      return map;
   }

   public static void closeHandyInventory() {
      for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
         getInventoryHolder(onlinePlayer).thenAccept(holder -> {
            if (holder instanceof HandyInventory) {
               PlayerSchedulerUtil.syncCloseInventory(onlinePlayer);
            }
         });
      }
   }

   public static CompletableFuture<InventoryHolder> getInventoryHolder(Player player) {
      CompletableFuture<InventoryHolder> future = new CompletableFuture<>();
      EntitySchedulerUtil.runSafeOnPlayerScheduler(player, () -> InventoryViewUtil.getInventoryHolder(player), future::complete, false);
      return future;
   }
}
