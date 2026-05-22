package cn.handyplus.lib.internal.base.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.jetbrains.annotations.ApiStatus.Internal;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.SOURCE)
@Documented
@Internal
public @interface XInfo {
   String since();

   String removedSince() default "";
}
