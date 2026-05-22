package cn.handyplus.lib.db;

import cn.handyplus.lib.core.CollUtil;
import cn.handyplus.lib.core.DateUtil;
import cn.handyplus.lib.core.StrUtil;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import lombok.Generated;

class DbSql {
   private String tableName;
   private TableInfoParam tableInfoParam;
   private String field;
   private LinkedHashMap<String, FieldInfoParam> fieldInfoMap;
   private String where;
   private LinkedHashMap<Integer, Object> whereMap;
   private List<String> updatefieldList;
   private LinkedHashMap<Integer, Object> updateFieldMap;
   private String limit;
   private String order;
   private String group;

   private static String assemblySql(String... sqlColl) {
      StringBuilder sb = new StringBuilder();

      for (String sql : sqlColl) {
         if (!StrUtil.isEmpty(sql)) {
            sb.append(sql);
         }
      }

      return sb.toString();
   }

   protected String selectCountSql(String field) {
      String countStr = StrUtil.isNotEmpty(field) ? String.format("COUNT(DISTINCT `%s`)", field) : "COUNT(*)";
      return assemblySql("SELECT ", countStr, " FROM ", this.tableName, this.where);
   }

   protected String selectDataSql() {
      return assemblySql("SELECT ", this.field, " FROM ", this.tableName, this.where, this.group, this.order, this.limit);
   }

   protected String insertDataSql() {
      List<String> questionMarkList = new ArrayList<>();

      for (int i = 0; i < this.fieldInfoMap.size(); i++) {
         questionMarkList.add("?");
      }

      return assemblySql("INSERT INTO ", this.tableName, "(", this.field, ")", " VALUES ", "(", CollUtil.listToStr(questionMarkList), ")");
   }

   protected String updateDataSql() {
      return assemblySql("UPDATE ", this.tableName, " SET ", CollUtil.listToStr(this.updatefieldList), this.where);
   }

   protected String deleteDataSql() {
      return assemblySql("DELETE FROM ", this.tableName, this.where);
   }

   protected void addCondition(boolean condition, String fieldName, SqlKeyword sqlKeyword, Object val) {
      if (condition) {
         val = this.specialHandling(val);
         this.where = this.where + SqlKeyword.AND.getKeyword() + "`" + fieldName + "`" + sqlKeyword.getKeyword() + "?";
         this.setWhereMap(val);
      }
   }

   protected void addCondition(boolean condition, String fieldName, SqlKeyword sqlKeyword, String compareFieldName) {
      if (condition) {
         this.where = this.where + SqlKeyword.AND.getKeyword() + "`" + fieldName + "`" + sqlKeyword.getKeyword() + "`" + compareFieldName + "`";
      }
   }

   protected void addLikeCondition(boolean condition, String fieldName, SqlKeyword sqlKeyword, Object val) {
      if (condition) {
         this.where = this.where + SqlKeyword.AND.getKeyword() + "`" + fieldName + "`" + sqlKeyword.getKeyword() + "?";
         this.setWhereMap(val);
      }
   }

   protected void addNull(boolean condition, String fieldName, SqlKeyword orderType) {
      if (condition) {
         this.where = this.where + SqlKeyword.AND.getKeyword() + "`" + fieldName + "`" + orderType.getKeyword();
      }
   }

   protected void addCondition(boolean condition, String fieldName, Object val, SqlKeyword sqlKeyword, String compareFieldName) {
      if (condition) {
         this.where = this.where + SqlKeyword.AND.getKeyword() + "`" + fieldName + "`" + " + " + "?" + sqlKeyword.getKeyword() + "`" + compareFieldName + "`";
         this.setWhereMap(val);
      }
   }

   protected void addCondition(boolean condition, String fieldName, Object val1, SqlKeyword sqlKeyword, Object val2) {
      if (condition) {
         this.where = this.where + SqlKeyword.AND.getKeyword() + "`" + fieldName + "`" + " + " + "?" + sqlKeyword.getKeyword() + "?";
         this.setWhereMap(val1);
         this.setWhereMap(val2);
      }
   }

   protected void addOrderByCondition(boolean condition, String fieldName, SqlKeyword orderType) {
      if (condition) {
         this.order = SqlKeyword.ORDER_BY.getKeyword() + "`" + fieldName + "`" + orderType.getKeyword();
      }
   }

   protected void addOrderByCondition(boolean condition, String fieldName, String fieldNameTwo, SqlKeyword orderType) {
      if (condition) {
         this.order = SqlKeyword.ORDER_BY.getKeyword()
            + "`"
            + fieldName
            + "`"
            + orderType.getKeyword()
            + " , "
            + "`"
            + fieldNameTwo
            + "`"
            + orderType.getKeyword();
      }
   }

