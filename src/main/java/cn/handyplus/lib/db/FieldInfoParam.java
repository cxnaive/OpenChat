package cn.handyplus.lib.db;

import java.lang.reflect.Field;
import lombok.Generated;

class FieldInfoParam {
   private String fieldName;
   private String fieldRealName;
   private String fieldType;
   private String fieldComment;
   private Boolean fieldNotNull;
   private String fieldDefault;
   private Integer fieldIndex;
   private Integer fieldLength;
   private IndexEnum indexEnum;
   private Field fieldCache;

   @Generated
   FieldInfoParam(
      String fieldName,
      String fieldRealName,
      String fieldType,
      String fieldComment,
      Boolean fieldNotNull,
      String fieldDefault,
      Integer fieldIndex,
      Integer fieldLength,
      IndexEnum indexEnum,
      Field fieldCache
   ) {
      this.fieldName = fieldName;
      this.fieldRealName = fieldRealName;
      this.fieldType = fieldType;
      this.fieldComment = fieldComment;
      this.fieldNotNull = fieldNotNull;
      this.fieldDefault = fieldDefault;
      this.fieldIndex = fieldIndex;
      this.fieldLength = fieldLength;
      this.indexEnum = indexEnum;
      this.fieldCache = fieldCache;
   }

   @Generated
   public static FieldInfoParam.FieldInfoParamBuilder builder() {
      return new FieldInfoParam.FieldInfoParamBuilder();
   }

   @Generated
   public String getFieldName() {
      return this.fieldName;
   }

   @Generated
   public String getFieldRealName() {
      return this.fieldRealName;
   }

   @Generated
   public String getFieldType() {
      return this.fieldType;
   }

   @Generated
   public String getFieldComment() {
      return this.fieldComment;
   }

   @Generated
   public Boolean getFieldNotNull() {
      return this.fieldNotNull;
   }

   @Generated
   public String getFieldDefault() {
      return this.fieldDefault;
   }

   @Generated
   public Integer getFieldIndex() {
      return this.fieldIndex;
   }

   @Generated
   public Integer getFieldLength() {
      return this.fieldLength;
   }

   @Generated
   public IndexEnum getIndexEnum() {
      return this.indexEnum;
   }

   @Generated
   public Field getFieldCache() {
      return this.fieldCache;
   }

   @Generated
   public void setFieldName(String fieldName) {
      this.fieldName = fieldName;
   }

   @Generated
   public void setFieldRealName(String fieldRealName) {
      this.fieldRealName = fieldRealName;
   }

   @Generated
   public void setFieldType(String fieldType) {
      this.fieldType = fieldType;
   }

   @Generated
   public void setFieldComment(String fieldComment) {
      this.fieldComment = fieldComment;
   }

   @Generated
   public void setFieldNotNull(Boolean fieldNotNull) {
      this.fieldNotNull = fieldNotNull;
   }

   @Generated
   public void setFieldDefault(String fieldDefault) {
      this.fieldDefault = fieldDefault;
   }

   @Generated
   public void setFieldIndex(Integer fieldIndex) {
      this.fieldIndex = fieldIndex;
   }

   @Generated
   public void setFieldLength(Integer fieldLength) {
      this.fieldLength = fieldLength;
   }

   @Generated
   public void setIndexEnum(IndexEnum indexEnum) {
      this.indexEnum = indexEnum;
   }

   @Generated
   public void setFieldCache(Field fieldCache) {
      this.fieldCache = fieldCache;
   }

