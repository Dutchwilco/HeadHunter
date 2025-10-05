package nl.dutchcoding.headhunter.gui;

import nl.dutchcoding.headhunter.HeadHunter;
import nl.dutchcoding.headhunter.models.MobTier;
import nl.dutchcoding.headhunter.models.PlayerMobData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class RaritySelectionGUI {

    private final HeadHunter plugin;

    public RaritySelectionGUI(HeadHunter plugin) {
        this.plugin = plugin;
    }

    public void openGUI(Player player) {
        String title = plugin.getMessagesConfig().getMessage("gui.rarity-selection.title");
        int rows = plugin.getMessagesConfig().getConfig().getInt("gui.rarity-selection.rows", 3);
        
        Inventory gui = Bukkit.createInventory(null, rows * 9, title);

        PlayerMobData playerData = plugin.getPlayerDataManager().getPlayerData(player.getUniqueId());

        // Get configured slots for each tier
        for (MobTier tier : MobTier.values()) {
            String tierName = tier.name().toLowerCase();
            int slot = plugin.getMessagesConfig().getConfig().getInt("gui.rarity-selection.items." + tierName + ".slot", -1);
            
            if (slot == -1 || slot >= gui.getSize()) continue;

            ItemStack item = createTierItem(tier, playerData, player);
            gui.setItem(slot, item);
        }

        player.openInventory(gui);
    }

    private ItemStack createTierItem(MobTier tier, PlayerMobData playerData, Player player) {
        String tierName = tier.name().toLowerCase();
        String materialPath = "gui.rarity-selection.items." + tierName + ".material";
        
        Material material;
        try {
            material = Material.valueOf(plugin.getMessagesConfig().getConfig().getString(materialPath, "PAPER"));
        } catch (IllegalArgumentException e) {
            material = Material.PAPER;
        }

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            // Get display name
            String displayName = plugin.getMessagesConfig().getMessage("gui.rarity-selection.items." + tierName + ".name");
            meta.setDisplayName(displayName);

            // Check if tier is unlocked
            int requiredLevel = plugin.getMobsConfig().getRequiredLevelForTier(tier);
            int playerMaxLevel = playerData.getHighestLevelInAnyTier();
            boolean unlocked = playerMaxLevel >= requiredLevel;

            // Get lore
            List<String> lore;
            if (unlocked) {
                int mobCount = plugin.getMobsConfig().getMobsByTier(tier).size();
                lore = plugin.getMessagesConfig().getStringList("gui.rarity-selection.items." + tierName + ".lore.unlocked",
                    "{count}", String.valueOf(mobCount),
                    "{required}", String.valueOf(requiredLevel));
            } else {
                lore = plugin.getMessagesConfig().getStringList("gui.rarity-selection.items." + tierName + ".lore.locked",
                    "{required}", String.valueOf(requiredLevel),
                    "{current}", String.valueOf(playerMaxLevel));
            }

            meta.setLore(lore);
            item.setItemMeta(meta);
        }

        return item;
    }

    public boolean isRaritySelectionGUI(String title) {
        String configTitle = plugin.getMessagesConfig().getMessage("gui.rarity-selection.title");
        return title.equals(configTitle);
    }

    public MobTier getTierFromSlot(int slot) {
        for (MobTier tier : MobTier.values()) {
            String tierName = tier.name().toLowerCase();
            int configSlot = plugin.getMessagesConfig().getConfig().getInt("gui.rarity-selection.items." + tierName + ".slot", -1);
            
            if (configSlot == slot) {
                return tier;
            }
        }
        return null;
    }
}
