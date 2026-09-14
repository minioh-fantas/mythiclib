package io.lumine.mythic.lib.command.mythiclib;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.player.MMOPlayerData;
import io.lumine.mythic.lib.command.api.CommandTreeNode;
import io.lumine.mythic.lib.skill.SimpleSkill;
import io.lumine.mythic.lib.skill.handler.SkillHandler;
import io.lumine.mythic.lib.skill.trigger.TriggerType;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CastCommand extends CommandTreeNode {
    public CastCommand(CommandTreeNode parent) {
        super(parent, "cast");
    }

    @Override
    public @NotNull CommandResult execute(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("> This command is only for players");
            return CommandResult.FAILURE;
        }

        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "> Usage: /mythiclib cast <skill_id>");
            return CommandResult.FAILURE;
        }

        String skillId = args[1].toUpperCase().replace("-", "_");
        SkillHandler<?> handler;
        try {
            handler = MythicLib.plugin.getSkills().getHandlerOrThrow(skillId);
        } catch (RuntimeException exception) {
            sender.sendMessage("> Could not find skill with ID '" + skillId + "'");
            return CommandResult.FAILURE;
        }

        new SimpleSkill(handler).cast(MMOPlayerData.get((Player) sender), TriggerType.COMMAND);

        return CommandResult.SUCCESS;
    }
}
