package cn.handyplus.lib.db;

import java.util.List;

public class Compare<T> {
   private final DbSql dbSql;

   protected Compare(DbSql dbSql) {
      this.dbSql = dbSql;
   }

   public <R> Compare<T> eq(DbFunction<R, ?> fn, Object val) {
      return this.eq(true, fn, val);
   }

   public <R> Compare<T> eq(DbFunction<R, ?> fn, DbFunction<R, ?> compareFn) {
      return this.eq(true, fn, compareFn);
   }

   public <R> Compare<T> eq(boolean condition, DbFunction<R, ?> fn, Object val) {
      this.dbSql.addCondition(condition, DbColumnUtil.getFieldName(fn), SqlKeyword.EQ, val);
      return this;
   }

   public <R> Compare<T> eq(boolean condition, DbFunction<R, ?> fn, DbFunction<R, ?> compareFn) {
      this.dbSql.addCondition(condition, DbColumnUtil.getFieldName(fn), SqlKeyword.EQ, DbColumnUtil.getFieldName(compareFn));
      return this;
   }

   public <R> Compare<T> ne(DbFunction<R, ?> fn, Object val) {
      return this.ne(true, fn, val);
   }

   public <R> Compare<T> ne(DbFunction<R, ?> fn, DbFunction<R, ?> compareFn) {
      return this.ne(true, fn, compareFn);
   }

   public <R> Compare<T> ne(boolean condition, DbFunction<R, ?> fn, Object val) {
      this.dbSql.addCondition(condition, DbColumnUtil.getFieldName(fn), SqlKeyword.NE, val);
      return this;
   }

   public <R> Compare<T> ne(boolean condition, DbFunction<R, ?> fn, DbFunction<R, ?> compareFn) {
      this.dbSql.addCondition(condition, DbColumnUtil.getFieldName(fn), SqlKeyword.NE, DbColumnUtil.getFieldName(compareFn));
      return this;
   }

   public <R> Compare<T> gt(DbFunction<R, ?> fn, Object val) {
      return this.gt(true, fn, val);
   }

   public <R> Compare<T> gt(DbFunction<R, ?> fn, DbFunction<R, ?> compareFn) {
      return this.gt(true, fn, compareFn);
   }

   public <R> Compare<T> gt(boolean condition, DbFunction<R, ?> fn, Object val) {
      this.dbSql.addCondition(condition, DbColumnUtil.getFieldName(fn), SqlKeyword.GT, val);
      return this;
   }

   public <R> Compare<T> gt(boolean condition, DbFunction<R, ?> fn, DbFunction<R, ?> compareFn) {
      this.dbSql.addCondition(condition, DbColumnUtil.getFieldName(fn), SqlKeyword.GT, DbColumnUtil.getFieldName(compareFn));
      return this;
   }

   public <R> Compare<T> gt(DbFunction<R, ?> fn, Object val, DbFunction<R, ?> compareFn) {
      return this.gt(true, fn, val, compareFn);
   }

   public <R> Compare<T> gt(boolean condition, DbFunction<R, ?> fn, Object val, DbFunction<R, ?> compareFn) {
      this.dbSql.addCondition(condition, DbColumnUtil.getFieldName(fn), val, SqlKeyword.GT, DbColumnUtil.getFieldName(compareFn));
      return this;
   }

   public <R> Compare<T> lt(DbFunction<R, ?> fn, Object val) {
      return this.lt(true, fn, val);
   }

   public <R> Compare<T> lt(DbFunction<R, ?> fn, DbFunction<R, ?> compareFn) {
      return this.lt(true, fn, compareFn);
   }

   public <R> Compare<T> lt(DbFunction<R, ?> fn, Object val, DbFunction<R, ?> compareFn) {
      return this.lt(true, fn, val, compareFn);
   }

   public <R> Compare<T> lt(boolean condition, DbFunction<R, ?> fn, Object val) {
      this.dbSql.addCondition(condition, DbColumnUtil.getFieldName(fn), SqlKeyword.LT, val);
      return this;
   }

   public <R> Compare<T> lt(boolean condition, DbFunction<R, ?> fn, DbFunction<R, ?> compareFn) {
      this.dbSql.addCondition(condition, DbColumnUtil.getFieldName(fn), SqlKeyword.LT, DbColumnUtil.getFieldName(compareFn));
      return this;
   }

   public <R> Compare<T> lt(boolean condition, DbFunction<R, ?> fn, Object val, DbFunction<R, ?> compareFn) {
      this.dbSql.addCondition(condition, DbColumnUtil.getFieldName(fn), val, SqlKeyword.LT, DbColumnUtil.getFieldName(compareFn));
      return this;
   }

