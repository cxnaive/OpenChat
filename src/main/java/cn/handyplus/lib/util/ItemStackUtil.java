package cn.handyplus.lib.util;

import cn.handyplus.lib.constants.BaseConstants;
import cn.handyplus.lib.constants.VersionCheckEnum;
import cn.handyplus.lib.core.CollUtil;
import cn.handyplus.lib.core.MapUtil;
import cn.handyplus.lib.core.NumberUtil;
import cn.handyplus.lib.core.StrUtil;
import cn.handyplus.lib.internal.PlayerSchedulerUtil;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.MaterialData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ItemStackUtil {
   private ItemStackUtil() {
   }

   public static String itemStackSerialize(ItemStack itemStack) {
      YamlConfiguration yml = new YamlConfiguration();
      yml.set("item", itemStack);
      return yml.saveToString();
   }

   public static ItemStack itemStackDeserialize(String str) {
      return itemStackDeserialize(str, Material.AIR);
   }

   public static ItemStack itemStackDeserialize(String str, Material material) {
      YamlConfiguration yml = new YamlConfiguration();

      ItemStack item;
      try {
         yml.loadFromString(str);
         item = yml.getItemStack("item", new ItemStack(material));
      } catch (Exception var5) {
         item = new ItemStack(material);
      }

      return item;
   }

   public static ItemStack getItemStack(String material) {
      return getItemStack(material, null, null);
   }

   public static ItemStack getItemStack(String material, String displayName) {
      return getItemStack(material, displayName, null);
   }

   public static ItemStack getItemStack(String material, String displayName, List<String> loreList) {
      return getItemStack(material, displayName, loreList, false);
   }

   public static ItemStack getItemStack(String material, String displayName, List<String> loreList, Boolean isEnchant) {
      return getItemStack(material, displayName, loreList, isEnchant, 0);
   }

   public static ItemStack getItemStack(String material, String displayName, List<String> loreList, Boolean isEnchant, int customModelData) {
      return getItemStack(material, displayName, loreList, isEnchant, customModelData, false);
   }

   public static ItemStack getItemStack(String material, String displayName, List<String> loreList, Boolean isEnchant, int customModelData, boolean hideFlag) {
      return getItemStack(material, displayName, loreList, isEnchant, customModelData, hideFlag, null);
   }

   public static ItemStack getItemStack(
      String material, String displayName, List<String> loreList, Boolean isEnchant, int customModelData, boolean hideFlag, Map<String, String> replaceMap
   ) {
      return getItemStack(material, displayName, loreList, isEnchant, customModelData, hideFlag, replaceMap, false);
   }

   public static ItemStack getItemStack(
      String material,
      String displayName,
      List<String> loreList,
      boolean isEnchant,
      int customModelData,
      boolean hideFlag,
      Map<String, String> replaceMap,
      boolean hideEnchant
   ) {
      return getItemStack(material, displayName, loreList, isEnchant, customModelData, hideFlag, replaceMap, hideEnchant, null);
   }

   public static ItemStack getItemStack(
      String material,
      String displayName,
      List<String> loreList,
      boolean isEnchant,
      int customModelData,
      boolean hideFlag,
      Map<String, String> replaceMap,
      boolean hideEnchant,
      String customData
   ) {
      return getItemStack(material, displayName, loreList, isEnchant, customModelData, hideFlag, replaceMap, hideEnchant, customData, null);
   }

   public static ItemStack getItemStack(
      String material,
      String displayName,
      List<String> loreList,
      boolean isEnchant,
      int customModelData,
      boolean hideFlag,
      Map<String, String> replaceMap,
      boolean hideEnchant,
      String customData,
      String tooltipStyle
   ) {
      return getItemStack(material, displayName, loreList, isEnchant, customModelData, hideFlag, replaceMap, hideEnchant, customData, tooltipStyle, null);
   }

   public static ItemStack getItemStack(
      String material,
      String displayName,
      List<String> loreList,
      boolean isEnchant,
      int customModelData,
      boolean hideFlag,
      Map<String, String> replaceMap,
      boolean hideEnchant,
      String customData,
      String tooltipStyle,
      String itemModel
   ) {
      ItemStack itemStack = getItemByMaterial(material);
      ItemMeta itemMeta = getItemMeta(itemStack);
      ItemMetaUtil.setDisplayName(itemMeta, displayName);
      ItemMetaUtil.setLore(itemMeta, loreReplaceMap(loreList, replaceMap));
      if (isEnchant) {
         ItemMetaUtil.setEnchant(itemMeta);
      }

      if (hideEnchant) {
         ItemMetaUtil.hideEnchant(itemMeta);
      }

      if (hideFlag) {
         ItemMetaUtil.hideAttributes(itemMeta);
      }

      ItemMetaUtil.setPersistentData(itemMeta, customData, "system");
      ItemMetaUtil.setCustomModelData(itemMeta, customModelData);
      ItemMetaUtil.setTooltipStyle(itemMeta, tooltipStyle);
      ItemMetaUtil.setItemModel(itemMeta, itemModel);
      itemStack.setItemMeta(itemMeta);
      return itemStack;
   }

   public static Boolean removeItem(Player player, ItemStack itemStack, Integer amount) {
      return removeItem(player, itemStack, amount, true);
   }

   public static Boolean removeItem(Player player, ItemStack itemStack, Integer amount, boolean strict) {
      if (player != null && player.isOnline()) {
         if (amount < 1) {
            throw new RuntimeException("数量不能为负数: " + amount);
         } else {
            PlayerInventory playerInventory = player.getInventory();
            ItemStack[] contents;
            if (BaseConstants.VERSION_ID <= VersionCheckEnum.V_1_8_8.getVersionId()) {
               contents = playerInventory.getContents();
            } else {
               contents = playerInventory.getStorageContents();
            }

            int num = 0;
            List<ItemStack> items = new ArrayList<>();

            for (ItemStack item : contents) {
               if (item != null && !Material.AIR.equals(item.getType()) && (strict ? item.isSimilar(itemStack) : isSimilar(item, itemStack))) {
                  num += item.getAmount();
                  items.add(item);
                  if (num >= amount) {
                     break;
                  }
               }
            }

            if (num != amount) {
               if (num > amount) {
                  for (ItemStack itemx : items) {
                     if (amount == 0) {
                        return true;
                     }

                     if (amount >= itemx.getAmount()) {
                        amount = amount - itemx.getAmount();
                        playerInventory.removeItem(new ItemStack[]{itemx});
                     } else {
                        itemx.setAmount(itemx.getAmount() - amount);
                        amount = 0;
                     }
                  }

                  return true;
               } else {
                  return false;
               }
            } else {
               for (ItemStack itemx : items) {
                  playerInventory.removeItem(new ItemStack[]{itemx});
               }

               return true;
            }
         }
      } else {
         return false;
      }
   }

   public static int getItemAmount(Player player, ItemStack itemStack) {
      return getItemAmount(player, itemStack, true);
   }

   public static int getItemAmount(Player player, ItemStack itemStack, boolean strict) {
      if (player != null && player.isOnline()) {
         PlayerInventory playerInventory = player.getInventory();
         ItemStack[] contents;
         if (BaseConstants.VERSION_ID <= VersionCheckEnum.V_1_8_8.getVersionId()) {
            contents = playerInventory.getContents();
         } else {
            contents = playerInventory.getStorageContents();
         }

         int num = 0;

         for (ItemStack item : contents) {
            if (item != null && !Material.AIR.equals(item.getType()) && (strict ? item.isSimilar(itemStack) : isSimilar(item, itemStack))) {
               num += item.getAmount();
            }
         }

         return num;
      } else {
         return 0;
      }
   }

   public static Boolean containsItem(PlayerInventory playerInventory, ItemStack itemStack, Integer amount, boolean strict) {
      ItemStack[] contents;
      if (BaseConstants.VERSION_ID <= VersionCheckEnum.V_1_8_8.getVersionId()) {
         contents = playerInventory.getContents();
      } else {
         contents = playerInventory.getStorageContents();
      }

      int num = 0;

      for (ItemStack item : contents) {
         if (item != null && !Material.AIR.equals(item.getType()) && (strict ? item.isSimilar(itemStack) : isSimilar(item, itemStack))) {
            num += item.getAmount();
            if (num >= amount) {
               break;
            }
         }
      }

      return num >= amount;
   }

   public static boolean addItem(Player player, ItemStack itemStack) {
      return addItem(player, itemStack, BaseUtil.getLangMsg("addItemMsg"));
   }

   public static boolean addItem(Player player, String itemStackStr) {
      return addItem(player, itemStackDeserialize(itemStackStr), BaseUtil.getLangMsg("addItemMsg"));
   }

   public static boolean addItem(Player player, ItemStack itemStack, String msg) {
      HashMap<Integer, ItemStack> dropItemMap = player.getInventory().addItem(new ItemStack[]{itemStack});
      if (dropItemMap.isEmpty()) {
         return false;
      } else {
         PlayerSchedulerUtil.dropItem(player, new ArrayList<>(dropItemMap.values()));
         MessageUtil.sendMessage(player, msg);
         return true;
      }
   }

   public static boolean addItem(Player player, ItemStack itemStack, int amount) {
      return addItem(player, itemStack, amount, BaseUtil.getLangMsg("addItemMsg"));
   }

   public static boolean addItem(Player player, String itemStackStr, int amount) {
      return addItem(player, itemStackDeserialize(itemStackStr), amount, BaseUtil.getLangMsg("addItemMsg"));
   }

   public static boolean addItem(Player player, ItemStack itemStack, int amount, String msg) {
      if (Material.AIR.equals(itemStack.getType())) {
         return false;
      } else {
         List<ItemStack> dropItemList = addItemReturnDropItemMap(player, itemStack, amount);
         if (CollUtil.isEmpty(dropItemList)) {
            return false;
         } else {
            dropItemBatch(player, dropItemList);
            MessageUtil.sendMessage(player, msg);
            return true;
         }
      }
   }

   public static Material getMaterial(String materialStr) {
      return getMaterial(materialStr, Material.STONE);
   }

   public static Material getMaterial(String materialStr, @NotNull Material defaultMaterial) {
      if (StrUtil.isEmpty(materialStr)) {
         return defaultMaterial;
      } else {
         Material material = findMaterial(materialStr);
         if (material != null) {
            return material;
         } else {
            return BaseConstants.VERSION_ID < VersionCheckEnum.V_1_8.getVersionId() ? defaultMaterial : XSeriesUtil.getMaterial(materialStr, defaultMaterial);
         }
      }
   }

   public static ItemStack getItemByMaterial(String materialStr) {
      return getItemByMaterial(materialStr, Material.STONE);
   }

   public static ItemStack getItemByMaterial(String materialStr, @NotNull Material defaultMaterial) {
      if (StrUtil.isEmpty(materialStr)) {
         return new ItemStack(defaultMaterial);
      } else {
         Material material = findMaterial(materialStr);
         if (material != null) {
            return new ItemStack(material);
         } else {
            return BaseConstants.VERSION_ID < VersionCheckEnum.V_1_8.getVersionId()
               ? getLegacyItemStack(materialStr, defaultMaterial)
               : XSeriesUtil.getMaterialItem(materialStr, defaultMaterial);
         }
      }
   }

   public static ItemStack getItemInMainHand(PlayerInventory playerInventory) {
      return BaseConstants.VERSION_ID < VersionCheckEnum.V_1_9.getVersionId() ? playerInventory.getItemInHand() : playerInventory.getItemInMainHand();
   }

   public static void setItemInMainHand(PlayerInventory playerInventory, ItemStack itemStack) {
      if (BaseConstants.VERSION_ID < VersionCheckEnum.V_1_9.getVersionId()) {
         playerInventory.setItemInHand(itemStack);
      } else {
         playerInventory.setItemInMainHand(itemStack);
      }
   }

   @NotNull
   public static ItemMeta getItemMeta(@Nullable ItemStack itemStack) {
      if (itemStack == null) {
         itemStack = new ItemStack(Material.STONE);
      }

      ItemMeta itemMeta = itemStack.getItemMeta();
      return itemMeta == null ? Objects.requireNonNull(new ItemStack(Material.STONE).getItemMeta()) : itemMeta;
   }

   public static List<String> loreReplaceMap(List<String> loreList, Map<String, String> replaceMap) {
      List<String> newLoreList = new ArrayList<>();
      if (CollUtil.isEmpty(loreList)) {
         return newLoreList;
      } else {
         if (MapUtil.isNotEmpty(replaceMap)) {
            for (String lore : loreList) {
               for (String key : replaceMap.keySet()) {
                  if (StrUtil.isNotEmpty(lore) && lore.contains("${" + key + "}") && replaceMap.get(key) != null) {
                     lore = StrUtil.replace(lore, key, replaceMap.get(key));
                  }
               }

               newLoreList.add(lore);
            }
         } else {
            newLoreList.addAll(loreList);
         }

         return newLoreList;
      }
   }

   public static List<String> loreBatchReplaceMap(List<String> loreList, Map<String, List<String>> replaceMap, String def) {
      if (!CollUtil.isEmpty(loreList) && replaceMap != null && !replaceMap.isEmpty()) {
         List<String> newLoreList = new ArrayList<>();

         for (String lore : loreList) {
            newLoreList.addAll(loreBatchReplaceMap(lore, replaceMap, def));
         }

         return newLoreList;
      } else {
         return loreList;
      }
   }

   public static List<String> loreBatchReplaceMap(String lore, Map<String, List<String>> replaceMap, String def) {
      List<String> loreList = new ArrayList<>();
      if (StrUtil.isEmpty(lore)) {
         loreList.add(lore);
         return loreList;
      } else if (replaceMap != null && !replaceMap.isEmpty()) {
         if (StrUtil.isEmpty(def)) {
            def = "";
         }

         for (String key : replaceMap.keySet()) {
            if (lore.contains("${" + key + "}")) {
               List<String> replaceList = replaceMap.get(key);
               if (CollUtil.isEmpty(replaceList)) {
                  loreList.add(StrUtil.replace(lore, key, def));
               } else {
                  for (String replaceStr : replaceList) {
                     loreList.add(StrUtil.replace(lore, key, replaceStr));
                  }
               }
               break;
            }
         }

         if (CollUtil.isEmpty(loreList)) {
            loreList.add(lore);
         }

         return loreList;
      } else {
         loreList.add(lore);
         return loreList;
      }
   }

   public static void setPersistentData(ItemStack itemStack, String customData, String key) {
      ItemMeta itemMeta = itemStack.getItemMeta();
      ItemMetaUtil.setPersistentData(itemMeta, customData, key);
      itemStack.setItemMeta(itemMeta);
   }

   public static Optional<String> getPersistentData(ItemStack itemStack, String key) {
      ItemMeta itemMeta = itemStack.getItemMeta();
      return ItemMetaUtil.getPersistentData(itemMeta, key);
   }

   public static boolean isSimilar(ItemStack one, ItemStack two) {
      if (one == two) {
         return true;
      } else if (one != null && two != null) {
         if (!one.getType().equals(two.getType())) {
            return false;
         } else {
            if (BaseConstants.VERSION_ID < VersionCheckEnum.V_1_13.getVersionId()) {
               MaterialData oneData = one.getData();
               MaterialData twoData = two.getData();
               if (oneData != null && twoData != null && oneData.getData() != twoData.getData()) {
                  return false;
               }
            }

            ItemMeta oneItemMeta = one.getItemMeta();
            ItemMeta twoItemMeta = two.getItemMeta();
            if (oneItemMeta == twoItemMeta) {
               return true;
            } else if (oneItemMeta != null && twoItemMeta != null) {
               return !StrUtil.equals(oneItemMeta.getDisplayName(), twoItemMeta.getDisplayName())
                  ? false
                  : CollUtil.equals(oneItemMeta.getLore(), twoItemMeta.getLore());
            } else {
               return false;
            }
         }
      } else {
         return false;
      }
   }

   public static void setOwner(ItemStack itemStack, String playerName) {
      if (itemStack != null && !StrUtil.isEmpty(playerName)) {
         ItemMeta itemMeta = itemStack.getItemMeta();
         if (itemMeta instanceof SkullMeta) {
            SkullMeta skullMeta = (SkullMeta)itemMeta;
            ItemMetaUtil.setOwner(skullMeta, playerName);
            itemStack.setItemMeta(skullMeta);
         }
      }
   }

   public static void setSkull(ItemStack itemStack, String base64) {
      if (itemStack != null && !StrUtil.isEmpty(base64)) {
         ItemMeta itemMeta = itemStack.getItemMeta();
         if (itemMeta instanceof SkullMeta) {
            SkullMeta skullMeta = (SkullMeta)itemMeta;
            ItemMetaUtil.setSkull(skullMeta, base64);
            itemStack.setItemMeta(skullMeta);
         }
      }
   }

   private static void dropItemBatch(Player player, List<ItemStack> dropItemList) {
      int totalAmount = dropItemList.stream().mapToInt(ItemStack::getAmount).sum();
      if (totalAmount < 300) {
         PlayerSchedulerUtil.dropItem(player, dropItemList);
      } else {
         List<ItemStack> batchList = new ArrayList<>();
         int batchAmount = 0;
         long tickDelay = 0L;

         for (ItemStack item : dropItemList) {
            int itemAmount = item.getAmount();

            while (itemAmount > 0) {
               int canAdd = Math.min(itemAmount, 64 - batchAmount);
               ItemStack batchItem = item.clone();
               batchItem.setAmount(canAdd);
               batchList.add(batchItem);
               batchAmount += canAdd;
               itemAmount -= canAdd;
               if (batchAmount >= 64) {
                  List<ItemStack> finalBatch = new ArrayList<>(batchList);
                  PlayerSchedulerUtil.dropItem(player, finalBatch, tickDelay);
                  batchList.clear();
                  batchAmount = 0;
                  tickDelay++;
               }
            }
         }

         if (!batchList.isEmpty()) {
            PlayerSchedulerUtil.dropItem(player, batchList, tickDelay);
         }
      }
   }

   private static List<ItemStack> addItemReturnDropItemMap(Player player, ItemStack itemStack, int amount) {
      PlayerInventory playerInventory = player.getInventory();
      List<ItemStack> dropItemList = new ArrayList<>();
      int maxStackSize = itemStack.getMaxStackSize();
      if (amount > maxStackSize) {
         itemStack.setAmount(maxStackSize);
         dropItemList.addAll(playerInventory.addItem(new ItemStack[]{itemStack}).values());
         dropItemList.addAll(addItemReturnDropItemMap(player, itemStack, amount - maxStackSize));
      } else {
         itemStack.setAmount(amount);
         dropItemList.addAll(playerInventory.addItem(new ItemStack[]{itemStack}).values());
      }

      return dropItemList;
   }

   private static ItemStack getLegacyItemStack(String materialStr, Material defaultMaterial) {
      if (materialStr.contains(":")) {
         String[] parts = materialStr.split(":");
         Material material = findMaterial(parts[0]);
         if (parts.length == 2 && material != null) {
            Optional<BigDecimal> dataOpt = NumberUtil.isNumericToBigDecimal(parts[1]);
            if (dataOpt.isPresent()) {
               return new ItemStack(material, 1, dataOpt.get().shortValue());
            }
         }
      }

      return new ItemStack(defaultMaterial);
   }

   @Nullable
   private static Material findMaterial(@NotNull String materialStr) {
      materialStr = materialStr.toUpperCase(Locale.ROOT);
      Material material = Material.getMaterial(materialStr);
      if (material != null) {
         return material;
      } else {
         return BaseConstants.VERSION_ID > VersionCheckEnum.V_1_12.getVersionId() ? Material.getMaterial(materialStr, true) : null;
      }
   }
}
