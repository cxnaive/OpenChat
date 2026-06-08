package com.example.messageservice.models;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Announcement {

    private String id;
    private boolean enabled;
    private int priority;

    private List<String> content;
    private DisplaySettings display;
    private TargetSettings target;
    private TriggerSettings trigger;
    private List<String> conditions;
    private ActionSettings actions;
    private CooldownSettings cooldown;

    /**
     * 生效区服列表
     * 如果为空或包含 "*"，则在所有区服生效
     * 否则只在指定的区服生效（如 ["server1", "server2"]）
     */
    private List<String> servers;

    /**
     * 版本号，用于跨服同步
     */
    private Integer version;

    /**
     * 音效设置（可选）
     */
    private SoundSettings sound;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SoundSettings {
        private String sound;
        private float volume;
        private float pitch;

        public static SoundSettings fromConfig(ConfigurationSection section) {
            if (section == null) {
                return null;
            }

            String sound = section.getString("sound", null);
            if (sound == null || sound.isEmpty()) {
                return null;
            }

            return SoundSettings.builder()
                    .sound(sound)
                    .volume((float) section.getDouble("volume", 1.0))
                    .pitch((float) section.getDouble("pitch", 1.0))
                    .build();
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DisplaySettings {
        private DisplayType type;
        private int fadeIn;
        private int stay;
        private int fadeOut;
        private String color;
        private String style;
        private String toastIcon;
        private boolean bossbarProgress;

        public static DisplaySettings fromConfig(ConfigurationSection section) {
            if (section == null) {
                return DisplaySettings.builder()
                        .type(DisplayType.CHAT)
                        .bossbarProgress(true)
                        .build();
            }
            
            String typeStr = section.getString("type", "chat");
            DisplayType type = DisplayType.fromString(typeStr);
            
            return DisplaySettings.builder()
                    .type(type)
                    .fadeIn(section.getInt("settings.fade-in", 10))
                    .stay(section.getInt("settings.stay", 70))
                    .fadeOut(section.getInt("settings.fade-out", 20))
                    .color(section.getString("settings.color", "WHITE"))
                    .style(section.getString("settings.style", "SOLID"))
                    .toastIcon(section.getString("settings.toast-icon", null))
                    .bossbarProgress(section.getBoolean("settings.bossbar-progress", true))
                    .build();
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TargetSettings {
        private TargetType type;
        private String value;

        public static TargetSettings fromConfig(ConfigurationSection section) {
            if (section == null) {
                return TargetSettings.builder()
                        .type(TargetType.ALL)
                        .value("*")
                        .build();
            }
            
            String typeStr = section.getString("type", "all");
            TargetType type = TargetType.fromString(typeStr);
            
            return TargetSettings.builder()
                    .type(type)
                    .value(section.getString("value", "*"))
                    .build();
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TriggerSettings {
        private TriggerType type;
        private String schedule;
        private List<String> events;
        private String command;

        public static TriggerSettings fromConfig(ConfigurationSection section) {
            if (section == null) {
                return TriggerSettings.builder()
                        .type(TriggerType.MANUAL)
                        .events(new ArrayList<>())
                        .build();
            }
            
            String typeStr = section.getString("type", "manual");
            TriggerType type = TriggerType.fromString(typeStr);
            
            return TriggerSettings.builder()
                    .type(type)
                    .schedule(section.getString("schedule", ""))
                    .events(section.getStringList("events"))
                    .command(section.getString("command", ""))
                    .build();
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ActionSettings {
        private String onClickCommand;
        private String onHoverText;

        public static ActionSettings fromConfig(ConfigurationSection section) {
            if (section == null) {
                return ActionSettings.builder().build();
            }
            
            ConfigurationSection clickSection = section.getConfigurationSection("on-click");
            ConfigurationSection hoverSection = section.getConfigurationSection("on-hover");
            
            return ActionSettings.builder()
                    .onClickCommand(clickSection != null ? clickSection.getString("command", "") : "")
                    .onHoverText(hoverSection != null ? hoverSection.getString("text", "") : "")
                    .build();
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CooldownSettings {
        private int global;
        private int perPlayer;

        public static CooldownSettings fromConfig(ConfigurationSection section) {
            if (section == null) {
                return CooldownSettings.builder()
                        .global(0)
                        .perPlayer(0)
                        .build();
            }
            
            return CooldownSettings.builder()
                    .global(section.getInt("global", 0))
                    .perPlayer(section.getInt("per-player", 0))
                    .build();
        }
    }

    public enum DisplayType {
        CHAT, TITLE, ACTIONBAR, BOSSBAR, TOAST, COMBINED;

        public static DisplayType fromString(String str) {
            try {
                return valueOf(str.toUpperCase());
            } catch (IllegalArgumentException e) {
                return CHAT;
            }
        }
    }

    public enum TargetType {
        ALL, WORLD, REGION, PERMISSION, RANGE, TRIGGER_PLAYER;

        public static TargetType fromString(String str) {
            try {
                return valueOf(str.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ALL;
            }
        }
    }

    public enum TriggerType {
        SCHEDULE, EVENT, COMMAND, MANUAL, FIRST_JOIN;

        public static TriggerType fromString(String str) {
            try {
                return valueOf(str.toUpperCase());
            } catch (IllegalArgumentException e) {
                return MANUAL;
            }
        }
    }

    public static Announcement fromConfig(String id, ConfigurationSection section) {
        if (section == null) {
            return null;
        }

        // 读取生效区服列表，默认为所有区服
        List<String> servers = section.getStringList("servers");
        if (servers.isEmpty()) {
            servers = new ArrayList<>();
            servers.add("*"); // 默认在所有区服生效
        }

        return Announcement.builder()
                .id(id)
                .enabled(section.getBoolean("enabled", true))
                .priority(section.getInt("priority", 1))
                .content(section.getStringList("content"))
                .display(DisplaySettings.fromConfig(section.getConfigurationSection("display")))
                .target(TargetSettings.fromConfig(section.getConfigurationSection("target")))
                .trigger(TriggerSettings.fromConfig(section.getConfigurationSection("trigger")))
                .conditions(section.getStringList("conditions"))
                .actions(ActionSettings.fromConfig(section.getConfigurationSection("actions")))
                .cooldown(CooldownSettings.fromConfig(section.getConfigurationSection("cooldown")))
                .servers(servers)
                .version(section.getInt("version", 1))
                .build();
    }

    /**
     * 将公告序列化为字符串，用于跨服传输
     */
    public String serialize() {
        StringBuilder sb = new StringBuilder();
        sb.append(id).append("|");
        sb.append(enabled).append("|");
        sb.append(priority).append("|");
        sb.append(String.join("\n", content != null ? content : new ArrayList<>())).append("|");

        // DisplaySettings
        if (display != null) {
            sb.append(display.getType() != null ? display.getType().name() : "CHAT").append("|");
            sb.append(display.getFadeIn()).append("|");
            sb.append(display.getStay()).append("|");
            sb.append(display.getFadeOut()).append("|");
            sb.append(display.getColor() != null ? display.getColor() : "").append("|");
            sb.append(display.getStyle() != null ? display.getStyle() : "").append("|");
            sb.append(display.getToastIcon() != null ? display.getToastIcon() : "").append("|");
            sb.append(display.isBossbarProgress()).append("|");
        } else {
            sb.append("CHAT|10|70|20||||true|");
        }

        // Servers
        sb.append(String.join(",", servers != null ? servers : List.of("*"))).append("|");

        // TargetSettings
        if (target != null) {
            sb.append(target.getType() != null ? target.getType().name() : "ALL").append("|");
            sb.append(target.getValue() != null ? target.getValue() : "*").append("|");
        } else {
            sb.append("ALL|*|");
        }

        // TriggerSettings
        if (trigger != null) {
            sb.append(trigger.getType() != null ? trigger.getType().name() : "MANUAL").append("|");
            sb.append(trigger.getSchedule() != null ? trigger.getSchedule() : "").append("|");
            sb.append(String.join(",", trigger.getEvents() != null ? trigger.getEvents() : new ArrayList<>())).append("|");
        } else {
            sb.append("MANUAL|||");
        }

        // CooldownSettings
        if (cooldown != null) {
            sb.append(cooldown.getGlobal()).append("|");
            sb.append(cooldown.getPerPlayer()).append("|");
        } else {
            sb.append("0|0|");
        }

        // Version
        sb.append(version != null ? version : 1).append("|");

        // SoundSettings
        if (sound != null) {
            sb.append(sound.getSound() != null ? sound.getSound() : "").append("|");
            sb.append(sound.getVolume()).append("|");
            sb.append(sound.getPitch()).append("|");
        } else {
            sb.append("|||");
        }

        return sb.toString();
    }

    /**
     * 从序列化字符串反序列化公告
     */
    public static Announcement deserialize(String str) {
        if (str == null || str.isEmpty()) {
            return null;
        }

        String[] parts = str.split("\\|", -1);
        if (parts.length < 15) {
            return null;
        }

        try {
            int index = 0;
            String id = parts[index++];
            boolean enabled = Boolean.parseBoolean(parts[index++]);
            int priority = Integer.parseInt(parts[index++]);

            // Content
            String contentStr = parts[index++];
            List<String> content = contentStr.isEmpty() ? new ArrayList<>() : new ArrayList<>(List.of(contentStr.split("\n")));

            // DisplaySettings
            DisplayType displayType = DisplayType.fromString(parts[index++]);
            int fadeIn = Integer.parseInt(parts[index++]);
            int stay = Integer.parseInt(parts[index++]);
            int fadeOut = Integer.parseInt(parts[index++]);
            String color = parts[index++];
            String style = parts[index++];
            String toastIcon = parts[index++];
            boolean bossbarProgress = Boolean.parseBoolean(parts[index++]);

            DisplaySettings display = DisplaySettings.builder()
                    .type(displayType)
                    .fadeIn(fadeIn)
                    .stay(stay)
                    .fadeOut(fadeOut)
                    .color(color.isEmpty() ? null : color)
                    .style(style.isEmpty() ? null : style)
                    .toastIcon(toastIcon.isEmpty() ? null : toastIcon)
                    .bossbarProgress(bossbarProgress)
                    .build();

            // Servers
            String serversStr = parts[index++];
            List<String> servers = serversStr.isEmpty() ? new ArrayList<>(List.of("*")) : new ArrayList<>(List.of(serversStr.split(",")));

            // TargetSettings
            TargetType targetType = TargetType.fromString(parts[index++]);
            String targetValue = parts[index++];
            TargetSettings target = TargetSettings.builder()
                    .type(targetType)
                    .value(targetValue)
                    .build();

            // TriggerSettings
            TriggerType triggerType = TriggerType.fromString(parts[index++]);
            String schedule = parts[index++];
            String eventsStr = parts[index++];
            List<String> events = eventsStr.isEmpty() ? new ArrayList<>() : new ArrayList<>(List.of(eventsStr.split(",")));
            TriggerSettings trigger = TriggerSettings.builder()
                    .type(triggerType)
                    .schedule(schedule.isEmpty() ? null : schedule)
                    .events(events)
                    .build();

            // CooldownSettings
            int globalCooldown = Integer.parseInt(parts[index++]);
            int perPlayerCooldown = Integer.parseInt(parts[index++]);
            CooldownSettings cooldown = CooldownSettings.builder()
                    .global(globalCooldown)
                    .perPlayer(perPlayerCooldown)
                    .build();

            // Version
            Integer version = parts.length > index ? Integer.parseInt(parts[index++]) : 1;

            // SoundSettings
            SoundSettings sound = null;
            if (parts.length > index && !parts[index].isEmpty()) {
                String soundName = parts[index++];
                float volume = parts.length > index ? Float.parseFloat(parts[index++]) : 1.0f;
                float pitch = parts.length > index ? Float.parseFloat(parts[index++]) : 1.0f;
                sound = SoundSettings.builder()
                        .sound(soundName)
                        .volume(volume)
                        .pitch(pitch)
                        .build();
            } else {
                index += 3;
            }

            return Announcement.builder()
                    .id(id)
                    .enabled(enabled)
                    .priority(priority)
                    .content(content)
                    .display(display)
                    .servers(servers)
                    .target(target)
                    .trigger(trigger)
                    .cooldown(cooldown)
                    .version(version)
                    .sound(sound)
                    .build();

        } catch (Exception e) {
            return null;
        }
    }
}