   public <R> Compare<T> ge(DbFunction<R, ?> fn, Object val) {
      return this.ge(true, fn, val);
   }

   public <R> Compare<T> ge(DbFunction<R, ?> fn, DbFunction<R, ?> compareFn) {
      return this.ge(true, fn, compareFn);
   }

   public <R> Compare<T> ge(boolean condition, DbFunction<R, ?> fn, Object val) {
      this.dbSql.addCondition(condition, DbColumnUtil.getFieldName(fn), SqlKeyword.GE, val);
      return this;
   }

   public <R> Compare<T> ge(boolean condition, DbFunction<R, ?> fn, DbFunction<R, ?> compareFn) {
      this.dbSql.addCondition(condition, DbColumnUtil.getFieldName(fn), SqlKeyword.GE, DbColumnUtil.getFieldName(compareFn));
      return this;
   }

   public <R> Compare<T> ge(DbFunction<R, ?> fn, Object val, DbFunction<R, ?> compareFn) {
      return this.ge(true, fn, val, compareFn);
   }

   public <R> Compare<T> ge(boolean condition, DbFunction<R, ?> fn, Object val, DbFunction<R, ?> compareFn) {
      this.dbSql.addCondition(condition, DbColumnUtil.getFieldName(fn), val, SqlKeyword.GE, DbColumnUtil.getFieldName(compareFn));
      return this;
   }

   public <R> Compare<T> ge(DbFunction<R, ?> fn, Object val1, Object val2) {
      return this.ge(true, fn, val1, val2);
   }

   public <R> Compare<T> ge(boolean condition, DbFunction<R, ?> fn, Object val1, Object val2) {
      this.dbSql.addCondition(condition, DbColumnUtil.getFieldName(fn), val1, SqlKeyword.GE, val2);
      return this;
   }

   public <R> Compare<T> le(DbFunction<R, ?> fn, Object val) {
      return this.le(true, fn, val);
   }

   public <R> Compare<T> le(DbFunction<R, ?> fn, DbFunction<R, ?> compareFn) {
      return this.le(true, fn, compareFn);
   }

   public <R> Compare<T> le(boolean condition, DbFunction<R, ?> fn, Object val) {
      this.dbSql.addCondition(condition, DbColumnUtil.getFieldName(fn), SqlKeyword.LE, val);
      return this;
   }

   public <R> Compare<T> le(boolean condition, DbFunction<R, ?> fn, DbFunction<R, ?> compareFn) {
      this.dbSql.addCondition(condition, DbColumnUtil.getFieldName(fn), SqlKeyword.LE, DbColumnUtil.getFieldName(compareFn));
      return this;
   }

   public <R> Compare<T> le(DbFunction<R, ?> fn, Object val, DbFunction<R, ?> compareFn) {
      return this.le(true, fn, val, compareFn);
   }

   public <R> Compare<T> le(boolean condition, DbFunction<R, ?> fn, Object val, DbFunction<R, ?> compareFn) {
      this.dbSql.addCondition(condition, DbColumnUtil.getFieldName(fn), val, SqlKeyword.LE, DbColumnUtil.getFieldName(compareFn));
      return this;
   }

   public <R> Compare<T> le(DbFunction<R, ?> fn, Object val1, Object val2) {
      return this.le(true, fn, val1, val2);
   }

   public <R> Compare<T> le(boolean condition, DbFunction<R, ?> fn, Object val1, Object val2) {
      this.dbSql.addCondition(condition, DbColumnUtil.getFieldName(fn), val1, SqlKeyword.LE, val2);
      return this;
   }

   public <R> Compare<T> like(DbFunction<R, ?> fn, Object val) {
      return this.like(true, fn, val);
   }

   public <R> Compare<T> like(boolean condition, DbFunction<R, ?> fn, Object val) {
      this.dbSql.addLikeCondition(condition, DbColumnUtil.getFieldName(fn), SqlKeyword.LIKE, "%" + val + "%");
      return this;
   }

   public <R> Compare<T> notLike(DbFunction<R, ?> fn, Object val) {
      return this.notLike(true, fn, val);
   }

   public <R> Compare<T> notLike(boolean condition, DbFunction<R, ?> fn, Object val) {
      this.dbSql.addLikeCondition(condition, DbColumnUtil.getFieldName(fn), SqlKeyword.NOT_LIKE, "%" + val + "%");
      return this;
   }

   public <R> Compare<T> likeLeft(DbFunction<R, ?> fn, Object val) {
      return this.likeLeft(true, fn, val);
   }

