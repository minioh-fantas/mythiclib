package io.lumine.mythic.lib.data.sql;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.data.SynchronizedDataHolder;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;

/**
 * This class is used to synchronize player data between
 * servers. This fixes the issue of player data being
 * lost when teleporting to another server.
 * TODO: Merge with {@link SQLSynchronizedDataHandler}
 */
public abstract class SQLDataSynchronizer<H extends SynchronizedDataHolder> {
    private final SQLDataSource dataSource;
    private final H playerData;
    private final UUID effectiveId;
    private final String tableName, uuidFieldName;
    private final long start = System.currentTimeMillis();

    private int tries;

    private static final int RETRIEVAL_PERIOD = 1000;

    @Deprecated
    public SQLDataSynchronizer(String tableName, String uuidFieldName, SQLDataSource dataSource, H playerData, boolean profilePlugin) {
        this(tableName, uuidFieldName, dataSource, playerData);
    }

    /**
     * The SQL data synchronizer will find the column in the database with
     * the given UUID. It may use either the profile or direct player UUID.
     * <p>
     * As general rule, profile plugins use the player UUID and other plugins
     * supporting profile-based data saving use the profile UUID.
     * <p>
     * One exception is when trying to access data from an offline player.
     * Their profile UUID is not clearly defined, and therefore the synchronizer
     * will directly use the player UUID. This is used for placeholder requests
     * in MMOProfiles and in the /exportdata command.
     *
     * @param tableName     Table name for player data storage
     * @param uuidFieldName UUID field name in table
     * @param dataSource    SQL connection being used
     * @param playerData    Player data being synchronized
     */
    public SQLDataSynchronizer(String tableName, String uuidFieldName, SQLDataSource dataSource, H playerData) {
        this.tableName = tableName;
        this.uuidFieldName = uuidFieldName;
        this.playerData = playerData;
        this.dataSource = dataSource;
        this.effectiveId = playerData.getEffectiveId();
    }

    public H getData() {
        return playerData;
    }

    /**
     * Tries to fetch data once. If the maximum amount of fetches
     * hasn't been reached yet, it will try again later if no up-to-date
     * data has been retrieved.
     * <p>
     * This method freezes the thread and shall be called async.
     */
    public boolean synchronize() {
        tries++;

        // Fields that must be closed afterwards
        @Nullable Connection connection = null;
        @Nullable PreparedStatement prepared = null;
        @Nullable ResultSet result = null;
        boolean retry = false, success = false;

        try {
            connection = dataSource.getConnection();
            prepared = connection.prepareStatement("SELECT * FROM `" + tableName + "` WHERE `" + uuidFieldName + "` = ?;");
            prepared.setString(1, effectiveId.toString());

            UtilityMethods.debug(dataSource.getPlugin(), "SQL", "Trying to load data of " + effectiveId);
            result = prepared.executeQuery(); // Freezes thread

            // Check if player went offline
            if (playerWentOffline()) {
                UtilityMethods.debug(dataSource.getPlugin(), "SQL", "Stopped data retrieval as '" + effectiveId + "' went offline");
                return false;
            }

            // Load data if found
            if (result.next()) {
                if (tries > MythicLib.plugin.getMMOConfig().maxSyncTries || result.getInt("is_saved") == 1) {
                    confirmReception(connection);
                    success = true;
                    loadData(result);
                    if (tries > MythicLib.plugin.getMMOConfig().maxSyncTries)
                        UtilityMethods.debug(dataSource.getPlugin(), "SQL", "Maximum number of tries reached.");
                    UtilityMethods.debug(dataSource.getPlugin(), "SQL", "Found and loaded data of '" + effectiveId + "'");
                    UtilityMethods.debug(dataSource.getPlugin(), "SQL", "Time taken: " + (System.currentTimeMillis() - start) + "ms");
                } else {
                    UtilityMethods.debug(dataSource.getPlugin(), "SQL", "Did not load data of '" + effectiveId + "' as 'is_saved' is set to 0, trying again in " + RETRIEVAL_PERIOD + "ms");
                    retry = true;
                }
            } else {

                // Empty player data
                confirmReception(connection);
                success = true;
                loadEmptyData();
                UtilityMethods.debug(dataSource.getPlugin(), "SQL", "Found empty data for '" + effectiveId + "', loading default...");
            }

        } catch (Exception throwable) {
            dataSource.getPlugin().getLogger().log(Level.WARNING, "Could not load player data of '" + effectiveId + "':");
            throwable.printStackTrace();
        } finally {

            // Close resources
            try {
                if (result != null) result.close();
                if (prepared != null) prepared.close();
                if (connection != null) connection.close();
            } catch (SQLException exception) {
                dataSource.getPlugin().getLogger().log(Level.WARNING, "Could not load player data of '" + effectiveId + "':");
                exception.printStackTrace();
            }
        }

        // Synchronize after closing resources
        if (retry) {
            try {
                Thread.sleep(RETRIEVAL_PERIOD);
            } catch (InterruptedException exception) {
                throw new RuntimeException(exception);
            }
            return synchronize();
        }

        return success;
    }

    private boolean playerWentOffline() {
        return !playerData.getMMOPlayerData().isLookup() && !playerData.getMMOPlayerData().isOnline();
    }

    /**
     * This confirms the loading of player and switches "is_saved" back to 0
     *
     * @param connection Current SQL connection
     * @throws SQLException Any exception. When thrown, the data will not be loaded.
     */
    private void confirmReception(Connection connection) throws SQLException {
        if (playerData.getMMOPlayerData().isLookup()) return;

        @Nullable PreparedStatement prepared = null;
        try {
            prepared = connection.prepareStatement("INSERT INTO " + tableName + "(`uuid`, `is_saved`) VALUES(?, 0) ON DUPLICATE KEY UPDATE `is_saved` = 0;");
            prepared.setString(1, effectiveId.toString());
            prepared.executeUpdate();
        } catch (Exception exception) {
            dataSource.getPlugin().getLogger().log(Level.WARNING, "Could not confirm data sync of " + effectiveId);
            exception.printStackTrace();
        } finally {
            if (prepared != null) prepared.close();
        }
    }

    /**
     * Called when the right result set has finally been found.
     *
     * @param result Row found in the database
     */
    public abstract void loadData(ResultSet result) throws SQLException, IOException, ClassNotFoundException;

    /**
     * Called when no data was found.
     */
    public abstract void loadEmptyData();
}
