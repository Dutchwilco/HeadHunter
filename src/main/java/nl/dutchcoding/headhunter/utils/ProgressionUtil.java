package nl.dutchcoding.headhunter.utils;

import nl.dutchcoding.headhunter.HeadHunter;
import nl.dutchcoding.headhunter.models.MobTier;
import nl.dutchcoding.headhunter.models.PlayerMobData;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.List;

public class ProgressionUtil {

    public static boolean hasUnlockedTier(Player player, MobTier tier, HeadHunter plugin) {
        if (tier == MobTier.COMMON) {
            return true; // Common tier is always unlocked
        }

        PlayerMobData playerData = plugin.getPlayerDataManager().getPlayerData(player.getUniqueId());
        int requiredLevel = plugin.getMobsConfig().getRequiredLevelForTier(tier);

        // Check if all mobs in the previous tier have reached the required level
        MobTier previousTier = getPreviousTier(tier);
        if (previousTier == null) return true;

        List<EntityType> previousTierMobs = plugin.getMobsConfig().getMobsByTier(previousTier);

        for (EntityType mobType : previousTierMobs) {
            int playerLevel = playerData.getMobLevel(mobType);
            if (playerLevel < requiredLevel) {
                return false;
            }
        }

        return true;
    }

    private static MobTier getPreviousTier(MobTier tier) {
        return switch (tier) {
            case UNCOMMON -> MobTier.COMMON;
            case RARE -> MobTier.UNCOMMON;
            case LEGENDARY -> MobTier.RARE;
            default -> null;
        };
    }
}
