package cn.handyplus.lib.util;

import cn.handyplus.lib.constants.BaseConstants;
import cn.handyplus.lib.constants.VersionCheckEnum;
import cn.handyplus.lib.core.CollUtil;
import cn.handyplus.lib.core.StrUtil;
import java.util.List;
import java.util.UUID;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class RgbTextUtil {
   private TextAdventureUtil textAdventureUtil;
   private TextLegacyUtil textLegacyUtil;

   private RgbTextUtil() {
   }

   public static RgbTextUtil init(String msg) {
      return init(msg, true);
   }

   public static RgbTextUtil init(String msg, boolean isColor) {
      RgbTextUtil util = new RgbTextUtil();
      if (BaseUtil.supportsComponentApi()) {
         util.textAdventureUtil = TextAdventureUtil.getInstance().init(msg, isColor);
         return util;
      } else {
         util.textLegacyUtil = TextLegacyUtil.getInstance().init(msg, isColor);
         return util;
      }
   }

   public RgbTextUtil addClickUrl(String url) {
      if (BaseUtil.supportsComponentApi()) {
         this.textAdventureUtil.addClickUrl(url);
      } else {
         this.textLegacyUtil.addClickUrl(url);
      }

      return this;
   }

   public RgbTextUtil addClickCommand(String command) {
      if (BaseUtil.supportsComponentApi()) {
         this.textAdventureUtil.addClickCommand(command);
      } else {
         this.textLegacyUtil.addClickCommand(command);
      }

      return this;
   }

   public RgbTextUtil addClickSuggestCommand(String suggestCommand) {
      if (BaseUtil.supportsComponentApi()) {
         this.textAdventureUtil.addClickSuggestCommand(suggestCommand);
      } else {
         this.textLegacyUtil.addClickSuggestCommand(suggestCommand);
      }

      return this;
   }

   public RgbTextUtil addClickCopyToClipboard(String text) {
      if (BaseConstants.VERSION_ID < VersionCheckEnum.V_1_15.getVersionId()) {
         return this;
      } else {
         if (BaseUtil.supportsComponentApi()) {
            this.textAdventureUtil.addClickCopyToClipboard(text);
         } else {
            this.textLegacyUtil.addClickCopyToClipboard(text);
         }

         return this;
      }
   }

   public RgbTextUtil addHoverText(String text) {
      if (StrUtil.isEmpty(text)) {
         return this;
      } else {
         if (BaseUtil.supportsComponentApi()) {
            this.textAdventureUtil.addHoverText(text);
         } else {
            this.textLegacyUtil.addHoverText(text);
         }

         return this;
      }
   }

   public RgbTextUtil addHoverText(ItemStack itemStack) {
      if (BaseConstants.VERSION_ID < VersionCheckEnum.V_1_16.getVersionId()) {
         return this;
      } else if (itemStack == null) {
         return this;
      } else {
         if (BaseUtil.supportsComponentApi()) {
            this.textAdventureUtil.addHoverItem(itemStack);
         } else {
            List<String> lore = ItemStackUtil.getItemMeta(itemStack).getLore();
            this.textLegacyUtil.addHoverText(CollUtil.listToStr(lore, "\n"));
         }

         return this;
      }
   }

   public RgbTextUtil addExtra(RgbTextUtil extra) {
      if (BaseUtil.supportsComponentApi()) {
         this.textAdventureUtil.addExtra(extra.textAdventureUtil.build());
      } else {
         this.textLegacyUtil.addExtra(extra.textLegacyUtil.build());
      }

      return this;
   }

   public RgbTextUtil addExtra(String text) {
      return StrUtil.isEmpty(text) ? this : this.addExtra(init(text));
   }

   public void send(CommandSender sender) {
      if (BaseUtil.supportsComponentApi()) {
         this.textAdventureUtil.send(sender);
      } else {
         this.textLegacyUtil.send(sender);
      }
   }

   public void send(List<UUID> playerUuidList) {
      if (!CollUtil.isEmpty(playerUuidList)) {
         if (BaseUtil.supportsComponentApi()) {
            playerUuidList.forEach(uuid -> this.textAdventureUtil.send((CommandSender)BaseUtil.getOnlinePlayer(uuid).orElse(null)));
         } else {
            playerUuidList.forEach(uuid -> this.textLegacyUtil.send((CommandSender)BaseUtil.getOnlinePlayer(uuid).orElse(null)));
         }
      }
   }

   public void sendAll() {
      if (BaseUtil.supportsComponentApi()) {
         this.textAdventureUtil.sendAll();
      } else {
         this.textLegacyUtil.sendAll();
      }
   }

   public void sendConsole() {
      if (BaseUtil.supportsComponentApi()) {
         this.textAdventureUtil.sendConsole();
      } else {
         this.textLegacyUtil.sendConsole();
      }
   }

   public void sendActionBar(Player player) {
      if (BaseUtil.supportsComponentApi()) {
         this.textAdventureUtil.sendActionBar(player);
      } else {
         this.textLegacyUtil.sendActionBar(player);
      }
   }

   public void sendAllActionBar() {
      if (BaseUtil.supportsComponentApi()) {
         this.textAdventureUtil.sendAllActionBar();
      } else {
         this.textLegacyUtil.sendAllActionBar();
      }
   }
}
