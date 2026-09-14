package io.lumine.mythic.lib.listener.option;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.event.IndicatorDisplayEvent;
import io.lumine.mythic.lib.hologram.Hologram;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Random;

public abstract class GameIndicators implements Listener {
    private final String format;
    private final DecimalFormat decimalFormat;
    private final boolean move;

    // Options to be used by the hologram module
    public final double radialVelocity, gravity, initialUpwardVelocity, entityHeightPercent, yOffset, rOffset, entityWidthPercent;
    public final long lifespan, tickPeriod;

    protected static final Random RANDOM = new Random();

    public GameIndicators(ConfigurationSection config) {
        decimalFormat = MythicLib.plugin.getMMOConfig().newDecimalFormat(config.getString("decimal-format"));
        format = config.getString("format");
        radialVelocity = config.getDouble("radial-velocity", 1);
        gravity = config.getDouble("gravity", 1);
        initialUpwardVelocity = config.getDouble("initial-upward-velocity", 1);
        entityHeightPercent = config.getDouble("entity-height-percent", .75);
        entityWidthPercent = config.getDouble("entity-width-percent", .75);
        yOffset = config.getDouble("y-offset", .1);
        rOffset = config.getDouble("r-offset", 0.1);
        move = config.getBoolean("move", true);
        lifespan = config.getLong("lifespan", 20);
        tickPeriod = config.getLong("tick-period", 3);
    }

    @NotNull
    public String formatNumber(double d) {
        return decimalFormat.format(d);
    }

    @NotNull
    public String getRaw() {
        return format;
    }

    /**
     * Displays a message using a hologram around an entity.
     * <p>
     * Since 1.3.4 holograms are not provided internally by
     * MythicLib with different providers and priorities. This
     * chooses the best provider depending on what plugins the
     * user has installed.
     *
     * @param entity  Entity used to find the hologram initial position.
     * @param message Message to display
     * @param dir     Average direction of the hologram indicator
     */
    public void displayIndicator(@NotNull Entity entity, @NotNull String message, @NotNull Vector dir, @NotNull IndicatorDisplayEvent.IndicatorType type) {
        IndicatorDisplayEvent called = new IndicatorDisplayEvent(entity, message, type);
        Bukkit.getPluginManager().callEvent(called);
        if (called.isCancelled())
            return;

        final double a = RANDOM.nextDouble() * 2 * Math.PI,

                // Entity width defined as arithmetical mean of widths across the two dimensions
                width = (entity.getBoundingBox().getWidthX() + entity.getBoundingBox().getWidthZ()) / 2,

                // Starting distance to center location
                r = rOffset + width * entityWidthPercent,

                // Starting Z coordinate
                h = yOffset + entity.getHeight() * entityHeightPercent;

        final Location loc = entity.getLocation().add(Math.cos(a) * r, h, Math.sin(a) * r);
        Hologram holo = Hologram.create(loc, Collections.singletonList(MythicLib.plugin.parseColors(called.getMessage())));

        // No movement
        if (!move)
            Bukkit.getScheduler().runTaskLater(MythicLib.plugin, holo::despawn, lifespan);
        else holo.flyOut(this, dir);
    }
}
