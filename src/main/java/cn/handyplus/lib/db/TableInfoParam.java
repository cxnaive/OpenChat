package cn.handyplus.lib.db;

import lombok.Generated;

class TableInfoParam {
   private String tableName;
   private String tableComment;

   @Generated
   TableInfoParam(String tableName, String tableComment) {
      this.tableName = tableName;
      this.tableComment = tableComment;
   }

   @Generated
   public static TableInfoParam.TableInfoParamBuilder builder() {
      return new TableInfoParam.TableInfoParamBuilder();
   }

   @Generated
   public String getTableName() {
      return this.tableName;
   }

   @Generated
   public String getTableComment() {
      return this.tableComment;
   }

   @Generated
   public void setTableName(String tableName) {
      this.tableName = tableName;
   }

   @Generated
   public void setTableComment(String tableComment) {
      this.tableComment = tableComment;
   }

   @Generated
   public static class TableInfoParamBuilder {
      @Generated
      private String tableName;
      @Generated
      private String tableComment;

      @Generated
      TableInfoParamBuilder() {
      }

      @Generated
      public TableInfoParam.TableInfoParamBuilder tableName(String tableName) {
         this.tableName = tableName;
         return this;
      }

      @Generated
      public TableInfoParam.TableInfoParamBuilder tableComment(String tableComment) {
         this.tableComment = tableComment;
         return this;
      }

      @Generated
      public TableInfoParam build() {
         return new TableInfoParam(this.tableName, this.tableComment);
      }

      @Generated
      @Override
      public String toString() {
         return "TableInfoParam.TableInfoParamBuilder(tableName=" + this.tableName + ", tableComment=" + this.tableComment + ")";
      }
   }
}
