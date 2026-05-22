package cn.handyplus.lib.internal.base;

import java.util.Arrays;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.ApiStatus.Internal;

public abstract class XModule<XForm extends XModule<XForm, BukkitForm>, BukkitForm> implements XBase<XForm, BukkitForm> {
   private final BukkitForm bukkitForm;
   private final String[] names;

   @Internal
   protected XModule(BukkitForm bukkitForm, String[] names) {
      this.bukkitForm = bukkitForm;
      this.names = names;
   }

   @NotNull
   @Override
   public final String name() {
      return this.names[0];
   }

   @Experimental
   protected void setEnumName(XRegistry<XForm, BukkitForm> registry, String enumName) {
      if (this.names[0] != null) {
         throw new IllegalStateException("Enum name already set " + enumName + " -> " + Arrays.toString((Object[])this.names));
      } else {
         this.names[0] = enumName;
         BukkitForm newForm = registry.getBukkit(this.names);
         if (this.bukkitForm != newForm) {
            registry.std((XForm)this);
         }
      }
   }

   @Internal
   @Override
   public String[] getNames() {
      return this.names;
   }

   @Nullable
   @Override
   public final BukkitForm get() {
      return this.bukkitForm;
   }

   @Override
   public final String toString() {
      return (this.isSupported() ? "" : "!") + this.getClass().getSimpleName() + '(' + this.name() + ')';
   }

   @Override
   public final int hashCode() {
      return super.hashCode();
   }

   @Deprecated
   @Override
   public final boolean equals(Object obj) {
      return super.equals(obj);
   }
}