   @Generated
   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof FieldInfoParam)) {
         return false;
      } else {
         FieldInfoParam other = (FieldInfoParam)o;
         if (!other.canEqual(this)) {
            return false;
         } else {
            Object this$fieldNotNull = this.getFieldNotNull();
            Object other$fieldNotNull = other.getFieldNotNull();
            if (this$fieldNotNull == null ? other$fieldNotNull == null : this$fieldNotNull.equals(other$fieldNotNull)) {
               Object this$fieldIndex = this.getFieldIndex();
               Object other$fieldIndex = other.getFieldIndex();
               if (this$fieldIndex == null ? other$fieldIndex == null : this$fieldIndex.equals(other$fieldIndex)) {
                  Object this$fieldLength = this.getFieldLength();
                  Object other$fieldLength = other.getFieldLength();
                  if (this$fieldLength == null ? other$fieldLength == null : this$fieldLength.equals(other$fieldLength)) {
                     Object this$fieldName = this.getFieldName();
                     Object other$fieldName = other.getFieldName();
                     if (this$fieldName == null ? other$fieldName == null : this$fieldName.equals(other$fieldName)) {
                        Object this$fieldRealName = this.getFieldRealName();
                        Object other$fieldRealName = other.getFieldRealName();
                        if (this$fieldRealName == null ? other$fieldRealName == null : this$fieldRealName.equals(other$fieldRealName)) {
                           Object this$fieldType = this.getFieldType();
                           Object other$fieldType = other.getFieldType();
                           if (this$fieldType == null ? other$fieldType == null : this$fieldType.equals(other$fieldType)) {
                              Object this$fieldComment = this.getFieldComment();
                              Object other$fieldComment = other.getFieldComment();
                              if (this$fieldComment == null ? other$fieldComment == null : this$fieldComment.equals(other$fieldComment)) {
                                 Object this$fieldDefault = this.getFieldDefault();
                                 Object other$fieldDefault = other.getFieldDefault();
                                 if (this$fieldDefault == null ? other$fieldDefault == null : this$fieldDefault.equals(other$fieldDefault)) {
                                    Object this$indexEnum = this.getIndexEnum();
                                    Object other$indexEnum = other.getIndexEnum();
                                    if (this$indexEnum == null ? other$indexEnum == null : this$indexEnum.equals(other$indexEnum)) {
                                       Object this$fieldCache = this.getFieldCache();
                                       Object other$fieldCache = other.getFieldCache();
                                       return this$fieldCache == null ? other$fieldCache == null : this$fieldCache.equals(other$fieldCache);
                                    } else {
                                       return false;
                                    }
                                 } else {
                                    return false;
                                 }
                              } else {
                                 return false;
                              }
                           } else {
                              return false;
                           }
                        } else {
                           return false;
                        }
                     } else {
                        return false;
                     }
                  } else {
                     return false;
                  }
               } else {
                  return false;
               }
            } else {
               return false;
            }
         }
      }
   }

   @Generated
   protected boolean canEqual(Object other) {
      return other instanceof FieldInfoParam;
   }

   @Generated
   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $fieldNotNull = this.getFieldNotNull();
      result = result * 59 + ($fieldNotNull == null ? 43 : $fieldNotNull.hashCode());
      Object $fieldIndex = this.getFieldIndex();
      result = result * 59 + ($fieldIndex == null ? 43 : $fieldIndex.hashCode());
      Object $fieldLength = this.getFieldLength();
      result = result * 59 + ($fieldLength == null ? 43 : $fieldLength.hashCode());
      Object $fieldName = this.getFieldName();
      result = result * 59 + ($fieldName == null ? 43 : $fieldName.hashCode());
      Object $fieldRealName = this.getFieldRealName();
      result = result * 59 + ($fieldRealName == null ? 43 : $fieldRealName.hashCode());
      Object $fieldType = this.getFieldType();
      result = result * 59 + ($fieldType == null ? 43 : $fieldType.hashCode());
      Object $fieldComment = this.getFieldComment();
      result = result * 59 + ($fieldComment == null ? 43 : $fieldComment.hashCode());
      Object $fieldDefault = this.getFieldDefault();
      result = result * 59 + ($fieldDefault == null ? 43 : $fieldDefault.hashCode());
      Object $indexEnum = this.getIndexEnum();
      result = result * 59 + ($indexEnum == null ? 43 : $indexEnum.hashCode());
      Object $fieldCache = this.getFieldCache();
      return result * 59 + ($fieldCache == null ? 43 : $fieldCache.hashCode());
   }

   @Generated
   @Override
   public String toString() {
      return "FieldInfoParam(fieldName="
         + this.getFieldName()
         + ", fieldRealName="
         + this.getFieldRealName()
         + ", fieldType="
         + this.getFieldType()
         + ", fieldComment="
         + this.getFieldComment()
         + ", fieldNotNull="
         + this.getFieldNotNull()
         + ", fieldDefault="
         + this.getFieldDefault()
         + ", fieldIndex="
         + this.getFieldIndex()
         + ", fieldLength="
         + this.getFieldLength()
         + ", indexEnum="
         + this.getIndexEnum()
         + ", fieldCache="
         + this.getFieldCache()
         + ")";
   }

   @Generated
   public static class FieldInfoParamBuilder {
      @Generated
      private String fieldName;
      @Generated
      private String fieldRealName;
      @Generated
      private String fieldType;
      @Generated
      private String fieldComment;
      @Generated
      private Boolean fieldNotNull;
      @Generated
      private String fieldDefault;
      @Generated
      private Integer fieldIndex;
      @Generated
      private Integer fieldLength;
      @Generated
      private IndexEnum indexEnum;
      @Generated
      private Field fieldCache;

      @Generated
      FieldInfoParamBuilder() {
      }

      @Generated
      public FieldInfoParam.FieldInfoParamBuilder fieldName(String fieldName) {
         this.fieldName = fieldName;
         return this;
      }

      @Generated
      public FieldInfoParam.FieldInfoParamBuilder fieldRealName(String fieldRealName) {
         this.fieldRealName = fieldRealName;
         return this;
      }

      @Generated
      public FieldInfoParam.FieldInfoParamBuilder fieldType(String fieldType) {
         this.fieldType = fieldType;
         return this;
      }

      @Generated
      public FieldInfoParam.FieldInfoParamBuilder fieldComment(String fieldComment) {
         this.fieldComment = fieldComment;
         return this;
      }

      @Generated
      public FieldInfoParam.FieldInfoParamBuilder fieldNotNull(Boolean fieldNotNull) {
         this.fieldNotNull = fieldNotNull;
         return this;
      }

      @Generated
      public FieldInfoParam.FieldInfoParamBuilder fieldDefault(String fieldDefault) {
         this.fieldDefault = fieldDefault;
         return this;
      }

      @Generated
      public FieldInfoParam.FieldInfoParamBuilder fieldIndex(Integer fieldIndex) {
         this.fieldIndex = fieldIndex;
         return this;
      }

      @Generated
      public FieldInfoParam.FieldInfoParamBuilder fieldLength(Integer fieldLength) {
         this.fieldLength = fieldLength;
         return this;
      }

      @Generated
      public FieldInfoParam.FieldInfoParamBuilder indexEnum(IndexEnum indexEnum) {
         this.indexEnum = indexEnum;
         return this;
      }

      @Generated
      public FieldInfoParam.FieldInfoParamBuilder fieldCache(Field fieldCache) {
         this.fieldCache = fieldCache;
         return this;
      }

      @Generated
      public FieldInfoParam build() {
         return new FieldInfoParam(
            this.fieldName,
            this.fieldRealName,
            this.fieldType,
            this.fieldComment,
            this.fieldNotNull,
            this.fieldDefault,
            this.fieldIndex,
            this.fieldLength,
            this.indexEnum,
            this.fieldCache
         );
      }

      @Generated
      @Override
      public String toString() {
         return "FieldInfoParam.FieldInfoParamBuilder(fieldName="
            + this.fieldName
            + ", fieldRealName="
            + this.fieldRealName
            + ", fieldType="
            + this.fieldType
            + ", fieldComment="
            + this.fieldComment
            + ", fieldNotNull="
            + this.fieldNotNull
            + ", fieldDefault="
            + this.fieldDefault
            + ", fieldIndex="
            + this.fieldIndex
            + ", fieldLength="
            + this.fieldLength
            + ", indexEnum="
            + this.indexEnum
            + ", fieldCache="
            + this.fieldCache
            + ")";
      }
   }
}
