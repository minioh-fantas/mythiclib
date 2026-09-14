package io.lumine.mythic.lib.command.mythiclib.statmod;

import io.lumine.mythic.lib.command.api.CommandTreeNode;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class TempStatCommand extends CommandTreeNode {

    public TempStatCommand(@NotNull CommandTreeNode parent) {
        super(parent, "tempstat");

        addChild(new AddCommand(this));
        addChild(new RemoveCommand(this));
    }

    @Override
    public @NotNull CommandResult execute(CommandSender sender, String[] args) {
        return CommandResult.THROW_USAGE;
    }
}
