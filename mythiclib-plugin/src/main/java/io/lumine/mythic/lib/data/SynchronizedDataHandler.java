package io.lumine.mythic.lib.data;

import io.lumine.mythic.lib.util.Closeable;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

/**
 * A data handler "handles" data, it decides what happens when
 * data is either saved OR loaded from either SQL or YAML. Remember
 * that a {@link SynchronizedDataManager} does not know if it is
 * using SQL or YAML.
 *
 * @author jules
 */
public interface SynchronizedDataHandler<H extends SynchronizedDataHolder, O extends OfflineDataHolder> extends Closeable {

    /**
     * Called once on server startup. This can be used for SQL support
     * to initialize database tables, and make sure they are up to date.
     */
    public void setup();

    /**
     * Retrieves all existing player UUIDs from the database.
     */
    public List<UUID> retrieveAllPlayerIds();

    /**
     * Called when player data must be saved in database.
     *
     * @param playerData Player data to save
     * @param autosave   When logging out, is_saved is switched back to 1. This behaviour
     *                   must be skipped when autosaving otherwise this will mess with
     *                   the database.
     * @implNote This method should be called async so there is no need
     * to run async tasks inside of this method implementation.
     */
    public void saveData(@NotNull H playerData, boolean autosave);

    /**
     * This method is always called ASYNC inside of a newly created thread. It should
     * run the SQL methods or local config lookups in order to load player data.
     *
     * @param playerData Player data to be loaded
     * @return If loading was successful. If not, no event shall be called.
     */
    public boolean loadData(@NotNull H playerData);

    public O getOffline(@NotNull UUID profileId);
}
