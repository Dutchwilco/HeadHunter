package nl.dutchcoding.headhunter.listeners;

import nl.dutchcoding.headhunter.HeadHunter;
import nl.dutchcoding.headhunter.utils.MobHeadUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

public class PreventHeadTradeListener implements Listener {

    private final HeadHunter plugin;

    public PreventHeadTradeListener(HeadHunter plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!plugin.getConfigManager().isPreventItemTrade()) {
            return;
        }

        ItemStack item = event.getCurrentItem();
        if (item != null && MobHeadUtil.isMobHead(item)) {
            if (!MobHeadUtil.isOwner(item, event.getWhoClicked().getUniqueId())) {
                event.setCancelled(true);
                event.getWhoClicked().sendMessage(plugin.getMessagesConfig().getMessage("messages.cannot-trade-heads"));
            }
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (!plugin.getConfigManager().isPreventItemTrade()) {
            return;
        }

        ItemStack item = event.getItemDrop().getItemStack();
        if (MobHeadUtil.isMobHead(item)) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(plugin.getMessagesConfig().getMessage("messages.cannot-drop-heads"));
        }
    }
}
