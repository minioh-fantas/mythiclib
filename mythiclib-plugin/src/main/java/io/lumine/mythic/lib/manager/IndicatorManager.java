package io.lumine.mythic.lib.manager;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.comp.mythicmobs.MythicMobsHealIndicators;
import io.lumine.mythic.lib.listener.option.DamageIndicators;
import io.lumine.mythic.lib.listener.option.RegenIndicators;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * mythiclib
 * 09/11/2022
 *
 * @author Roch Blondiaux (Kiwix).
 */
public class IndicatorManager {

    private final List<Listener> indicatorsListeners = new ArrayList<>();

    /**
     * Register all indicators listeners
     * and add them to the list.
     */
    public void load(FileConfiguration configuration) {
        final PluginManager manager = Bukkit.getPluginManager();
        final MythicLib plugin = MythicLib.plugin;

        // Damage
        if (configuration.getBoolean("game-indicators.damage.enabled"))
            try {
                Listener damageIndicators = new DamageIndicators(configuration.getConfigurationSection("game-indicators.damage"));
                manager.registerEvents(damageIndicators, plugin);
                indicatorsListeners.add(damageIndicators);
            } catch (RuntimeException exception) {
                plugin.getLogger().log(Level.WARNING, "Could not load damage indicators: " + exception.getMessage());
            }

        // Regen
        if (configuration.getBoolean("game-indicators.regen.enabled")) {
            RegenIndicators regenIndicators = new RegenIndicators(configuration.getConfigurationSection("game-indicators.regen"));
            manager.registerEvents(regenIndicators, plugin);
            indicatorsListeners.add(regenIndicators);

            // Support for MythicMobs heal mechanic
            if (Bukkit.getPluginManager().getPlugin("MythicMobs") != null) {
                final MythicMobsHealIndicators mmIndicators = new MythicMobsHealIndicators(regenIndicators);
                manager.registerEvents(mmIndicators, plugin);
                indicatorsListeners.add(mmIndicators);
            }
        }
    }

    /**
     * Unregister all listeners, remove them from the
     * list and call the {@link IndicatorManager#load(FileConfiguration)} method.
     */
    public void reload(FileConfiguration configuration) {
        // Unregister listeners & clear list
        indicatorsListeners.forEach(HandlerList::unregisterAll);
        indicatorsListeners.clear();

        // Register listeners
        load(configuration);
    }
}
