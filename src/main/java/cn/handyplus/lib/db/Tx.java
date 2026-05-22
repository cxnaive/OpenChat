package cn.handyplus.lib.db;

import java.sql.Connection;
import lombok.Generated;

public class Tx {
   private Connection conn;

   private Tx() {
   }

   public static Tx use() {
      return new Tx();
   }

   public void tx(VoidFunc<Tx> func) {
      this.tx(func, null);
   }

   public void tx(VoidFunc<Tx> func, String storageMethod) {
      try {
         this.conn = SqlManagerUtil.getInstance().getConnection(storageMethod);
         this.conn.setAutoCommit(false);

         try {
            func.call(this);
            this.conn.commit();
         } catch (Exception var8) {
            this.conn.rollback();
         } finally {
            SqlManagerUtil.getInstance().quietSetAutoCommit(this.conn, true);
            SqlManagerUtil.getInstance().closeSql(this.conn, null, null);
         }
      } catch (Throwable var10) {
         throw new RuntimeException(var10);
      }
   }

   @Generated
   public Connection getConn() {
      return this.conn;
   }
}
