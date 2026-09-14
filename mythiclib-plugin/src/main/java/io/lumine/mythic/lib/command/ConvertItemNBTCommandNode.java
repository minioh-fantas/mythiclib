package io.lumine.mythic.lib.command;

import io.lumine.mythic.lib.command.api.CommandTreeNode;
import io.lumine.mythic.lib.command.api.Parameter;
import io.lumine.mythic.lib.data.*;
import io.lumine.mythic.lib.util.Lazy;
import io.lumine.mythic.lib.util.annotation.BackwardsCompatibility;
import io.lumine.mythic.lib.util.io.SafeBukkitObjectOutputStream;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * 1.20.4 changed the way NBT tags are stored inside item NBTs. Spigot
 * does not provide backwards compatibility for external NBT paths like
 * "MMOITEMS_xxxx" or literally any other plugin. This creates a major issue
 * when upgrading from 1.20.4 to 1.20.5+ as all NBT-related item data is lost,
 * including MMOItems item data.
 */
@BackwardsCompatibility(version = "1.20.4")
public class ConvertItemNBTCommandNode<H extends SynchronizedDataHolder, O extends OfflineDataHolder> extends CommandTreeNode {

    @Nullable
    private final String permission;
    private final Supplier<String> permissionMessage;

    private final SynchronizedDataManager<H, O> dataManager;

    public ConvertItemNBTCommandNode(CommandTreeNode parent,
                                     @NotNull SynchronizedDataManager<H, O> dataManager,
                                     @Nullable String permission,
                                     @Nullable Supplier<String> permissionMessage) {
        super(parent, "convert-item-nbts");

        addParameter(new Parameter("enable", true, (tree, list) -> {
            list.add("to");
            list.add("from");
        }));

        this.permission = permission;
        this.permissionMessage = permissionMessage;
        this.dataManager = dataManager;
    }

    @NotNull
    @Override
    public CommandResult execute(CommandSender sender, String[] args) {
        if (permission != null && !sender.hasPermission(permission)) {
            sender.sendMessage(permissionMessage.get());
            return CommandResult.FAILURE;
        }

        final boolean toNbtApi = !(args.length > 1 && args[1].equalsIgnoreCase("from"));
        final String fromString = toNbtApi ? "Bukkit" : "NBTAPI", toString = toNbtApi ? "NBTAPI" : "Bukkit";

        if (toNbtApi && Bukkit.getPluginManager().getPlugin("NBTAPI") == null) {
            sender.sendMessage("You are asking to convert item data to NBTAPI however the plugin is not installed. Please install NBTAPI and try again.");
            return CommandResult.FAILURE;
        }

        sender.sendMessage("Converting data to NBTAPI...");

        // Export data from/to the same data source (ingenious!!)
        Lazy<SynchronizedDataHandler<H, O>> dataHandlerLazy = Lazy.of(dataManager.getDataHandler());
        try {
            SafeBukkitObjectOutputStream.USE_NBT_API = toNbtApi;
            sender.sendMessage("Using NBTAPI: " + toNbtApi);
            DataExport<H, O> work = new DataExport<>(dataManager, sender);
            work.setCallback(() -> {
                SafeBukkitObjectOutputStream.USE_NBT_API = false;
                sender.sendMessage(String.format("Successfully converted item data from %s to %s. Please now restart your server.", fromString, toString));
            });

            boolean startResult = work.start(dataHandlerLazy, dataHandlerLazy);

            if (startResult) {
                sender.sendMessage(String.format("Item data conversion from %s to %s started, please wait... ", fromString, toString));
                return CommandResult.SUCCESS;
            }

            return CommandResult.FAILURE;

        } catch (Throwable throwable) {
            sender.sendMessage("An error occured: " + throwable.getMessage());
            sender.sendMessage("Please check console for more information.");
            throwable.printStackTrace();
            return CommandResult.FAILURE;
        }
    }
}
