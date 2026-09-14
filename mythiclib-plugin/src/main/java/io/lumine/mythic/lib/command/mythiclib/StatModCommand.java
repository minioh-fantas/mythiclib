package io.lumine.mythic.lib.command.mythiclib;

import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.player.EquipmentSlot;
import io.lumine.mythic.lib.api.player.MMOPlayerData;
import io.lumine.mythic.lib.api.stat.modifier.StatModifier;
import io.lumine.mythic.lib.api.stat.modifier.TemporaryStatModifier;
import io.lumine.mythic.lib.command.api.CommandTreeNode;
import io.lumine.mythic.lib.command.api.Parameter;
import io.lumine.mythic.lib.player.modifier.ModifierSource;
import io.lumine.mythic.lib.player.modifier.ModifierType;
import io.lumine.mythic.lib.util.Pair;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@Deprecated
public class StatModCommand extends CommandTreeNode {

    @Deprecated
    public StatModCommand(CommandTreeNode parent) {
        super(parent, "statmod");

        addParameter(Parameter.PLAYER);
        addParameter(Parameter.STAT);
        addParameter(Parameter.AMOUNT.key("value"));
        addParameter(Parameter.DURATION_TICKS.optional(true));
    }

    @Override
    public @NotNull CommandResult execute(CommandSender sender, String[] args) {
        sender.sendMessage("/ml statmod is deprecated. Use instead /ml tempstat ...");
        if (args.length < 4)
            return CommandResult.THROW_USAGE;

        final @Nullable Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player not found.");
            return CommandResult.FAILURE;
        }

        final String statName = UtilityMethods.enumName(args[2]);
        final MMOPlayerData playerData = MMOPlayerData.get(target);
        Pair<ModifierType, Double> modifierPair = ModifierType.pairFromString(args[3]);
        final ModifierType type = modifierPair.getLeft();
        final double value = modifierPair.getRight();
        final long duration = args.length > 4 ? Math.max(1, (long) Double.parseDouble(args[4])) : 0;

        if (duration <= 0)
            new StatModifier(UUID.randomUUID().toString(), statName, value, type, EquipmentSlot.OTHER, ModifierSource.OTHER).register(playerData);
        else
            new TemporaryStatModifier(UUID.randomUUID().toString(), statName, value, type, EquipmentSlot.OTHER, ModifierSource.OTHER).register(playerData, duration);
        return CommandResult.SUCCESS;
    }
}
