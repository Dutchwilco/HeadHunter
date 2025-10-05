package nl.dutchcoding.headhunter.listeners;

import nl.dutchcoding.headhunter.HeadHunter;
import nl.dutchcoding.headhunter.models.PlayerMobData;
import nl.dutchcoding.headhunter.utils.MobHeadUtil;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class MobHeadInteractListener implements Listener {

    private final HeadHunter plugin;

    public MobHeadInteractListener(HeadHunter plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null || !MobHeadUtil.isMobHead(item)) {
            return;
        }

        // Check if player owns this head
        if (!MobHeadUtil.isOwner(item, player.getUniqueId())) {
            player.sendMessage(plugin.getMessagesConfig().getMessage("messages.not-your-head"));
            return;
        }

        // Get mob type, XP, and coins from head
        EntityType mobType = MobHeadUtil.getMobType(item);
        int xpToGain = MobHeadUtil.getStoredXP(item);
        double coinsToGain = MobHeadUtil.getStoredCoins(item);

        if (mobType == null) {
            return;
        }

        // Grant XP to player
        PlayerMobData playerData = plugin.getPlayerDataManager().getPlayerData(player.getUniqueId());
        playerData.addMobXP(mobType, xpToGain);
        
        int currentXP = playerData.getMobXP(mobType);
        int currentLevel = playerData.getMobLevel(mobType);

        // Check if player leveled up
        var mobData = plugin.getMobsConfig().getMobData(mobType);
        if (mobData != null) {
            int nextLevelXP = mobData.getXpRequirementForLevel(currentLevel + 1);
            
            if (currentXP >= nextLevelXP && currentLevel < mobData.getMaxLevel()) {
                // Level up!
                playerData.setMobLevel(mobType, currentLevel + 1);
                playerData.setMobXP(mobType, 0);
                
                String levelUpMessage = plugin.getMessagesConfig().getMessage("messages.level-up",
                        "{mob}", mobType.name(),
                        "{level}", String.valueOf(currentLevel + 1));
                player.sendMessage(levelUpMessage);
                
                // Play sound
                if (plugin.getConfigManager().isPlaySoundOnLevelUp()) {
                    try {
                        Sound sound = Sound.valueOf(plugin.getConfigManager().getLevelUpSound());
                        player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
                    } catch (IllegalArgumentException e) {
                        plugin.getLogger().warning("Invalid sound in config: " + plugin.getConfigManager().getLevelUpSound());
                    }
                }
            } else {
                // Show progress
                String xpMessage = plugin.getMessagesConfig().getMessage("messages.head-consumed",
                        "{mob}", mobType.name(),
                        "{xp}", String.valueOf(xpToGain),
                        "{current}", String.valueOf(currentXP),
                        "{required}", String.valueOf(nextLevelXP));
                player.sendMessage(xpMessage);
            }
        }

        // Grant coins via Vault
        if (coinsToGain > 0 && plugin.getEconomy() != null) {
            plugin.getEconomy().depositPlayer(player, coinsToGain);
            String coinsMessage = plugin.getMessagesConfig().getMessage("messages.coins-earned",
                    "{amount}", String.format("%.2f", coinsToGain));
            player.sendMessage(coinsMessage);
        }

        // Remove head from inventory
        item.setAmount(item.getAmount() - 1);

        // Save data
        plugin.getPlayerDataManager().savePlayerData(player.getUniqueId());

        event.setCancelled(true);
    }
}
