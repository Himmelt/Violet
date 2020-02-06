package org.soraworld.violet.command;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.soraworld.violet.wrapper.Wrapper;

import java.util.List;

/**
 * @author Himmelt
 */
public class SpigotCommand extends Command {

    private final CommandCore core;

    public SpigotCommand(@NotNull CommandCore core) {
        super(core.getName(), core.getDescription(), core.getUsage(), core.getAliases());
        this.core = core;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        core.execute(Wrapper.wrapper(sender), new Args(args));
        return true;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
        return core.tabComplete(Wrapper.wrapper(sender), new Args(args));
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args, @Nullable Location location) throws IllegalArgumentException {
        return core.tabComplete(Wrapper.wrapper(sender), new Args(args));
    }

    @Override
    public @NotNull String getName() {
        return core.getName();
    }

    @Override
    public boolean testPermission(@NotNull CommandSender sender) {
        return core.testPermission(Wrapper.wrapper(sender));
    }

    @Override
    public boolean testPermissionSilent(@NotNull CommandSender sender) {
        return core.testPermission(Wrapper.wrapper(sender));
    }

    @Override
    public @NotNull List<String> getAliases() {
        return core.getAliases();
    }

    @Override
    public @NotNull String getDescription() {
        return core.getDescription();
    }

    @Override
    public @NotNull String getUsage() {
        return core.getUsage();
    }
}
