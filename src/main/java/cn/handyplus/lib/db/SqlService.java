package cn.handyplus.lib.db;

import cn.handyplus.lib.InitApi;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class SqlService {
   private SqlService() {
   }

   public static SqlService getInstance() {
      return SqlService.SingletonHolder.INSTANCE;
   }

   protected void executionSql(String sql, String storageMethod) {
      Connection conn = null;
      PreparedStatement ps = null;

      try {
         conn = SqlManagerUtil.getInstance().getConnection(storageMethod);
         ps = conn.prepareStatement(sql);
         ps.executeUpdate();
      } catch (SQLException var9) {
         InitApi.PLUGIN.getLogger().log(Level.SEVERE, "executionSql 发生异常", (Throwable)var9);
      } finally {
         SqlManagerUtil.getInstance().closeSql(conn, ps, null);
      }
   }

   protected List<String> getTableInfo(String sql, String storageMethod) {
      Connection conn = null;
      PreparedStatement ps = null;
      ResultSet rst = null;
      List<String> fieldNameList = new ArrayList<>();

      try {
         conn = SqlManagerUtil.getInstance().getConnection(storageMethod);
         ps = conn.prepareStatement(sql);
         rst = ps.executeQuery();

         while (rst.next()) {
            String fieId;
            if (DbTypeEnum.SQLite.getType().equalsIgnoreCase(storageMethod)) {
               fieId = rst.getString("name");
            } else {
               fieId = rst.getString("column_name");
            }

            fieldNameList.add(fieId);
         }
      } catch (SQLException var11) {
         InitApi.PLUGIN.getLogger().log(Level.SEVERE, "getTableInfo 发生异常", (Throwable)var11);
      } finally {
         SqlManagerUtil.getInstance().closeSql(conn, ps, rst);
      }

      return fieldNameList;
   }

   protected List<String> getMysqlTableIndex(String sql, String storageMethod) {
      Connection conn = null;
      PreparedStatement ps = null;
      ResultSet rst = null;
      List<String> columnNameList = new ArrayList<>();

      try {
         conn = SqlManagerUtil.getInstance().getConnection(storageMethod);
         ps = conn.prepareStatement(sql);
         rst = ps.executeQuery();

         while (rst.next()) {
            String columnName = rst.getString("Column_name");
            columnNameList.add(columnName);
         }
      } catch (SQLException var11) {
         InitApi.PLUGIN.getLogger().log(Level.SEVERE, "getMysqlTableIndex 发生异常", (Throwable)var11);
      } finally {
         SqlManagerUtil.getInstance().closeSql(conn, ps, rst);
      }

      return columnNameList;
   }

   public Map<String, Object> selectMap(String sql) {
      return this.selectMap(sql, SqlManagerUtil.getInstance().getStorageMethod());
   }

   public Map<String, Object> selectMap(String sql, String storageMethod) {
      Connection conn = null;
      PreparedStatement ps = null;
      ResultSet rst = null;
      Map<String, Object> map = new HashMap<>();

      try {
         conn = SqlManagerUtil.getInstance().getConnection(storageMethod);
         ps = conn.prepareStatement(sql);
         rst = ps.executeQuery();
         ResultSetMetaData rstMetaData = rst.getMetaData();
         if (rst.next()) {
            for (int i = 1; i <= rstMetaData.getColumnCount(); i++) {
               map.put(rstMetaData.getColumnLabel(i), rst.getObject(i));
            }
         }
      } catch (SQLException var12) {
         InitApi.PLUGIN.getLogger().log(Level.SEVERE, "selectMap 发生异常", (Throwable)var12);
      } finally {
         SqlManagerUtil.getInstance().closeSql(conn, ps, rst);
      }

      return map;
   }

   public List<Map<String, Object>> selectListMap(String sql) {
      return this.selectListMap(sql, SqlManagerUtil.getInstance().getStorageMethod());
   }

   public List<Map<String, Object>> selectListMap(String sql, String storageMethod) {
      Connection conn = null;
      PreparedStatement ps = null;
      ResultSet rst = null;
      List<Map<String, Object>> list = new ArrayList<>();

      try {
         conn = SqlManagerUtil.getInstance().getConnection(storageMethod);
         ps = conn.prepareStatement(sql);
         rst = ps.executeQuery();
         ResultSetMetaData rstMetaData = rst.getMetaData();

         while (rst.next()) {
            Map<String, Object> map = new HashMap<>();

            for (int i = 1; i <= rstMetaData.getColumnCount(); i++) {
               map.put(rstMetaData.getColumnName(i), rst.getObject(i));
            }

            list.add(map);
         }
      } catch (SQLException var13) {
         InitApi.PLUGIN.getLogger().log(Level.SEVERE, "selectListMap 发生异常", (Throwable)var13);
      } finally {
         SqlManagerUtil.getInstance().closeSql(conn, ps, rst);
      }

      return list;
   }

   private static class SingletonHolder {
      private static final SqlService INSTANCE = new SqlService();
   }
}
