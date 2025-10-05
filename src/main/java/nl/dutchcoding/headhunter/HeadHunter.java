package nl.dutchcoding.headhunter;

import net.milkbowl.vault.economy.Economy;
import nl.dutchcoding.headhunter.commands.HeadHunterCommand;
import nl.dutchcoding.headhunter.config.ConfigManager;
import nl.dutchcoding.headhunter.config.MessagesConfig;
import nl.dutchcoding.headhunter.config.MobsConfig;
import nl.dutchcoding.headhunter.data.PlayerDataManager;
import nl.dutchcoding.headhunter.gui.MobListGUI;
import nl.dutchcoding.headhunter.gui.MobStatsGUI;
import nl.dutchcoding.headhunter.gui.RaritySelectionGUI;
import nl.dutchcoding.headhunter.listeners.*;
import nl.dutchcoding.headhunter.placeholders.HeadHunterPlaceholders;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class HeadHunter extends JavaPlugin {

    private static HeadHunter instance;
    private ConfigManager configManager;
    private MessagesConfig messagesConfig;
    private MobsConfig mobsConfig;
    private PlayerDataManager playerDataManager;
    private Economy economy;
    private MobStatsGUI mobStatsGUI;
    private RaritySelectionGUI raritySelectionGUI;
    private MobListGUI mobListGUI;

    @Override
    public void onEnable() {
        instance = this;

        // Initialize configuration
        configManager = new ConfigManager(this);
        messagesConfig = new MessagesConfig(this);
        mobsConfig = new MobsConfig(this);

        // Initialize data manager
        playerDataManager = new PlayerDataManager(this);
        playerDataManager.loadAllData();

        // Initialize GUIs
        mobStatsGUI = new MobStatsGUI(this);
        raritySelectionGUI = new RaritySelectionGUI(this);
        mobListGUI = new MobListGUI(this);

        // Setup Vault economy
        if (!setupEconomy()) {
            getLogger().warning("Vault not found! Economy features will be disabled.");
        } else {
            getLogger().info("Vault economy hooked successfully!");
        }
        
        // Register PlaceholderAPI expansions
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new HeadHunterPlaceholders(this).register();
            getLogger().info("PlaceholderAPI hooked successfully!");
        }

        // Register listeners
        getServer().getPluginManager().registerEvents(new MobDeathListener(this), this);
        getServer().getPluginManager().registerEvents(new MobHeadInteractListener(this), this);
        getServer().getPluginManager().registerEvents(new MobNuggetRedeemListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        getServer().getPluginManager().registerEvents(new PreventHeadTradeListener(this), this);
        getServer().getPluginManager().registerEvents(new CoinItemListener(this), this);
        getServer().getPluginManager().registerEvents(new GUIListener(), this);

        // Register commands
        getCommand("headhunter").setExecutor(new HeadHunterCommand(this));

        getLogger().info("HeadHunter has been enabled!");
    }
    
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }

    @Override
    public void onDisable() {
        if (playerDataManager != null) {
            playerDataManager.saveAllData();
        }
        getLogger().info("HeadHunter has been disabled!");
    }

    public void reload() {
        configManager.reload();
        messagesConfig.reload();
        mobsConfig.reload();
    }

    public static HeadHunter getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public MessagesConfig getMessagesConfig() {
        return messagesConfig;
    }

    public MobsConfig getMobsConfig() {
        return mobsConfig;
    }

    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }
    
    public Economy getEconomy() {
        return economy;
    }
    
    public boolean hasEconomy() {
        return economy != null;
    }

    public MobStatsGUI getMobStatsGUI() {
        return mobStatsGUI;
    }

    public RaritySelectionGUI getRaritySelectionGUI() {
        return raritySelectionGUI;
    }

    public MobListGUI getMobListGUI() {
        return mobListGUI;
    }
}
