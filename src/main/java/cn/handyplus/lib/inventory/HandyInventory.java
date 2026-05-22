package cn.handyplus.lib.inventory;

import cn.handyplus.lib.core.NumberUtil;
import cn.handyplus.lib.core.StrUtil;
import cn.handyplus.lib.internal.PlayerSchedulerUtil;
import cn.handyplus.lib.util.BaseUtil;
import cn.handyplus.lib.util.ComponentUtil;
import cn.handyplus.lib.util.LegacyUtil;
import cn.handyplus.lib.util.MessageUtil;
import cn.handyplus.lib.util.XSeriesUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.Generated;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class HandyInventory implements InventoryHolder {
   private Inventory inventory;
   private Map<Integer, Long> map;
   private Map<Integer, Integer> intMap;
   private Map<Integer, Object> objMap;
   private Map<Integer, List<String>> listMap;
   private Map<Integer, String> strMap;
   private Map<Integer, String> soundMap;
   private Object obj;
   private Integer id;
   private String guiType;
   private Integer pageNum = 1;
   private Integer pageSize = 10;
   private Integer pageCount = 0;
   private String searchType;
   private Player player;
   private boolean toCancel;
   private String lockKey;
   private String sound;

   public HandyInventory(String guiType, String title) {
      this(guiType, title, 54);
   }

   public HandyInventory(String guiType, String title, boolean toCancel) {
      this(guiType, title, 54, toCancel, null);
   }

   public HandyInventory(String guiType, String title, int size) {
      this(guiType, title, size, true, null);
   }

   public HandyInventory(String guiType, String title, int size, String sound) {
      this(guiType, title, size, true, sound);
   }

   public HandyInventory(String guiType, String title, int size, boolean toCancel, String sound) {
      this.map = new HashMap<>();
      this.intMap = new HashMap<>();
      this.objMap = new HashMap<>();
      this.listMap = new HashMap<>();
      this.strMap = new HashMap<>();
      this.soundMap = new HashMap<>();
      this.guiType = guiType;
      this.createInventory(title, size);
      this.toCancel = toCancel;
      this.sound = sound;
   }

   public void setPageCount(Integer total) {
      this.pageCount = (int)Math.ceil((double)total.intValue() / this.pageSize.intValue());
   }

   public void syncOpen(Inventory inv) {
      PlayerSchedulerUtil.syncOpenInventory(this.player, inv);
   }

   public void syncClose() {
      PlayerSchedulerUtil.syncCloseInventory(this.player);
   }

   public void playSound(int rawSlot) {
      String sound = this.soundMap.getOrDefault(rawSlot, this.sound);
      if (!StrUtil.isEmpty(sound)) {
         if (!Boolean.FALSE.toString().equalsIgnoreCase(sound)) {
            List<String> soundList = StrUtil.strToStrList(sound, ":");
            String soundStr = soundList.get(0);
            int volume = soundList.size() > 1 ? NumberUtil.isNumericToInt(soundList.get(1), 1) : 1;
            int pitch = soundList.size() > 2 ? NumberUtil.isNumericToInt(soundList.get(2), 1) : 1;
            Optional<Sound> soundOpt = XSeriesUtil.getSound(soundStr);
            if (!soundOpt.isPresent()) {
               MessageUtil.sendConsoleMessage("配置错误,没有对应音效:" + soundStr);
            } else {
               PlayerSchedulerUtil.playSound(this.player, soundOpt.get(), (float)volume, (float)pitch);
            }
         }
      }
   }

   private void createInventory(String title, int size) {
      if (BaseUtil.supportsComponentApi()) {
         this.inventory = Bukkit.createInventory(this, size, ComponentUtil.parseColor(title));
      } else {
         this.inventory = Bukkit.createInventory(this, size, LegacyUtil.parseColor(title));
      }
   }

   @Generated
   public Inventory getInventory() {
      return this.inventory;
   }

   @Generated
   public Map<Integer, Long> getMap() {
      return this.map;
   }

   @Generated
   public Map<Integer, Integer> getIntMap() {
      return this.intMap;
   }

   @Generated
   public Map<Integer, Object> getObjMap() {
      return this.objMap;
   }

   @Generated
   public Map<Integer, List<String>> getListMap() {
      return this.listMap;
   }

   @Generated
   public Map<Integer, String> getStrMap() {
      return this.strMap;
   }

   @Generated
   public Map<Integer, String> getSoundMap() {
      return this.soundMap;
   }

   @Generated
   public Object getObj() {
      return this.obj;
   }

   @Generated
   public Integer getId() {
      return this.id;
   }

   @Generated
   public String getGuiType() {
      return this.guiType;
   }

   @Generated
   public Integer getPageNum() {
      return this.pageNum;
   }

   @Generated
   public Integer getPageSize() {
      return this.pageSize;
   }

   @Generated
   public Integer getPageCount() {
      return this.pageCount;
   }

   @Generated
   public String getSearchType() {
      return this.searchType;
   }

   @Generated
   public Player getPlayer() {
      return this.player;
   }

   @Generated
   public boolean isToCancel() {
      return this.toCancel;
   }

   @Generated
   public String getLockKey() {
      return this.lockKey;
   }

   @Generated
   public String getSound() {
      return this.sound;
   }

   @Generated
   public void setInventory(Inventory inventory) {
      this.inventory = inventory;
   }

   @Generated
   public void setMap(Map<Integer, Long> map) {
      this.map = map;
   }

   @Generated
   public void setIntMap(Map<Integer, Integer> intMap) {
      this.intMap = intMap;
   }

   @Generated
   public void setObjMap(Map<Integer, Object> objMap) {
      this.objMap = objMap;
   }

   @Generated
   public void setListMap(Map<Integer, List<String>> listMap) {
      this.listMap = listMap;
   }

   @Generated
   public void setStrMap(Map<Integer, String> strMap) {
      this.strMap = strMap;
   }

   @Generated
   public void setSoundMap(Map<Integer, String> soundMap) {
      this.soundMap = soundMap;
   }

   @Generated
   public void setObj(Object obj) {
      this.obj = obj;
   }

   @Generated
   public void setId(Integer id) {
      this.id = id;
   }

   @Generated
   public void setGuiType(String guiType) {
      this.guiType = guiType;
   }

   @Generated
   public void setPageNum(Integer pageNum) {
      this.pageNum = pageNum;
   }

   @Generated
   public void setPageSize(Integer pageSize) {
      this.pageSize = pageSize;
   }

   @Generated
   public void setSearchType(String searchType) {
      this.searchType = searchType;
   }

   @Generated
   public void setPlayer(Player player) {
      this.player = player;
   }

   @Generated
   public void setToCancel(boolean toCancel) {
      this.toCancel = toCancel;
   }

   @Generated
   public void setLockKey(String lockKey) {
      this.lockKey = lockKey;
   }

   @Generated
   public void setSound(String sound) {
      this.sound = sound;
   }
}
