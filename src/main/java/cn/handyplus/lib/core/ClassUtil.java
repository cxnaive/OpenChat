package cn.handyplus.lib.core;

import cn.handyplus.lib.InitApi;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class ClassUtil {
   private static final String CLASS = ".class";
   private final File FILE;
   private final ClassLoader CLASS_LOADER = InitApi.PLUGIN.getClass().getClassLoader();
   private static ClassUtil INSTANCE;

   public static ClassUtil getInstance() {
      if (INSTANCE == null) {
         INSTANCE = new ClassUtil();
      }

      return INSTANCE;
   }

   private ClassUtil() {
      try {
         this.FILE = new File(
            URLDecoder.decode(InitApi.PLUGIN.getClass().getProtectionDomain().getCodeSource().getLocation().getPath(), StandardCharsets.UTF_8.toString())
         );
      } catch (UnsupportedEncodingException var2) {
         throw new NullPointerException("加载异常...");
      }
   }

   public List<Class<?>> getClassByAnnotation(String packageName, Class<? extends Annotation> annotation) {
      try {
         List<Class<?>> classList = new ArrayList<>();
         URL jar = this.FILE.toURI().toURL();
         URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{jar}, this.CLASS_LOADER);

         try {
            JarInputStream jarInputStream = new JarInputStream(jar.openStream());

            try {
               while (true) {
                  JarEntry nextJarEntry = jarInputStream.getNextJarEntry();
                  if (nextJarEntry == null) {
                     break;
                  }

                  String name = this.getName(nextJarEntry);
                  if (name != null && name.startsWith(packageName)) {
                     String cname = name.substring(0, name.lastIndexOf(".class"));
                     Class<?> loadClass = urlClassLoader.loadClass(cname);
                     if (loadClass.isAnnotationPresent(annotation)) {
                        classList.add(loadClass);
                     }
                  }
               }
            } catch (Throwable var13) {
               try {
                  jarInputStream.close();
               } catch (Throwable var12) {
                  var13.addSuppressed(var12);
               }

               throw new RuntimeException(var13);
            }

            jarInputStream.close();
         } catch (Throwable var14) {
            try {
               urlClassLoader.close();
            } catch (Throwable var11) {
               var14.addSuppressed(var11);
            }

            throw new RuntimeException(var14);
         }

         urlClassLoader.close();
         return classList;
      } catch (Throwable var15) {
         throw new RuntimeException(var15);
      }
   }

   public Map<Class<?>, List<Method>> getMethodByAnnotation(String packageName, Class<? extends Annotation> annotation) {
      try {
         Map<Class<?>, List<Method>> map = MapUtil.of();
         URL jar = this.FILE.toURI().toURL();
         URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{jar}, this.CLASS_LOADER);

         try {
            JarInputStream jarInputStream = new JarInputStream(jar.openStream());

            try {
               while (true) {
                  JarEntry nextJarEntry = jarInputStream.getNextJarEntry();
                  if (nextJarEntry == null) {
                     break;
                  }

                  String name = this.getName(nextJarEntry);
                  if (name != null && name.startsWith(packageName)) {
                     List<Method> methods = new ArrayList<>();
                     String cname = name.substring(0, name.lastIndexOf(".class"));
                     Class<?> loadClass = urlClassLoader.loadClass(cname);
                     Method[] declaredMethods = loadClass.getDeclaredMethods();
                     List<Method> subCommandMethods = Stream.of(declaredMethods)
                        .filter(method -> method.isAnnotationPresent(annotation))
                        .collect(Collectors.toList());
                     if (CollUtil.isNotEmpty(subCommandMethods)) {
                        methods.addAll(subCommandMethods);
                     }

                     map.put(loadClass, methods);
                  }
               }
            } catch (Throwable var16) {
               try {
                  jarInputStream.close();
               } catch (Throwable var15) {
                  var16.addSuppressed(var15);
               }

               throw new RuntimeException(var16);
            }

            jarInputStream.close();
         } catch (Throwable var17) {
            try {
               urlClassLoader.close();
            } catch (Throwable var14) {
               var17.addSuppressed(var14);
            }

            throw new RuntimeException(var17);
         }

         urlClassLoader.close();
         return map;
      } catch (Throwable var18) {
         throw new RuntimeException(var18);
      }
   }

   public <T> List<Class<T>> getClassByIsAssignableFrom(String packageName, Class<? extends T> clazz) {
      try {
         List<Class<T>> classList = new ArrayList<>();
         URL jar = this.FILE.toURI().toURL();
         URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{jar}, this.CLASS_LOADER);

         try {
            JarInputStream jarInputStream = new JarInputStream(jar.openStream());

            try {
               while (true) {
                  JarEntry nextJarEntry = jarInputStream.getNextJarEntry();
                  if (nextJarEntry == null) {
                     break;
                  }

                  String name = this.getName(nextJarEntry);
                  if (name != null && name.startsWith(packageName)) {
                     String cname = name.substring(0, name.lastIndexOf(".class"));
                     Class loadClass = urlClassLoader.loadClass(cname);
                     if (clazz.isAssignableFrom(loadClass)) {
                        classList.add(loadClass);
                     }
                  }
               }
            } catch (Throwable var13) {
               try {
                  jarInputStream.close();
               } catch (Throwable var12) {
                  var13.addSuppressed(var12);
               }

               throw new RuntimeException(var13);
            }

            jarInputStream.close();
         } catch (Throwable var14) {
            try {
               urlClassLoader.close();
            } catch (Throwable var11) {
               var14.addSuppressed(var11);
            }

            throw new RuntimeException(var14);
         }

         urlClassLoader.close();
         return classList;
      } catch (Throwable var15) {
         throw new RuntimeException(var15);
      }
   }

   private String getName(JarEntry jarEntry) {
      String name = jarEntry.getName();
      if (name.isEmpty()) {
         return null;
      } else {
         return !name.endsWith(".class") ? null : name.replace("/", ".");
      }
   }
}
