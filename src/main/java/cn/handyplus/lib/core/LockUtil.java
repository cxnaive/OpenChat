package cn.handyplus.lib.core;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

public final class LockUtil {
   private static final ConcurrentHashMap<String, Boolean> DEDUP = new ConcurrentHashMap<>();
   private static final ConcurrentHashMap<UUID, Long> TIME_LOCK_MAP = new ConcurrentHashMap<>();
   private static final Map<String, ReentrantLock> REENTRANT_LOCK_MAP = new ConcurrentHashMap<>();
   private static final long LOCK_TIME = 300L;

   private LockUtil() {
   }

   public static boolean tryPass(String key) {
      return DEDUP.putIfAbsent(key, Boolean.TRUE) == null;
   }

   public static void done(String key) {
      DEDUP.remove(key);
   }

   public static boolean timeLock(UUID key) {
      long now = System.currentTimeMillis();
      AtomicBoolean passed = new AtomicBoolean(false);
      TIME_LOCK_MAP.compute(key, (k, last) -> {
         if (last != null && now - last < 300L) {
            return (Long)last;
         } else {
            passed.set(true);
            return now;
         }
      });
      return passed.get();
   }

   public static void unTimeLock(UUID key) {
      TIME_LOCK_MAP.remove(key);
   }

   public static void reentrantLock(String key) {
      REENTRANT_LOCK_MAP.computeIfAbsent(key, k -> new ReentrantLock(true)).lock();
   }

   public static void unReentrantLock(String key) {
      ReentrantLock lock = REENTRANT_LOCK_MAP.get(key);
      if (lock != null) {
         if (lock.isHeldByCurrentThread()) {
            lock.unlock();
         }

         REENTRANT_LOCK_MAP.computeIfPresent(key, (k, v) -> (ReentrantLock)(!v.isLocked() && !v.hasQueuedThreads() ? null : v));
      }
   }
}
