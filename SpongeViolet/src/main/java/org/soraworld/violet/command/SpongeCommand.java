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
import java.util.List;
import java.util.Optional;

/**
 * @author Himmelt
 */
public class SpongeCommand implements CommandCallable {

    private final CommandCore core;

    public SpongeCommand(@NotNull CommandCore core) {
        this.core = core;
    }

    @Override
    public @NotNull CommandResult process(@NotNull CommandSource source, @NotNull String args) {
        core.execute(Wrapper.wrapper(source), new Args(args));
        return CommandResult.success();
    }

    @Override
    public @NotNull List<String> getSuggestions(@NotNull CommandSource source, @NotNull String args, @Nullable Location<World> targetPosition) {
        return core.tabComplete(Wrapper.wrapper(source), new Args(args));
    }

    @Override
    public boolean testPermission(@NotNull CommandSource source) {
        return core.testPermission(Wrapper.wrapper(source));
    }

    @Override
    public @NotNull Optional<Text> getShortDescription(@NotNull CommandSource source) {
        return Optional.of(Text.of(core.getDescription()));
    }

    @Override
    public @NotNull Optional<Text> getHelp(@NotNull CommandSource source) {
        return Optional.of(Text.of(core.getUsage()));
    }

    @Override
    public @NotNull Text getUsage(@NotNull CommandSource source) {
        return Text.of(core.getUsage());
    }

    public List<String> getAliases() {
        return core.getAliases();
    }
}
