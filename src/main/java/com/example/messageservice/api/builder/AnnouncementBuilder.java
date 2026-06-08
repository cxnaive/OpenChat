package com.example.messageservice.api.builder;

import com.example.messageservice.models.Announcement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 公告构建器
 * 用于简化公告对象的创建
 */
public class AnnouncementBuilder {

    private String id;
    private boolean enabled = true;
    private int priority = 100;
    private List<String> content = new ArrayList<>();
    private Announcement.DisplaySettings display;
    private Announcement.TargetSettings target;
    private Announcement.TriggerSettings trigger;
    private Announcement.CooldownSettings cooldown;
    private List<String> servers = Arrays.asList("*");
    private Integer version = 1;
    private Announcement.SoundSettings sound;

    /**
     * 默认构造函数
     */
    public AnnouncementBuilder() {
        // 设置默认值
        this.display = Announcement.DisplaySettings.builder()
            .type(Announcement.DisplayType.CHAT)
            .fadeIn(10)
            .stay(70)
            .fadeOut(20)
            .build();
        
        this.target = Announcement.TargetSettings.builder()
            .type(Announcement.TargetType.ALL)
            .value("*")
            .build();
        
        this.trigger = Announcement.TriggerSettings.builder()
            .type(Announcement.TriggerType.MANUAL)
            .build();
        
        this.cooldown = Announcement.CooldownSettings.builder()
            .global(0)
            .perPlayer(0)
            .build();
    }

    /**
     * 带ID的构造函数
     *
     * @param id 公告ID
     */
    public AnnouncementBuilder(@NotNull String id) {
        this();
        this.id = id;
    }

    /**
     * 设置公告ID
     *
     * @param id 公告ID
     * @return 构建器
     */
    @NotNull
    public AnnouncementBuilder id(@NotNull String id) {
        this.id = id;
        return this;
    }

    /**
     * 设置是否启用
     *
     * @param enabled 是否启用
     * @return 构建器
     */
    @NotNull
    public AnnouncementBuilder enabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    /**
     * 设置优先级
     *
     * @param priority 优先级
     * @return 构建器
     */
    @NotNull
    public AnnouncementBuilder priority(int priority) {
        this.priority = priority;
        return this;
    }

    /**
     * 设置内容
     *
     * @param content 内容列表
     * @return 构建器
     */
    @NotNull
    public AnnouncementBuilder content(@NotNull List<String> content) {
        this.content = new ArrayList<>(content);
        return this;
    }

    /**
     * 设置内容
     *
     * @param content 内容数组
     * @return 构建器
     */
    @NotNull
    public AnnouncementBuilder content(@NotNull String... content) {
        this.content = Arrays.asList(content);
        return this;
    }

    /**
     * 添加内容行
     *
     * @param line 内容行
     * @return 构建器
     */
    @NotNull
    public AnnouncementBuilder addLine(@NotNull String line) {
        this.content.add(line);
        return this;
    }

    /**
     * 设置显示类型
     *
     * @param type 显示类型
     * @return 构建器
     */
    @NotNull
    public AnnouncementBuilder displayType(@NotNull Announcement.DisplayType type) {
        this.display.setType(type);
        return this;
    }

    /**
     * 设置显示设置
     *
     * @param display 显示设置
     * @return 构建器
     */
    @NotNull
    public AnnouncementBuilder display(@NotNull Announcement.DisplaySettings display) {
        this.display = display;
        return this;
    }

    /**
     * 设置标题淡入时间
     *
     * @param fadeIn 淡入时间（tick）
     * @return 构建器
     */
    @NotNull
    public AnnouncementBuilder fadeIn(int fadeIn) {
        this.display.setFadeIn(fadeIn);
        return this;
    }

    /**
     * 设置标题停留时间
     *
     * @param stay 停留时间（tick）
     * @return 构建器
     */
    @NotNull
    public AnnouncementBuilder stay(int stay) {
        this.display.setStay(stay);
        return this;
    }

    /**
     * 设置标题淡出时间
     *
     * @param fadeOut 淡出时间（tick）
     * @return 构建器
     */
    @NotNull
    public AnnouncementBuilder fadeOut(int fadeOut) {
        this.display.setFadeOut(fadeOut);
        return this;
    }

    /**
     * 设置目标类型
     *
     * @param type 目标类型
     * @param value 目标值
     * @return 构建器
     */
    @NotNull
    public AnnouncementBuilder target(@NotNull Announcement.TargetType type, @NotNull String value) {
        this.target = Announcement.TargetSettings.builder()
            .type(type)
            .value(value)
            .build();
        return this;
    }

    /**
     * 设置目标为所有玩家
     *
     * @return 构建器
     */
    @NotNull
    public AnnouncementBuilder targetAll() {
        this.target = Announcement.TargetSettings.builder()
            .type(Announcement.TargetType.ALL)
            .value("*")
            .build();
        return this;
    }

