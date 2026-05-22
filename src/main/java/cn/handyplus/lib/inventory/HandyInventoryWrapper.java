package cn.handyplus.lib.inventory;

import cn.handyplus.lib.InitApi;
import cn.handyplus.lib.annotation.HandyListener;
import cn.handyplus.lib.constants.BaseConstants;
import cn.handyplus.lib.core.ClassUtil;
import cn.handyplus.lib.core.CollUtil;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

public class HandyInventoryWrapper {
   public static void initClickEvent(String packageName) {
      try {
         List<Class<IHandyClickEvent>> handyClickEventList = ClassUtil.getInstance().getClassByIsAssignableFrom(packageName, IHandyClickEvent.class);
         if (!CollUtil.isEmpty(handyClickEventList)) {
            List<IHandyClickEvent> handyClickEvents = new ArrayList<>();

            for (Class<?> aClass : handyClickEventList) {
               handyClickEvents.add((IHandyClickEvent)aClass.newInstance());
            }

            HandyClickFactory.getInstance().init(handyClickEvents);
         }
      } catch (Throwable var5) {
         throw new RuntimeException(var5);
      }
   }

   public static void initListener(String packageName, List<String> ignoreList) {
      try {
         List<Class<?>> listenerTypesAnnotatedWith = ClassUtil.getInstance().getClassByAnnotation(packageName, HandyListener.class);
         if (!CollUtil.isEmpty(listenerTypesAnnotatedWith)) {
            for (Class<?> aClass : listenerTypesAnnotatedWith) {
               if (!CollUtil.isNotEmpty(ignoreList) || !ignoreList.contains(aClass.getName())) {
                  HandyListener handyListener = aClass.getAnnotation(HandyListener.class);
                  if (handyListener != null && BaseConstants.VERSION_ID >= handyListener.version().getVersionId()) {
                     Bukkit.getServer().getPluginManager().registerEvents((Listener)aClass.newInstance(), InitApi.PLUGIN);
                  }
               }
            }
         }
      } catch (Throwable var6) {
         throw new RuntimeException(var6);
      }
   }
}