   protected void addOrderByRand(boolean condition) {
      if (condition) {
         if (DbTypeEnum.SQLite.getType().equalsIgnoreCase(SqlManagerUtil.getInstance().getStorageMethod())) {
            this.order = " ORDER BY RANDOM()";
         } else {
            this.order = " ORDER BY RAND()";
         }
      }
   }

   protected void addGroupByCondition(boolean condition, String fieldName) {
      if (condition) {
         this.group = SqlKeyword.GROUP_BY.getKeyword() + "`" + fieldName + "`";
      }
   }

   protected void addLimitCondition(boolean condition, int pageNo, int pageSize) {
      if (condition) {
         int ret = (pageNo - 1) * pageSize;
         int offset = Math.max(ret, 0);
         this.limit = SqlKeyword.LIMIT.getKeyword() + pageSize + SqlKeyword.OFFSET.getKeyword() + offset;
      }
   }

   protected void addInCondition(boolean condition, String fieldName, SqlKeyword sqlKeyword, List<?> val) {
      if (condition && !CollUtil.isEmpty(val)) {
         String placeholders = String.join(",", Collections.nCopies(val.size(), "?"));
         this.where = this.where + SqlKeyword.AND.getKeyword() + "`" + fieldName + "`" + sqlKeyword.getKeyword() + "(" + placeholders + ")";
         this.setWhereMap(val);
      }
   }

   protected void updateCondition(boolean condition, String fieldName, Object val) {
      if (condition) {
         val = this.updateSpecialHandling(val);
         this.updatefieldList.add("`" + fieldName + "`" + " = " + "?");
         this.updateFieldMap.put(this.updatefieldList.size(), val);
      }
   }

   protected void updateCondition(boolean condition, String fieldName, String calculateFieldName, String sqlKeyword, Object val) {
      if (condition) {
         this.updatefieldList.add("`" + fieldName + "`" + " = " + "`" + calculateFieldName + "`" + sqlKeyword + "?");
         this.updateFieldMap.put(this.updatefieldList.size(), val);
      }
   }

   private Object specialHandling(Object val) {
      if (val == null) {
         return null;
      } else {
         if (val instanceof Boolean) {
            Boolean bool = (Boolean)val;
            val = bool ? 1 : 0;
         }

         if (val instanceof UUID) {
            val = val.toString();
         }

         if (DbTypeEnum.SQLite.getType().equalsIgnoreCase(SqlManagerUtil.getInstance().getStorageMethod())) {
            if (val instanceof LocalDateTime) {
               val = DateUtil.toEpochSecond((LocalDateTime)val);
            }

            if (val instanceof Date) {
               Date date = (Date)val;
               val = date.getTime();
            }
         }

         return val;
      }
   }

   private Object updateSpecialHandling(Object val) {
      if (val == null) {
         return null;
      } else {
         if (val instanceof Boolean) {
            Boolean bool = (Boolean)val;
            val = bool ? 1 : 0;
         }

         if (val instanceof UUID) {
            val = val.toString();
         }

         if (DbTypeEnum.SQLite.getType().equalsIgnoreCase(SqlManagerUtil.getInstance().getStorageMethod())) {
            if (val instanceof LocalDateTime) {
               val = DateUtil.toEpochSecond((LocalDateTime)val);
            }

            if (val instanceof Date) {
               Date date = (Date)val;
               val = date.getTime();
            }
         } else {
            if (val instanceof LocalDateTime) {
               val = new Timestamp(DateUtil.toEpochSecond((LocalDateTime)val));
            }

            if (val instanceof Date) {
               Date date = (Date)val;
               val = new Timestamp(date.getTime());
            }
         }

         return val;
      }
   }

   private void setWhereMap(Object val) {
      if (this.whereMap == null) {
         this.whereMap = new LinkedHashMap<>();
      }

      if (val instanceof List) {
         List<?> list = (List<?>)val;
         list.forEach(item -> this.whereMap.put(this.whereMap.size() + 1, this.specialHandling(item)));
      } else {
         this.whereMap.put(this.whereMap.size() + 1, val);
      }
   }

