package cn.handyplus.lib.expand;

import lombok.Generated;

public final class TexturesParam {
   private TexturesParam.Textures textures;

   public String getUrl() {
      return this.textures.SKIN.url;
   }

   @Generated
   public void setTextures(TexturesParam.Textures textures) {
      this.textures = textures;
   }

   @Generated
   public TexturesParam.Textures getTextures() {
      return this.textures;
   }

   public static class Skin {
      private String url;

      @Generated
      public void setUrl(String url) {
         this.url = url;
      }
   }

   public static class Textures {
      private TexturesParam.Skin SKIN;

      @Generated
      public void setSKIN(TexturesParam.Skin SKIN) {
         this.SKIN = SKIN;
      }
   }
}
