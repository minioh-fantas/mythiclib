package io.lumine.mythic.lib.module;

import io.lumine.mythic.lib.util.annotation.NotUsed;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * @see io.lumine.mythic.lib.util.MMOPlugin
 */
@NotUsed
public class MMOPluginImpl extends JavaPlugin {
    private final List<Module> modules = new ArrayList<>();

    public MMOPluginImpl() {
        ModuleRegistry.getInstance().registerPlugin(this);
    }

    /**
     * Does this plugin store data? This determines if MythicLib
     * must wait for this plugin to mark his data as synchronized
     * before marking the MMOPlayerData instance as fully synchronized.
     */
    public boolean hasData() {
        return true;
    }

    /**
     * It is plugin a profile plugin
     */
    public boolean hasProfiles() {
        return false;
    }

    public void registerModule(@NotNull Module module) {
        this.modules.add(module);
    }

    /*
    @NotNull
    public List<GeneralManager> getManagers() {
        return managers;
    }

    public void registerManager(@NotNull GeneralManager manager) {
        Validate.isTrue(MMOPluginRegistry.getInstance().isRegistrationAllowed(), "Manager registration is not allowed");

        for (GeneralManager checked : managers)
            if (checked.getId().equals(manager.getId()))
                throw new IllegalArgumentException("A manager with the same name already exists");

        this.managers.add(manager);
    }*/

    public void debug(String message) {
        String msg = String.format("[DEBUG] %s", message);
        getLogger().log(Level.INFO, msg);

        for (Player online : Bukkit.getOnlinePlayers())
            online.sendMessage(msg);
    }
}