package io.lumine.mythic.lib.command.api;

import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class ToggleableCommand {
    private final String name, description, permission;
    private final Function<ConfigurationSection, CommandTreeRoot> generator;
    private final List<String> aliases;
    @Nullable
    private final Predicate<Void> enabled;
    private final boolean forced;

    public ToggleableCommand(@NotNull String name,
                             @Nullable String permission,
                             @NotNull String description,
                             @NotNull Function<ConfigurationSection, CommandTreeRoot> generator,
                             @NotNull String... aliases) {
        this(name, permission, description, generator, null, aliases);
    }

    public ToggleableCommand(@NotNull String name,
                             @Nullable String permission,
                             @NotNull String description,
                             @NotNull Function<ConfigurationSection, CommandTreeRoot> generator,
                             @Nullable Predicate<Void> enabled,
                             @NotNull String... aliases) {
        this(name, permission, description, generator, enabled, false, aliases);
    }

    public ToggleableCommand(@NotNull String name,
                             @Nullable String permission,
                             @NotNull String description,
                             @NotNull Function<ConfigurationSection, CommandTreeRoot> generator,
                             @Nullable Predicate<Void> enabled,
                             boolean forced,
                             @NotNull String... aliases) {
        this.name = name;
        this.description = description;
        this.generator = generator;
        this.aliases = Arrays.asList(aliases);
        this.permission = permission;
        this.enabled = enabled;
        this.forced = forced;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public String getDescription() {
        return description;
    }

    @NotNull
    public List<String> getAliases() {
        return aliases;
    }

    @Nullable
    public String getPermission() {
        return permission;
    }

    /**
     * @return If true, this command will register whatever is set in the config.
     */
    public boolean isForced() {
        return forced;
    }

    @NotNull
    public String getConfigPath() {
        return name.toLowerCase().replace("_", "-");
    }

    @NotNull
    @Deprecated
    public CommandTreeRoot generate(ConfigurationSection config) {
        return generator.apply(config);
    }

    public @NotNull BukkitCommand toBukkit(ConfigurationSection config) {
        return generator.apply(config).getCommand();
    }

    public boolean isEnabled() {
        return enabled == null || enabled.test(null);
    }
}
