package cn.handyplus.lib.internal.base;

import cn.handyplus.lib.internal.base.annotations.XChange;
import cn.handyplus.lib.internal.base.annotations.XInfo;
import cn.handyplus.lib.internal.base.annotations.XMerge;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class XModuleMetadata {
   private final boolean wasRemoved;
   private final XChange[] changes;
   private final XMerge[] merges;
   private final XInfo info;

   public XModuleMetadata(boolean wasRemoved, XChange[] changes, XMerge[] merges, XInfo info) {
      this.wasRemoved = wasRemoved;
      this.changes = changes;
      this.merges = merges;
      this.info = info;
   }

   public boolean wasRemoved() {
      return this.wasRemoved;
   }

   public XChange[] getChanges() {
      return this.changes;
   }

   public XMerge[] getMerges() {
      return this.merges;
   }

   public XInfo getInfo() {
      return this.info;
   }
}
