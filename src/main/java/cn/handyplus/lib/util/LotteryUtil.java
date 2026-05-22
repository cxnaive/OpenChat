package cn.handyplus.lib.util;

import cn.handyplus.lib.core.CollUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class LotteryUtil {
   private final List<LotteryUtil.ContinuousList> lotteryList = new ArrayList<>();
   private double maxElement;

   public LotteryUtil(List<Double> list) {
      if (!CollUtil.isNotEmpty(list)) {
         throw new IllegalArgumentException("抽奖集合不能为空！");
      } else {
         for (Double d : list) {
            double minElement = this.maxElement;
            this.maxElement = this.maxElement + d;
            LotteryUtil.ContinuousList continuousList = new LotteryUtil.ContinuousList(minElement, this.maxElement);
            this.lotteryList.add(continuousList);
         }
      }
   }

   public int randomIndex() {
      int index = -1;
      Random r = new Random();
      double d = r.nextDouble() * this.maxElement;
      if (d == 0.0) {
         d = r.nextDouble() * this.maxElement;
      }

      int size = this.lotteryList.size();

      for (int i = 0; i < size; i++) {
         LotteryUtil.ContinuousList cl = this.lotteryList.get(i);
         if (cl.isContainKey(d)) {
            index = i;
            break;
         }
      }

      if (index == -1) {
         throw new IllegalArgumentException("概率集合设置不合理！");
      } else {
         return index;
      }
   }

   private static class ContinuousList {
      private final double minElement;
      private final double maxElement;

      public ContinuousList(double minElement, double maxElement) {
         if (minElement > maxElement) {
            throw new IllegalArgumentException("区间不合理，minElement不能大于maxElement！");
         } else {
            this.minElement = minElement;
            this.maxElement = maxElement;
         }
      }

      public boolean isContainKey(double element) {
         return element > this.minElement && element <= this.maxElement;
      }
   }
}
