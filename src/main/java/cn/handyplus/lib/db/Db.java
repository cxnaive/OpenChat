package cn.handyplus.lib.db;

import cn.handyplus.lib.annotation.TableField;
import cn.handyplus.lib.annotation.TableName;
import cn.handyplus.lib.core.CollUtil;
import cn.handyplus.lib.core.StrUtil;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import lombok.Generated;

public class Db<T> {
   private final Class<T> clazz;
   private DbSql dbSql;

   public Db(Class<T> clazz) {
      this.clazz = clazz;
      this.init();
   }

   public static <T> Db<T> use(Class<T> clazz) {
      return new Db<>(clazz);
   }

   @SafeVarargs
   public final <R> void select(DbFunction<R, ?>... fn) {
      List<String> fieldList = new ArrayList<>();

      for (DbFunction<R, ?> rDbFunction : fn) {
         fieldList.add(DbColumnUtil.getFieldName(rDbFunction));
      }

      this.dbSql.setField(CollUtil.listToStr(fieldList));
      this.dbSql.getFieldInfoMap().entrySet().removeIf(entry -> !fieldList.contains(entry.getKey()));
   }

   public final <R> void select(String... fields) {
      List<String> fieldList = new ArrayList<>(Arrays.asList(fields));
      this.dbSql.setField(CollUtil.listToStr(fieldList));
      this.dbSql.getFieldInfoMap().entrySet().removeIf(entry -> !fieldList.contains(entry.getKey()));
   }

   public Compare<T> where() {
      return new Compare<>(this.dbSql);
   }

   public UpdateCondition<T> update() {
      return new UpdateCondition<>(this.dbSql);
   }

   public DbExecution<T> execution() {
      return new DbExecution<>(this.dbSql, this.clazz);
   }

   public void createTable() {
      new DbExecution<>(this.dbSql, this.clazz, false).create();
   }

   public DbExecution<T> execution(String storageMethod) {
      return new DbExecution<>(this.dbSql, this.clazz, storageMethod);
   }

   public DbExecution<T> execution(Connection connection) {
      return new DbExecution<>(this.dbSql, this.clazz, connection);
   }

   private void init() {
      TableName tableName = this.clazz.getAnnotation(TableName.class);
      if (tableName == null) {
         throw new RuntimeException("tableName 为空");
      } else {
         TableInfoParam tableInfoParam = TableInfoParam.builder().tableName(tableName.value()).tableComment(tableName.comment()).build();
         Field[] fields = this.clazz.getDeclaredFields();
         List<String> fieldList = new ArrayList<>();
         LinkedHashMap<String, FieldInfoParam> fieldInfoMap = new LinkedHashMap<>();

         for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            TableField tableField = field.getAnnotation(TableField.class);
            if (tableField != null && StrUtil.isNotEmpty(tableField.value())) {
               fieldList.add("`" + tableField.value() + "`");
               field.setAccessible(true);
               FieldInfoParam build = FieldInfoParam.builder()
                  .fieldName(tableField.value())
                  .fieldRealName(field.getName())
                  .fieldType(field.getGenericType().getTypeName())
                  .fieldComment(tableField.comment())
                  .fieldNotNull(tableField.notNull())
                  .fieldDefault(tableField.fieldDefault())
                  .fieldIndex(i + 1)
                  .fieldLength(tableField.length())
                  .indexEnum(tableField.indexEnum())
                  .fieldCache(field)
                  .build();
               fieldInfoMap.put(tableField.value(), build);
            }
         }

         if (CollUtil.isEmpty(fieldList)) {
            throw new RuntimeException("fieldList 为空");
         } else {
            this.dbSql = DbSql.builder()
               .tableName("`" + tableName.value() + "`")
               .tableInfoParam(tableInfoParam)
               .field(CollUtil.listToStr(fieldList))
               .fieldInfoMap(fieldInfoMap)
               .where(" where 1 = 1")
               .updatefieldList(new ArrayList<>())
               .updateFieldMap(new LinkedHashMap<>())
               .build();
         }
      }
   }

   @Generated
   public DbSql getDbSql() {
      return this.dbSql;
   }
}
