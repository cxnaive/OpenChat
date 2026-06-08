package com.example.messageservice.database;

import cn.handyplus.chat.PlayerChat;
import org.bukkit.Bukkit;
import com.example.messageservice.MessageServicePlugin;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;

/**
 * 数据库操作队列管理器
 * 参考 FoliaMail 的 DatabaseQueue 实现
 * 使用单线程队列处理数据库操作，避免并发问题
 */
public class DatabaseQueue {

    private final MessageServicePlugin plugin;
    private final BlockingQueue<DatabaseTask<?>> taskQueue;
    private final ExecutorService executor;
    private final AtomicBoolean running;
    private Thread workerThread;

    // 熔断机制相关
    private final AtomicLong lastOverloadWarningTime = new AtomicLong(0);
    private static final int OVERLOAD_WARNING_INTERVAL_MS = 30000; // 30秒
    private static final int QUEUE_OVERLOAD_THRESHOLD = 200; // 队列超载阈值
    private static final int QUEUE_WARNING_THRESHOLD = 100;  // 队列告警阈值

    public DatabaseQueue(MessageServicePlugin plugin) {
        this.plugin = plugin;
        this.taskQueue = new LinkedBlockingQueue<>();
        this.executor = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "MessageService-DB-Queue");
            t.setDaemon(true);
            return t;
        });
        this.running = new AtomicBoolean(false);
    }

    public void start() {
        if (running.compareAndSet(false, true)) {
            workerThread = new Thread(this::processLoop, "MessageService-DB-Worker");
            workerThread.setDaemon(true);
            workerThread.start();
            plugin.getLogger().info("数据库操作队列已启动");
        }
    }

    public void stop() {
        running.set(false);
        workerThread.interrupt();
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
        plugin.getLogger().info("数据库操作队列已停止");
    }

    private void processLoop() {
        while (running.get() && !Thread.currentThread().isInterrupted()) {
            try {
                // 队列深度监控
                checkQueueDepth();

                DatabaseTask<?> task = taskQueue.poll(100, TimeUnit.MILLISECONDS);
                if (task != null) {
                    executeTask(task);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "数据库任务执行错误: " + e.getMessage(), e);
            }
        }
    }

    /**
     * 检查队列深度，如果超过阈值则输出警告
     */
    private void checkQueueDepth() {
        int queueSize = taskQueue.size();

        // 队列超载告警（带节流，避免日志刷屏）
        if (queueSize >= QUEUE_WARNING_THRESHOLD) {
            long now = System.currentTimeMillis();
            long lastWarning = lastOverloadWarningTime.get();

            if (now - lastWarning > OVERLOAD_WARNING_INTERVAL_MS &&
                lastOverloadWarningTime.compareAndSet(lastWarning, now)) {
                plugin.getLogger().warning("数据库队列堆积: " + queueSize + " 个任务待处理，" +
                    "可能影响公告同步功能响应速度！");
            }
        }
    }

    private <T> void executeTask(DatabaseTask<T> task) {
        try {
            T result = task.getFunction().apply(null); // Connection 由具体实现提供
            if (task.getOnSuccess() != null) {
                // 回调到主线程
                Bukkit.getGlobalRegionScheduler().execute(PlayerChat.INSTANCE, () -> {
                    task.getOnSuccess().accept(result);
                });
            }
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "数据库任务 '" + task.getTaskName() + "' 执行失败", e);
            if (task.getOnError() != null) {
                Bukkit.getGlobalRegionScheduler().execute(PlayerChat.INSTANCE, () -> {
                    task.getOnError().accept(e);
                });
            }
        }
    }

    /**
     * 提交数据库任务
     *
     * @param taskName 任务名称（用于日志）
     * @param function 数据库操作函数
     * @param onSuccess 成功回调（在主线程执行）
     * @param onError 错误回调（在主线程执行）
     */
    public <T> void submit(String taskName, Function<Connection, T> function,
                           Consumer<T> onSuccess, Consumer<Exception> onError) {
        if (!running.get()) {
            plugin.getLogger().warning("数据库队列已停止，无法提交任务: " + taskName);
            return;
        }
        taskQueue.offer(new DatabaseTask<>(taskName, function, onSuccess, onError));
    }

    /**
     * 提交数据库任务（简化版，无回调）
     */
    public void submit(String taskName, Function<Connection, ?> function) {
        submit(taskName, function, null, null);
    }

    /**
     * 获取队列大小
     */
    public int getQueueSize() {
        return taskQueue.size();
    }

    /**
     * 是否正在运行
     */
    public boolean isRunning() {
        return running.get();
    }

    /**
     * 数据库任务封装类
     */
    private static class DatabaseTask<T> {
        private final String taskName;
        private final Function<Connection, T> function;
        private final Consumer<T> onSuccess;
        private final Consumer<Exception> onError;

        public DatabaseTask(String taskName, Function<Connection, T> function,
                           Consumer<T> onSuccess, Consumer<Exception> onError) {
            this.taskName = taskName;
            this.function = function;
            this.onSuccess = onSuccess;
            this.onError = onError;
        }

        public String getTaskName() {
            return taskName;
        }

        public Function<Connection, T> getFunction() {
            return function;
        }

        public Consumer<T> getOnSuccess() {
            return onSuccess;
        }

        public Consumer<Exception> getOnError() {
            return onError;
        }
    }
}
