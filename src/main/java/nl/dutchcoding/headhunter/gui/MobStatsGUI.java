package nl.dutchcoding.headhunter.gui;

import nl.dutchcoding.headhunter.HeadHunter;
import nl.dutchcoding.headhunter.models.MobData;
import nl.dutchcoding.headhunter.models.PlayerMobData;
import nl.dutchcoding.headhunter.utils.MobHeadUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MobStatsGUI {

    private final HeadHunter plugin;

    public MobStatsGUI(HeadHunter plugin) {
        this.plugin = plugin;
    }

    public void openGUI(Player player) {
        String title = plugin.getMessagesConfig().getMessage("messages.gui-title");
        Inventory gui = Bukkit.createInventory(null, 54, title);

        PlayerMobData playerData = plugin.getPlayerDataManager().getPlayerData(player.getUniqueId());
        Map<EntityType, MobData> allMobs = plugin.getMobsConfig().getAllMobData();

        int slot = 0;
        for (Map.Entry<EntityType, MobData> entry : allMobs.entrySet()) {
            if (slot >= 54) break;

            EntityType mobType = entry.getKey();
            MobData mobData = entry.getValue();

            int level = playerData.getMobLevel(mobType);
            int xp = playerData.getMobXP(mobType);
            int maxLevel = mobData.getMaxLevel();
            int nextLevelXP = level >= maxLevel ? 0 : mobData.getXpRequirementForLevel(level + 1);

            // Create mob head
            ItemStack head = MobHeadUtil.createMobHead(mobType, player.getUniqueId(), 1, 0, mobData.getHeadTexture());
            ItemMeta meta = head.getItemMeta();

            if (meta != null) {
                // Set display name
                meta.setDisplayName("§6§l" + formatMobName(mobType));

                // Create lore with level and progress
                List<String> lore;
                
                if (level >= maxLevel) {
                    String maxLabel = plugin.getMessagesConfig().getMessage("messages.gui-max-level");
                    String maxReached = plugin.getMessagesConfig().getMessage("messages.gui-max-level-reached");
                    lore = plugin.getMessagesConfig().getStringList("lore.gui-mob-max",
                        "{level}", String.valueOf(level),
                        "{maxlabel}", maxLabel,
                        "{maxreached}", maxReached,
                        "{tiercolor}", getTierColor(mobData.getTier()),
                        "{tier}", mobData.getTier().name(),
                        "{coins}", String.valueOf(mobData.getBaseCoins()));
                } else {
                    String progressBar = plugin.getMessagesConfig().getProgressBar(xp, nextLevelXP);
                    lore = plugin.getMessagesConfig().getStringList("lore.gui-mob",
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

            gui.setItem(slot, head);
            slot++;
        }

        player.openInventory(gui);
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

    private String getTierColor(nl.dutchcoding.headhunter.models.MobTier tier) {
        return switch (tier) {
            case COMMON -> "§f";
            case UNCOMMON -> "§a";
            case RARE -> "§9";
            case LEGENDARY -> "§6";
        };
    }
}
