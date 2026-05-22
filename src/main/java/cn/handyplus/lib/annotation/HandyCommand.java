package cn.handyplus.lib.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.bukkit.permissions.PermissionDefault;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface HandyCommand {
   String name();

   String permission() default "";

   String[] aliases() default {""};

   String description() default "";

   String permissionMessage() default "§4你没有权限执行该命令";

   String usage() default "";

   PermissionDefault PERMISSION_DEFAULT() default PermissionDefault.OP;
}
