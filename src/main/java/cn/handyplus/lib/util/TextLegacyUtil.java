package cn.handyplus.lib.util;

import cn.handyplus.lib.constants.BaseConstants;
import cn.handyplus.lib.constants.VersionCheckEnum;
import cn.handyplus.lib.core.StrUtil;
import java.util.Arrays;
import java.util.stream.Stream;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

class TextLegacyUtil {
   private BaseComponent[] baseComponents;

   private TextLegacyUtil() {
   }

   protected static TextLegacyUtil getInstance() {
      return new TextLegacyUtil();
   }

   protected TextLegacyUtil init(String msg) {
      return this.init(msg, true);
   }

   protected TextLegacyUtil init(String msg, boolean isColor) {
      this.baseComponents = TextComponent.fromLegacyText(isColor ? LegacyUtil.parseColor(msg) : msg);
      return this;
   }

   protected void addClick(Action action, String msg) {
      if (!StrUtil.isEmpty(msg)) {
         for (BaseComponent baseComponent : this.baseComponents) {
            baseComponent.setClickEvent(new ClickEvent(action, msg));
         }
      }
   }

   protected void addClickUrl(String url) {
      this.addClick(Action.OPEN_URL, url);
   }

   protected void addClickCommand(String command) {
      this.addClick(Action.RUN_COMMAND, command);
   }

   protected void addClickSuggestCommand(String suggestCommand) {
      this.addClick(Action.SUGGEST_COMMAND, suggestCommand);
   }

   protected void addClickCopyToClipboard(String text) {
      if (BaseConstants.VERSION_ID >= VersionCheckEnum.V_1_15.getVersionId()) {
         this.addClick(Action.COPY_TO_CLIPBOARD, text);
      }
   }

   protected void addHoverText(String text) {
      if (!StrUtil.isEmpty(text)) {
         for (BaseComponent baseComponent : this.baseComponents) {
            baseComponent.setHoverEvent(
               new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(LegacyUtil.parseColor(text)).create())
            );
         }
      }
   }

   protected void addExtra(BaseComponent[] extra) {
      this.baseComponents = Stream.concat(Arrays.stream(this.baseComponents), Arrays.stream(extra)).toArray(BaseComponent[]::new);
   }

   protected BaseComponent[] build() {
      return this.baseComponents;
   }

   protected void send(CommandSender sender) {
      if (sender != null) {
         if (BaseUtil.isPlayer(sender)) {
            ((Player)sender).spigot().sendMessage(ChatMessageType.CHAT, this.build());
         } else {
            sender.sendMessage(BaseComponent.toLegacyText(this.build()));
         }
      }
   }

   protected void sendAll() {
      Bukkit.getOnlinePlayers().forEach(this::send);
   }

   protected void sendConsole() {
      Bukkit.getConsoleSender().sendMessage(BaseComponent.toLegacyText(this.build()));
   }

   protected void sendActionBar(Player player) {
      player.spigot().sendMessage(ChatMessageType.ACTION_BAR, this.build());
   }

   protected void sendAllActionBar() {
      Bukkit.getOnlinePlayers().forEach(this::sendActionBar);
   }
}
