package nl.dutchcoding.headhunter.listeners;

import nl.dutchcoding.headhunter.HeadHunter;
import nl.dutchcoding.headhunter.models.MobData;
import nl.dutchcoding.headhunter.models.PlayerMobData;
import nl.dutchcoding.headhunter.utils.MobNuggetUtil;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class MobNuggetRedeemListener implements Listener {

    private final HeadHunter plugin;

    public MobNuggetRedeemListener(HeadHunter plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onNuggetRedeem(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (!MobNuggetUtil.isMobNugget(item)) return;

        event.setCancelled(true);

        EntityType mobType = MobNuggetUtil.getMobType(item);
        if (mobType == null) return;

        int amount = item.getAmount();
        int xpPerNugget = MobNuggetUtil.getXP(item);
        double coinsPerNugget = MobNuggetUtil.getCoins(item);

        int totalXP = xpPerNugget * amount;
        double totalCoins = coinsPerNugget * amount;

        // Get player data
        PlayerMobData playerData = plugin.getPlayerDataManager().getPlayerData(player.getUniqueId());
        MobData mobData = plugin.getMobsConfig().getMobData(mobType);

        if (mobData == null) return;

        int oldLevel = playerData.getMobLevel(mobType);
        
        // Add XP
        playerData.addMobXP(mobType, totalXP);
        
        // Check for level up
        int currentXP = playerData.getMobXP(mobType);
        int currentLevel = playerData.getMobLevel(mobType);
        int requiredXP = mobData.getXpRequirementForLevel(currentLevel + 1);

        while (currentXP >= requiredXP && currentLevel < mobData.getMaxLevel()) {
            currentLevel++;
            currentXP -= requiredXP;
            playerData.setMobLevel(mobType, currentLevel);
            playerData.setMobXP(mobType, currentXP);
            requiredXP = mobData.getXpRequirementForLevel(currentLevel + 1);

            // Send level up title
            String mobName = formatMobName(mobType);
            String title = plugin.getMessagesConfig().getMessage("titles.level-up.title");
            String subtitle = plugin.getMessagesConfig().getMessage("titles.level-up.subtitle",
                "{mob}", mobName, "{level}", String.valueOf(currentLevel));
            int fadeIn = plugin.getMessagesConfig().getInt("titles.level-up.fade-in");
            int stay = plugin.getMessagesConfig().getInt("titles.level-up.stay");
            int fadeOut = plugin.getMessagesConfig().getInt("titles.level-up.fade-out");
            player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        }

        // Add coins
        plugin.getEconomy().depositPlayer(player, totalCoins);

        // Remove nuggets
        player.getInventory().setItemInMainHand(null);

        // Send message
        player.sendMessage(plugin.getMessagesConfig().getMessage("messages.nugget-redeemed",
            "{amount}", String.valueOf(amount)));
        player.sendMessage(plugin.getMessagesConfig().getMessage("messages.nugget-xp-gained",
            "{xp}", String.valueOf(totalXP),
            "{coins}", String.format("%.1f", totalCoins)));
        
        if (currentLevel > oldLevel) {
            String mobName = formatMobName(mobType);
            player.sendMessage(plugin.getMessagesConfig().getMessage("messages.nugget-level-up",
                "{mob}", mobName,
                "{level}", String.valueOf(currentLevel)));
        }

        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.2f);
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
}
