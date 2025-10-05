package nl.dutchcoding.headhunter.listeners;

import nl.dutchcoding.headhunter.HeadHunter;
import nl.dutchcoding.headhunter.models.MobData;
import nl.dutchcoding.headhunter.models.PlayerMobData;
import nl.dutchcoding.headhunter.utils.CoinItemUtil;
import nl.dutchcoding.headhunter.utils.MobHeadUtil;
import nl.dutchcoding.headhunter.utils.MobNuggetUtil;
import nl.dutchcoding.headhunter.utils.ProgressionUtil;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

public class MobDeathListener implements Listener {

    private final HeadHunter plugin;

    public MobDeathListener(HeadHunter plugin) {
        this.plugin = plugin;
        CoinItemUtil.initialize(plugin);
        MobNuggetUtil.initialize(plugin);
    }

    @EventHandler
    public void onMobDeath(EntityDeathEvent event) {
        if (event.getEntity().getKiller() == null) return;
        if (!(event.getEntity().getKiller() instanceof Player)) return;

        Player player = event.getEntity().getKiller();
        EntityType entityType = event.getEntityType();

        // Check if spawner only mode is enabled
        if (plugin.getConfigManager().isSpawnerOnlyMode()) {
            if (event.getEntity().fromMobSpawner() == false) {
                return;
            }
        }

        // Check if mob is tracked
        MobData mobData = plugin.getMobsConfig().getMobData(entityType);
        if (mobData == null) return;

        PlayerMobData playerData = plugin.getPlayerDataManager().getPlayerData(player.getUniqueId());

        // Check if player has unlocked this tier
        if (!ProgressionUtil.hasUnlockedTier(player, mobData.getTier(), plugin)) {
            return;
        }

        int currentLevel = playerData.getMobLevel(entityType);

        // Calculate coin bonus for this kill
        double coinMultiplier = plugin.getConfigManager().getBaseCoinMultiplier() +
                (currentLevel * plugin.getConfigManager().getCoinMultiplierPerLevel());
        double coins = mobData.getBaseCoins() * coinMultiplier;

        // Check if mob is from spawner
        boolean isFromSpawner = event.getEntity().fromMobSpawner();

        ItemStack dropItem;
        if (isFromSpawner) {
            // Drop gold nugget for spawner mobs
            dropItem = MobNuggetUtil.createMobNugget(entityType, 1, coins);
            
            // Send message to player
            player.sendMessage(plugin.getMessagesConfig().getMessage("messages.nugget-received",
                "{mob}", entityType.name()));
        } else {
            // Drop mob head for natural mobs
            dropItem = MobHeadUtil.createMobHead(entityType, player.getUniqueId(), 1, coins, mobData.getHeadTexture());
        }

        if (plugin.getConfigManager().isDropHeadsOnGround()) {
            event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(), dropItem);
        } else {
            player.getInventory().addItem(dropItem);
        }
    }
}
