package cn.handyplus.lib.db;

import lombok.Generated;

public enum IndexEnum {
   NOT("NOT"),
   INDEX("INDEX"),
   UNIQUE("UNIQUE");

   private final String name;

   @Generated
   public String getName() {
      return this.name;
   }

   @Generated
   private IndexEnum(final String name) {
      this.name = name;
   }
}
