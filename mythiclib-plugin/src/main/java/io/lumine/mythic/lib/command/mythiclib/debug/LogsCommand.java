package io.lumine.mythic.lib.command.mythiclib.debug;

import io.lumine.mythic.lib.comp.mclogs.APIResponse;
import io.lumine.mythic.lib.comp.mclogs.MclogsAPI;
import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.command.api.CommandTreeNode;
import io.lumine.mythic.lib.util.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Scanner;
import java.util.logging.Level;

public class LogsCommand extends CommandTreeNode {
    public LogsCommand(CommandTreeNode parent) {
        super(parent, "logs");
    }

    @Override
    public @NotNull CommandResult execute(CommandSender sender, String[] args) {

        try {
            sender.sendMessage("Reading and uploading logs..");
            StringBuilder builder = new StringBuilder();

            // Append latest log
            File log = new File(MythicLib.plugin.getDataFolder(), "../../logs/latest.log");
            Scanner scanner = new Scanner(log);
            while (scanner.hasNextLine())
                builder.append(scanner.nextLine()).append("\n");
            scanner.close();

            // Append plugin versions
            builder.append("Plugin versions:\n");
            for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
                builder.append("> ").append(plugin.getName()).append(" ").append(plugin.getDescription().getVersion()).append(" by ").append(String.join(",", plugin.getDescription().getAuthors()));
                if (!Bukkit.getPluginManager().isPluginEnabled(plugin))
                    builder.append(" (Disabled)");
                builder.append("\n");
            }

            APIResponse response = MclogsAPI.share(builder.toString());
            Validate.isTrue(response.success, "Custom error (" + response.id + "): " + response.error);
            sender.sendMessage("Uploaded here: " + response.url);
            MythicLib.plugin.getLogger().log(Level.INFO, "Latest logs uploaded at " + response.url);

        } catch (Exception exception) {
            exception.printStackTrace();
            sender.sendMessage("Could not upload latest logs: " + exception.getMessage() + " (check console for stack strace)");
        }

        return CommandResult.SUCCESS;
    }
}
