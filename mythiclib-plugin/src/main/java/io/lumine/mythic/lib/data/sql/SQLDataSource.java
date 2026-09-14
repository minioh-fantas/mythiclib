package io.lumine.mythic.lib.data.sql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.util.Tasks;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.logging.Level;

// TODO clear methods and implement CompletableFutures
public class SQLDataSource {
    private final JavaPlugin plugin;
    private final HikariDataSource dataSource;

    private static final String
            DEFAULT_HOST = "localhost",
            DEFAULT_USERNAME = "root",
            DEFAULT_PASSWORD = "",
            DEFAULT_DATABASE = "minecraft";
    private static final int DEFAULT_PORT = 3306;

    public SQLDataSource(@NotNull JavaPlugin plugin) {
        this.plugin = plugin;

        // Prepare Hikari config
        final ConfigurationSection config = plugin.getConfig().getConfigurationSection("mysql");
        final HikariConfig hikari = new HikariConfig();
        hikari.setPoolName("hikari-" + plugin.getName());
        hikari.setJdbcUrl("jdbc:mysql://" + config.getString("host", DEFAULT_HOST) + ":" + config.getInt("port", DEFAULT_PORT) + "/" + config.getString("database", DEFAULT_DATABASE));
        hikari.setUsername(config.getString("user", DEFAULT_USERNAME));
        hikari.setPassword(config.getString("pass", DEFAULT_PASSWORD));
        hikari.setMaximumPoolSize(config.getInt("maxPoolSize", 10));
        hikari.setMaxLifetime(config.getLong("maxLifeTime", 300000));
        hikari.setConnectionTimeout(config.getLong("connectionTimeOut", 10000));
        hikari.setLeakDetectionThreshold(config.getLong("leakDetectionThreshold", 150000));
        if (config.isConfigurationSection("properties"))
            for (String s : config.getConfigurationSection("properties").getKeys(false))
                hikari.addDataSourceProperty(s, config.getString("properties." + s));

        dataSource = new HikariDataSource(hikari);
    }

    @NotNull
    public JavaPlugin getPlugin() {
        return plugin;
    }

    public void getResult(String sql, Consumer<ResultSet> supplier) {
        execute(connection -> {
            try {
                final PreparedStatement statement = connection.prepareStatement(sql);
                try {
                    supplier.accept(statement.executeQuery());
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
                statement.close();
            } catch (SQLException exception) {
                MythicLib.plugin.getLogger().log(Level.WARNING, "Could not open SQL result statement:");
                exception.printStackTrace();
            }
        });
    }

    public CompletableFuture<Void> getResultAsync(String sql, Consumer<ResultSet> supplier) {
        return Tasks.runAsync(plugin, () -> getResult(sql, supplier));
    }

    public void executeUpdate(String sql) {
        execute(connection -> {
            try {
                final PreparedStatement statement = connection.prepareStatement(sql);
                try {
                    statement.executeUpdate();
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
                statement.close();
            } catch (SQLException exception) {
                MythicLib.plugin.getLogger().log(Level.WARNING, "Could not open SQL statement:");
                exception.printStackTrace();
            }
        });
    }

    public CompletableFuture<Void> executeUpdateAsync(String sql) {
        return Tasks.runAsync(plugin, () -> executeUpdate(sql));
    }

    /**
     * Retrieve a connection from pool and prepare it for
     * use. Connection is closed when consumer is called.
     *
     * @param execute Action to be done with connection
     */
    public void execute(Consumer<Connection> execute) {
        try {
            final Connection connection = dataSource.getConnection();
            try {
                execute.accept(connection);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            } finally {
                connection.close();
            }
        } catch (SQLException exception) {
            MythicLib.plugin.getLogger().log(Level.WARNING, "Could not open SQL connection:");
            exception.printStackTrace();
        }
    }

    /**
     * Retrieve a connection from pool and prepare it for
     * use. Connection is closed when consumer is called.
     * <p>
     * Called asynchronously.
     *
     * @param execute Action to be done with connection
     */
    public CompletableFuture<Void> executeAsync(Consumer<Connection> execute) {
        return Tasks.runAsync(plugin, () -> execute(execute));
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void close() {
        if (dataSource != null)
            dataSource.close();
    }
}

