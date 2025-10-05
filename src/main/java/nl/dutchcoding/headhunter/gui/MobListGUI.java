package nl.dutchcoding.headhunter.gui;

import nl.dutchcoding.headhunter.HeadHunter;
import nl.dutchcoding.headhunter.models.MobData;
import nl.dutchcoding.headhunter.models.MobTier;
import nl.dutchcoding.headhunter.models.PlayerMobData;
import nl.dutchcoding.headhunter.utils.MobHeadUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class MobListGUI {

    private final HeadHunter plugin;

    public MobListGUI(HeadHunter plugin) {
        this.plugin = plugin;
    }

    public void openGUI(Player player, MobTier tier) {
        String title = plugin.getMessagesConfig().getMessage("gui.mob-list.title", "{tier}", tier.name());
        int rows = plugin.getMessagesConfig().getConfig().getInt("gui.mob-list.rows", 6);
        
        Inventory gui = Bukkit.createInventory(null, rows * 9, title);

        PlayerMobData playerData = plugin.getPlayerDataManager().getPlayerData(player.getUniqueId());
        List<EntityType> mobsInTier = plugin.getMobsConfig().getMobsByTier(tier);

        // Add back button
        int backSlot = plugin.getMessagesConfig().getConfig().getInt("gui.mob-list.back-button.slot", rows * 9 - 5);
        if (backSlot >= 0 && backSlot < gui.getSize()) {
            gui.setItem(backSlot, createBackButton());
        }

        // Add mob heads
        int slot = 0;
        for (EntityType mobType : mobsInTier) {
            // Skip the back button slot
            if (slot == backSlot) slot++;
            if (slot >= gui.getSize()) break;

            MobData mobData = plugin.getMobsConfig().getMobData(mobType);
            if (mobData == null) continue;

            ItemStack mobItem = createMobItem(mobType, mobData, playerData, player);
            gui.setItem(slot, mobItem);
            slot++;
        }

        player.openInventory(gui);
    }

    private ItemStack createMobItem(EntityType mobType, MobData mobData, PlayerMobData playerData, Player player) {
        int level = playerData.getMobLevel(mobType);
        int xp = playerData.getMobXP(mobType);
        int maxLevel = mobData.getMaxLevel();
        int nextLevelXP = level >= maxLevel ? 0 : mobData.getXpRequirementForLevel(level + 1);

        // Create mob head
        ItemStack head = MobHeadUtil.createMobHead(mobType, player.getUniqueId(), 1, 0, mobData.getHeadTexture());
        ItemMeta meta = head.getItemMeta();

        if (meta != null) {
            // Get custom name format or use default
            String nameFormat = plugin.getMessagesConfig().getMessage("gui.mob-list.mob-item.name", 
                "{mob}", formatMobName(mobType));
            meta.setDisplayName(nameFormat);

            // Create lore with level and progress
            List<String> lore;
            
            if (level >= maxLevel) {
                String maxLabel = plugin.getMessagesConfig().getMessage("gui.mob-list.max-level-label");
                String maxReached = plugin.getMessagesConfig().getMessage("gui.mob-list.max-level-reached");
                lore = plugin.getMessagesConfig().getStringList("gui.mob-list.mob-item.lore.max",
                    "{level}", String.valueOf(level),
                    "{maxlabel}", maxLabel,
                    "{maxreached}", maxReached,
                    "{tiercolor}", getTierColor(mobData.getTier()),
                    "{tier}", mobData.getTier().name(),
                    "{coins}", String.valueOf(mobData.getBaseCoins()));
            } else {
                String progressBar = plugin.getMessagesConfig().getProgressBar(xp, nextLevelXP);
                lore = plugin.getMessagesConfig().getStringList("gui.mob-list.mob-item.lore.default",
                    "{level}", String.valueOf(level),
                    "{current}", String.valueOf(xp),
                    "{required}", String.valueOf(nextLevelXP),
                    "{progressbar}", progressBar,
                    "{tiercolor}", getTierColor(mobData.getTier()),
                    "{tier}", mobData.getTier().name(),
                    "{coins}", String.valueOf(mobData.getBaseCoins()));
            }

            meta.setLore(lore);
            head.setItemMeta(meta);
        }

        return head;
    }

    private ItemStack createBackButton() {
        String materialStr = plugin.getMessagesConfig().getConfig().getString("gui.mob-list.back-button.material", "ARROW");
        Material material;
        try {
            material = Material.valueOf(materialStr);
        } catch (IllegalArgumentException e) {
            material = Material.ARROW;
        }

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            String name = plugin.getMessagesConfig().getMessage("gui.mob-list.back-button.name");
            List<String> lore = plugin.getMessagesConfig().getStringList("gui.mob-list.back-button.lore");
            
            meta.setDisplayName(name);
            meta.setLore(lore);
            item.setItemMeta(meta);
        }

        return item;
    }

    private String formatMobName(EntityType mobType) {
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

    private String getTierColor(MobTier tier) {
        return switch (tier) {
            case COMMON -> "§f";
            case UNCOMMON -> "§a";
            case RARE -> "§9";
            case LEGENDARY -> "§6";
        };
    }

    public boolean isMobListGUI(String title) {
        // Check if title matches any tier
        for (MobTier tier : MobTier.values()) {
            String configTitle = plugin.getMessagesConfig().getMessage("gui.mob-list.title", "{tier}", tier.name());
            if (title.equals(configTitle)) {
                return true;
            }
        }
        return false;
    }

    public boolean isBackButton(int slot, int inventorySize) {
        int backSlot = plugin.getMessagesConfig().getConfig().getInt("gui.mob-list.back-button.slot", inventorySize - 5);
        return slot == backSlot;
    }
}
