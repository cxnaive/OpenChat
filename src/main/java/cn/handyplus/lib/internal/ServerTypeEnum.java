package cn.handyplus.lib.internal;

public enum ServerTypeEnum {
   FOLIA("io.papermc.paper.threadedregions.RegionizedServer"),
   PAPER("com.destroystokyo.paper.PaperConfig"),
   SPIGOT("org.spigotmc.SpigotConfig"),
   BUKKIT("org.bukkit.Bukkit");

   private final String className;

   private ServerTypeEnum(String className) {
      this.className = className;
   }

   public static ServerTypeEnum getServerType() {
      if (isClassPresent(FOLIA.className)) {
         return FOLIA;
      } else if (isClassPresent(PAPER.className)) {
         return PAPER;
      } else {
         return isClassPresent(SPIGOT.className) ? SPIGOT : BUKKIT;
      }
   }

   private static boolean isClassPresent(String className) {
      try {
         Class.forName(className, false, ServerTypeEnum.class.getClassLoader());
         return true;
      } catch (ClassNotFoundException var2) {
         return false;
      }
   }
}
