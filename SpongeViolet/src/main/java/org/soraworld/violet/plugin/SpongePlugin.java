package org.soraworld.violet.plugin;

import org.soraworld.violet.Violet;
import org.soraworld.violet.api.IPlugin;
import org.soraworld.violet.command.CommandArgs;
import org.soraworld.violet.command.SpongeCommand;
import org.soraworld.violet.manager.SpongeManager;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public abstract class SpongePlugin implements IPlugin, CommandCallable {

    protected SpongeManager manager;
    protected SpongeCommand command;

    @Inject
    @ConfigDir(sharedRoot = false)
    private Path path;

    @Listener
    public void onEnable(GameInitializationEvent event) {
        if (path == null) path = new File("config", getId()).toPath();
        if (Files.notExists(path)) {
            try {
                Files.createDirectories(path);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        manager = registerManager(path);
        manager.load();
        manager.afterLoad();
        command = registerCommand();
        Sponge.getCommandManager().register(this, this, getId());
        List<Object> listeners = registerListeners();
        if (listeners != null && !listeners.isEmpty()) {
            for (Object listener : listeners) {
                Sponge.getEventManager().registerListeners(this, listener);
            }
        }
        afterEnable();
    }

    @Listener
    public void onDisable(GameStoppingServerEvent event) {
        beforeDisable();
    }

    @Nonnull
    protected abstract SpongeManager registerManager(Path path);

    @Nonnull
    protected abstract SpongeCommand registerCommand();

    @Nonnull
    public CommandResult process(@Nonnull CommandSource sender, @Nonnull String args) {
        if (sender instanceof Player) command.execute(((Player) sender), new CommandArgs(args));
        else if (command.nop()) command.execute(sender, new CommandArgs(args));
        else manager.sendKey(sender, Violet.KEY_ONLY_PLAYER);
        return CommandResult.success();
    }

    @Nonnull
    public List<String> getSuggestions(@Nonnull CommandSource sender, @Nonnull String args, @Nullable Location<World> location) {
        return command.tabCompletions(new CommandArgs(args));
    }

    public boolean testPermission(@Nonnull CommandSource source) {
        return true;
    }

    @Nonnull
    public Optional<Text> getShortDescription(@Nonnull CommandSource source) {
        return Optional.of(getUsage(source));
    }

    @Nonnull
    public Optional<Text> getHelp(@Nonnull CommandSource source) {
        return Optional.of(getUsage(source));
    }

    @Nonnull
    public Text getUsage(@Nonnull CommandSource source) {
        return Text.of(command.getUsage());
    }

    public void afterEnable() {
        if (manager != null) manager.consoleKey(Violet.KEY_PLUGIN_ENABLED, getId());
    }

    public void beforeDisable() {
        if (manager != null) manager.consoleKey(Violet.KEY_PLUGIN_DISABLED, getId());
    }
}
