package cn.handyplus.lib.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class ProbabilityUtil {
   private final Random random = new Random();

   private ProbabilityUtil() {
   }

   public static ProbabilityUtil getInstance() {
      return ProbabilityUtil.SingletonHolder.INSTANCE;
   }

   public boolean pickIndex(double num) {
      if (num == 0.0) {
         return false;
      } else {
         BigDecimal rate = BigDecimal.ONE.divide(new BigDecimal(num + ""), RoundingMode.UP);
         return this.pickIndex(1, rate.intValue());
      }
   }

   public boolean pickIndex(int num, int maxNum) {
      if (num >= maxNum) {
         num = maxNum;
      }

      int[] nums = new int[]{num, maxNum - num};
      return this.randomIndex(nums) == 0;
   }

   public synchronized boolean pickSyncIndex(double num) {
      return this.pickIndex(num);
   }

   public synchronized boolean pickSyncIndex(int num, int maxNum) {
      return this.pickIndex(num, maxNum);
   }

   private int randomIndex(int[] nums) {
      List<Integer> probabilityNumList = new ArrayList<>();
      int tot = 0;

      for (int num : nums) {
         tot += num;
         probabilityNumList.add(tot);
      }

      int randomNum = this.random.nextInt(tot);
      int hi = probabilityNumList.size() - 1;
      int lo = 0;

      while (lo != hi) {
         int mid = (lo + hi) / 2;
         if (randomNum >= probabilityNumList.get(mid)) {
            lo = mid + 1;
         } else {
            hi = mid;
         }
      }

      return lo;
   }

   private static class SingletonHolder {
      private static final ProbabilityUtil INSTANCE = new ProbabilityUtil();
   }
}
