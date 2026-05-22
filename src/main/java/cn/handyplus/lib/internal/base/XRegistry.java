package cn.handyplus.lib.internal.base;

import cn.handyplus.lib.internal.XBiome;
import cn.handyplus.lib.internal.XEnchantment;
import cn.handyplus.lib.internal.XEntityType;
import cn.handyplus.lib.internal.XPotion;
import cn.handyplus.lib.internal.XSound;
import cn.handyplus.lib.internal.base.annotations.XChange;
import cn.handyplus.lib.internal.base.annotations.XInfo;
import cn.handyplus.lib.internal.base.annotations.XMerge;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class XRegistry<XForm extends XBase<XForm, BukkitForm>, BukkitForm> implements Iterable<XForm> {
   @Internal
   private static boolean PERFORM_AUTO_ADD = true;
   @Internal
   private static boolean DISCARD_METADATA = true;
   private static final boolean KEYED_EXISTS;
   private static final Map<Class<? extends XBase<?, ?>>, XRegistry<?, ?>> REGISTRIES;
   private static boolean ensureLoaded;
   private final Map<String, XForm> nameMappings = new HashMap<>(20);
   private final Map<BukkitForm, XForm> bukkitToX = new IdentityHashMap<>(20);
   private Map<XForm, XModuleMetadata> metadata;
   private Map<XForm, Field> backingFields;
   private final Class<BukkitForm> bukkitFormClass;
   private final Class<XForm> xFormClass;
   private final Supplier<Object> registrySupplier;
   private final BiFunction<BukkitForm, String[], XForm> creator;
   private final Function<Integer, XForm[]> createArray;
   private final String registryName;
   private final boolean supportsRegistry;
   private final XRegistry.ClassType bukkitClassType;
   private boolean pulled = false;
   private boolean alreadyDiscardedMetadata = false;

   private static void ensureLoadedRegistries() {
      if (!ensureLoaded) {
         XSound.REGISTRY.getClass();
         XBiome.REGISTRY.getClass();
         XPotion.REGISTRY.getClass();
         XEntityType.REGISTRY.getClass();
         XEnchantment.REGISTRY.getClass();
         ensureLoaded = true;
      }
   }

   @Nullable
   @Experimental
   public static XRegistry<?, ?> rawRegistryOf(Class<?> clazz) {
      ensureLoadedRegistries();
      return REGISTRIES.get(clazz);
   }

   @Nullable
   @Experimental
   public static <XForm extends XBase<XForm, BukkitForm>, BukkitForm> XRegistry<XForm, BukkitForm> registryOf(Class<? extends XForm> clazz) {
      ensureLoadedRegistries();
      return (XRegistry<XForm, BukkitForm>)REGISTRIES.get(clazz);
   }

   protected static <XForm extends XBase<XForm, BukkitForm>, BukkitForm> void registerModule(
      XRegistry<XForm, BukkitForm> registry, Class<? extends XForm> clazz
   ) {
      REGISTRIES.put(clazz, registry);
   }

   @Internal
   public XRegistry(
      Class<BukkitForm> bukkitFormClass,
      Class<XForm> xFormClass,
      Supplier<Object> registrySupplier,
      BiFunction<BukkitForm, String[], XForm> creator,
      Function<Integer, XForm[]> createArray
   ) {
      boolean supported;
      try {
         registrySupplier.get();
         supported = true;
      } catch (Throwable var8) {
         supported = false;
      }

      this.bukkitFormClass = Objects.requireNonNull(bukkitFormClass);
      this.xFormClass = Objects.requireNonNull(xFormClass);
      this.registryName = this.bukkitFormClass.getSimpleName();
      this.registrySupplier = registrySupplier;
      this.createArray = Objects.requireNonNull(createArray);
      this.creator = creator;
      this.supportsRegistry = supported;
      if (bukkitFormClass.isEnum()) {
         this.bukkitClassType = XRegistry.ClassType.ENUM;
      } else if (Modifier.isAbstract(bukkitFormClass.getModifiers())) {
         this.bukkitClassType = XRegistry.ClassType.ABSTRACTION;
      } else {
         this.bukkitClassType = null;
      }

      if (!this.supportsRegistry && this.bukkitClassType == null) {
         throw new IllegalStateException("Bukkit form is not an enum, abstraction or a registry " + bukkitFormClass);
      } else {
         registerModule(this, xFormClass);
      }
   }

   @Internal
   public XRegistry(Class<BukkitForm> bukkitFormClass, Class<XForm> xFormClass, Function<Integer, XForm[]> createArray) {
      this(bukkitFormClass, xFormClass, null, null, createArray);
   }

   @Internal
   @NotNull
   public Map<String, XForm> nameMapping() {
      return this.nameMappings;
   }

   @Internal
   @NotNull
   public Map<BukkitForm, XForm> bukkitMapping() {
      return this.bukkitToX;
   }

   public Class<BukkitForm> getBukkitFormClass() {
      return this.bukkitFormClass;
   }

   public Class<XForm> getXFormClass() {
      return this.xFormClass;
   }

   public String getName() {
      return this.registryName;
   }

   private void pullValues() {
      if (!this.pulled) {
         this.pulled = true;
         if (this.creator == null) {
            return;
         }

         this.pullFieldNames();
         if (PERFORM_AUTO_ADD) {
            this.pullSystemValues();
         }
      }
   }

   private static <T> void processEnumLikeFields(Class<T> clazz, BiConsumer<Field, T> consumer) {
      for (Field field : clazz.getDeclaredFields()) {
         int modifiers = field.getModifiers();
         if (field.getType() == clazz && Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers)) {
            try {
               consumer.accept(field, (T)field.get(null));
            } catch (IllegalAccessException var8) {
               throw new IllegalStateException("Cannot process enum-like fields of: " + clazz, var8);
            }
         }
      }
   }

   @Internal
   public void registerName(String name, XForm xForm) {
      this.nameMappings.put(normalizeName(name), xForm);
   }

   private void pullFieldNames() {
      processEnumLikeFields(this.xFormClass, (field, x) -> this.registerMerged(x, field));
   }

   private void pullSystemValues() {
      if (this.bukkitClassType == XRegistry.ClassType.ENUM) {
         for (BukkitForm bukkitForm : this.bukkitFormClass.getEnumConstants()) {
            this.std(((Enum)bukkitForm).name(), bukkitForm);
         }
      } else {
         processEnumLikeFields(this.bukkitFormClass, (field, bukkit) -> {
            if (bukkit != null) {
               this.std(field.getName(), bukkit);
            }
         });
      }

      if (this.supportsRegistry) {
         for (Keyed bukkitForm : this.bukkitRegistry()) {
            this.std((BukkitForm)bukkitForm);
         }
      }
   }

   private BukkitForm valueOf(String name) {
      name = name.toUpperCase(Locale.ENGLISH).replace('.', '_');
      Class<? extends Enum> clazz = (Class<? extends Enum>) this.bukkitFormClass;

      try {
         return (BukkitForm) Enum.valueOf(clazz, name);
      } catch (IllegalArgumentException var4) {
         return null;
      }
   }

   private BukkitForm fieldOf(String name) {
      try {
         return (BukkitForm)this.bukkitFormClass.getDeclaredField(name).get(null);
      } catch (NoSuchFieldException | IllegalAccessException var3) {
         return null;
      }
   }

   @NotNull
   private Registry<?> bukkitRegistry() {
      return (Registry<?>)this.registrySupplier.get();
   }

   @Nullable
   protected BukkitForm getBukkit(String[] names) {
      for (String name : names) {
         BukkitForm bukkitForm;
         if (this.supportsRegistry) {
            name = name.toLowerCase(Locale.ENGLISH);
            NamespacedKey key;
            if (name.contains(":")) {
               key = XNamespacedKey.fromString(name);
            } else {
               key = NamespacedKey.minecraft(name);
            }

            Keyed bukkit = this.bukkitRegistry().get(key);
            if (bukkit != null) {
               bukkitForm = (BukkitForm)bukkit;
            } else {
               bukkitForm = null;
            }
         } else if (this.bukkitClassType == XRegistry.ClassType.ENUM) {
            bukkitForm = this.valueOf(name);
         } else {
            if (this.bukkitClassType != XRegistry.ClassType.ABSTRACTION) {
               throw new AssertionError("None of the class strategies worked for " + this);
            }

            bukkitForm = this.fieldOf(name);
         }

         if (bukkitForm != null) {
            return bukkitForm;
         }
      }

      return null;
   }

   @Internal
   public void discardMetadata() {
      if (DISCARD_METADATA) {
         this.backingFields = null;
         this.metadata = null;
      }
   }

   @NotNull
   public @Unmodifiable Collection<XForm> getValues() {
      this.pullValues();
      return Collections.unmodifiableCollection(this.bukkitToX.values());
   }

   @Deprecated
   public XForm[] values() {
      this.pullValues();
      Collection<XForm> values = this.bukkitToX.values();
      return (XForm[])values.toArray((XBase[])this.createArray.apply(values.size()));
   }

   @NotNull
   @Override
   public Iterator<XForm> iterator() {
      return this.getValues().iterator();
   }

   @NotNull
   public XForm getByBukkitForm(BukkitForm bukkit) {
      Objects.requireNonNull(bukkit, () -> "Cannot match null " + this.registryName);
      XForm mapping = this.bukkitToX.get(bukkit);
      if (mapping == null) {
         if (!PERFORM_AUTO_ADD) {
            throw new UnsupportedOperationException("Unknown standard bukkit form (no auto-add) for " + this.registryName + ": " + bukkit);
         }

         if (this.creator == null) {
            throw new UnsupportedOperationException("Unsupported value for " + this.registryName + ": " + bukkit);
         }

         XForm xForm = this.std(bukkit);
         if (xForm == null) {
            throw new IllegalStateException("Unknown " + this.registryName + ": " + bukkit);
         }
      }

      return mapping;
   }

   public Optional<XForm> getByName(@NotNull String name) {
      Objects.requireNonNull(name, () -> "Cannot match null " + this.registryName);
      if (name.isEmpty()) {
         return Optional.empty();
      } else {
         this.pullValues();
         return Optional.ofNullable(this.nameMappings.get(normalizeName(name)));
      }
   }

   @Internal
   @NotNull
   public static String getBukkitName(@NotNull Object bukkitForm) {
      Objects.requireNonNull(bukkitForm, "Cannot get name of a null bukkit form");
      if (bukkitForm instanceof Enum) {
         return ((Enum)bukkitForm).name();
      } else if (KEYED_EXISTS && bukkitForm instanceof Keyed) {
         return ((Keyed)bukkitForm).getKey().toString();
      } else if (bukkitForm instanceof PotionEffectType) {
         return ((PotionEffectType)bukkitForm).getName();
      } else if (bukkitForm instanceof Enchantment) {
         return ((Enchantment)bukkitForm).getName();
      } else {
         throw new AssertionError("Unknown xform type: " + bukkitForm + " (" + bukkitForm.getClass() + ')');
      }
   }

   @NotNull
   private static String format(@NotNull String name) {
      int len = name.length();
      char[] chs = new char[len];
      int count = 0;
      boolean appendUnderline = false;

      for (int i = 0; i < len; i++) {
         char ch = name.charAt(i);
         if (!appendUnderline && count != 0 && (ch == '-' || ch == ' ' || ch == '_') && chs[count] != '_') {
            appendUnderline = true;
         } else {
            boolean number = false;
            if (ch >= 'A' && ch <= 'Z' || ch >= 'a' && ch <= 'z' || (number = ch >= '0' && ch <= '9')) {
               if (appendUnderline) {
                  chs[count++] = '_';
                  appendUnderline = false;
               }

               if (number) {
                  chs[count++] = ch;
               } else {
                  chs[count++] = (char)(ch & '_');
               }
            }
         }
      }

      return new String(chs, 0, count);
   }

   private static String normalizeName(String name) {
      name = name.toLowerCase(Locale.ENGLISH);
      if (name.startsWith("minecraft:")) {
         name = name.substring("minecraft:".length());
      }

      return name.replace('.', '_');
   }

   private XForm std(BukkitForm bukkit) {
      return this.std(null, bukkit);
   }

   private XForm std(@Nullable String extraFieldName, BukkitForm bukkit) {
      XForm xForm = this.bukkitToX.get(bukkit);
      if (xForm != null) {
         return xForm;
      } else {
         String name = getBukkitName(bukkit);
         if (this.getBukkit(new String[]{name}) == null && extraFieldName == null) {
            throw new IllegalArgumentException(
               "Unknown standard bukkit form for " + this.registryName + ": " + bukkit + (bukkit.toString().equals(name) ? "" : " (" + name + ')')
            );
         } else {
            xForm = this.creator.apply(bukkit, extraFieldName == null ? new String[]{name} : new String[]{extraFieldName, name});
            if (!PERFORM_AUTO_ADD) {
               return xForm;
            } else {
               this.registerName(name, xForm);
               if (extraFieldName != null) {
                  this.registerName(extraFieldName, xForm);
               }

               this.bukkitToX.put(bukkit, xForm);
               return xForm;
            }
         }
      }
   }

   @Internal
   public XForm std(String[] names) {
      BukkitForm bukkit = this.getBukkit(names);
      XForm xForm = this.creator.apply(bukkit, names);
      return this.std(xForm);
   }

   @Internal
   public BukkitForm stdEnum(XForm xForm, String[] names) {
      String enumName = xForm.name();
      boolean merged = false;
      BukkitForm bukkit = this.getBukkit(new String[]{enumName});
      if (bukkit == null) {
         bukkit = this.getBukkit(names);
      }

      if (bukkit == null) {
         bukkit = this.registerMerged(xForm);
         merged = true;
      }

      return this.stdEnum0(xForm, names, bukkit, merged);
   }

   public BukkitForm stdEnum(XForm xForm, String[] names, BukkitForm bukkit) {
      return this.stdEnum0(xForm, names, bukkit, false);
   }

   @Internal
   private BukkitForm stdEnum0(XForm xForm, String[] names, BukkitForm bukkit, boolean merged) {
      String enumName = xForm.name();
      if (!merged) {
         this.registerMerged(xForm);
      }

      this.registerName(enumName, xForm);

      for (String name : names) {
         this.registerName(name, xForm);
      }

      if (bukkit != null) {
         this.bukkitToX.put(bukkit, xForm);
      }

      return bukkit;
   }

   private BukkitForm registerMerged(XForm xForm) {
      return this.registerMerged(xForm, this.getBackingField(xForm));
   }

   @NotNull
   @Internal
   public Field getBackingField(XForm xForm) {
      try {
         return xForm.getClass().getDeclaredField(xForm.name());
      } catch (NoSuchFieldException var6) {
         try {
            if (this.backingFields == null) {
               this.cacheBackingFields();
            }

            Field field = this.backingFields.get(xForm);
            if (field != null) {
               return field;
            }
         } catch (Throwable var5) {
            IllegalStateException newEx = new IllegalStateException("Cannot find field for XForm: " + xForm + " - " + xForm.getClass(), var5);
            newEx.addSuppressed(var6);
            throw newEx;
         }

         throw new IllegalStateException("Cannot find field for XForm: " + xForm + " - " + xForm.getClass(), var6);
      }
   }

   private void cacheBackingFields() {
      if (this.backingFields != null) {
         throw new IllegalStateException("Backing fields are already cached");
      } else if (this.alreadyDiscardedMetadata) {
         throw new IllegalStateException("Metadata have already been used and discarded");
      } else {
         this.backingFields = new IdentityHashMap<>();
         this.alreadyDiscardedMetadata = true;

         for (Field field : this.xFormClass.getDeclaredFields()) {
            int mods = field.getModifiers();
            if (Modifier.isPublic(mods)
               && Modifier.isStatic(mods)
               && Modifier.isFinal(mods)
               && field.getType() == this.xFormClass
               && !field.isAnnotationPresent(XRegistry.Ignore.class)) {
               try {
                  Object xform = Objects.requireNonNull(
                     field.get(null), () -> "XForm backing field returned null: " + field + " for registry of " + this.xFormClass
                  );
                  XForm castForm = (XForm)xform;
                  this.backingFields.put(castForm, field);
               } catch (IllegalAccessException var8) {
                  throw new RuntimeException(var8);
               }
            }
         }
      }
   }

   @Internal
   public XModuleMetadata getOrRegisterMetadata(XForm form, Field formField, boolean peekOnly) {
      XModuleMetadata meta = this.metadata == null ? null : this.metadata.get(form);
      if (meta != null) {
         return meta;
      } else {
         meta = new XModuleMetadata(
            formField.isAnnotationPresent(Deprecated.class),
            formField.getAnnotationsByType(XChange.class),
            formField.getAnnotationsByType(XMerge.class),
            formField.getAnnotation(XInfo.class)
         );
         if (!peekOnly) {
            if (this.metadata == null) {
               this.metadata = new IdentityHashMap<>(10);
            }

            this.metadata.put(form, meta);
         }

         return meta;
      }
   }

   private BukkitForm registerMerged(XForm xForm, Field formField) {
      XMerge[] merges = this.getOrRegisterMetadata(xForm, formField, true).getMerges();
      BukkitForm mergedBukkit = null;

      for (XMerge merge : merges) {
         mergedBukkit = this.getBukkit(new String[]{merge.name()});
         this.registerName(merge.name(), xForm);
         if (mergedBukkit != null) {
            this.bukkitToX.put(mergedBukkit, xForm);
         }
      }

      return mergedBukkit;
   }

   @Internal
   public XForm std(Function<BukkitForm, XForm> xForm, String[] names) {
      BukkitForm bukkit = this.getBukkit(names);
      return this.std(xForm.apply(bukkit));
   }

   @Internal
   public XForm std(Function<BukkitForm, XForm> xForm, XForm tryOther, String[] names) {
      BukkitForm bukkit = this.getBukkit(names);
      if (bukkit == null) {
         bukkit = tryOther.get();
      }

      return this.std(xForm.apply(bukkit));
   }

   @Internal
   public XForm std(XForm xForm) {
      for (String name : xForm.getNames()) {
         this.registerName(name, xForm);
      }

      if (xForm.isSupported()) {
         this.bukkitToX.put(xForm.get(), xForm);
      }

      return xForm;
   }

   @Override
   public String toString() {
      return "XRegistry<"
         + this.registryName
         + ">(nameMappings="
         + this.nameMappings.size()
         + ", bukkitToX="
         + this.bukkitToX.size()
         + ", bukkitFormClass="
         + this.bukkitFormClass.getName()
         + ", xFormClass="
         + this.xFormClass.getName()
         + ", supportsRegistry="
         + this.supportsRegistry
         + ", bukkitFormClassType="
         + this.bukkitClassType
         + ", pulled="
         + this.pulled
         + ", values=["
         + this.bukkitToX.values().stream().limit(10L).map(XBase::name).collect(Collectors.joining(", "))
         + ']'
         + ')';
   }

   static {
      boolean keyedExists = false;

      try {
         Class.forName("org.bukkit.Keyed");
         keyedExists = true;
      } catch (ClassNotFoundException var2) {
      }

      KEYED_EXISTS = keyedExists;
      REGISTRIES = new IdentityHashMap<>();
      ensureLoaded = false;
   }

   private static enum ClassType {
      ENUM,
      ABSTRACTION;
   }

   @Retention(RetentionPolicy.RUNTIME)
   @Target(ElementType.FIELD)
   @Documented
   @Internal
   public @interface Ignore {
   }
}
