package cn.handyplus.lib.db;

import lombok.Generated;

enum FieldTypeEnum {
   STRING("java.lang.String", "VARCHAR", 64),
   DATE("java.util.Date", "DATETIME", 0),
   INTEGER("java.lang.Integer", "INT", 11),
   BASIC_INT("int", "INT", 11),
   LONG("java.lang.Long", "BIGINT", 20),
   BASIC_LONG("long", "BIGINT", 20),
   BOOLEAN("java.lang.Boolean", "INT", 2),
   BASIC_BOOLEAN("boolean", "INT", 2),
   DOUBLE("java.lang.Double", "DOUBLE", 11),
   BASIC_DOUBLE("double", "DOUBLE", 11),
   TEXT("java.lang.String", "TEXT", 0),
   BIG_DECIMAL("java.math.BigDecimal", "DECIMAL", 21),
   UUID("java.util.UUID", "VARCHAR", 64);

   private final String javaType;
   private final String mysqlType;
   private final Integer length;

   static FieldTypeEnum getEnum(FieldInfoParam fieldInfoParam) {
      String javaType = fieldInfoParam.getFieldType();
      Integer fieldLength = fieldInfoParam.getFieldLength();
      if (STRING.javaType.equals(javaType) && fieldLength >= DbConstant.FIELD_TEXT_LENGTH) {
         return TEXT;
      } else {
         for (FieldTypeEnum fieldTypeEnum : values()) {
            if (fieldTypeEnum.getJavaType().equals(javaType)) {
               return fieldTypeEnum;
            }
         }

         return STRING;
      }
   }

   @Generated
   public String getJavaType() {
      return this.javaType;
   }

   @Generated
   public String getMysqlType() {
      return this.mysqlType;
   }

   @Generated
   public Integer getLength() {
      return this.length;
   }

   @Generated
   private FieldTypeEnum(final String javaType, final String mysqlType, final Integer length) {
      this.javaType = javaType;
      this.mysqlType = mysqlType;
      this.length = length;
   }
}
