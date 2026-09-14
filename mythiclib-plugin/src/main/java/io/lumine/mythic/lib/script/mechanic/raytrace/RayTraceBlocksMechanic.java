package io.lumine.mythic.lib.script.mechanic.raytrace;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.script.Script;
import io.lumine.mythic.lib.script.mechanic.MechanicMetadata;
import io.lumine.mythic.lib.script.mechanic.shaped.RayTraceMechanic;
import io.lumine.mythic.lib.script.mechanic.type.DirectionMechanic;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.util.DoubleFormula;
import io.lumine.mythic.lib.util.configobject.ConfigObject;
import io.lumine.mythic.lib.util.lang3.Validate;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

@Deprecated
@MechanicMetadata
public class RayTraceBlocksMechanic extends DirectionMechanic {
    private final DoubleFormula range, step;
    private final Script onHit, onTick;
    private final boolean ignorePassable;

    public RayTraceBlocksMechanic(ConfigObject config) {
        super(config);

        onTick = config.contains("tick") ? MythicLib.plugin.getSkills().getScriptOrThrow(config.getString("tick")) : null;
        onHit = config.contains("hit_block") ? MythicLib.plugin.getSkills().getScriptOrThrow(config.getString("hit_block")) : null;
        ignorePassable = config.getBoolean("ignore_passable", false);

        range = config.getDoubleFormula("range", DoubleFormula.constant(RayTraceMechanic.DEFAULT_RANGE));
        step = config.getDoubleFormula("step", DoubleFormula.constant(RayTraceMechanic.DEFAULT_STEP));
    }

    @Override
    public void cast(SkillMetadata meta, Location source, Vector dir) {
        final double range = this.range.evaluate(meta);
        final double step = this.step.evaluate(meta);

        Validate.isTrue(range > 0, "Range must be strictly positive");
        Validate.isTrue(step > 0, "Step must be strictly positive (don't make it too low)");

        final RayTraceResult result = source.getWorld().rayTraceBlocks(source, dir, range, FluidCollisionMode.NEVER, ignorePassable);
        final double length = result == null ? range : result.getHitPosition().distance(source.toVector());

        if (onTick != null) for (double j = 0; j < length; j += step) {
            Location intermediate = source.clone().add(dir.clone().multiply(j));
            onTick.cast(meta.clone(source, intermediate, null, null));
        }

        if (result != null && onHit != null && result.getHitBlock() != null) {
            Location hitPosition = result.getHitPosition().toLocation(source.getWorld());
            onHit.cast(meta.clone(source, hitPosition, null, null));
        }
    }
}