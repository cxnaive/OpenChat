package cn.handyplus.lib.internal.base.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.jetbrains.annotations.ApiStatus.Internal;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.SOURCE)
@Repeatable(XChanges.class)
@Documented
@Internal
public @interface XChange {
   String version();

   String from();

   String to();
}
