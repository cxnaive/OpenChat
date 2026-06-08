package com.example.messageservice.api.exception;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 公告操作异常
 * 当公告操作失败时抛出
 */
public class AnnouncementException extends Exception {

    private final String announcementId;

    /**
     * 构造函数
     *
     * @param message 错误信息
     */
    public AnnouncementException(@NotNull String message) {
        super(message);
        this.announcementId = null;
    }

    /**
     * 构造函数
     *
     * @param message 错误信息
     * @param announcementId 公告ID
     */
    public AnnouncementException(@NotNull String message, @Nullable String announcementId) {
        super(message);
        this.announcementId = announcementId;
    }

    /**
     * 构造函数
     *
     * @param message 错误信息
     * @param cause 异常原因
     */
    public AnnouncementException(@NotNull String message, @NotNull Throwable cause) {
        super(message, cause);
        this.announcementId = null;
    }

    /**
     * 构造函数
     *
     * @param message 错误信息
     * @param announcementId 公告ID
     * @param cause 异常原因
     */
    public AnnouncementException(@NotNull String message, @Nullable String announcementId, @NotNull Throwable cause) {
        super(message, cause);
        this.announcementId = announcementId;
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
     * 创建"公告不存在"异常
     *
     * @param announcementId 公告ID
     * @return 异常对象
     */
    @NotNull
    public static AnnouncementException notFound(@NotNull String announcementId) {
        return new AnnouncementException("公告不存在: " + announcementId, announcementId);
    }

    /**
     * 创建"公告ID已存在"异常
     *
     * @param announcementId 公告ID
     * @return 异常对象
     */
    @NotNull
    public static AnnouncementException alreadyExists(@NotNull String announcementId) {
        return new AnnouncementException("公告ID已存在: " + announcementId, announcementId);
    }

    /**
     * 创建"公告已禁用"异常
     *
     * @param announcementId 公告ID
     * @return 异常对象
     */
    @NotNull
    public static AnnouncementException disabled(@NotNull String announcementId) {
        return new AnnouncementException("公告已禁用: " + announcementId, announcementId);
    }

    /**
     * 创建"无效的显示类型"异常
     *
     * @param displayType 显示类型
     * @return 异常对象
     */
    @NotNull
    public static AnnouncementException invalidDisplayType(@NotNull String displayType) {
        return new AnnouncementException("无效的显示类型: " + displayType);
    }

    /**
     * 创建"无效的目标类型"异常
     *
     * @param targetType 目标类型
     * @return 异常对象
     */
    @NotNull
    public static AnnouncementException invalidTargetType(@NotNull String targetType) {
        return new AnnouncementException("无效的目标类型: " + targetType);
    }

    /**
     * 创建"MessageService未启用"异常
     *
     * @return 异常对象
     */
    @NotNull
    public static AnnouncementException serviceNotAvailable() {
        return new AnnouncementException("MessageService插件未安装或未启用");
    }
}
