package cn.handyplus.lib.inventory;

import lombok.Generated;

public class InventoryCheckParam {
   private boolean check;
   private HandyInventory handyInventory;

   @Generated
   public boolean isCheck() {
      return this.check;
   }

   @Generated
   public HandyInventory getHandyInventory() {
      return this.handyInventory;
   }

   @Generated
   public void setCheck(boolean check) {
      this.check = check;
   }

   @Generated
   public void setHandyInventory(HandyInventory handyInventory) {
      this.handyInventory = handyInventory;
   }
}
