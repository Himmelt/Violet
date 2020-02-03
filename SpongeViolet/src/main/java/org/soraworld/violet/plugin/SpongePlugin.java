package org.soraworld.violet.plugin;

import org.jetbrains.annotations.NotNull;
import org.soraworld.violet.Violet;
import org.soraworld.violet.api.IPlugin;
import org.soraworld.violet.command.CommandCore;
import org.soraworld.violet.command.SpongeCommand;
import org.soraworld.violet.core.PluginCore;
import org.soraworld.violet.util.FileUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;

import javax.inject.Inject;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Himmelt
 */
@Plugin(
        id = Violet.PLUGIN_ID,
        name = Violet.PLUGIN_NAME,
        version = Violet.PLUGIN_VERSION,
        authors = {"Himmelt"},
        url = "https://github.com/Himmelt/Violet",
        description = "Violet Plugin Library."
)
public class SpongePlugin implements IPlugin {

    protected final Path path;
    protected final PluginContainer container;
    protected final PluginCore core;

    @Inject
    public SpongePlugin(@ConfigDir(sharedRoot = false) Path path, PluginContainer container) {
        this.path = path;
        this.container = container;
        this.core = new PluginCore(this);
    }

    @Listener
    public void onLoad(GamePreInitializationEvent event) {
        core.onLoad();
    }

    @Listener
    public void onEnable(GameInitializationEvent event) {
        // TODO makesure sync for spigot , worlds load ??
        core.onEnable();
    }

    @Listener
    public void onDisable(GameStoppingServerEvent event) {
        core.onDisable();
    }


    @Override
    public final @NotNull Path getRootPath() {
        return path;
    }

    @Override
    public final @NotNull File getJarFile() {
        Path jarPath = container.getSource().orElse(null);
        return jarPath == null ? FileUtils.getJarPath(getClass()).toFile() : jarPath.toFile();
    }

    @Override
    public String id() {
        return container.getId();
    }

    @Override
    public String bStatsId() {
        return id().equals(Violet.PLUGIN_ID) ? "3176" : "";
    }

    @Override
    public String name() {
        return container.getName();
    }

    @Override
    public String version() {
        return container.getVersion().orElse("x.y.z");
    }

    @Override
    public @NotNull PluginCore getCore() {
        return core;
    }

    @Override
    public boolean isEnabled() {
        return Sponge.getPluginManager().isLoaded(container.getId());
    }

    @Override
    public void registerListener(@NotNull Object listener) {
        Sponge.getEventManager().registerListeners(this, listener);
    }

    @Override
    public boolean registerCommand(@NotNull CommandCore core) {
        ArrayList<String> aliases = new ArrayList<>(core.getAliases());
        aliases.add(0, core.getName());
        return registerCommand(new SpongeCommand(core), aliases);
    }

    @Override
    public boolean registerCommand(@NotNull Object command, String... aliases) {
        if (command instanceof CommandCallable) {
            return Sponge.getCommandManager().register(this, (CommandCallable) command, aliases).isPresent();
        }
        return false;
    }

    @Override
    public boolean registerCommand(@NotNull Object command, @NotNull List<String> aliases) {
        if (command instanceof CommandCallable) {
            return Sponge.getCommandManager().register(this, (CommandCallable) command, aliases).isPresent();
        }
        return false;
    }

    @Override
    public boolean setLang(String lang) {
        return core.setLang(lang);
    }

    @Override
    public String getLang() {
        return core.getLang();
    }

    @Override
    public String trans(@NotNull String key, Object... args) {
        return core.trans(key, args);
    }

    @Override
    public boolean extract() {
        return core.extract();
    }

    @Override
    public void console(@NotNull String message) {
        Sponge.getServer().getConsole().sendMessage(Text.of(core.getChatHead() + message));
    }

    @Override
    public void consoleKey(String key, Object... args) {
        console(core.trans(key, args));
    }

    @Override
    public void log(@NotNull String text) {
        core.log(text);
    }

    @Override
    public void logKey(@NotNull String key, Object... args) {
        core.log(core.trans(key, args));
    }

    @Override
    public void consoleLog(@NotNull String text) {
        console(text);
        log(text);
    }

    @Override
    public void consoleLogKey(@NotNull String key, Object... args) {
        consoleLog(core.trans(key, args));
    }

    @Override
    public void broadcast(@NotNull String message) {
        Sponge.getServer().getBroadcastChannel().send(Text.of(core.getChatHead() + message));
    }

    @Override
    public void broadcastKey(@NotNull String key, Object... args) {
        Sponge.getServer().getBroadcastChannel().send(Text.of(core.getChatHead() + core.trans(key, args)));
    }

    @Override
    public void debug(@NotNull String message) {
        core.debug(message);
    }

    @Override
    public void debug(@NotNull Throwable e) {
        core.debug(e);
    }

    @Override
    public void debugKey(@NotNull String key, Object... args) {
        core.debugKey(key, args);
    }

    @Override
    public void notifyOps(@NotNull String message) {

    }

    @Override
    public void notifyOpsKey(@NotNull String key, Object... args) {

    }

    @Override
    public void runTask(@NotNull Runnable task) {
        Sponge.getScheduler().createSyncExecutor(this).execute(task);
    }

    @Override
    public void runTaskAsync(@NotNull Runnable task) {
        Sponge.getScheduler().createAsyncExecutor(this).execute(task);
    }

    @Override
    public void runTaskLater(@NotNull Runnable task, long delay) {
        Sponge.getScheduler().createSyncExecutor(this).schedule(task, delay * 50, TimeUnit.MILLISECONDS);
    }

    @Override
    public void runTaskLaterAsync(@NotNull Runnable task, long delay) {
        Sponge.getScheduler().createAsyncExecutor(this).schedule(task, delay * 50, TimeUnit.MILLISECONDS);
    }
}
