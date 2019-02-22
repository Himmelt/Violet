package org.soraworld.violet.command;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.soraworld.violet.api.IManager;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class VioletCommand implements CommandCallable {

    @NotNull
    public final ICommand command;

    public VioletCommand(@NotNull String name, @Nullable String permission, @Nullable ICommand parent, @NotNull IManager manager) {
        this.name = name;
        this.permission = permission;
        this.parent = parent;
        this.manager = manager;
    }

    /* execute */
    @NotNull
    public CommandResult process(@NotNull CommandSource sender, @NotNull String args) {
        command.execute(sender, new Args(args));
        return CommandResult.success();
    }

    /* tabComplete */
    @NotNull
    public List<String> getSuggestions(@NotNull CommandSource sender, @NotNull String args, Location<World> location) {
        String[] ss = args.trim().split("[ ]+");
        if (!args.isEmpty() && args.endsWith(" ")) {
            ss = Arrays.copyOf(ss, ss.length + 1);
            ss[ss.length - 1] = "";
        }
        return command.tabComplete(sender, new Args(ss));
    }

    /* testPermission */
    public boolean testPermission(@NotNull CommandSource sender) {
        return command.permission == null || sender.hasPermission(command.permission);
    }

    /* usage */
    @NotNull
    public Text getUsage(@NotNull CommandSource sender) {
        return Text.of(command.usage);
    }

    @NotNull
    public Optional<Text> getShortDescription(@NotNull CommandSource source) {
        return Optional.of(getUsage(source));
    }

    @NotNull
    public Optional<Text> getHelp(@NotNull CommandSource source) {
        return Optional.of(getUsage(source));
    }

    /* others */
    public int hashCode() {
        return command.hashCode();
    }

    public boolean equals(Object obj) {
        if (obj instanceof VioletCommand) return command.equals(((VioletCommand) obj).command);
        return false;
    }
}
