package io.lumine.mythic.lib.command.mythiclib;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.command.api.CommandTreeNode;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ReloadCommand extends CommandTreeNode {
    public ReloadCommand(CommandTreeNode parent) {
        super(parent, "reload");
    }

    @Override
    public @NotNull CommandResult execute(CommandSender sender, String[] args) {
        MythicLib.plugin.reload();
        sender.sendMessage(ChatColor.GREEN + "> MythicLib reloaded!");
        return CommandResult.SUCCESS;
    }
}
