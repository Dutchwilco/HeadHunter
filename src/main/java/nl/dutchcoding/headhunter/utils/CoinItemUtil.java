package nl.dutchcoding.headhunter.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import nl.dutchcoding.headhunter.HeadHunter;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class CoinItemUtil {
    
    private static NamespacedKey coinKey;
    
    public static void initialize(HeadHunter plugin) {
        coinKey = new NamespacedKey(plugin, "coin_value");
    }
    
    public static ItemStack createCoinItem(double value) {
        ItemStack item = new ItemStack(Material.GOLD_NUGGET);
        ItemMeta meta = item.getItemMeta();
        
        if (meta != null) {
            // Set display name
            meta.displayName(Component.text("⚜ Coin Pouch ⚜")
                    .color(TextColor.color(255, 215, 0))
                    .decoration(TextDecoration.ITALIC, false));
            
            // Set lore
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text(""));
            lore.add(Component.text("Value: ")
                    .color(TextColor.color(170, 170, 170))
                    .decoration(TextDecoration.ITALIC, false)
                    .append(Component.text("$" + String.format("%.2f", value))
                            .color(TextColor.color(255, 215, 0))
                            .decoration(TextDecoration.ITALIC, false)));
            lore.add(Component.text(""));
            lore.add(Component.text("Right-click to redeem!")
                    .color(TextColor.color(255, 255, 85))
                    .decoration(TextDecoration.ITALIC, false));
            
            meta.lore(lore);
            
            // Store coin value in PDC
            meta.getPersistentDataContainer().set(coinKey, PersistentDataType.DOUBLE, value);
            
            item.setItemMeta(meta);
        }
        
        return item;
    }
}
