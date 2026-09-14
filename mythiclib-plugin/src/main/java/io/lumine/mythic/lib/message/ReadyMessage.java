package io.lumine.mythic.lib.message;

import io.lumine.mythic.lib.api.player.MMOPlayerData;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public abstract class ReadyMessage {

    public abstract void send(@NotNull MMOPlayerData playerData);

    public abstract void send(@NotNull Player player);

    /**
     * Only used for MMOCore guild system. Plugins should not
     * inspect the content of messages.
     *
     * @deprecated
     */
    @Deprecated
    @NotNull
    public abstract String getRawContent();
}