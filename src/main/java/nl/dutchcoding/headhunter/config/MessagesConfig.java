package nl.dutchcoding.headhunter.config;

import nl.dutchcoding.headhunter.HeadHunter;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class MessagesConfig {

    private final HeadHunter plugin;
    private File messagesFile;
    private FileConfiguration messagesConfig;

    public MessagesConfig(HeadHunter plugin) {
        this.plugin = plugin;
        createMessagesFile();
    }

    private void createMessagesFile() {
        messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            messagesFile.getParentFile().mkdirs();
            plugin.saveResource("messages.yml", false);
        }
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
    }

    public void reload() {
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
    }

    public String getMessage(String path) {
        return ChatColor.translateAlternateColorCodes('&', messagesConfig.getString(path, "Message not found: " + path));
    }

    public String getMessage(String path, String... replacements) {
        String message = getMessage(path);
        for (int i = 0; i < replacements.length; i += 2) {
            if (i + 1 < replacements.length) {
                message = message.replace(replacements[i], replacements[i + 1]);
            }
        }
        return message;
    }

    public java.util.List<String> getStringList(String path) {
        java.util.List<String> list = new java.util.ArrayList<>();
        for (String line : messagesConfig.getStringList(path)) {
            list.add(ChatColor.translateAlternateColorCodes('&', line));
        }
        return list;
    }

    public java.util.List<String> getStringList(String path, String... replacements) {
        java.util.List<String> list = getStringList(path);
        java.util.List<String> result = new java.util.ArrayList<>();
        for (String line : list) {
            String replacedLine = line;
            for (int i = 0; i < replacements.length; i += 2) {
                if (i + 1 < replacements.length) {
                    replacedLine = replacedLine.replace(replacements[i], replacements[i + 1]);
                }
            }
            result.add(replacedLine);
        }
        return result;
    }

    public int getInt(String path) {
        return messagesConfig.getInt(path, 0);
    }

    public String getProgressBar(int current, int required) {
        int length = messagesConfig.getInt("progressbar.length", 20);
        String character = messagesConfig.getString("progressbar.character", "▌");
        String completedColor = ChatColor.translateAlternateColorCodes('&', 
            messagesConfig.getString("progressbar.completed-color", "&a"));
        String remainingColor = ChatColor.translateAlternateColorCodes('&', 
            messagesConfig.getString("progressbar.remaining-color", "&8"));
        String format = ChatColor.translateAlternateColorCodes('&',
            messagesConfig.getString("progressbar.format", "&7[{bar}&7] &f{percentage}%"));
        
        double percentage = required > 0 ? (double) current / required : 0;
        int completed = (int) (percentage * length);
        
        StringBuilder bar = new StringBuilder();
        bar.append(completedColor);
        for (int i = 0; i < completed; i++) {
            bar.append(character);
        }
        bar.append(remainingColor);
        for (int i = completed; i < length; i++) {
            bar.append(character);
        }
        
        return format.replace("{bar}", bar.toString())
                    .replace("{percentage}", String.format("%.1f", percentage * 100));
    }

    public FileConfiguration getConfig() {
        return messagesConfig;
    }

    public void saveConfig() {
        try {
            messagesConfig.save(messagesFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save messages.yml: " + e.getMessage());
        }
    }
}
