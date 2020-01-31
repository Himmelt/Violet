package org.soraworld.violet.plugin;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.soraworld.violet.Violet;
import org.soraworld.violet.api.IPlugin;
import org.soraworld.violet.command.CommandCore;
import org.soraworld.violet.command.SpongeCommand;
import org.soraworld.violet.core.PluginCore;
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

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

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

    @javax.inject.Inject
    @ConfigDir(sharedRoot = false)
    protected Path path;
    @javax.inject.Inject
    protected PluginContainer container;
    private final PluginCore core = new PluginCore(this);

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
        if (path == null) {
            path = new File("config", getId()).toPath();
        }
        return path;
    }

    @Override
    public final @NotNull File getJarFile() {
        Path jarPath = container.getSource().orElse(null);
        return jarPath == null ? getFile() : jarPath.toFile();
    }

    private File getFile() {
        //TODO
        return new File("");
    }

    @Override
    public String getId() {
        return container.getId();
    }

    @Override
    public String getName() {
        return container.getName();
    }

    @Override
    public String getVersion() {
        return container.getVersion().orElse("x.y.z");
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
    public boolean load() {
        return core.load();
    }

    @Override
    public boolean save() {
        return core.save();
    }

    @Override
    public void asyncSave(@Nullable Consumer<Boolean> callback) {
        core.asyncSave(callback);
    }

    @Override
    public boolean backup() {
        return core.backup();
    }

    @Override
    public void asyncBackup(@Nullable Consumer<Boolean> callback) {
        core.asyncBackup(callback);
    }

    @Override
    public boolean isDebug() {
        return core.isDebug();
    }

    @Override
    public void setDebug(boolean debug) {
        core.setDebug(debug);
    }

    @Override
    public void console(@NotNull String message) {
        Sponge.getServer().getConsole().sendMessage(Text.of(core.getChatHead() + message));
    }

    @Override
    public void consoleKey(String key, Object... args) {
        console(core.getChatHead() + core.trans(key, args));
    }

    @Override
    public void log(@NotNull String text) {
        console(text);
    }

    @Override
    public void logKey(@NotNull String key, Object... args) {
        consoleKey(key, args);
    }

    @Override
    public void consoleLog(@NotNull String text) {
        console(text);
    }

    @Override
    public void consoleLogKey(@NotNull String key, Object... args) {
        consoleKey(key, args);
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

    }

    @Override
    public void debug(@NotNull Throwable e) {

    }

    @Override
    public void debugKey(@NotNull String key, Object... args) {

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
