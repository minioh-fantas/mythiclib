package io.lumine.mythic.lib.data.sql;

import io.lumine.mythic.lib.data.OfflineDataHolder;
import io.lumine.mythic.lib.data.SynchronizedDataHandler;
import io.lumine.mythic.lib.data.SynchronizedDataHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class SQLSynchronizedDataHandler<H extends SynchronizedDataHolder, O extends OfflineDataHolder> implements SynchronizedDataHandler<H, O> {
    private final SQLDataSource dataSource;
    private final String userdataTableName, uuidFieldName;

    @Deprecated
    public SQLSynchronizedDataHandler(SQLDataSource dataSource) {
        this(dataSource, null, null);
    }

    public SQLSynchronizedDataHandler(@NotNull SQLDataSource dataSource, @NotNull String userdataTableName, @NotNull String uuidFieldName) {
        this.dataSource = dataSource;
        this.userdataTableName = userdataTableName;
        this.uuidFieldName = uuidFieldName;
    }

    public SQLDataSource getDataSource() {
        return dataSource;
    }

    @Override
    public boolean loadData(@NotNull H playerData) {
        return newDataSynchronizer(playerData).synchronize();
    }

    @Override
    public abstract void saveData(@NotNull H playerData, boolean autosave);

    @Override
    public void close() {
        getDataSource().close();
    }

    public abstract SQLDataSynchronizer newDataSynchronizer(@NotNull H playerData);

    @Override
    public List<UUID> retrieveAllPlayerIds() {

        // Fields that must be closed afterward
        @Nullable Connection connection = null;
        @Nullable PreparedStatement prepared = null;
        @Nullable ResultSet result = null;
        final List<UUID> uuids;

        try {
            connection = dataSource.getConnection();
            prepared = connection.prepareStatement(String.format("SELECT `%s` FROM `%s`;", uuidFieldName, userdataTableName));
            result = prepared.executeQuery(); // Freezes thread

            uuids = new ArrayList<>();
            while (result.next()) {
                var uuid = UUID.fromString(result.getString(uuidFieldName));
                uuids.add(uuid);
            }

        } catch (Exception throwable) {
            throw new RuntimeException("Could not retrieve player UUIDs", throwable);
        } finally {

            // Close resources
            try {
                if (result != null) result.close();
                if (prepared != null) prepared.close();
                if (connection != null) connection.close();
            } catch (SQLException exception) {
                throw new RuntimeException("Could not close SQL resources", exception);
            }
        }

        return uuids;
    }
}
