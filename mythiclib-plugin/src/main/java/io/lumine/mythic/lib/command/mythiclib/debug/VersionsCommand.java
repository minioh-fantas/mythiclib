package io.lumine.mythic.lib.command.mythiclib.debug;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.command.api.CommandTreeNode;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

public class VersionsCommand extends CommandTreeNode {
    public VersionsCommand(CommandTreeNode parent) {
        super(parent, "versions");
    }

    @Override
    public @NotNull CommandResult execute(CommandSender sender, String[] args) {

        MythicLib.plugin.getLogger().log(Level.INFO, "Plugin versions:");
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins())
            MythicLib.plugin.getLogger().log(Level.INFO, "> " + plugin.getName() + " " + plugin.getDescription().getVersion()
                    + " by " + String.join(",", plugin.getDescription().getAuthors())
                    + (Bukkit.getPluginManager().isPluginEnabled(plugin) ? "" : " (Disabled)"));

        sender.sendMessage("Plugin versions pasted to your server console");

        return CommandResult.SUCCESS;
    }
}