   @Generated
   DbSql(
      String tableName,
      TableInfoParam tableInfoParam,
      String field,
      LinkedHashMap<String, FieldInfoParam> fieldInfoMap,
      String where,
      LinkedHashMap<Integer, Object> whereMap,
      List<String> updatefieldList,
      LinkedHashMap<Integer, Object> updateFieldMap,
      String limit,
      String order,
      String group
   ) {
      this.tableName = tableName;
      this.tableInfoParam = tableInfoParam;
      this.field = field;
      this.fieldInfoMap = fieldInfoMap;
      this.where = where;
      this.whereMap = whereMap;
      this.updatefieldList = updatefieldList;
      this.updateFieldMap = updateFieldMap;
      this.limit = limit;
      this.order = order;
      this.group = group;
   }

   @Generated
   public static DbSql.DbSqlBuilder builder() {
      return new DbSql.DbSqlBuilder();
   }

   @Generated
   public TableInfoParam getTableInfoParam() {
      return this.tableInfoParam;
   }

   @Generated
   public void setField(String field) {
      this.field = field;
   }

   @Generated
   public LinkedHashMap<String, FieldInfoParam> getFieldInfoMap() {
      return this.fieldInfoMap;
   }

   @Generated
   public LinkedHashMap<Integer, Object> getWhereMap() {
      return this.whereMap;
   }

   @Generated
   public LinkedHashMap<Integer, Object> getUpdateFieldMap() {
      return this.updateFieldMap;
   }

   @Generated
   public static class DbSqlBuilder {
      @Generated
      private String tableName;
      @Generated
      private TableInfoParam tableInfoParam;
      @Generated
      private String field;
      @Generated
      private LinkedHashMap<String, FieldInfoParam> fieldInfoMap;
      @Generated
      private String where;
      @Generated
      private LinkedHashMap<Integer, Object> whereMap;
      @Generated
      private List<String> updatefieldList;
      @Generated
      private LinkedHashMap<Integer, Object> updateFieldMap;
      @Generated
      private String limit;
      @Generated
      private String order;
      @Generated
      private String group;

      @Generated
      DbSqlBuilder() {
      }

      @Generated
      public DbSql.DbSqlBuilder tableName(String tableName) {
         this.tableName = tableName;
         return this;
      }

      @Generated
      public DbSql.DbSqlBuilder tableInfoParam(TableInfoParam tableInfoParam) {
         this.tableInfoParam = tableInfoParam;
         return this;
      }

      @Generated
      public DbSql.DbSqlBuilder field(String field) {
         this.field = field;
         return this;
      }

      @Generated
      public DbSql.DbSqlBuilder fieldInfoMap(LinkedHashMap<String, FieldInfoParam> fieldInfoMap) {
         this.fieldInfoMap = fieldInfoMap;
         return this;
      }

      @Generated
      public DbSql.DbSqlBuilder where(String where) {
         this.where = where;
         return this;
      }

      @Generated
      public DbSql.DbSqlBuilder whereMap(LinkedHashMap<Integer, Object> whereMap) {
         this.whereMap = whereMap;
         return this;
      }

      @Generated
      public DbSql.DbSqlBuilder updatefieldList(List<String> updatefieldList) {
         this.updatefieldList = updatefieldList;
         return this;
      }

      @Generated
      public DbSql.DbSqlBuilder updateFieldMap(LinkedHashMap<Integer, Object> updateFieldMap) {
         this.updateFieldMap = updateFieldMap;
         return this;
      }

      @Generated
      public DbSql.DbSqlBuilder limit(String limit) {
         this.limit = limit;
         return this;
      }

      @Generated
      public DbSql.DbSqlBuilder order(String order) {
         this.order = order;
         return this;
      }

      @Generated
      public DbSql.DbSqlBuilder group(String group) {
         this.group = group;
         return this;
      }

      @Generated
      public DbSql build() {
         return new DbSql(
            this.tableName,
            this.tableInfoParam,
            this.field,
            this.fieldInfoMap,
            this.where,
            this.whereMap,
            this.updatefieldList,
            this.updateFieldMap,
            this.limit,
            this.order,
            this.group
         );
      }

      @Generated
      @Override
      public String toString() {
         return "DbSql.DbSqlBuilder(tableName="
            + this.tableName
            + ", tableInfoParam="
            + this.tableInfoParam
            + ", field="
            + this.field
            + ", fieldInfoMap="
            + this.fieldInfoMap
            + ", where="
            + this.where
            + ", whereMap="
            + this.whereMap
            + ", updatefieldList="
            + this.updatefieldList
            + ", updateFieldMap="
            + this.updateFieldMap
            + ", limit="
            + this.limit
            + ", order="
            + this.order
            + ", group="
            + this.group
            + ")";
      }
   }
}
