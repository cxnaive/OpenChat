package cn.handyplus.lib.constants;

import cn.handyplus.lib.core.PatternUtil;
import java.util.regex.Matcher;
import lombok.Generated;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public enum VersionCheckEnum {
   V_26_1_2("26.1.2", "26", "v_26", 26102),
   V_26_1_1("26.1.1", "26", "v_26", 26101),
   V_26_1("26.1", "26", "v_26", 26100),
   V_1_21_11("1.21.11", "1.21", "v1_21_R7", 12111),
   V_1_21_10("1.21.10", "1.21", "v1_21_R6", 12110),
   V_1_21_9("1.21.9", "1.21", "v1_21_R6", 1219),
   V_1_21_8("1.21.8", "1.21", "v1_21_R5", 1218),
   V_1_21_7("1.21.7", "1.21", "v1_21_R5", 1217),
   V_1_21_6("1.21.6", "1.21", "v1_21_R5", 1216),
   V_1_21_5("1.21.5", "1.21", "v1_21_R4", 1215),
   V_1_21_4("1.21.4", "1.21", "v1_21_R3", 1214),
   V_1_21_3("1.21.3", "1.21", "v1_21_R2", 1213),
   V_1_21_2("1.21.2", "1.21", "v1_21_R2", 1212),
   V_1_21_1("1.21.1", "1.21", "v1_21_R1", 1211),
   V_1_21("1.21", "1.21", "v1_21_R1", 1210),
   V_1_20_6("1.20.6", "1.20", "v1_20_R4", 1206),
   V_1_20_5("1.20.5", "1.20", "v1_20_R4", 1205),
   V_1_20_4("1.20.4", "1.20", "v1_20_R3", 1204),
   V_1_20_3("1.20.3", "1.20", "v1_20_R3", 1203),
   V_1_20_2("1.20.2", "1.20", "v1_20_R2", 1202),
   V_1_20("1.20", "1.20", "v1_20_R1", 1200),
   V_1_19_4("1.19.4", "1.19", "v1_19_R3", 1194),
   V_1_19_3("1.19.3", "1.19", "v1_19_R2", 1193),
   V_1_19("1.19", "1.19", "v1_19_R1", 1190),
   V_1_18_2("1.18.2", "1.18", "v1_18_R2", 1182),
   V_1_18("1.18", "1.18", "v1_18_R1", 1180),
   V_1_17("1.17", "1.17", "v1_17_R1", 1170),
   V_1_16_5("1.16.5", "1.16", "v1_16_R3", 1165),
   V_1_16_4("1.16.4", "1.16", "v1_16_R2", 1164),
   V_1_16_3("1.16.3", "1.16", "v1_16_R2", 1163),
   V_1_16_2("1.16.2", "1.16", "v1_16_R2", 1162),
   V_1_16("1.16", "1.16", "v1_16_R1", 1160),
   V_1_15("1.15", "1.15", "v1_15_R1", 1150),
   V_1_14("1.14", "1.14", "v1_14_R1", 1140),
   V_1_13_2("1.13.2", "1.13", "v1_13_R2", 1132),
   V_1_13("1.13", "1.13", "v1_13_R1", 1130),
   V_1_12("1.12", "1.12", "v1_12_R1", 1120),
   V_1_11("1.11", "1.11", "v1_11_R1", 1110),
   V_1_10("1.10", "1.10", "v1_10_R1", 1100),
   V_1_9_4("1.9.4", "1.9", "v1_9_R2", 194),
   V_1_9("1.9", "1.9", "v1_9_R1", 190),
   V_1_8_8("1.8.8", "1.8", "v1_8_R3", 188),
   V_1_8_7("1.8.7", "1.8", "v1_8_R3", 187),
   V_1_8_6("1.8.6", "1.8", "v1_8_R3", 186),
   V_1_8_5("1.8.5", "1.8", "v1_8_R3", 185),
   V_1_8_4("1.8.4", "1.8", "v1_8_R3", 184),
   V_1_8_3("1.8.3", "1.8", "v1_8_R2", 183),
   V_1_8("1.8", "1.8", "v1_8_R1", 180),
   V_1_7("1.7", "1.7", "v1_7_R1", 170),
   V_1_6("1.6", "1.6", "v1_6_R1", 160);

   private final String version;
   private final String mainVersion;
   private final String nmsVersion;
   private final Integer versionId;

   public static VersionCheckEnum getEnum() {
      return getEnum(getMinecraftVersion(Bukkit.getVersion()));
   }

   public static VersionCheckEnum getEnum(@NotNull String version) {
      for (VersionCheckEnum versionCheckEnum : values()) {
         if (version.equals(versionCheckEnum.getVersion())) {
            return versionCheckEnum;
         }
      }

      for (VersionCheckEnum versionCheckEnumx : values()) {
         if (version.contains(versionCheckEnumx.getVersion())) {
            return versionCheckEnumx;
         }
      }

      String[] split = version.split("\\.");
      if (split.length > 0) {
         String mainVersion = split[0];

         for (VersionCheckEnum versionCheckEnumxx : values()) {
            if (mainVersion.equals(versionCheckEnumxx.getMainVersion())) {
               return versionCheckEnumxx;
            }
         }
      }

      throw new IllegalStateException("Unsupported Bukkit version: " + version);
   }

   public static String getMinecraftVersion(String version) {
      Matcher matcher = PatternUtil.VERSION_PATTERN.matcher(version);
      if (matcher.find()) {
         return matcher.group("version");
      } else {
         throw new IllegalStateException("Unsupported Bukkit version: " + version);
      }
   }

   @Generated
   public String getVersion() {
      return this.version;
   }

   @Generated
   public String getMainVersion() {
      return this.mainVersion;
   }

   @Generated
   public String getNmsVersion() {
      return this.nmsVersion;
   }

   @Generated
   public Integer getVersionId() {
      return this.versionId;
   }

   @Generated
   private VersionCheckEnum(final String version, final String mainVersion, final String nmsVersion, final Integer versionId) {
      this.version = version;
      this.mainVersion = mainVersion;
      this.nmsVersion = nmsVersion;
      this.versionId = versionId;
   }
}
