package cn.handyplus.lib.db;

public class UpdateCondition<T> {
   private final DbSql dbSql;

   protected UpdateCondition(DbSql dbSql) {
      this.dbSql = dbSql;
   }

   public <R> UpdateCondition<T> set(DbFunction<R, ?> fn, Object val) {
      return this.set(true, fn, val);
   }

   public <R> UpdateCondition<T> set(boolean condition, DbFunction<R, ?> fn, Object val) {
      this.dbSql.updateCondition(condition, DbColumnUtil.getFieldName(fn), val);
      return this;
   }

   public <R> UpdateCondition<T> subtract(DbFunction<R, ?> fn, DbFunction<R, ?> calculateFieldName, Object val) {
      return this.subtract(true, fn, calculateFieldName, val);
   }

   public <R> UpdateCondition<T> subtract(boolean condition, DbFunction<R, ?> fn, DbFunction<R, ?> calculateFieldName, Object val) {
      this.dbSql.updateCondition(condition, DbColumnUtil.getFieldName(fn), DbColumnUtil.getFieldName(calculateFieldName), " - ", val);
      return this;
   }

   public <R> UpdateCondition<T> add(DbFunction<R, ?> fn, DbFunction<R, ?> calculateFieldName, Object val) {
      return this.add(true, fn, calculateFieldName, val);
   }

   public <R> UpdateCondition<T> add(boolean condition, DbFunction<R, ?> fn, DbFunction<R, ?> calculateFieldName, Object val) {
      this.dbSql.updateCondition(condition, DbColumnUtil.getFieldName(fn), DbColumnUtil.getFieldName(calculateFieldName), " + ", val);
      return this;
   }
}
