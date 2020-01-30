package org.soraworld.violet.command;

import org.jetbrains.annotations.NotNull;
import org.soraworld.violet.wrapper.Wrapper;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author Himmelt
 */
public class SpongeCommand implements CommandCallable {

    private final String name;
    private final CommandCore core;
    private final List<String> aliases = new ArrayList<>();

    public SpongeCommand(@NotNull String name) {
        this.name = name;
        this.core = null;
    }

    public SpongeCommand(@NotNull CommandCore core) {
        this.core = core;
        this.name = core.getName();
        this.aliases.add(name);
        this.aliases.addAll(core.aliases);
    }

    @NotNull
    @Override
    public CommandResult process(@NotNull CommandSource source, @NotNull String args) {
        if (core != null) {
            core.execute(Wrapper.wrapper(source), new Args(args));
        }
        return CommandResult.success();
    }

    @Override
    public @NotNull List<String> getSuggestions(@NotNull CommandSource source, @NotNull String args, @Nullable Location<World> targetPosition) {
        return core == null ? Collections.emptyList() : core.tabComplete(Wrapper.wrapper(source), new Args(args));
    }

    @Override
    public boolean testPermission(@NotNull CommandSource source) {
        return core.testPermission(Wrapper.wrapper(source));
    }

    @NotNull
    @Override
    public Optional<Text> getShortDescription(@NotNull CommandSource source) {
        return Optional.of(getUsage(source));
    }

    @NotNull
    @Override
    public Optional<Text> getHelp(@NotNull CommandSource source) {
        return Optional.of(getUsage(source));
    }

    @NotNull
    @Override
    public Text getUsage(@NotNull CommandSource source) {
        return Text.of(core.getUsage());
    }

    public List<String> getAliases() {
        return aliases;
    }
}
