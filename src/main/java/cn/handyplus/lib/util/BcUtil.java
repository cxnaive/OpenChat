package cn.handyplus.lib.util;

import cn.handyplus.lib.InitApi;
import cn.handyplus.lib.core.JsonUtil;
import cn.handyplus.lib.core.MapUtil;
import cn.handyplus.lib.core.StrUtil;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import lombok.Generated;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class BcUtil {
   public static final String BUNGEE_CORD_CHANNEL = "BungeeCord";
   private static final String CONNECT = "Connect";
   private static final String FORWARD = "Forward";
   private static final String GET_SERVER = "GetServer";
   private static final String PLAYER_COUNT = "PlayerCount";
   private static final String PLAYER_LIST = "PlayerList";
   private static final String ALL = "ALL";

   private BcUtil() {
   }

   public static void registerOut() {
      Bukkit.getMessenger().registerOutgoingPluginChannel(InitApi.PLUGIN, "BungeeCord");
   }

   public static void unregisterOut() {
      Bukkit.getMessenger().unregisterOutgoingPluginChannel(InitApi.PLUGIN, "BungeeCord");
   }

   public static void tpConnect(Player player, String serverName) {
      ByteArrayDataOutput out = ByteStreams.newDataOutput();
      out.writeUTF("Connect");
      out.writeUTF(serverName);
      player.sendPluginMessage(InitApi.PLUGIN, "BungeeCord", out.toByteArray());
   }

   public static void sendForward(CommandSender sender, String content) {
      Player player = BaseUtil.isPlayer(sender) ? (Player)sender : (Player)Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
      if (player != null) {
         sendForward(player, content);
      }
   }

   public static void sendForward(Player player, String content) {
      ByteArrayDataOutput out = ByteStreams.newDataOutput();
      out.writeUTF("Forward");
      out.writeUTF("ALL");
      out.writeUTF("BungeeCord");
      ByteArrayDataOutput dataOut = ByteStreams.newDataOutput();
      dataOut.writeUTF(content);
      out.writeShort(dataOut.toByteArray().length);
      out.write(dataOut.toByteArray());
      player.sendPluginMessage(InitApi.PLUGIN, "BungeeCord", out.toByteArray());
   }

   public static Optional<String> getContentByForward(byte[] message) {
      ByteArrayDataInput in = ByteStreams.newDataInput(message);
      String subChannel = in.readUTF();
      if (!"BungeeCord".equals(subChannel)) {
         return Optional.empty();
      } else {
         byte[] bytes = new byte[in.readShort()];
         in.readFully(bytes);
         ByteArrayDataInput dataInput = ByteStreams.newDataInput(bytes);
         return Optional.of(dataInput.readUTF());
      }
   }

   public static void sendParamForward(CommandSender sender, BcUtil.BcMessageParam content) {
      sendForward(sender, JsonUtil.toJson(content));
   }

   public static void sendParamForward(Player player, BcUtil.BcMessageParam content) {
      sendForward(player, JsonUtil.toJson(content));
   }

   public static Optional<BcUtil.BcMessageParam> getParamByForward(byte[] message) {
      Optional<String> jsonOpt = getContentByForward(message);
      if (!jsonOpt.isPresent()) {
         return Optional.empty();
      } else {
         MessageUtil.sendConsoleDebugMessage("消息内容为:" + jsonOpt.get());
         BcUtil.BcMessageParam param = JsonUtil.toBean(jsonOpt.get(), BcUtil.BcMessageParam.class);
         return param != null && !StrUtil.isEmpty(param.getPluginName()) && InitApi.PLUGIN.getName().equals(param.getPluginName())
            ? Optional.of(param)
            : Optional.empty();
      }
   }

   public static void sendGetServer() {
      Player player = (Player)Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
      if (player != null) {
         ByteArrayDataOutput out = ByteStreams.newDataOutput();
         out.writeUTF("GetServer");
         player.sendPluginMessage(InitApi.PLUGIN, "BungeeCord", out.toByteArray());
      }
   }

   public static Optional<String> getServerName(byte[] message) {
      ByteArrayDataInput in = ByteStreams.newDataInput(message);
      String type = in.readUTF();
      return !"GetServer".equals(type) ? Optional.empty() : Optional.of(in.readUTF());
   }

   public static void sendPlayerCount() {
      Player player = (Player)Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
      if (player != null) {
         ByteArrayDataOutput out = ByteStreams.newDataOutput();
         out.writeUTF("PlayerCount");
         out.writeUTF("ALL");
         player.sendPluginMessage(InitApi.PLUGIN, "BungeeCord", out.toByteArray());
      }
   }

   public static Map<String, Integer> getPlayerCount(byte[] message) {
      try {
         ByteArrayDataInput in = ByteStreams.newDataInput(message);
         String type = in.readUTF();
         if (!"PlayerCount".equals(type)) {
            return MapUtil.of();
         } else {
            String server = in.readUTF();
            int count = in.readInt();
            return ImmutableMap.of(server, count);
         }
      } catch (Exception var5) {
         InitApi.PLUGIN.getLogger().log(Level.SEVERE, "getPlayerCount 发生异常", (Throwable)var5);
         return MapUtil.of();
      }
   }

   public static void sendPlayerList() {
      Player player = (Player)Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
      if (player != null) {
         ByteArrayDataOutput out = ByteStreams.newDataOutput();
         out.writeUTF("PlayerList");
         out.writeUTF("ALL");
         player.sendPluginMessage(InitApi.PLUGIN, "BungeeCord", out.toByteArray());
      }
   }

   public static List<String> getPlayerList(byte[] message) {
      try {
         ByteArrayDataInput in = ByteStreams.newDataInput(message);
         String type = in.readUTF();
         if (!"PlayerList".equals(type)) {
            return new ArrayList<>();
         } else {
            String server = in.readUTF();
            String playerList = in.readUTF();
            return StrUtil.strToStrList(playerList, ",");
         }
      } catch (Exception var5) {
         InitApi.PLUGIN.getLogger().log(Level.SEVERE, "getPlayerList 发生异常", (Throwable)var5);
         return new ArrayList<>();
      }
   }

   public static class BcMessageParam {
      private String pluginName;
      private String type;
      private String message;
      private String playerName;
      private Long timestamp;

      @Generated
      public String getPluginName() {
         return this.pluginName;
      }

      @Generated
      public String getType() {
         return this.type;
      }

      @Generated
      public String getMessage() {
         return this.message;
      }

      @Generated
      public String getPlayerName() {
         return this.playerName;
      }

      @Generated
      public Long getTimestamp() {
         return this.timestamp;
      }

      @Generated
      public void setPluginName(String pluginName) {
         this.pluginName = pluginName;
      }

      @Generated
      public void setType(String type) {
         this.type = type;
      }

      @Generated
      public void setMessage(String message) {
         this.message = message;
      }

      @Generated
      public void setPlayerName(String playerName) {
         this.playerName = playerName;
      }

      @Generated
      public void setTimestamp(Long timestamp) {
         this.timestamp = timestamp;
      }
   }
}
