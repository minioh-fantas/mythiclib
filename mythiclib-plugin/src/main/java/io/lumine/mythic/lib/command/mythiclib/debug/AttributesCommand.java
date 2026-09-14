package io.lumine.mythic.lib.command.mythiclib.debug;

import io.lumine.mythic.lib.command.api.CommandTreeNode;
import io.lumine.mythic.lib.command.api.Parameter;
import io.lumine.mythic.lib.gui.builtin.AttributeExplorer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class AttributesCommand extends CommandTreeNode {
    public AttributesCommand(CommandTreeNode parent) {
        super(parent, "attributes");

        addParameter(Parameter.PLAYER.optional(true));
    }

    @Override
    public @NotNull CommandResult execute(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only for players.");
            return CommandResult.FAILURE;
        }

        final Player target = args.length > 2 ? Bukkit.getPlayer(args[2]) : (Player) sender;
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Could not find player called " + args[2] + ".");
            return CommandResult.FAILURE;
        }

        new AttributeExplorer((Player) sender, target).open();

        return CommandResult.SUCCESS;
    }
}
