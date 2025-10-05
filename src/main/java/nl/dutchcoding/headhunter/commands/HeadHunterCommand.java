package nl.dutchcoding.headhunter.commands;

import nl.dutchcoding.headhunter.HeadHunter;
import nl.dutchcoding.headhunter.models.MobData;
import nl.dutchcoding.headhunter.models.PlayerMobData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class HeadHunterCommand implements CommandExecutor, TabCompleter {

    private final HeadHunter plugin;

    public HeadHunterCommand(HeadHunter plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (sender instanceof Player) {
                plugin.getRaritySelectionGUI().openGUI((Player) sender);
            } else {
                sender.sendMessage(plugin.getMessagesConfig().getMessage("messages.player-only"));
            }
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "stats":
                if (!(sender instanceof Player)) {
                    sender.sendMessage(plugin.getMessagesConfig().getMessage("messages.player-only"));
                    return true;
                }
                showStats((Player) sender);
                break;

            case "help":
                showHelp(sender);
                break;

            case "reload":
                if (!sender.hasPermission("headhunter.reload")) {
                    sender.sendMessage(plugin.getMessagesConfig().getMessage("messages.no-permission"));
                    return true;
                }
                plugin.reload();
                sender.sendMessage(plugin.getMessagesConfig().getMessage("messages.reload"));
                break;

            default:
                sender.sendMessage(plugin.getMessagesConfig().getMessage("messages.unknown-command"));
                break;
        }

        return true;
    }

    private void showStats(Player player) {
        PlayerMobData playerData = plugin.getPlayerDataManager().getPlayerData(player.getUniqueId());

        player.sendMessage("§6§l=== HeadHunter Stats ===");
        player.sendMessage("");

        Map<EntityType, Integer> mobLevels = playerData.getAllMobLevels();
        Map<EntityType, Integer> mobXP = playerData.getAllMobXP();

        if (mobLevels.isEmpty()) {
            player.sendMessage("§7You haven't leveled up any mobs yet!");
            player.sendMessage("§7Kill mobs from spawners to start your journey!");
        } else {
            for (Map.Entry<EntityType, Integer> entry : mobLevels.entrySet()) {
                EntityType mobType = entry.getKey();
                int level = entry.getValue();
                int xp = mobXP.getOrDefault(mobType, 0);

                MobData mobData = plugin.getMobsConfig().getMobData(mobType);
                if (mobData == null) continue;

                int nextLevelXP = mobData.getXpRequirementForLevel(level + 1);
                String progress = level >= mobData.getMaxLevel() ? "§a§lMAX" : xp + "/" + nextLevelXP;

                player.sendMessage("§e" + mobType.name() + " §7- §fLevel " + level + " §7(" + progress + ")");
            }
        }

        player.sendMessage("");
        player.sendMessage("§6§l====================");
    }

    private void showHelp(CommandSender sender) {
        sender.sendMessage("§6§l=== HeadHunter Help ===");
        sender.sendMessage("§e/hh stats §7- View your mob stats");
        sender.sendMessage("§e/hh help §7- Show this help message");
        if (sender.hasPermission("headhunter.reload")) {
            sender.sendMessage("§e/hh reload §7- Reload the plugin");
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.add("stats");
            completions.add("help");
            if (sender.hasPermission("headhunter.reload")) {
                completions.add("reload");
            }
        }

        return completions;
    }
}
