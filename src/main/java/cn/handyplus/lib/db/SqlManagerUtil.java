package cn.handyplus.lib.db;

import cn.handyplus.lib.InitApi;
import cn.handyplus.lib.constants.BaseConstants;
import cn.handyplus.lib.core.StrUtil;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import cn.handyplus.lib.util.HandyConfigUtil;
import cn.handyplus.lib.util.MessageUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

public class SqlManagerUtil {
   private static final SqlManagerUtil INSTANCE = new SqlManagerUtil();
   public static final String STORAGE_METHOD = "storage-method";
   private HikariDataSource ds;

   private SqlManagerUtil() {
   }

   public static SqlManagerUtil getInstance() {
      return INSTANCE;
   }

   public void enableSql() {
      this.enableSql(null);
   }

   public void enableSql(String storageMethod) {
      BaseConstants.STORAGE_CONFIG = HandyConfigUtil.load("storage.yml");
      this.close();
      this.enableTable(storageMethod);
   }

   private void enableTable(String storageMethod) {
      if (StrUtil.isEmpty(storageMethod)) {
         storageMethod = this.getStorageMethod();
      }

      HikariConfig hikariConfig = new HikariConfig();
      hikariConfig.setPoolName((InitApi.PLUGIN != null ? InitApi.PLUGIN.getName() : "HandyLib") + "HikariPool");
      if (DbTypeEnum.MySQL.getType().equalsIgnoreCase(storageMethod)) {
         String host = BaseConstants.STORAGE_CONFIG.getString("MySQL.Host");
         String database = BaseConstants.STORAGE_CONFIG.getString("MySQL.Database");
         int port = BaseConstants.STORAGE_CONFIG.getInt("MySQL.Port");
         String useSsl = BaseConstants.STORAGE_CONFIG.getString("MySQL.UseSSL");
         String param = BaseConstants.STORAGE_CONFIG.getString("MySQL.param", "");
         String jdbcUrl = "jdbc:mysql://"
            + host
            + ":"
            + port
            + "/"
            + database
            + "?useSSL="
            + useSsl
            + "&useUnicode=true&characterEncoding=UTF-8&rewriteBatchedStatements=true"
            + param;
         hikariConfig.setJdbcUrl(jdbcUrl);
         hikariConfig.setUsername(BaseConstants.STORAGE_CONFIG.getString("MySQL.User"));
         hikariConfig.setPassword(BaseConstants.STORAGE_CONFIG.getString("MySQL.Password"));
         this.setCustomConfig(hikariConfig);
      } else {
         String jdbcUrl;
         if (InitApi.PLUGIN != null) {
            jdbcUrl = "jdbc:sqlite:" + InitApi.PLUGIN.getDataFolder().getAbsolutePath() + "/" + InitApi.PLUGIN.getName() + ".db";
         } else {
            jdbcUrl = "jdbc:sqlite:HandyLib.db";
         }

         hikariConfig.setDriverClassName("org.sqlite.JDBC");
         hikariConfig.setConnectionTestQuery("SELECT 1");
         hikariConfig.setJdbcUrl(jdbcUrl);
         hikariConfig.setMaximumPoolSize(1);
         hikariConfig.setMinimumIdle(1);
      }

      this.ds = new HikariDataSource(hikariConfig);
   }

   protected Connection getConnection(String storageMethod) {
      if (StrUtil.isEmpty(storageMethod)) {
         storageMethod = this.getStorageMethod();
      }

      if (this.ds == null || this.ds.isClosed()) {
         MessageUtil.sendConsoleDebugMessage(" HikariDataSource 链接异常关闭，重新打开");
         this.enableTable(storageMethod);
      }

      try {
         return this.ds.getConnection();
      } catch (SQLException var3) {
         InitApi.PLUGIN.getLogger().log(Level.SEVERE, "getConnection 发生异常", (Throwable)var3);
         throw new RuntimeException(var3);
      }
   }

   protected void quietSetAutoCommit(Connection conn, Boolean autoCommit) {
      if (null != conn && null != autoCommit) {
         try {
            conn.setAutoCommit(autoCommit);
         } catch (Exception var4) {
            InitApi.PLUGIN.getLogger().log(Level.SEVERE, "quietSetAutoCommit 发生异常", (Throwable)var4);
         }
      }
   }

   protected void closeSql(Connection conn, PreparedStatement ps, ResultSet rst) {
      try {
         if (rst != null) {
            rst.close();
         }

         if (ps != null) {
            ps.close();
         }

         if (conn != null && conn.getAutoCommit()) {
            conn.close();
         }
      } catch (SQLException var5) {
         InitApi.PLUGIN.getLogger().log(Level.SEVERE, "closeSql 发生异常", (Throwable)var5);
      }
   }

   public void close() {
      if (this.ds != null) {
         this.ds.close();
      }
   }

   protected String getStorageMethod() {
      if (BaseConstants.STORAGE_CONFIG == null) {
         return DbTypeEnum.SQLite.getType();
      } else {
         String storageMethod = BaseConstants.STORAGE_CONFIG.getString("storage-method");
         return DbTypeEnum.MySQL.getType().equalsIgnoreCase(storageMethod) ? DbTypeEnum.MySQL.getType() : DbTypeEnum.SQLite.getType();
      }
   }

   private void setCustomConfig(HikariConfig hikariConfig) {
      int maximumPoolSize = BaseConstants.STORAGE_CONFIG.getInt("MySQL.maximumPoolSize");
      if (maximumPoolSize > 0) {
         hikariConfig.setMaximumPoolSize(maximumPoolSize);
      }

      int minimumIdle = BaseConstants.STORAGE_CONFIG.getInt("MySQL.minimumIdle");
      if (minimumIdle > 0) {
         hikariConfig.setMinimumIdle(minimumIdle);
      }

      int connectionTimeout = BaseConstants.STORAGE_CONFIG.getInt("MySQL.connectionTimeout");
      if (connectionTimeout > 0) {
         hikariConfig.setConnectionTimeout(connectionTimeout);
      }
   }
}
