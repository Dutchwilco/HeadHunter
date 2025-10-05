package nl.dutchcoding.headhunter.listeners;

import nl.dutchcoding.headhunter.HeadHunter;
import nl.dutchcoding.headhunter.models.MobTier;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class GUIListener implements Listener {

    private final HeadHunter plugin;

    public GUIListener() {
        this.plugin = HeadHunter.getInstance();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String title = event.getView().getTitle();

        // Check if it's any of our GUIs
        if (plugin.getRaritySelectionGUI().isRaritySelectionGUI(title) ||
            plugin.getMobListGUI().isMobListGUI(title) ||
            title.equals("§6§lHeadHunter Stats")) { // Legacy support
            event.setCancelled(true);

            // Handle back button clicks
            if (plugin.getMobListGUI().isMobListGUI(title)) {
                if (plugin.getMobListGUI().isBackButton(event.getSlot(), event.getInventory().getSize())) {
                    // Go back to rarity selection
                    plugin.getRaritySelectionGUI().openGUI((org.bukkit.entity.Player) event.getWhoClicked());
                }
            }

            // Handle rarity selection clicks
            if (plugin.getRaritySelectionGUI().isRaritySelectionGUI(title)) {
                MobTier selectedTier = plugin.getRaritySelectionGUI().getTierFromSlot(event.getSlot());
                if (selectedTier != null) {
                    // Check if tier is unlocked
                    int requiredLevel = plugin.getMobsConfig().getRequiredLevelForTier(selectedTier);
                    int playerMaxLevel = plugin.getPlayerDataManager()
                        .getPlayerData(event.getWhoClicked().getUniqueId())
                        .getHighestLevelInAnyTier();

                    if (playerMaxLevel >= requiredLevel) {
                        // Open mob list for this tier
                        plugin.getMobListGUI().openGUI((org.bukkit.entity.Player) event.getWhoClicked(), selectedTier);
                    }
                }
            }
        }
    }
}
