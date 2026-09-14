package io.lumine.mythic.lib.command.mythiclib;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.player.EquipmentSlot;
import io.lumine.mythic.lib.api.player.MMOPlayerData;
import io.lumine.mythic.lib.command.api.CommandTreeNode;
import io.lumine.mythic.lib.command.api.Parameter;
import io.lumine.mythic.lib.damage.AttackMetadata;
import io.lumine.mythic.lib.damage.DamageMetadata;
import io.lumine.mythic.lib.player.PlayerMetadata;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class DamageCommand extends CommandTreeNode {
    public DamageCommand(CommandTreeNode parent) {
        super(parent, "damage");

        addParameter(Parameter.PLAYER.key("damager"));
        addParameter(Parameter.PLAYER.key("target"));
        addParameter(Parameter.AMOUNT.key("value"));
    }

    @Override
    public @NotNull CommandResult execute(CommandSender sender, String[] args) {
        final World world = sender instanceof Entity ? ((Entity) sender).getWorld() : null;

        // Find damager
        final LivingEntity damager = entity(world, args[1]);
        if (!(damager instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Damager must be a player. Please provide either player name or UUID");
            return CommandResult.FAILURE;
        }

        // Find target
        final LivingEntity target = entity(world, args[2]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Could not find target. Please provide either player name or UUID");
            return CommandResult.FAILURE;
        }

        // Find value
        final double value;
        try {
            value = Double.parseDouble(args[3]);
        } catch (Throwable throwable) {
            return CommandResult.FAILURE;
        }

        // Register attack
        final PlayerMetadata player = MMOPlayerData.get(damager.getUniqueId()).getStatMap().cache(EquipmentSlot.MAIN_HAND);
        final DamageMetadata damage = new DamageMetadata(value); // TODO damage types & elements
        final AttackMetadata attack = new AttackMetadata(damage, target, player);
        MythicLib.plugin.getDamage().registerAttack(attack);

        return CommandResult.SUCCESS;
    }

    @Nullable
    private LivingEntity entity(@Nullable World world, @NotNull String input) {

        // By player name
        final Player found = Bukkit.getPlayer(input);
        if (found != null) return found;

        // Try by UUID
        try {
            UUID uuid = UUID.fromString(input);

            // Player by UUID
            Entity temp = Bukkit.getPlayer(uuid);
            if (temp != null) return (LivingEntity) temp;

            // Entity by UUID
            temp = Bukkit.getEntity(uuid);
            if (temp instanceof LivingEntity) return (LivingEntity) temp;

        } catch (Throwable ignored) {
            // Ignore
        }

        return null;
    }
}
