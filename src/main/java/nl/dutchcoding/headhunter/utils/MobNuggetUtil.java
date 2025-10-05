package nl.dutchcoding.headhunter.utils;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class MobNuggetUtil {

    private static JavaPlugin plugin;
    private static NamespacedKey mobTypeKey;
    private static NamespacedKey xpKey;
    private static NamespacedKey coinsKey;

    public static void initialize(JavaPlugin pluginInstance) {
        plugin = pluginInstance;
        mobTypeKey = new NamespacedKey(plugin, "mob_type");
        xpKey = new NamespacedKey(plugin, "xp");
        coinsKey = new NamespacedKey(plugin, "coins");
    }

    public static ItemStack createMobNugget(EntityType mobType, int xp, double coins) {
        ItemStack nugget = new ItemStack(Material.GOLD_NUGGET);
        ItemMeta meta = nugget.getItemMeta();

        if (meta != null) {
            meta.setDisplayName("§6§l" + formatMobName(mobType) + " Token");
            
            nl.dutchcoding.headhunter.HeadHunter pluginInstance = (nl.dutchcoding.headhunter.HeadHunter) plugin;
            List<String> lore = pluginInstance.getMessagesConfig().getStringList("lore.nugget",
                "{xp}", String.valueOf(xp),
                "{coins}", String.format("%.1f", coins));
            meta.setLore(lore);

            // Store data in PDC
            meta.getPersistentDataContainer().set(mobTypeKey, PersistentDataType.STRING, mobType.name());
            meta.getPersistentDataContainer().set(xpKey, PersistentDataType.INTEGER, xp);
            meta.getPersistentDataContainer().set(coinsKey, PersistentDataType.DOUBLE, coins);

            nugget.setItemMeta(meta);
        }

        return nugget;
    }

    public static boolean isMobNugget(ItemStack item) {
        if (item == null || item.getType() != Material.GOLD_NUGGET) return false;
        if (!item.hasItemMeta()) return false;

        ItemMeta meta = item.getItemMeta();
        return meta.getPersistentDataContainer().has(mobTypeKey, PersistentDataType.STRING);
    }

    public static EntityType getMobType(ItemStack item) {
        if (!isMobNugget(item)) return null;

        ItemMeta meta = item.getItemMeta();
        String mobTypeName = meta.getPersistentDataContainer().get(mobTypeKey, PersistentDataType.STRING);
        
        try {
            return EntityType.valueOf(mobTypeName);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static int getXP(ItemStack item) {
        if (!isMobNugget(item)) return 0;

        ItemMeta meta = item.getItemMeta();
        Integer xp = meta.getPersistentDataContainer().get(xpKey, PersistentDataType.INTEGER);
        return xp != null ? xp : 0;
    }

    public static double getCoins(ItemStack item) {
        if (!isMobNugget(item)) return 0;

        ItemMeta meta = item.getItemMeta();
        Double coins = meta.getPersistentDataContainer().get(coinsKey, PersistentDataType.DOUBLE);
        return coins != null ? coins : 0;
    }

    private static String formatMobName(EntityType mobType) {
        String name = mobType.name().toLowerCase().replace("_", " ");
        String[] words = name.split(" ");
        StringBuilder formatted = new StringBuilder();
        
        for (String word : words) {
            if (word.length() > 0) {
                formatted.append(Character.toUpperCase(word.charAt(0)))
                         .append(word.substring(1))
                         .append(" ");
            }
        }
        
        return formatted.toString().trim();
    }
}
