package org.soraworld.violet.plugin;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.soraworld.violet.api.ICommand;
import org.soraworld.violet.api.IPlugin;
import org.soraworld.violet.command.SpongeCommand;
import org.soraworld.violet.exception.MainManagerException;
import org.soraworld.violet.inject.Command;
import org.soraworld.violet.inject.PluginData;
import org.soraworld.violet.manager.SpongeManager;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.PluginContainer;

import javax.inject.Inject;
import java.io.File;
import java.nio.file.Path;

public class SpongePlugin<M extends SpongeManager> implements IPlugin<M> {

    @Inject
    @ConfigDir(sharedRoot = false)
    protected Path path;
    @Inject
    protected PluginContainer container;
    protected M manager;
    protected final PluginData pluginData = new PluginData();

    @Listener
    public void onLoad(GamePreInitializationEvent event) throws MainManagerException {
        onLoad();
    }

    @Listener
    public void onEnable(GameInitializationEvent event) {
        onEnable();
    }

    @Listener
    public void onDisable(GameStoppingServerEvent event) {
        onDisable();
    }

    public Path getRootPath() {
        if (path == null) path = new File("config", getId()).toPath();
        return path;
    }

    public String getId() {
        return container.getId();
    }

    public String getName() {
        return container.getName();
    }

    public String getVersion() {
        return container.getVersion().orElse("x.y.z");
    }

    public boolean isEnabled() {
        return Sponge.getPluginManager().isLoaded(container.getId());
    }

    public M getManager() {
        return manager;
    }

    public void setManager(M manager) {
        this.manager = manager;
    }

    public void registerListener(@NotNull Object listener) {
    }

    @Nullable
    public ICommand registerCommand(@NotNull Command annotation) {
        return null;
    }

    public boolean registerCommand(@NotNull ICommand command) {
        if (command instanceof SpongeCommand) {
            Sponge.getCommandManager().register(this, (SpongeCommand) command, command.getAliases());
            pluginData.commands.add(command);
        }
        return false;
    }

    public PluginData getPluginData() {
        return pluginData;
    }
}
