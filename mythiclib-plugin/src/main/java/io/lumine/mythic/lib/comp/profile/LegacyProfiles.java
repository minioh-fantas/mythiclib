package io.lumine.mythic.lib.comp.profile;

import fr.phoenixdevt.profiles.ProfileDataModule;
import fr.phoenixdevt.profiles.ProfileProvider;
import fr.phoenixdevt.profiles.event.ProfileSelectEvent;
import fr.phoenixdevt.profiles.event.ProfileUnloadEvent;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.event.SynchronizedDataLoadEvent;
import io.lumine.mythic.lib.api.player.MMOPlayerData;
import io.lumine.mythic.lib.data.SynchronizedDataHolder;
import io.lumine.mythic.lib.data.SynchronizedDataManager;
import io.lumine.mythic.lib.skill.trigger.TriggerMetadata;
import io.lumine.mythic.lib.skill.trigger.TriggerType;
import io.lumine.mythic.lib.util.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public class LegacyProfiles implements Listener {

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onProfileChooseSkillTrigger(ProfileSelectEvent event) {
        final MMOPlayerData playerData = MMOPlayerData.get(event.getPlayer());
        playerData.triggerSkills(new TriggerMetadata(playerData, TriggerType.LOGIN));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void updateProfileId(ProfileSelectEvent event) {
        MMOPlayerData.get(event.getPlayer()).setProfileId(event.getProfile().getUniqueId());
    }

    /**
     * This is used to hook plugins which support legacy profiles:
     * - MMOCore
     * - MMOItems
     * - MMOInventory
     * <p>
     * This class tells these plugins to load data when a profile is being
     * selected by a player. By default, player data loads on login so this
     * has to be changed to support profiles.
     */
    public static <H extends SynchronizedDataHolder> void hook(@NotNull ProfileProvider profilePlugin,
                                                               @NotNull ProfileDataModule module,
                                                               @NotNull SynchronizedDataManager<H, ?> manager,
                                                               @NotNull Listener fictiveListener,
                                                               @NotNull EventPriority joinEventPriority,
                                                               @NotNull EventPriority quitEventPriority) {

        // Register data holder
        profilePlugin.registerModule(module);

        // Load data on profile select
        UtilityMethods.registerEvent(ProfileSelectEvent.class, fictiveListener, joinEventPriority, event -> {
            final @NotNull H data = manager.get(event.getPlayer());
            if (data.isSynchronized()) event.validate(module); // More resilience
            else
                manager.loadData(data).thenAccept(Tasks.sync(manager.getOwningPlugin(), v -> {
                    event.validate(module);
                    data.markAsSynchronized();
                    Bukkit.getPluginManager().callEvent(new SynchronizedDataLoadEvent(manager, data, event));
                }));
        }, manager.getOwningPlugin(), false);

        // TODO remove data from other plugins when removing profiles in order to empty databases

        // Save data on profile unload
        UtilityMethods.registerEvent(ProfileUnloadEvent.class, fictiveListener, quitEventPriority, event -> {
            manager.unregister(event.getPlayer()).thenAccept(Tasks.sync(manager.getOwningPlugin(), v -> event.validate(module)));
        }, manager.getOwningPlugin(), false);
    }
}
