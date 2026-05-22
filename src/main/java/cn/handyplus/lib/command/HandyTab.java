package cn.handyplus.lib.command;

import cn.handyplus.lib.core.CollUtil;
import cn.handyplus.lib.core.StrUtil;
import cn.handyplus.lib.util.BaseUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import lombok.Generated;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public final class HandyTab {
   private final HandyTab.Root root;
   private final HandyTab.Node current;
   private final HandyTab.Node branchNode;
   private final boolean fromWhen;

   public HandyTab next(String value) {
      if (StrUtil.isEmpty(value)) {
         throw new IllegalArgumentException("tab next value can not be empty");
      } else {
         HandyTab.Node child = this.current.getOrCreateChild(value);
         return new HandyTab(this.root, child, null, false);
      }
   }

   public HandyTab next(List<String> values) {
      return this.next(context -> values);
   }

   public HandyTab next(Function<HandyTab.Context, List<String>> provider) {
      if (provider == null) {
         throw new IllegalArgumentException("tab provider can not be null");
      } else {
         this.current.addProvider(provider);
         HandyTab.Node anyChild = this.current.getOrCreateAnyChild();
         HandyTab.Node nextBranchNode = this.resolveNextBranchNode();
         return new HandyTab(this.root, anyChild, nextBranchNode, false);
      }
   }

   public HandyTab nextLang(String langKey) {
      return this.next(Collections.singletonList(BaseUtil.getLangMsg(langKey)));
   }

   public HandyTab nextNull() {
      this.current.setDefaultCompletion(true);
      HandyTab.Node anyChild = this.current.getOrCreateAnyChild();
      HandyTab.Node nextBranchNode = this.resolveNextBranchNode();
      return new HandyTab(this.root, anyChild, nextBranchNode, false);
   }

   private HandyTab.Node resolveNextBranchNode() {
      return this.fromWhen && this.branchNode != null ? this.branchNode : this.current;
   }

   public HandyTab when(String value) {
      if (this.branchNode == null) {
         throw new IllegalStateException("when must be called after next");
      } else if (StrUtil.isEmpty(value)) {
         throw new IllegalArgumentException("when value can not be empty");
      } else {
         HandyTab.Node child = this.branchNode.getOrCreateChild(value);
         return new HandyTab(this.root, child, this.branchNode, true);
      }
   }

   static HandyTab create() {
      HandyTab.Root root = new HandyTab.Root();
      return new HandyTab(root, root.rootNode, null, false);
   }

   void register(IHandyCommandEvent handyCommandEvent) {
      if (handyCommandEvent != null && !StrUtil.isEmpty(handyCommandEvent.command())) {
         HandyTab.Node child = this.root.rootNode.getOrCreateChild(handyCommandEvent.command().trim());
         child.setPermission(StrUtil.isEmpty(handyCommandEvent.permission()) ? null : handyCommandEvent.permission().trim());
         handyCommandEvent.tab(new HandyTab(this.root, child, null, false));
      }
   }

   List<String> complete(CommandSender sender, Command cmd, String label, String[] args) {
      return this.root.complete(sender, cmd, label, args);
   }

   @Generated
   private HandyTab(HandyTab.Root root, HandyTab.Node current, HandyTab.Node branchNode, boolean fromWhen) {
      this.root = root;
      this.current = current;
      this.branchNode = branchNode;
      this.fromWhen = fromWhen;
   }

   public static final class Context {
      private final CommandSender sender;
      private final Command cmd;
      private final String label;
      private final String[] args;
      private final int index;

      @Generated
      public CommandSender getSender() {
         return this.sender;
      }

      @Generated
      public Command getCmd() {
         return this.cmd;
      }

      @Generated
      public String getLabel() {
         return this.label;
      }

      @Generated
      public String[] getArgs() {
         return this.args;
      }

      @Generated
      public int getIndex() {
         return this.index;
      }

      @Generated
      private Context(CommandSender sender, Command cmd, String label, String[] args, int index) {
         this.sender = sender;
         this.cmd = cmd;
         this.label = label;
         this.args = args;
         this.index = index;
      }
   }

   private static final class Node {
      private final String value;
      private final Map<String, HandyTab.Node> children = new LinkedHashMap<>();
      private final List<Function<HandyTab.Context, List<String>>> providers = new ArrayList<>();
      private HandyTab.Node anyChild;
      private boolean defaultCompletion;
      private String permission;

      private HandyTab.Node getOrCreateChild(String childValue) {
         String key = childValue.toLowerCase();
         HandyTab.Node child = this.children.get(key);
         if (child != null) {
            return child;
         } else {
            HandyTab.Node node = new HandyTab.Node(childValue);
            this.children.put(key, node);
            return node;
         }
      }

      private HandyTab.Node getOrCreateAnyChild() {
         if (this.anyChild == null) {
            this.anyChild = new HandyTab.Node("*");
         }

         return this.anyChild;
      }

      private void addProvider(Function<HandyTab.Context, List<String>> provider) {
         this.providers.add(provider);
      }

      private HandyTab.Node findChild(CommandSender sender, String input) {
         if (input == null) {
            input = "";
         }

         HandyTab.Node exact = this.children.get(input.toLowerCase());
         if (exact != null && exact.hasPermission(sender)) {
            return exact;
         } else {
            return this.anyChild != null && this.anyChild.hasPermission(sender) ? this.anyChild : null;
         }
      }

      private boolean hasPermission(CommandSender sender) {
         return StrUtil.isEmpty(this.permission) || sender.hasPermission(this.permission);
      }

      private List<String> collectSuggestions(HandyTab.Context context) {
         if (this.defaultCompletion) {
            return null;
         } else {
            Map<String, String> result = new LinkedHashMap<>();
            if (CollUtil.isNotEmpty(this.providers)) {
               for (Function<HandyTab.Context, List<String>> provider : this.providers) {
                  List<String> values = provider.apply(context);
                  if (!CollUtil.isEmpty(values)) {
                     for (String value : values) {
                        if (value != null) {
                           result.putIfAbsent(value.toLowerCase(), value);
                        }
                     }
                  }
               }
            }

            for (HandyTab.Node child : this.children.values()) {
               if (child.hasPermission(context.getSender()) && child.value != null) {
                  result.putIfAbsent(child.value.toLowerCase(), child.value);
               }
            }

            return new ArrayList<>(result.values());
         }
      }

      @Generated
      private Node(String value) {
         this.value = value;
      }

      @Generated
      private void setDefaultCompletion(boolean defaultCompletion) {
         this.defaultCompletion = defaultCompletion;
      }

      @Generated
      private void setPermission(String permission) {
         this.permission = permission;
      }
   }

   private static final class Root {
      private final HandyTab.Node rootNode = new HandyTab.Node(null);

      private Root() {
      }

      private List<String> complete(CommandSender sender, Command cmd, String label, String[] args) {
         String[] actualArgs = args == null ? new String[0] : args;
         HandyTab.Node currentNode = this.rootNode;
         int lastIndex = actualArgs.length == 0 ? 0 : actualArgs.length - 1;

         for (int i = 0; i < actualArgs.length - 1; i++) {
            currentNode = currentNode.findChild(sender, actualArgs[i]);
            if (currentNode == null) {
               return Collections.emptyList();
            }
         }

         String keyword = actualArgs.length == 0 ? "" : actualArgs[lastIndex];
         HandyTab.Context context = new HandyTab.Context(sender, cmd, label, actualArgs, lastIndex);
         List<String> suggestions = currentNode.collectSuggestions(context);
         return suggestions == null ? null : this.filterAndSort(suggestions, keyword);
      }

      private List<String> filterAndSort(List<String> suggestions, String keyword) {
         if (CollUtil.isEmpty(suggestions)) {
            return Collections.emptyList();
         } else {
            String lowerKeyword = keyword == null ? "" : keyword.toLowerCase();
            List<String> result = new ArrayList<>();

            for (String suggestion : suggestions) {
               if (suggestion != null && (lowerKeyword.isEmpty() || suggestion.toLowerCase().startsWith(lowerKeyword))) {
                  result.add(suggestion);
               }
            }

            result.sort(String.CASE_INSENSITIVE_ORDER);
            return result;
         }
      }
   }
}
