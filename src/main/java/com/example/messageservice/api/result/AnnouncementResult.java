package com.example.messageservice.api.result;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 公告操作结果
 */
public class AnnouncementResult {

    private final boolean success;
    private final String announcementId;
    private final String message;
    private final Throwable error;

    private AnnouncementResult(boolean success, @Nullable String announcementId, @Nullable String message, @Nullable Throwable error) {
        this.success = success;
        this.announcementId = announcementId;
        this.message = message;
        this.error = error;
    }

    /**
     * 创建成功结果
     *
     * @param announcementId 公告ID
     * @param message 成功信息
     * @return 结果对象
     */
    @NotNull
    public static AnnouncementResult success(@NotNull String announcementId, @NotNull String message) {
        return new AnnouncementResult(true, announcementId, message, null);
    }

    /**
     * 创建成功结果
     *
     * @param announcementId 公告ID
     * @return 结果对象
     */
    @NotNull
    public static AnnouncementResult success(@NotNull String announcementId) {
        return new AnnouncementResult(true, announcementId, "操作成功", null);
    }

    /**
     * 创建失败结果
     *
     * @param message 错误信息
     * @return 结果对象
     */
    @NotNull
    public static AnnouncementResult failure(@NotNull String message) {
        return new AnnouncementResult(false, null, message, null);
    }

    /**
     * 创建失败结果
     *
     * @param message 错误信息
     * @param error 异常
     * @return 结果对象
     */
    @NotNull
    public static AnnouncementResult failure(@NotNull String message, @NotNull Throwable error) {
        return new AnnouncementResult(false, null, message, error);
    }

    /**
     * 检查是否成功
     *
     * @return true如果成功
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * 获取公告ID
     *
     * @return 公告ID（可能为空）
     */
    @Nullable
    public String getAnnouncementId() {
        return announcementId;
    }

    /**
     * 获取信息
     *
     * @return 信息（可能为空）
     */
    @Nullable
    public String getMessage() {
        return message;
    }

    /**
     * 获取异常
     *
     * @return 异常（可能为空）
     */
    @Nullable
    public Throwable getError() {
        return error;
    }

    @Override
    public String toString() {
        return "AnnouncementResult{" +
                "success=" + success +
                ", announcementId='" + announcementId + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
