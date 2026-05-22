package cn.handyplus.lib.internal;

public class CompatCore {
   private static final Integer V1_12 = 1120;
   private static final Integer V1_20_6 = 1206;
   protected static boolean IS_1_21;
   protected static boolean IS_1_13;

   public static void init(Integer versionId) {
      if (versionId > V1_12) {
         IS_1_13 = true;
      }

      if (versionId >= V1_20_6) {
         IS_1_21 = true;
      }
   }
}
