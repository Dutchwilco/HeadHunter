package nl.dutchcoding.headhunter.models;

import org.bukkit.entity.EntityType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerMobData {

    private final UUID playerUUID;
    private final Map<EntityType, Integer> mobXP;
    private final Map<EntityType, Integer> mobLevels;

    public PlayerMobData(UUID playerUUID) {
        this.playerUUID = playerUUID;
        this.mobXP = new HashMap<>();
        this.mobLevels = new HashMap<>();
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public int getMobXP(EntityType entityType) {
        return mobXP.getOrDefault(entityType, 0);
    }

    public void setMobXP(EntityType entityType, int xp) {
        mobXP.put(entityType, xp);
    }

    public void addMobXP(EntityType entityType, int xp) {
        mobXP.put(entityType, getMobXP(entityType) + xp);
    }

    public int getMobLevel(EntityType entityType) {
        return mobLevels.getOrDefault(entityType, 0);
    }

    public void setMobLevel(EntityType entityType, int level) {
        mobLevels.put(entityType, level);
    }

    public void incrementMobLevel(EntityType entityType) {
        mobLevels.put(entityType, getMobLevel(entityType) + 1);
    }

    public Map<EntityType, Integer> getAllMobXP() {
        return new HashMap<>(mobXP);
    }

    public Map<EntityType, Integer> getAllMobLevels() {
        return new HashMap<>(mobLevels);
    }

    public int getHighestLevelInAnyTier() {
        return mobLevels.values().stream().mapToInt(Integer::intValue).max().orElse(0);
    }
}
