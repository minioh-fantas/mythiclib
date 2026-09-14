package io.lumine.mythic.lib.api.stat.handler;

import io.lumine.mythic.lib.api.stat.StatInstance;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

/**
 * A stat handler that calls the update of another stat when itself
 * needs an update. For example, Speed Malus Reduction calls the update
 * of Movement Speed whenever changed. Obviously Movement Speed still
 * calls its own updates.
 *
 * @author jules
 */
public class DelegateStatHandler extends StatHandler {
    private final StatHandler delegate;

    public DelegateStatHandler(@NotNull ConfigurationSection config, @NotNull String stat, @NotNull StatHandler delegate) {
        super(config, stat);

        this.delegate = delegate;
    }

    @Override
    public void runUpdate(@NotNull StatInstance instance) {
        delegate.runUpdate(instance.getMap().getInstance(delegate.getStat()));
    }
}
