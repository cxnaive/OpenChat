package cn.handyplus.lib.db;

import cn.handyplus.lib.InitApi;
import cn.handyplus.lib.constants.BaseConstants;
import cn.handyplus.lib.core.BeanUtil;
import cn.handyplus.lib.core.DateUtil;
import cn.handyplus.lib.core.NumberUtil;
import cn.handyplus.lib.core.StrUtil;
import cn.handyplus.lib.util.MessageUtil;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class DbExecution<T> implements BaseMapper<T> {
   private final Class<T> clazz;
   private final DbSql dbSql;
   private final String storageMethod;
   private final boolean isMysql;
   private final Connection conn;

   protected DbExecution(DbSql dbSql, Class<T> clazz) {
      this.dbSql = dbSql;
      this.clazz = clazz;
      this.storageMethod = SqlManagerUtil.getInstance().getStorageMethod();
      this.isMysql = !DbTypeEnum.SQLite.getType().equalsIgnoreCase(this.storageMethod);
      this.conn = SqlManagerUtil.getInstance().getConnection(this.storageMethod);
   }

   protected DbExecution(DbSql dbSql, Class<T> clazz, boolean initConn) {
      this.dbSql = dbSql;
      this.clazz = clazz;
      this.storageMethod = SqlManagerUtil.getInstance().getStorageMethod();
      this.isMysql = !DbTypeEnum.SQLite.getType().equalsIgnoreCase(this.storageMethod);
      this.conn = initConn ? SqlManagerUtil.getInstance().getConnection(this.storageMethod) : null;
   }

   protected DbExecution(DbSql dbSql, Class<T> clazz, Connection conn) {
      this.dbSql = dbSql;
      this.clazz = clazz;
      this.storageMethod = SqlManagerUtil.getInstance().getStorageMethod();
      this.isMysql = !DbTypeEnum.SQLite.getType().equalsIgnoreCase(this.storageMethod);
      this.conn = conn;
   }

   protected DbExecution(DbSql dbSql, Class<T> clazz, String storageMethod) {
      this.dbSql = dbSql;
      this.clazz = clazz;
      this.storageMethod = storageMethod;
      this.isMysql = !DbTypeEnum.SQLite.getType().equalsIgnoreCase(storageMethod);
      this.conn = SqlManagerUtil.getInstance().getConnection(storageMethod);
   }

   protected void create() {
      TableInfoParam tableInfoParam = this.dbSql.getTableInfoParam();
      String createTable = this.isMysql
         ? "CREATE TABLE IF NOT EXISTS `%s` (`id` INTEGER (11) AUTO_INCREMENT,PRIMARY KEY (`id`)) CHARACTER SET = utf8mb4 ENGINE=INNODB;"
         : "CREATE TABLE IF NOT EXISTS `%s` ( `id` INTEGER PRIMARY KEY AUTOINCREMENT);";
      String createTableSql = String.format(createTable, tableInfoParam.getTableName());
      this.printSql(createTableSql, "createTableSql: ");
      SqlService.getInstance().executionSql(createTableSql, this.storageMethod);
      if (this.isMysql) {
         String tableCommentSql = String.format("ALTER TABLE `%s` COMMENT '%s';", tableInfoParam.getTableName(), tableInfoParam.getTableComment());
         this.printSql(tableCommentSql, "tableCommentSql: ");
         SqlService.getInstance().executionSql(tableCommentSql, this.storageMethod);
      }

      this.addColumn(tableInfoParam.getTableName(), this.dbSql.getFieldInfoMap());
      this.addIndex(tableInfoParam.getTableName());
   }

   private void addColumn(String tableName, LinkedHashMap<String, FieldInfoParam> fieldInfoMap) {
      String sql;
      if (this.isMysql) {
         String database = BaseConstants.STORAGE_CONFIG.getString("MySQL.Database");
         sql = String.format("SELECT column_name FROM information_schema.COLUMNS WHERE table_name = '%s' AND table_schema = '%s';", tableName, database);
      } else {
         sql = String.format("PRAGMA table_info ( %s )", tableName);
      }

      this.printSql(sql, "addColumnSelectSql: ");
      List<String> fieldNameList = SqlService.getInstance().getTableInfo(sql, this.storageMethod);

      for (String fieldName : fieldInfoMap.keySet()) {
         FieldInfoParam fieldInfoParam = fieldInfoMap.get(fieldName);
         FieldTypeEnum fieldTypeEnum = FieldTypeEnum.getEnum(fieldInfoParam);
         if (!fieldNameList.contains(fieldName)) {
            String addColumn = this.isMysql ? "ALTER TABLE `%s` ADD `%s` %s(%s) %s;" : "ALTER TABLE '%s' ADD '%s' %s(%s) %s;";
            String fieldSql;
            if (this.isMysql) {
               fieldSql = fieldInfoParam.getFieldNotNull() ? "NOT NULL" : "";
               if (StrUtil.isNotEmpty(fieldInfoParam.getFieldDefault())) {
                  fieldSql = fieldSql + String.format(" DEFAULT '%s'", fieldInfoParam.getFieldDefault());
               }
            } else {
               fieldSql = fieldInfoParam.getFieldNotNull() ? "NOT NULL" : "";
               if (StrUtil.isNotEmpty(fieldInfoParam.getFieldDefault())) {
                  fieldSql = fieldSql + String.format(" DEFAULT '%s'", fieldInfoParam.getFieldDefault());
               } else if (fieldInfoParam.getFieldNotNull()) {
                  fieldSql = fieldSql + String.format(" DEFAULT '%s'", "");
               }
            }

            Integer fieldLength = fieldInfoParam.getFieldLength() != 0 ? fieldInfoParam.getFieldLength() : fieldTypeEnum.getLength();
            String mysqlType = fieldTypeEnum.getMysqlType();
            String fieldLengthStr = fieldLength.toString();
            if (FieldTypeEnum.DOUBLE.getMysqlType().equals(mysqlType)
               || FieldTypeEnum.BASIC_DOUBLE.getMysqlType().equals(mysqlType)
               || FieldTypeEnum.BIG_DECIMAL.getMysqlType().equals(mysqlType)) {
               fieldLengthStr = fieldLength + ", 2";
            }

            String createFieldSql = String.format(addColumn, tableName, fieldInfoParam.getFieldName(), mysqlType, fieldLengthStr, fieldSql);
            createFieldSql = createFieldSql.replace("(0)", "");
            this.printSql(createFieldSql, "addColumnSql: ");
            SqlService.getInstance().executionSql(createFieldSql, this.storageMethod);
         }

         if (this.isMysql) {
            String fieldSqlx = fieldInfoParam.getFieldNotNull() ? "NOT NULL" : "";
            if ("id".equals(fieldName)) {
               fieldSqlx = "NOT NULL AUTO_INCREMENT";
            }

            if (StrUtil.isNotEmpty(fieldInfoParam.getFieldDefault())) {
               fieldSqlx = fieldSqlx + String.format(" DEFAULT '%s'", fieldInfoParam.getFieldDefault());
            }

            if (StrUtil.isNotEmpty(fieldInfoParam.getFieldComment())) {
               fieldSqlx = fieldSqlx + String.format(" COMMENT '%s'", fieldInfoParam.getFieldComment());
            }

            Integer fieldLength = fieldInfoParam.getFieldLength() != 0 ? fieldInfoParam.getFieldLength() : fieldTypeEnum.getLength();
            String mysqlType = fieldTypeEnum.getMysqlType();
            String fieldLengthStr = fieldLength.toString();
            if (FieldTypeEnum.DOUBLE.getMysqlType().equals(mysqlType)
               || FieldTypeEnum.BASIC_DOUBLE.getMysqlType().equals(mysqlType)
               || FieldTypeEnum.BIG_DECIMAL.getMysqlType().equals(mysqlType)) {
               fieldLengthStr = fieldLength + ", 2";
            }

            String fieldCommentSql = String.format(
               "ALTER TABLE `%s` MODIFY `%s` %s(%s) %s;", tableName, fieldInfoParam.getFieldName(), fieldTypeEnum.getMysqlType(), fieldLengthStr, fieldSqlx
            );
            fieldCommentSql = fieldCommentSql.replace("(0)", "");
            this.printSql(fieldCommentSql, "addColumnCommentSql: ");
            SqlService.getInstance().executionSql(fieldCommentSql, this.storageMethod);
         }
      }
   }

   @Override
   public Optional<T> selectById(Integer id) {
      this.dbSql.addCondition(true, "id", SqlKeyword.EQ, id);
      return this.selectOne();
   }

   @Override
   public List<T> selectBatchIds(List<Integer> ids) {
      this.dbSql.addInCondition(true, "id", SqlKeyword.IN, ids);
      return this.list();
   }

   @Override
   public List<Map<String, Object>> selectListMap() {
      PreparedStatement ps = null;
      ResultSet rst = null;
      List<Map<String, Object>> list = new ArrayList<>();
      String sql = null;

      try {
         sql = this.dbSql.selectDataSql();
         ps = this.conn.prepareStatement(sql);
         this.psSetVal(ps);
         rst = ps.executeQuery();
         ResultSetMetaData rstMetaData = rst.getMetaData();

         while (rst.next()) {
            Map<String, Object> map = new HashMap<>();

            for (int i = 1; i <= rstMetaData.getColumnCount(); i++) {
               map.put(rstMetaData.getColumnName(i), rst.getObject(i));
            }

            list.add(map);
         }

         return list;
      } catch (SQLException var11) {
         InitApi.PLUGIN.getLogger().log(Level.SEVERE, "selectListMap 发生异常", (Throwable)var11);
      } finally {
         SqlManagerUtil.getInstance().closeSql(this.conn, ps, null);
         this.printSql(sql, "selectListMap: ");
      }

      return list;
   }

   @Override
   public Optional<T> selectOne() {
      PreparedStatement ps = null;
      ResultSet rst = null;
      String sql = null;

      Optional e;
      try {
         sql = this.dbSql.selectDataSql();
         ps = this.conn.prepareStatement(sql);
         this.psSetVal(ps);
         rst = ps.executeQuery();
         if (rst.isBeforeFirst()) {
            Constructor<?> constructor = this.clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            T newInstance = (T)constructor.newInstance();
            LinkedHashMap<String, FieldInfoParam> fieldInfoMap = this.dbSql.getFieldInfoMap();
            if (rst.next()) {
               for (String fieldName : fieldInfoMap.keySet()) {
                  FieldInfoParam fieldInfoParam = fieldInfoMap.get(fieldName);
                  Object obj = rst.getObject(fieldInfoParam.getFieldName());
                  if (obj != null) {
                     obj = this.specialHandling(fieldInfoParam, obj);
                     fieldInfoParam.getFieldCache().set(newInstance, obj);
                  }
               }
            }

            return Optional.of(newInstance);
         }

         e = Optional.empty();
      } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException | SQLException var14) {
         InitApi.PLUGIN.getLogger().log(Level.SEVERE, "selectOne 发生异常", (Throwable)var14);
         return Optional.empty();
      } finally {
         SqlManagerUtil.getInstance().closeSql(this.conn, ps, rst);
         this.printSql(sql, "selectOne: ");
      }

      return e;
   }

   @Override
   public int count() {
      return this.count(null);
   }

   @Override
   public int count(String field) {
      return this.count(field, true);
   }

   private int count(String field, boolean isClose) {
      PreparedStatement ps = null;
      ResultSet rst = null;
      int count = 0;
      String sql = null;

      try {
         sql = this.dbSql.selectCountSql(field);
         ps = this.conn.prepareStatement(sql);
         this.psSetVal(ps);
         rst = ps.executeQuery();

         while (rst.next()) {
            count = rst.getInt(1);
         }
      } catch (SQLException var11) {
         InitApi.PLUGIN.getLogger().log(Level.SEVERE, "count 发生异常", (Throwable)var11);
      } finally {
         SqlManagerUtil.getInstance().closeSql(isClose ? this.conn : null, ps, rst);
         this.printSql(sql, "count: ");
      }

      return count;
   }

   @Override
   public List<T> list() {
      return this.list(true);
   }

   private List<T> list(boolean isClose) {
      PreparedStatement ps = null;
      ResultSet rst = null;
      List<T> list = new ArrayList<>();
      String sql = null;

      Object e;
      try {
         sql = this.dbSql.selectDataSql();
         ps = this.conn.prepareStatement(sql);
         this.psSetVal(ps);
         rst = ps.executeQuery();
         if (rst.isBeforeFirst()) {
            LinkedHashMap<String, FieldInfoParam> fieldInfoMap = this.dbSql.getFieldInfoMap();
            Constructor<?> constructor = this.clazz.getDeclaredConstructor();
            constructor.setAccessible(true);

            while (rst.next()) {
               T newInstance = (T)constructor.newInstance();

               for (String fieldName : fieldInfoMap.keySet()) {
                  FieldInfoParam fieldInfoParam = fieldInfoMap.get(fieldName);
                  Object obj = rst.getObject(fieldInfoParam.getFieldName());
                  if (obj != null) {
                     obj = this.specialHandling(fieldInfoParam, obj);
                     fieldInfoParam.getFieldCache().set(newInstance, obj);
                  }
               }

               list.add(newInstance);
            }

            return list;
         }

         e = list;
      } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException | SQLException var16) {
         InitApi.PLUGIN.getLogger().log(Level.SEVERE, "list 发生异常", (Throwable)var16);
         return list;
      } finally {
         SqlManagerUtil.getInstance().closeSql(isClose ? this.conn : null, ps, rst);
         this.printSql(sql, "list: ");
      }

      return (List<T>)e;
   }

   @Override
   public Page<T> page() {
      Page var3;
      try {
         int count = this.count(null, false);
         List<T> list = new ArrayList<>();
         if (count > 0) {
            list = this.list(false);
         }

         var3 = new Page<>(count, list);
      } finally {
         SqlManagerUtil.getInstance().closeSql(this.conn, null, null);
      }

      return var3;
   }

   @Override
   public int insert(T obj) {
      PreparedStatement ps = null;
      ResultSet rst = null;

      try {
         String sql = this.dbSql.insertDataSql();
         MessageUtil.sendConsoleDebugMessage("insert: " + sql);
         ps = this.conn.prepareStatement(sql, 1);
         Map<String, FieldInfoParam> fieldInfoParamMap = this.dbSql
            .getFieldInfoMap()
            .values()
            .stream()
            .collect(Collectors.groupingBy(FieldInfoParam::getFieldRealName, Collectors.collectingAndThen(Collectors.toList(), value -> value.get(0))));
         Map<String, Object> paramMap = BeanUtil.beanToMap(obj);

         for (String key : paramMap.keySet()) {
            FieldInfoParam fieldInfoParam = fieldInfoParamMap.get(key);
            if (fieldInfoParam != null) {
               Object paramObj = paramMap.get(key);
               if (FieldTypeEnum.UUID.getJavaType().equals(fieldInfoParam.getFieldType()) && paramObj != null) {
                  ps.setString(fieldInfoParam.getFieldIndex(), paramObj.toString());
               } else {
                  ps.setObject(fieldInfoParam.getFieldIndex(), paramObj);
               }
            }
         }

         ps.executeUpdate();
         int id = 0;
         rst = ps.getGeneratedKeys();
         if (rst.next()) {
            id = rst.getInt(1);
         }

         return id;
      } catch (SQLException var14) {
         InitApi.PLUGIN.getLogger().log(Level.SEVERE, "insert 发生异常", (Throwable)var14);
      } finally {
         SqlManagerUtil.getInstance().closeSql(this.conn, ps, rst);
      }

      return 0;
   }

   @Override
   public boolean insertBatch(List<T> objList) {
      PreparedStatement ps = null;
      boolean autoCommit = true;

      try {
         String sql = this.dbSql.insertDataSql();
         MessageUtil.sendConsoleDebugMessage("insertBatch: " + sql);
         autoCommit = this.conn.getAutoCommit();
         if (autoCommit) {
            this.conn.setAutoCommit(false);
         }

         ps = this.conn.prepareStatement(sql, 1);
         Map<String, FieldInfoParam> fieldInfoParamMap = this.dbSql
            .getFieldInfoMap()
            .values()
            .stream()
            .collect(Collectors.groupingBy(FieldInfoParam::getFieldRealName, Collectors.collectingAndThen(Collectors.toList(), value -> value.get(0))));

         for (int i = 0; i < objList.size(); i++) {
            Map<String, Object> paramMap = BeanUtil.beanToMap(objList.get(i));

            for (String key : paramMap.keySet()) {
               FieldInfoParam fieldInfoParam = fieldInfoParamMap.get(key);
               if (fieldInfoParam != null) {
                  Object paramObj = paramMap.get(key);
                  if (FieldTypeEnum.UUID.getJavaType().equals(fieldInfoParam.getFieldType()) && paramObj != null) {
                     ps.setString(fieldInfoParam.getFieldIndex(), paramObj.toString());
                  } else {
                     ps.setObject(fieldInfoParam.getFieldIndex(), paramObj);
                  }
               }
            }

            ps.addBatch();
            if (i != 0 && (i + 1) % 500 == 0) {
               ps.executeBatch();
               ps.clearBatch();
            }
         }

         ps.executeBatch();
         if (autoCommit) {
            this.conn.commit();
         }

         return true;
      } catch (SQLException var17) {
         try {
            if (autoCommit) {
               this.conn.rollback();
            }
         } catch (SQLException var16) {
            throw new RuntimeException(var16);
         }

         InitApi.PLUGIN.getLogger().log(Level.SEVERE, "insertBatch 发生异常", (Throwable)var17);
      } finally {
         if (autoCommit) {
            SqlManagerUtil.getInstance().quietSetAutoCommit(this.conn, true);
         }

         SqlManagerUtil.getInstance().closeSql(this.conn, ps, null);
      }

      return false;
   }

   @Override
   public int updateById(Integer id) {
      this.dbSql.addCondition(true, "id", SqlKeyword.EQ, id);
      return this.update();
   }

   @Override
   public int update() {
      PreparedStatement ps = null;
      String sql = null;

      try {
         sql = this.dbSql.updateDataSql();
         ps = this.conn.prepareStatement(sql);
         LinkedHashMap<Integer, Object> updateFieldMap = this.dbSql.getUpdateFieldMap();

         for (Integer index : updateFieldMap.keySet()) {
            ps.setObject(index, updateFieldMap.get(index));
         }

         this.psSetVal(ps, updateFieldMap.size());
         return ps.executeUpdate();
      } catch (SQLException var9) {
         InitApi.PLUGIN.getLogger().log(Level.SEVERE, "update 发生异常", (Throwable)var9);
      } finally {
         SqlManagerUtil.getInstance().closeSql(this.conn, ps, null);
         this.printSql(sql, "update: ");
      }

      return 0;
   }

   @Override
   public int deleteById(Integer id) {
      this.dbSql.addCondition(true, "id", SqlKeyword.EQ, id);
      return this.delete();
   }

   @Override
   public int deleteBatchIds(List<Integer> ids) {
      this.dbSql.addInCondition(true, "id", SqlKeyword.IN, ids);
      return this.delete();
   }

   @Override
   public int delete() {
      PreparedStatement ps = null;
      String sql = null;

      try {
         sql = this.dbSql.deleteDataSql();
         ps = this.conn.prepareStatement(sql);
         this.psSetVal(ps);
         return ps.executeUpdate();
      } catch (SQLException var7) {
         InitApi.PLUGIN.getLogger().log(Level.SEVERE, "delete 发生异常", (Throwable)var7);
      } finally {
         SqlManagerUtil.getInstance().closeSql(this.conn, ps, null);
         this.printSql(sql, "delete: ");
      }

      return 0;
   }

   private Object specialHandling(FieldInfoParam fieldInfoParam, Object obj) {
      FieldTypeEnum fieldTypeEnum = FieldTypeEnum.getEnum(fieldInfoParam);
      switch (fieldTypeEnum) {
         case DATE:
            if (!this.isMysql) {
               String str = obj.toString();
               obj = new Date(Long.parseLong(str));
            } else if (obj instanceof LocalDateTime) {
               obj = DateUtil.toDate((LocalDateTime)obj);
            }
            break;
         case BOOLEAN:
         case BASIC_BOOLEAN:
            if (obj instanceof Integer) {
               Integer bool = (Integer)obj;
               obj = bool == 1;
            }
            break;
         case LONG:
         case BASIC_LONG:
            if (obj instanceof Integer) {
               Integer number = (Integer)obj;
               obj = number.longValue();
            }
            break;
         case BIG_DECIMAL:
            if (!(obj instanceof BigDecimal)) {
               obj = NumberUtil.isNumericToBigDecimal(String.valueOf(obj), BigDecimal.ZERO);
            }
            break;
         case UUID:
            if ("".equals(obj)) {
               obj = null;
            }

            if (obj != null) {
               obj = UUID.fromString(String.valueOf(obj));
            }
      }

      return obj;
   }

   private void addIndex(String tableName) {
      if (this.isMysql) {
         String showIndexSql = String.format("SHOW INDEX FROM %s;", tableName);
         this.printSql(showIndexSql, "showIndexSql: ");
         List<String> mysqlTableIndexList = SqlService.getInstance().getMysqlTableIndex(showIndexSql, this.storageMethod);

         for (String fieldName : this.dbSql.getFieldInfoMap().keySet()) {
            FieldInfoParam fieldInfoParam = this.dbSql.getFieldInfoMap().get(fieldName);
            if (!IndexEnum.NOT.equals(fieldInfoParam.getIndexEnum()) && !mysqlTableIndexList.contains(fieldName)) {
               String indexName;
               if (IndexEnum.UNIQUE.equals(fieldInfoParam.getIndexEnum())) {
                  indexName = "un_" + fieldName;
               } else {
                  indexName = "idx_" + fieldName;
               }

               String addIndexSql = String.format("ALTER TABLE %s ADD INDEX %s (%s);", tableName, indexName, fieldName);
               this.printSql(addIndexSql, "addIndexSql: ");
               SqlService.getInstance().executionSql(addIndexSql, this.storageMethod);
            }
         }
      }
   }

   private void psSetVal(PreparedStatement ps) throws SQLException {
      this.psSetVal(ps, 0);
   }

   private void psSetVal(PreparedStatement ps, Integer size) throws SQLException {
      if (this.dbSql.getWhereMap() != null) {
         for (Integer index : this.dbSql.getWhereMap().keySet()) {
            ps.setObject(index + size, this.dbSql.getWhereMap().get(index));
         }
      }
   }

   private void printSql(String sql, String method) {
      if (BaseConstants.DEBUG && !StrUtil.isEmpty(sql)) {
         LinkedHashMap<Integer, Object> whereMap = this.dbSql.getWhereMap();
         LinkedHashMap<Integer, Object> updateFieldMap = this.dbSql.getUpdateFieldMap();
         if (updateFieldMap != null) {
            for (Integer index : updateFieldMap.keySet()) {
               Object value = updateFieldMap.get(index);
               String replacement = value == null ? "NULL" : value.toString();
               sql = sql.replaceFirst("\\?", replacement);
            }
         }

         if (whereMap != null) {
            for (Integer index : whereMap.keySet()) {
               Object value = whereMap.get(index);
               String replacement = value == null ? "NULL" : value.toString();
               sql = sql.replaceFirst("\\?", replacement);
            }
         }

         MessageUtil.sendConsoleDebugMessage(method + sql);
      }
   }
}
