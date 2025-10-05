package nl.dutchcoding.headhunter.utils;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

public class MobHeadUtil {

    private static final NamespacedKey MOBHEAD_KEY = new NamespacedKey("headhunter", "mobhead");
    private static final NamespacedKey OWNER_KEY = new NamespacedKey("headhunter", "owner");
    private static final NamespacedKey MOBTYPE_KEY = new NamespacedKey("headhunter", "mobtype");
    private static final NamespacedKey XP_KEY = new NamespacedKey("headhunter", "xp");
    private static final NamespacedKey COINS_KEY = new NamespacedKey("headhunter", "coins");

    public static ItemStack createMobHead(EntityType mobType, UUID ownerUUID, int xp, double coins, String customTexture) {
        ItemStack head = new ItemStack(getMaterialForMob(mobType));
        ItemMeta meta = head.getItemMeta();

        meta.setDisplayName("§6§l" + mobType.name() + " Head");

        List<String> lore = new ArrayList<>();
        lore.add("§7Right-click to consume!");
        lore.add("§7");
        lore.add("§eXP: §f+" + xp);
        lore.add("§eCoins: §f+" + String.format("%.2f", coins));
        lore.add("§7");
        lore.add("§eOwner: §f" + ownerUUID.toString().substring(0, 8));
        meta.setLore(lore);

        // Store data in persistent data container
        meta.getPersistentDataContainer().set(MOBHEAD_KEY, PersistentDataType.STRING, "true");
        meta.getPersistentDataContainer().set(OWNER_KEY, PersistentDataType.STRING, ownerUUID.toString());
        meta.getPersistentDataContainer().set(MOBTYPE_KEY, PersistentDataType.STRING, mobType.name());
        meta.getPersistentDataContainer().set(XP_KEY, PersistentDataType.INTEGER, xp);
        meta.getPersistentDataContainer().set(COINS_KEY, PersistentDataType.DOUBLE, coins);

        head.setItemMeta(meta);

        // Apply custom texture if provided and it's a player head
        if (customTexture != null && !customTexture.isEmpty() && head.getType() == Material.PLAYER_HEAD) {
            SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
            PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());
            
            // Convert texture ID to base64 if it's just the hash
            String textureValue = customTexture;
            if (!customTexture.startsWith("eyJ")) { // Not already base64
                textureValue = textureHashToBase64(customTexture);
            }
            
            profile.getProperties().add(new ProfileProperty("textures", textureValue));
            skullMeta.setPlayerProfile(profile);
            head.setItemMeta(skullMeta);
        }

        return head;
    }

    public static boolean isMobHead(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer().has(MOBHEAD_KEY, PersistentDataType.STRING);
    }

    public static boolean isOwner(ItemStack item, UUID playerUUID) {
        if (!isMobHead(item)) return false;
        String ownerString = item.getItemMeta().getPersistentDataContainer().get(OWNER_KEY, PersistentDataType.STRING);
        return ownerString != null && ownerString.equals(playerUUID.toString());
    }

    public static EntityType getMobType(ItemStack item) {
        if (!isMobHead(item)) return null;
        String mobTypeString = item.getItemMeta().getPersistentDataContainer().get(MOBTYPE_KEY, PersistentDataType.STRING);
        try {
            return EntityType.valueOf(mobTypeString);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static int getStoredXP(ItemStack item) {
        if (!isMobHead(item)) return 0;
        Integer xp = item.getItemMeta().getPersistentDataContainer().get(XP_KEY, PersistentDataType.INTEGER);
        return xp != null ? xp : 1;
    }

    public static double getStoredCoins(ItemStack item) {
        if (!isMobHead(item)) return 0.0;
        Double coins = item.getItemMeta().getPersistentDataContainer().get(COINS_KEY, PersistentDataType.DOUBLE);
        return coins != null ? coins : 0.0;
    }

    private static Material getMaterialForMob(EntityType mobType) {
        return switch (mobType) {
            case SKELETON -> Material.SKELETON_SKULL;
            case ZOMBIE -> Material.ZOMBIE_HEAD;
            case CREEPER -> Material.CREEPER_HEAD;
            case WITHER_SKELETON -> Material.WITHER_SKELETON_SKULL;
            case ENDER_DRAGON -> Material.DRAGON_HEAD;
            case PIGLIN -> Material.PIGLIN_HEAD;
            default -> Material.PLAYER_HEAD;
        };
    }

    public static ItemStack createCoinItem(double coins) {
        ItemStack coinItem = new ItemStack(Material.GOLD_NUGGET);
        ItemMeta meta = coinItem.getItemMeta();
        meta.setDisplayName("§6§l+" + String.format("%.1f", coins) + " Coins");
        coinItem.setItemMeta(meta);
        return coinItem;
    }

    /**
     * Converts a texture hash (from minecraft.net URL) to base64 format
     * @param textureHash The texture hash (e.g., 42885ec6e1b58798158e03cc02d8983cc4a1536394f4e4c4c82a06910d64ad2b)
     * @return Base64 encoded texture data
     */
    private static String textureHashToBase64(String textureHash) {
        String url = "{\"textures\":{\"SKIN\":{\"url\":\"http://textures.minecraft.net/texture/" + textureHash + "\"}}}";
        return Base64.getEncoder().encodeToString(url.getBytes());
    }
}
