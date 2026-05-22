package cn.handyplus.lib.annotation;

import cn.handyplus.lib.db.IndexEnum;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface TableField {
   String value();

   String comment() default "";

   int length() default 0;

   boolean notNull() default false;

   String fieldDefault() default "";

   IndexEnum indexEnum() default IndexEnum.NOT;
}
