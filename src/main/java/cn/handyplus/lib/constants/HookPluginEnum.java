package cn.handyplus.lib.constants;

import lombok.Generated;

public enum HookPluginEnum {
   VAULT("Vault", "vaultSucceedMsg", "vaultFailureMsg"),
   PLACEHOLDER_API("PlaceholderAPI", "placeholderAPISucceedMsg", "placeholderAPIFailureMsg"),
   PLAYER_POINTS("PlayerPoints", "playerPointsSucceedMsg", "playerPointsFailureMsg"),
   MYTHIC_MOBS("MythicMobs", "MythicMobsSucceedMsg", "MythicMobsFailureMsg"),
   CITIZENS("Citizens", "CitizensSucceedMsg", "CitizensFailureMsg"),
   ATTRIBUTE_PLUS("AttributePlus", "attributePlusSucceedMsg", "attributePlusFailureMsg"),
   ATTRIBUTE_SYSTEM("AttributeSystem", "attributeSystemSucceedMsg", "attributeSystemFailureMsg"),
   SX_ATTRIBUTE("SX-Attribute", "sxAttributeSucceedMsg", "sxAttributeFailureMsg"),
   PLAYER_CURRENCY("PlayerCurrency", "playerCurrencySucceedMsg", "playerCurrencyFailureMsg"),
   MMO_ITEMS("MMOItems", "mmoItemsSucceedMsg", "mmoItemsFailureMsg"),
   PLAYER_PARTICLES("PlayerParticles", "playerParticlesSucceedMsg", "playerParticlesFailureMsg"),
   SUPER_TRAILS("SuperTrails", "superTrailsSucceedMsg", "superTrailsFailureMsg"),
   SUPER_TRAILS_PRO("SuperTrailsPro", "superTrailsSucceedMsg", "superTrailsFailureMsg"),
   PLAYER_GUILD("PlayerGuild", "playerGuildSucceedMsg", "playerGuildFailureMsg"),
   MULTIVERSE_CORE("Multiverse-Core", "multiverseCoreSucceedMsg", "multiverseCoreFailureMsg"),
   WORLD_BORDER("WorldBorder", "worldBorderSucceedMsg", "worldBorderFailureMsg"),
   PLAYER_TITLE("PlayerTitle", "playerTitleSucceedMsg", "playerTitleFailureMsg"),
   PLAYER_TASK("PlayerTask", "playerTaskSucceedMsg", "playerTaskFailureMsg"),
   PLAYER_RACE("PlayerRace", "playerRaceSucceedMsg", "playerRaceFailureMsg"),
   WORLD_EDIT("WorldEdit", "worldEditSucceedMsg", "worldEditFailureMsg"),
   PLAYER_CHAT("PlayerChat", "playerChatSucceedMsg", "playerChatFailureMsg"),
   ADAPT("Adapt", "adaptSucceedMsg", "adaptFailureMsg"),
   MC_MMO("mcMMO", "mcMMOSucceedMsg", "mcMMOFailureMsg"),
   Jobs("Jobs", "jobsSucceedMsg", "jobsFailureMsg"),
   ADYESHACH("Adyeshach", "adyeshachSucceedMsg", "adyeshachFailureMsg"),
   Z_NPC_S_PLUS("ZNPCsPlus", "zNPCsPlusSucceedMsg", "zNPCsPlusFailureMsg"),
   FANCY_NPCS("FancyNpcs", "fancyNpcsSucceedMsg", "fancyNpcsFailureMsg"),
   NBT_API("NBTAPI", "nbtApiSucceedMsg", "nbtApiFailureMsg"),
   PLAYER_INTENSIFY("PlayerIntensify", "playerIntensifySucceedMsg", "playerIntensifyFailureMsg"),
   LIBS_DISGUISES("LibsDisguises", "libsDisguisesSucceedMsg", "libsDisguisesFailureMsg"),
   MONSTER_PLUS("MonsterPlus", "monsterPlusSucceedMsg", "monsterPlusFailureMsg"),
   MY_PET("MyPet", "myPetSucceedMsg", "myPetFailureMsg"),
   MYTHIC_LIB("MythicLib", "mythicLibSucceedMsg", "mythicLibFailureMsg"),
   SAGA_LORE_STATS("SagaLoreStats", "sagaLoreStatsSucceedMsg", "sagaLoreStatsFailureMsg"),
   AUTH_ME("AuthMe", "authMeSucceedMsg", "authMeFailureMsg"),
   CAT_SEED_LOGIN("CatSeedLogin", "catSeedLoginSucceedMsg", "catSeedLoginFailureMsg"),
   OAUTH_LOGIN("OauthLogin", "oauthLoginSucceedMsg", "oauthLoginFailureMsg"),
   CUSTOM_FISHING("CustomFishing", "customFishingSucceedMsg", "customFishingFailureMsg"),
   LUCK_PERMS("LuckPerms", "luckPermsSucceedMsg", "luckPermsFailureMsg"),
   CRAFT_ENGINE("CraftEngine", "craftEngineSucceedMsg", "craftEngineFailureMsg"),
   DISCORD_SRV("DiscordSRV", "discordSRVSucceedMsg", "discordSRVFailureMsg"),
   DEEP_SEEK("DeepSeek", "deepSeekSucceedMsg", "deepSeekFailureMsg"),
   DECENT_HOLOGRAMS("DecentHolograms", "decentHologramsSucceedMsg", "decentHologramsFailureMsg"),
   HOLOGRAPHIC_DISPLAYS("HolographicDisplays", "holographicDisplaysSucceedMsg", "holographicDisplaysFailureMsg"),
   CMI("CMI", "cmiSucceedMsg", "cmiFailureMsg"),
   FANCY_HOLOGRAMS("FancyHolograms", "fancyHologramsSucceedMsg", "fancyHologramsFailureMsg"),
   MONSTER_API("MonsterAPI", "monsterAPISucceedMsg", "monsterAPIFailureMsg"),
   GEMS_ECONOMY("GemsEconomy", "gemsEconomySucceedMsg", "gemsEconomyFailureMsg"),
   COINS_ENGINE("CoinsEngine", "coinsEngineSucceedMsg", "coinsEngineFailureMsg"),
   PROTOCOL_LIB("ProtocolLib", "protocolLibSucceedMsg", "protocolLibFailureMsg"),
   PLAYER_SCOREBOARD("PlayerScoreboard", "playerScoreboardSucceedMsg", "playerScoreboardFailureMsg"),
   ITEMS_ADDER("ItemsAdder", "itemsAdderSucceedMsg", "itemsAdderFailureMsg");

   private final String name;
   private final String successMsg;
   private final String failMsg;

   @Generated
   public String getName() {
      return this.name;
   }

   @Generated
   public String getSuccessMsg() {
      return this.successMsg;
   }

   @Generated
   public String getFailMsg() {
      return this.failMsg;
   }

   @Generated
   private HookPluginEnum(final String name, final String successMsg, final String failMsg) {
      this.name = name;
      this.successMsg = successMsg;
      this.failMsg = failMsg;
   }
}
