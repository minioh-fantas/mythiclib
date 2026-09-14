package io.lumine.mythic.lib.listener.option;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.event.FixPlayerInteractEvent;
import io.lumine.mythic.lib.util.annotation.Debug;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

@Debug
public class EntityInteractRangeDebug implements Listener {

    @EventHandler
    public void interact(PlayerInteractEvent event) {

        if (event instanceof FixPlayerInteractEvent)
            MythicLib.plugin.debug("Caught FIX interactEvent: " + event.getAction() + " " + event.getHand() + " " + event.getMaterial());
        else
            MythicLib.plugin.debug("Caught NATURAL interactEvent: " + event.getAction() + " " + event.getHand() + " " + event.getMaterial());

    }

    @EventHandler
    public void attackEvent(EntityDamageByEntityEvent event) {

        MythicLib.plugin.debug("caught EntityDamageByEntityEvent: " + event.getCause());


    }

}