   public <R> Compare<T> likeLeft(boolean condition, DbFunction<R, ?> fn, Object val) {
      this.dbSql.addLikeCondition(condition, DbColumnUtil.getFieldName(fn), SqlKeyword.LIKE, "%" + val);
      return this;
   }

   public <R> Compare<T> likeRight(DbFunction<R, ?> fn, Object val) {
      return this.likeRight(true, fn, val);
   }

   public <R> Compare<T> likeRight(boolean condition, DbFunction<R, ?> fn, Object val) {
      this.dbSql.addLikeCondition(condition, DbColumnUtil.getFieldName(fn), SqlKeyword.LIKE, val + "%");
      return this;
   }

   public <R> Compare<T> in(DbFunction<R, ?> fn, List<?> in) {
      return this.in(true, fn, in);
   }

   public <R> Compare<T> in(boolean condition, DbFunction<R, ?> fn, List<?> in) {
      this.dbSql.addInCondition(condition, DbColumnUtil.getFieldName(fn), SqlKeyword.IN, in);
      return this;
   }

   public <R> Compare<T> notIn(DbFunction<R, ?> fn, List<?> in) {
      return this.notIn(true, fn, in);
   }

   public <R> Compare<T> notIn(boolean condition, DbFunction<R, ?> fn, List<?> in) {
      this.dbSql.addInCondition(condition, DbColumnUtil.getFieldName(fn), SqlKeyword.NOT_IN, in);
      return this;
   }

   public <R> Compare<T> orderByAsc(DbFunction<R, ?> fn) {
      return this.orderByAsc(true, fn);
   }

   public <R> Compare<T> orderByAsc(boolean condition, DbFunction<R, ?> fn) {
      return this.orderByAsc(condition, DbColumnUtil.getFieldName(fn));
   }

   public <R> Compare<T> orderByAsc(boolean condition, String fieldName) {
      this.dbSql.addOrderByCondition(condition, fieldName, SqlKeyword.ASC);
      return this;
   }

   public <R> Compare<T> orderByAsc(boolean condition, DbFunction<R, ?> fn, DbFunction<R, ?> fnTwo) {
      this.dbSql.addOrderByCondition(condition, DbColumnUtil.getFieldName(fn), DbColumnUtil.getFieldName(fnTwo), SqlKeyword.ASC);
      return this;
   }

   public <R> Compare<T> orderByDesc(DbFunction<R, ?> fn) {
      return this.orderByDesc(true, fn);
   }

   public <R> Compare<T> orderByDesc(boolean condition, DbFunction<R, ?> fn) {
      return this.orderByDesc(condition, DbColumnUtil.getFieldName(fn));
   }

   public <R> Compare<T> orderByDesc(boolean condition, String fieldName) {
      this.dbSql.addOrderByCondition(condition, fieldName, SqlKeyword.DESC);
      return this;
   }

   public <R> Compare<T> orderByDesc(boolean condition, DbFunction<R, ?> fn, DbFunction<R, ?> fnTwo) {
      this.dbSql.addOrderByCondition(condition, DbColumnUtil.getFieldName(fn), DbColumnUtil.getFieldName(fnTwo), SqlKeyword.DESC);
      return this;
   }

   public Compare<T> orderByRand() {
      return this.orderByRand(true);
   }

   public Compare<T> orderByRand(boolean condition) {
      this.dbSql.addOrderByRand(condition);
      return this;
   }

   public <R> Compare<T> isNull(DbFunction<R, ?> fn) {
      return this.isNull(true, fn);
   }

   public <R> Compare<T> isNull(boolean condition, DbFunction<R, ?> fn) {
      this.dbSql.addNull(condition, DbColumnUtil.getFieldName(fn), SqlKeyword.IS_NULL);
      return this;
   }

   public <R> Compare<T> isNotNull(DbFunction<R, ?> fn) {
      return this.isNotNull(true, fn);
   }

   public <R> Compare<T> isNotNull(boolean condition, DbFunction<R, ?> fn) {
      this.dbSql.addNull(condition, DbColumnUtil.getFieldName(fn), SqlKeyword.IS_NOT_NULL);
      return this;
   }

   public <R> Compare<T> groupBy(DbFunction<R, ?> fn) {
      return this.groupBy(true, fn);
   }

   public <R> Compare<T> groupBy(boolean condition, DbFunction<R, ?> fn) {
      this.dbSql.addGroupByCondition(condition, DbColumnUtil.getFieldName(fn));
      return this;
   }

   public <R> Compare<T> limit(int pageNo, int pageSize) {
      return this.limit(true, pageNo, pageSize);
   }

   public <R> Compare<T> limit(boolean condition, int pageNo, int pageSize) {
      this.dbSql.addLimitCondition(condition, pageNo, pageSize);
      return this;
   }
}
