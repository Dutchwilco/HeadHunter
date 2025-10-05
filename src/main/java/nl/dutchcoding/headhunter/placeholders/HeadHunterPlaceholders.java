package nl.dutchcoding.headhunter.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import nl.dutchcoding.headhunter.HeadHunter;
import nl.dutchcoding.headhunter.models.PlayerMobData;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.Map;

public class HeadHunterPlaceholders extends PlaceholderExpansion {
    
    private final HeadHunter plugin;
    
    public HeadHunterPlaceholders(HeadHunter plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public @NotNull String getIdentifier() {
        return "headhunter";
    }
    
    @Override
    public @NotNull String getAuthor() {
        return "wilcodwg";
    }
    
    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }
    
    @Override
    public boolean persist() {
        return true;
    }
    
    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (player == null || !player.hasPlayedBefore()) {
            return "";
        }
        
        PlayerMobData playerData = plugin.getPlayerDataManager().getPlayerData(player.getUniqueId());
        
        // %headhunter_highest_level%
        if (params.equals("highest_level")) {
            return String.valueOf(getHighestLevel(playerData));
        }
        
        // %headhunter_highest_mob%
        if (params.equals("highest_mob")) {
            return getHighestMobName(playerData);
        }
        
        // %headhunter_highest_xp%
        if (params.equals("highest_xp")) {
            return String.valueOf(getHighestXp(playerData));
        }
        
        // %headhunter_total_levels%
        if (params.equals("total_levels")) {
            return String.valueOf(getTotalLevels(playerData));
        }
        
        // %headhunter_total_xp%
        if (params.equals("total_xp")) {
            return String.valueOf(getTotalXp(playerData));
        }
        
        // %headhunter_mob_count%
        if (params.equals("mob_count")) {
            return String.valueOf(playerData.getAllMobLevels().size());
        }
        
        // %headhunter_<mob>_level%
        if (params.endsWith("_level")) {
            String mobName = params.replace("_level", "").toUpperCase();
            try {
                EntityType mobType = EntityType.valueOf(mobName);
                return String.valueOf(playerData.getMobLevel(mobType));
            } catch (IllegalArgumentException e) {
                return "0";
            }
        }
        
        // %headhunter_<mob>_xp%
        if (params.endsWith("_xp")) {
            String mobName = params.replace("_xp", "").toUpperCase();
            try {
                EntityType mobType = EntityType.valueOf(mobName);
                return String.valueOf(playerData.getMobXP(mobType));
            } catch (IllegalArgumentException e) {
                return "0";
            }
        }
        
        return null;
    }
    
    private int getHighestLevel(PlayerMobData playerData) {
        return playerData.getAllMobLevels().values().stream()
                .max(Comparator.naturalOrder())
                .orElse(0);
    }
    
    private String getHighestMobName(PlayerMobData playerData) {
        return playerData.getAllMobLevels().entrySet().stream()
                .max(Comparator.comparingInt(Map.Entry::getValue))
                .map(entry -> formatMobName(entry.getKey()))
                .orElse("None");
    }
    
    private int getHighestXp(PlayerMobData playerData) {
        return playerData.getAllMobXP().values().stream()
                .max(Comparator.naturalOrder())
                .orElse(0);
    }
    
    private int getTotalLevels(PlayerMobData playerData) {
        return playerData.getAllMobLevels().values().stream()
                .mapToInt(Integer::intValue)
                .sum();
    }
    
    private int getTotalXp(PlayerMobData playerData) {
        return playerData.getAllMobXP().values().stream()
                .mapToInt(Integer::intValue)
                .sum();
    }
    
    private String formatMobName(EntityType type) {
        String name = type.name().replace("_", " ").toLowerCase();
        String[] words = name.split(" ");
        StringBuilder result = new StringBuilder();
        
        for (String word : words) {
            if (!word.isEmpty()) {
                result.append(Character.toUpperCase(word.charAt(0)))
                      .append(word.substring(1))
                      .append(" ");
            }
        }
        
        return result.toString().trim();
    }
}
