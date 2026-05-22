package cn.handyplus.lib;

import cn.handyplus.lib.annotation.TableName;
import cn.handyplus.lib.command.HandyCommandWrapper;
import cn.handyplus.lib.constants.BaseConstants;
import cn.handyplus.lib.constants.VersionCheckEnum;
import cn.handyplus.lib.core.ClassUtil;
import cn.handyplus.lib.core.CollUtil;
import cn.handyplus.lib.db.Db;
import cn.handyplus.lib.db.SqlManagerUtil;
import cn.handyplus.lib.internal.CompatCore;
import cn.handyplus.lib.internal.HandySchedulerUtil;
import cn.handyplus.lib.internal.LoginCompatUtil;
import cn.handyplus.lib.internal.bukkit.Metrics;
import cn.handyplus.lib.internal.charts.CustomChart;
import cn.handyplus.lib.inventory.HandyInventoryUtil;
import cn.handyplus.lib.inventory.HandyInventoryWrapper;
import cn.handyplus.lib.util.BcUtil;
import cn.handyplus.lib.util.HandyHttpUtil;
import cn.handyplus.lib.util.LegacyUtil;
import java.util.List;
import org.bukkit.plugin.java.JavaPlugin;

public final class InitApi {
   private static final InitApi INSTANCE = new InitApi();
   public static JavaPlugin PLUGIN;

   private InitApi() {
   }

   public static InitApi getInstance(JavaPlugin plugin) {
      PLUGIN = plugin;
      BaseConstants.VERSION_CHECK_ENUM = VersionCheckEnum.getEnum();
      BaseConstants.VERSION_ID = BaseConstants.VERSION_CHECK_ENUM.getVersionId();
      HandySchedulerUtil.init(plugin);
      LegacyUtil.initClasses();
      CompatCore.init(BaseConstants.VERSION_ID);
      LoginCompatUtil.init(plugin);
      return INSTANCE;
   }

   public InitApi initCommand(String packageName) {
      try {
         HandyCommandWrapper.initCommand(packageName);
         return this;
      } catch (Throwable var3) {
         throw new RuntimeException(var3);
      }
   }

   public InitApi initListener(String packageName) {
      return this.initListener(packageName, null);
   }

   public InitApi initListener(String packageName, List<String> ignoreList) {
      try {
         HandyInventoryWrapper.initListener(packageName, ignoreList);
         return this;
      } catch (Throwable var4) {
         throw new RuntimeException(var4);
      }
   }

   public InitApi initClickEvent(String packageName) {
      try {
         HandyInventoryWrapper.initClickEvent(packageName);
         return this;
      } catch (Throwable var3) {
         throw new RuntimeException(var3);
      }
   }

   public InitApi checkVersion() {
      if (BaseConstants.IS_CHECK_UPDATE) {
         HandyHttpUtil.checkVersion(null);
      }

      return this;
   }

   public InitApi addMetrics(int pluginId) {
      return this.addMetrics(pluginId, null);
   }

   public InitApi addMetrics(int pluginId, List<CustomChart> customCharts) {
      try {
         Metrics metrics = new Metrics(PLUGIN, pluginId);
         if (CollUtil.isNotEmpty(customCharts)) {
            for (CustomChart customChart : customCharts) {
               metrics.addCustomChart(customChart);
            }
         }
      } catch (Throwable var6) {
      }

      return this;
   }

   public InitApi enableSql(String packageName) {
      List<Class<?>> tableList = ClassUtil.getInstance().getClassByAnnotation(packageName, TableName.class);
      if (CollUtil.isEmpty(tableList)) {
         return this;
      } else {
         SqlManagerUtil.getInstance().enableSql();

         for (Class<?> aClass : tableList) {
            if (aClass.getAnnotation(TableName.class).create()) {
               Db.use(aClass).createTable();
            }
         }

         return this;
      }
   }

   public InitApi enableBc() {
      BcUtil.registerOut();
      return this;
   }

   public static void disable() {
      SqlManagerUtil.getInstance().close();
      HandySchedulerUtil.cancelTask();
      if (!HandySchedulerUtil.isFolia()) {
         HandyInventoryUtil.closeHandyInventory();
      }
   }
}
