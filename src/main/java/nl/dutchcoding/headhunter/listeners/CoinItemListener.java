package nl.dutchcoding.headhunter.listeners;

import nl.dutchcoding.headhunter.HeadHunter;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class CoinItemListener implements Listener {
    
    private final HeadHunter plugin;
    private final NamespacedKey coinKey;
    
    public CoinItemListener(HeadHunter plugin) {
        this.plugin = plugin;
        this.coinKey = new NamespacedKey(plugin, "coin_value");
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        
        if (item == null || item.getType() != Material.GOLD_NUGGET) {
            return;
        }
        
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.getPersistentDataContainer().has(coinKey, PersistentDataType.DOUBLE)) {
            return;
        }
        
        // Get coin value
        double coinValue = meta.getPersistentDataContainer().get(coinKey, PersistentDataType.DOUBLE);
        
        // Check if economy is available
        if (!plugin.hasEconomy()) {
            player.sendMessage(plugin.getMessagesConfig().getMessage("messages.coin-no-economy"));
            return;
        }
        
        // Get stack amount and calculate total coins
        int amount = item.getAmount();
        double totalCoins = amount * coinValue;

        // Deposit all coins at once
        plugin.getEconomy().depositPlayer(player, totalCoins);
        double newBalance = plugin.getEconomy().getBalance(player);

        // Remove entire stack
        player.getInventory().setItem(event.getHand(), null);

        // Send message with placeholders
        player.sendMessage(plugin.getMessagesConfig().getMessage("messages.coin-redeemed",
                "{amount}", String.format("%.2f", totalCoins),
                "{balance}", String.format("%.2f", newBalance)));
        
        event.setCancelled(true);
    }
}
