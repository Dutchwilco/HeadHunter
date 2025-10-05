package nl.dutchcoding.headhunter.models;

import org.bukkit.entity.EntityType;

import java.util.List;

public class MobData {

    private final EntityType entityType;
    private final MobTier tier;
    private final List<Integer> xpRequirements;
    private final double baseCoins;
    private final String headTexture;

    public MobData(EntityType entityType, MobTier tier, List<Integer> xpRequirements, double baseCoins, String headTexture) {
        this.entityType = entityType;
        this.tier = tier;
        this.xpRequirements = xpRequirements;
        this.baseCoins = baseCoins;
        this.headTexture = headTexture;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public MobTier getTier() {
        return tier;
    }

    public List<Integer> getXpRequirements() {
        return xpRequirements;
    }

    public int getXpRequirementForLevel(int level) {
        if (level <= 0 || level > xpRequirements.size()) {
            return Integer.MAX_VALUE;
        }
        return xpRequirements.get(level - 1);
    }

    public double getBaseCoins() {
        return baseCoins;
    }

    public int getMaxLevel() {
        return xpRequirements.size();
    }

    public String getHeadTexture() {
        return headTexture != null ? headTexture : "";
    }
}
