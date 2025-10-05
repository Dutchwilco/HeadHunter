package nl.dutchcoding.headhunter.config;

import nl.dutchcoding.headhunter.HeadHunter;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {

    private final HeadHunter plugin;
    private FileConfiguration config;

    public ConfigManager(HeadHunter plugin) {
        this.plugin = plugin;
        plugin.saveDefaultConfig();
        this.config = plugin.getConfig();
    }

    public void reload() {
        plugin.reloadConfig();
        this.config = plugin.getConfig();
    }

    public boolean isSpawnerOnlyMode() {
        return config.getBoolean("spawner-only-mode", true);
    }

    public boolean isPreventItemTrade() {
        // Check new config first, fall back to old config for backwards compatibility
        if (config.contains("prevent-item-trade")) {
            return config.getBoolean("prevent-item-trade", true);
        }
        return config.getBoolean("prevent-head-trade", true);
    }

    public boolean isDropHeadsOnGround() {
        return config.getBoolean("drop-heads-on-ground", false);
    }

    public boolean isShowActionBarOnKill() {
        return config.getBoolean("show-actionbar-on-kill", true);
    }

    public boolean isShowTitleOnLevelUp() {
        return config.getBoolean("show-title-on-levelup", true);
    }

    public boolean isPlaySoundOnLevelUp() {
        return config.getBoolean("play-sound-on-levelup", true);
    }

    public String getLevelUpSound() {
        return config.getString("levelup-sound", "ENTITY_PLAYER_LEVELUP");
    }

    public boolean isPlaySoundOnRedeem() {
        return config.getBoolean("play-sound-on-redeem", true);
    }

    public String getRedeemSound() {
        return config.getString("redeem-sound", "ENTITY_EXPERIENCE_ORB_PICKUP");
    }

    public String getNuggetMaterial() {
        return config.getString("nugget-material", "GOLD_NUGGET");
    }

    public boolean isNuggetGlow() {
        return config.getBoolean("nugget-glow", true);
    }

    public int getGuiSize() {
        return config.getInt("gui-size", 54);
    }

    public boolean isGuiFillEmptySlots() {
        return config.getBoolean("gui-fill-empty-slots", true);
    }

    public String getGuiFillerMaterial() {
        return config.getString("gui-filler-material", "BLACK_STAINED_GLASS_PANE");
    }

    public boolean isVaultEconomyEnabled() {
        return config.getBoolean("vault-economy-enabled", false);
    }

    public String getStorageType() {
        return config.getString("storage-type", "YAML");
    }

    public double getBaseCoinMultiplier() {
        return config.getDouble("base-coin-multiplier", 1.0);
    }

    public double getCoinMultiplierPerLevel() {
        return config.getDouble("coin-multiplier-per-level", 0.1);
    }

    public FileConfiguration getConfig() {
        return config;
    }
}
