package io.lumine.mythic.lib.command.mythiclib.debug;

import io.lumine.mythic.lib.command.api.CommandTreeNode;
import io.lumine.mythic.lib.util.formula.NumericalExpression;
import io.lumine.mythic.lib.util.formula.preprocess.ExpressionPreprocessor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class ParseCommand extends CommandTreeNode {
    public ParseCommand(CommandTreeNode parent) {
        super(parent, "parse");
    }

    @Override
    public @NotNull CommandResult execute(CommandSender sender, String[] args) {
        final String expression = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
        final double value;
        if (sender instanceof Player)
            value = new NumericalExpression<>(expression, ExpressionPreprocessor.PLAYER).evaluate((OfflinePlayer) sender);
        else value = new NumericalExpression<>(expression, ExpressionPreprocessor.EMPTY).evaluate(null);
        sender.sendMessage(String.valueOf(value));
        return CommandResult.SUCCESS;
    }
}
