package cn.handyplus.lib.util;

import cn.handyplus.lib.core.StrUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.event.ClickEvent.Action;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

class TextAdventureUtil {
   private Component component;

   private TextAdventureUtil() {
   }

   protected static TextAdventureUtil getInstance() {
      return new TextAdventureUtil();
   }

   protected TextAdventureUtil init(String msg) {
      return this.init(msg, true);
   }

   protected TextAdventureUtil init(String msg, boolean isColor) {
      this.component = (Component)(isColor ? ComponentUtil.parseColor(msg) : Component.text(msg));
      return this;
   }

   protected void addClick(Action action, String msg) {
      if (!StrUtil.isEmpty(msg)) {
         this.component = this.component.clickEvent(ClickEvent.clickEvent(action, msg));
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
      this.addClick(Action.COPY_TO_CLIPBOARD, text);
   }

   protected void addHoverText(String text) {
      if (!StrUtil.isEmpty(text)) {
         Component hoverComponent = ComponentUtil.parseColor(text);
         this.component = this.component.hoverEvent(HoverEvent.showText(hoverComponent));
      }
   }

   protected void addHoverItem(ItemStack itemStack) {
      if (itemStack != null) {
         this.component = this.component.hoverEvent(itemStack.asHoverEvent());
      }
   }

   protected void addExtra(Component component) {
      if (component != null) {
         this.component = this.component.append(component);
      }
   }

   protected Component build() {
      return this.component;
   }

   protected void send(CommandSender sender) {
      if (sender != null) {
         sender.sendMessage(this.component);
      }
   }

   protected void sendAll() {
      Bukkit.getOnlinePlayers().forEach(this::send);
   }

   protected void sendConsole() {
      Bukkit.getConsoleSender().sendMessage(this.build());
   }

   protected void sendActionBar(Player player) {
      player.sendActionBar(this.build());
   }

   protected void sendAllActionBar() {
      Bukkit.getOnlinePlayers().forEach(this::sendActionBar);
   }
}
