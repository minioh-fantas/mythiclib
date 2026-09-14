package io.lumine.mythic.lib.comp.profile;

import fr.phoenixdevt.profiles.ProfileDataModule;
import fr.phoenixdevt.profiles.event.ProfileCreateEvent;
import fr.phoenixdevt.profiles.event.ProfileRemoveEvent;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.data.SynchronizedDataManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * Used for simple data plugins like MMOItems or MMOInventory,
 * featuring profile data support but which do not have any
 * placeholders or profile-based features.
 * <p>
 * Very basic implementation of event listening for {@link ProfileCreateEvent}
 * and {@link ProfileRemoveEvent}, the two other profile events being already
 * implemented inside of {@link SynchronizedDataManager#initialize(EventPriority, EventPriority)}
 *
 * @author Jules
 */
public class DefaultProfileDataModule implements ProfileDataModule {
    private final JavaPlugin plugin;
    private final String id;

    public DefaultProfileDataModule(@NotNull JavaPlugin plugin) {
        this.plugin = plugin;
        this.id = UtilityMethods.enumName(plugin.getName()).toLowerCase();
    }

    @Override
    public JavaPlugin getOwningPlugin() {
        return plugin;
    }

    @Override
    public String getIdentifier() {
        return id;
    }

    @EventHandler
    public void onProfileCreate(ProfileCreateEvent event) {
        event.validate(this);
    }

    @EventHandler
    public void onProfileDelete(ProfileRemoveEvent event) {
        event.validate(this);
    }
}
