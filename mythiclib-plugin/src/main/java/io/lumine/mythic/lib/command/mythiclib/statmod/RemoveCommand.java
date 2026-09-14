package io.lumine.mythic.lib.command.mythiclib.statmod;

import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.player.MMOPlayerData;
import io.lumine.mythic.lib.command.api.CommandTreeNode;
import io.lumine.mythic.lib.command.api.Parameter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RemoveCommand extends CommandTreeNode {
    public RemoveCommand(@NotNull CommandTreeNode parent) {
        super(parent, "remove");

        addParameter(Parameter.PLAYER);
        addParameter(Parameter.STAT);
        addParameter(AddCommand.STAT_KEY);
    }

    @Override
    public @NotNull CommandResult execute(CommandSender sender, String[] args) {
        if (args.length < 4) return CommandResult.THROW_USAGE;

        final @Nullable Player target = Bukkit.getPlayer(args[2]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player not found.");
            return CommandResult.FAILURE;
        }

        final String statName = UtilityMethods.enumName(args[3]);
        final MMOPlayerData playerData = MMOPlayerData.get(target);
        final String key = args.length > 4 ? args[4] : AddCommand.DEFAULT_STAT_KEY;

        playerData.getStatMap().getInstance(statName).removeIf(key::equals);
        return CommandResult.SUCCESS;
    }
}
