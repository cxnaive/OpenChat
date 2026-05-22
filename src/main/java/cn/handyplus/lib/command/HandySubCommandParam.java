package cn.handyplus.lib.command;

import java.lang.reflect.Method;
import lombok.Generated;

public class HandySubCommandParam {
   private String command;
   private String subCommand;
   private String permission;
   private Class<?> aClass;
   private Method method;
   private boolean isAsync;

   @Generated
   public String getCommand() {
      return this.command;
   }

   @Generated
   public String getSubCommand() {
      return this.subCommand;
   }

   @Generated
   public String getPermission() {
      return this.permission;
   }

   @Generated
   public Class<?> getAClass() {
      return this.aClass;
   }

   @Generated
   public Method getMethod() {
      return this.method;
   }

   @Generated
   public boolean isAsync() {
      return this.isAsync;
   }

   @Generated
   public void setCommand(String command) {
      this.command = command;
   }

   @Generated
   public void setSubCommand(String subCommand) {
      this.subCommand = subCommand;
   }

   @Generated
   public void setPermission(String permission) {
      this.permission = permission;
   }

   @Generated
   public void setAClass(Class<?> aClass) {
      this.aClass = aClass;
   }

   @Generated
   public void setMethod(Method method) {
      this.method = method;
   }

   @Generated
   public void setAsync(boolean isAsync) {
      this.isAsync = isAsync;
   }
}
