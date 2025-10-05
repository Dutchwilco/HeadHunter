package nl.dutchcoding.headhunter.config;

import nl.dutchcoding.headhunter.HeadHunter;
import nl.dutchcoding.headhunter.models.MobData;
import nl.dutchcoding.headhunter.models.MobTier;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;

import java.io.File;
import java.util.*;

public class MobsConfig {

    private final HeadHunter plugin;
    private File mobsFile;
    private FileConfiguration mobsConfig;
    private Map<EntityType, MobData> mobDataMap;

    public MobsConfig(HeadHunter plugin) {
        this.plugin = plugin;
        this.mobDataMap = new HashMap<>();
        createMobsFile();
        loadMobData();
    }

    private void createMobsFile() {
        mobsFile = new File(plugin.getDataFolder(), "mobs.yml");
        if (!mobsFile.exists()) {
            mobsFile.getParentFile().mkdirs();
            plugin.saveResource("mobs.yml", false);
        }
        mobsConfig = YamlConfiguration.loadConfiguration(mobsFile);
    }

    public void reload() {
        mobsConfig = YamlConfiguration.loadConfiguration(mobsFile);
        loadMobData();
    }

    private void loadMobData() {
        mobDataMap.clear();
        ConfigurationSection mobsSection = mobsConfig.getConfigurationSection("mobs");
        if (mobsSection == null) return;

        for (String mobKey : mobsSection.getKeys(false)) {
            try {
                EntityType entityType = EntityType.valueOf(mobKey.toUpperCase());
                ConfigurationSection mobSection = mobsSection.getConfigurationSection(mobKey);

                String tierString = mobSection.getString("tier", "COMMON");
                MobTier tier = MobTier.valueOf(tierString.toUpperCase());

                List<Integer> xpRequirements = mobSection.getIntegerList("xp-requirements");
                double baseCoins = mobSection.getDouble("base-coins", 10.0);
                String headTexture = mobSection.getString("head-texture", "");

                MobData mobData = new MobData(entityType, tier, xpRequirements, baseCoins, headTexture);
                mobDataMap.put(entityType, mobData);
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Invalid mob type or tier in mobs.yml: " + mobKey);
            }
        }
    }

    public MobData getMobData(EntityType entityType) {
        return mobDataMap.get(entityType);
    }

    public Map<EntityType, MobData> getAllMobData() {
        return new HashMap<>(mobDataMap);
    }

    public List<EntityType> getMobsByTier(MobTier tier) {
        List<EntityType> mobs = new ArrayList<>();
        for (Map.Entry<EntityType, MobData> entry : mobDataMap.entrySet()) {
            if (entry.getValue().getTier() == tier) {
                mobs.add(entry.getKey());
            }
        }
        return mobs;
    }

    public int getRequiredLevelForTier(MobTier tier) {
        return mobsConfig.getInt("tier-unlock-requirements." + tier.name(), 0);
    }
}
