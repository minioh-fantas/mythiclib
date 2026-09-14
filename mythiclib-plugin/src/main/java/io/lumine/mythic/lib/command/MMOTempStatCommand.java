package io.lumine.mythic.lib.command;

import io.lumine.mythic.lib.api.player.EquipmentSlot;
import io.lumine.mythic.lib.api.player.MMOPlayerData;
import io.lumine.mythic.lib.api.stat.modifier.TemporaryStatModifier;
import io.lumine.mythic.lib.player.modifier.ModifierSource;
import io.lumine.mythic.lib.player.modifier.ModifierType;
import io.lumine.mythic.lib.util.Pair;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

@Deprecated
public class MMOTempStatCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage("/mmotempstat is deprecated. Use instead /ml statmod ...");


        if (args.length < 4) {
            sender.sendMessage(ChatColor.RED + "Not enough args.");
            sender.sendMessage(ChatColor.YELLOW + "Usage: /mmotempstat <player> <stat name> <value> <tick duration>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) sender.sendMessage(ChatColor.RED + "Player not found.");

        MMOPlayerData playerData = MMOPlayerData.get(target);

        Pair<ModifierType, Double> modifierPair = ModifierType.pairFromString(args[2]);
        ModifierType type = modifierPair.getLeft();
        double value = modifierPair.getRight();
        long duration = Long.parseLong(args[3]);

        new TemporaryStatModifier(UUID.randomUUID().toString(), args[1], value, type, EquipmentSlot.OTHER, ModifierSource.OTHER).register(playerData, duration);

        return true;
    }
}
