package cn.handyplus.lib.annotation;

import cn.handyplus.lib.constants.VersionCheckEnum;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface HandyListener {
   VersionCheckEnum version() default VersionCheckEnum.V_1_6;
}
