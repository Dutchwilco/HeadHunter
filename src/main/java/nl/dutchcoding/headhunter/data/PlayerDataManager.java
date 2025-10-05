package nl.dutchcoding.headhunter.data;

import nl.dutchcoding.headhunter.HeadHunter;
import nl.dutchcoding.headhunter.models.PlayerMobData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDataManager {

    private final HeadHunter plugin;
    private final File dataFolder;
    private final Map<UUID, PlayerMobData> playerDataCache;

    public PlayerDataManager(HeadHunter plugin) {
        this.plugin = plugin;
        this.dataFolder = new File(plugin.getDataFolder(), "playerdata");
        this.playerDataCache = new HashMap<>();
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
    }

    public PlayerMobData getPlayerData(UUID playerUUID) {
        return playerDataCache.computeIfAbsent(playerUUID, uuid -> {
            PlayerMobData data = loadPlayerData(uuid);
            return data != null ? data : new PlayerMobData(uuid);
        });
    }

    public void loadAllData() {
        File[] files = dataFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files != null) {
            for (File file : files) {
                String fileName = file.getName().replace(".yml", "");
                try {
                    UUID playerUUID = UUID.fromString(fileName);
                    loadPlayerData(playerUUID);
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Invalid player data file: " + fileName);
                }
            }
        }
    }

    private PlayerMobData loadPlayerData(UUID playerUUID) {
        File playerFile = new File(dataFolder, playerUUID.toString() + ".yml");
        if (!playerFile.exists()) {
            return null;
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
        PlayerMobData playerData = new PlayerMobData(playerUUID);

        ConfigurationSection xpSection = config.getConfigurationSection("mob-xp");
        if (xpSection != null) {
            for (String mobKey : xpSection.getKeys(false)) {
                try {
                    EntityType entityType = EntityType.valueOf(mobKey.toUpperCase());
                    int xp = xpSection.getInt(mobKey);
                    playerData.setMobXP(entityType, xp);
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Invalid entity type in player data: " + mobKey);
                }
            }
        }

        ConfigurationSection levelsSection = config.getConfigurationSection("mob-levels");
        if (levelsSection != null) {
            for (String mobKey : levelsSection.getKeys(false)) {
                try {
                    EntityType entityType = EntityType.valueOf(mobKey.toUpperCase());
                    int level = levelsSection.getInt(mobKey);
                    playerData.setMobLevel(entityType, level);
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Invalid entity type in player data: " + mobKey);
                }
            }
        }

        playerDataCache.put(playerUUID, playerData);
        return playerData;
    }

    public void savePlayerData(UUID playerUUID) {
        PlayerMobData playerData = playerDataCache.get(playerUUID);
        if (playerData == null) return;

        File playerFile = new File(dataFolder, playerUUID.toString() + ".yml");
        FileConfiguration config = new YamlConfiguration();

        for (Map.Entry<EntityType, Integer> entry : playerData.getAllMobXP().entrySet()) {
            config.set("mob-xp." + entry.getKey().name(), entry.getValue());
        }

        for (Map.Entry<EntityType, Integer> entry : playerData.getAllMobLevels().entrySet()) {
            config.set("mob-levels." + entry.getKey().name(), entry.getValue());
        }

        try {
            config.save(playerFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save player data for " + playerUUID + ": " + e.getMessage());
        }
    }

    public void saveAllData() {
        for (UUID playerUUID : playerDataCache.keySet()) {
            savePlayerData(playerUUID);
        }
    }

    public void unloadPlayerData(UUID playerUUID) {
        savePlayerData(playerUUID);
        playerDataCache.remove(playerUUID);
    }
}