    /**
     * 设置目标为指定权限
     *
     * @param permission 权限
     * @return 构建器
     */
    @NotNull
    public AnnouncementBuilder targetPermission(@NotNull String permission) {
        this.target = Announcement.TargetSettings.builder()
            .type(Announcement.TargetType.PERMISSION)
            .value(permission)
            .build();
        return this;
    }

    /**
     * 设置目标为指定世界
     *
     * @param world 世界名称
     * @return 构建器
     */
    @NotNull
    public AnnouncementBuilder targetWorld(@NotNull String world) {
        this.target = Announcement.TargetSettings.builder()
            .type(Announcement.TargetType.WORLD)
            .value(world)
            .build();
        return this;
    }

    /**
     * 设置目标设置
     *
     * @param target 目标设置
     * @return 构建器
     */
    @NotNull
    public AnnouncementBuilder target(@NotNull Announcement.TargetSettings target) {
        this.target = target;
        return this;
    }

    /**
     * 设置触发类型
     *
     * @param type 触发类型
     * @return 构建器
     */
    @NotNull
    public AnnouncementBuilder trigger(@NotNull Announcement.TriggerType type) {
        this.trigger.setType(type);
        return this;
    }

    /**
     * 设置触发设置
     *
     * @param trigger 触发设置
     * @return 构建器
     */
    @NotNull
    public AnnouncementBuilder trigger(@NotNull Announcement.TriggerSettings trigger) {
        this.trigger = trigger;
        return this;
    }

    /**
     * 设置冷却
     *
     * @param global 全局冷却（秒）
     * @param perPlayer 玩家冷却（秒）
     * @return 构建器
     */
    @NotNull
    public AnnouncementBuilder cooldown(int global, int perPlayer) {
        this.cooldown = Announcement.CooldownSettings.builder()
            .global(global)
            .perPlayer(perPlayer)
            .build();
        return this;
    }

    /**
     * 设置全局冷却
     *
     * @param seconds 冷却时间（秒）
     * @return 构建器
     */
    @NotNull
    public AnnouncementBuilder globalCooldown(int seconds) {
        this.cooldown.setGlobal(seconds);
        return this;
    }

    /**
     * 设置玩家冷却
     *
     * @param seconds 冷却时间（秒）
     * @return 构建器
     */
    @NotNull
    public AnnouncementBuilder playerCooldown(int seconds) {
        this.cooldown.setPerPlayer(seconds);
        return this;
    }

    /**
     * 设置冷却设置
     *
     * @param cooldown 冷却设置
     * @return 构建器
     */
    @NotNull
    public AnnouncementBuilder cooldown(@NotNull Announcement.CooldownSettings cooldown) {
        this.cooldown = cooldown;
        return this;
    }

    /**
     * 设置生效服务器
     *
     * @param servers 服务器列表
     * @return 构建器
     */
    @NotNull
    public AnnouncementBuilder servers(@NotNull List<String> servers) {
        this.servers = new ArrayList<>(servers);
        return this;
    }

    /**
     * 设置生效服务器
     *
     * @param servers 服务器数组
     * @return 构建器
     */
    @NotNull
    public AnnouncementBuilder servers(@NotNull String... servers) {
        this.servers = Arrays.asList(servers);
        return this;
    }

    /**
     * 设置生效服务器为所有服务器
     *
     * @return 构建器
     */
    @NotNull
    public AnnouncementBuilder allServers() {
        this.servers = Arrays.asList("*");
        return this;
    }

    /**
     * 设置版本号
     *
     * @param version 版本号
     * @return 构建器
     */
    @NotNull
    public AnnouncementBuilder version(int version) {
        this.version = version;
        return this;
    }

    /**
     * 设置音效
     *
     * @param sound 音效名称
     * @param volume 音量
     * @param pitch 音调
     * @return 构建器
     */
    @NotNull
    public AnnouncementBuilder sound(@NotNull String sound, float volume, float pitch) {
        this.sound = Announcement.SoundSettings.builder()
            .sound(sound)
            .volume(volume)
            .pitch(pitch)
            .build();
        return this;
    }

    /**
     * 设置音效
     *
     * @param sound 音效设置
     * @return 构建器
     */
    @NotNull
    public AnnouncementBuilder sound(@NotNull Announcement.SoundSettings sound) {
        this.sound = sound;
        return this;
    }

    /**
     * 构建公告对象
     *
     * @return 公告对象
     * @throws IllegalStateException 如果ID未设置
     */
    @NotNull
    public Announcement build() {
        if (id == null || id.isEmpty()) {
            throw new IllegalStateException("公告ID不能为空");
        }

        return Announcement.builder()
            .id(id)
            .enabled(enabled)
            .priority(priority)
            .content(content)
            .display(display)
            .target(target)
            .trigger(trigger)
            .cooldown(cooldown)
            .servers(servers)
            .version(version)
            .sound(sound)
            .build();
    }
}
