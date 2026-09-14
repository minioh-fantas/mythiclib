package io.lumine.mythic.lib.api.event;

import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;

public class FixPlayerInteractEvent extends PlayerInteractEvent {
    public FixPlayerInteractEvent(@NotNull Player who) {
        super(who, Action.LEFT_CLICK_AIR, null, null, BlockFace.EAST, EquipmentSlot.HAND);
    }
}
