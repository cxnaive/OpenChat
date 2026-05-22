package cn.handyplus.lib.db;

import java.util.Arrays;
import java.util.List;
import lombok.Generated;

public enum DbTypeEnum {
   MySQL("MySQL"),
   SQLite("SQLite");

   private final String type;

   public static List<String> getEnum() {
      return Arrays.asList(MySQL.getType(), SQLite.getType());
   }

   @Generated
   public String getType() {
      return this.type;
   }

   @Generated
   private DbTypeEnum(final String type) {
      this.type = type;
   }
}
