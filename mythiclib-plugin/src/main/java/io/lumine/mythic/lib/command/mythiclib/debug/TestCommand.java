package io.lumine.mythic.lib.command.mythiclib.debug;

import io.lumine.mythic.lib.command.api.CommandTreeNode;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

/**
 * Used to test some code sections. It should be kept empty
 * so that users don't randomly just perform that command
 *
 * @author jules
 */
public class TestCommand extends CommandTreeNode {
    public TestCommand(CommandTreeNode parent) {
        super(parent, "test");
    }

    @Override
    public @NotNull CommandResult execute(CommandSender sender, String[] args) {

        // Nothing

        return CommandResult.SUCCESS;
    }
}
