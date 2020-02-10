package org.soraworld.violet.plugin;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.soraworld.violet.Violet;
import org.soraworld.violet.api.IPlugin;
import org.soraworld.violet.command.CommandCore;
import org.soraworld.violet.command.SpigotCommand;
import org.soraworld.violet.core.PluginCore;
import org.soraworld.violet.text.ChatType;
import org.soraworld.violet.wrapper.Wrapper;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

/**
 * @author Himmelt
 */
public class SpigotPlugin extends JavaPlugin implements IPlugin {

    private final PluginCore core;
    private static final CommandMap COMMAND_MAP;

    static {
        CommandMap map = null;
        try {
            Field field = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            field.setAccessible(true);
            Object object = field.get(Bukkit.getServer());
            if (object instanceof CommandMap) {
                map = (CommandMap) object;
            } else {
                System.out.println("Invalid CommandMap in Server !!!");
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        COMMAND_MAP = map;
    }

    public SpigotPlugin() {
        if (!Violet.VIOLET_VERSION.match(violetVersion())) {
            throw new RuntimeException("Plugin " + id() + " incompatible with server violet@" + Violet.VIOLET_VERSION + " , need violet@" + violetVersion());
        }
        core = new PluginCore(this);
    }

    @Override
    public final String version() {
        return getDescription().getVersion();
    }

    @Override
    public @NotNull PluginCore getCore() {
        return core;
    }

    @Override
    public final void onLoad() {
        core.onLoad();
    }

    @Override
    public final void onEnable() {
        core.onEnable();
    }

    @Override
    public final void onDisable() {
        core.onDisable();
    }

    @Override
    public final @NotNull Path getRootPath() {
        return getDataFolder().toPath();
    }

    @Override
    public final @NotNull File getJarFile() {
        return getFile();
    }

    @Override
    public final void registerListener(@NotNull Object listener) {
        if (listener instanceof Listener) {
            getServer().getPluginManager().registerEvents((Listener) listener, this);
        }
    }

    @Override
    public boolean registerCommand(@NotNull CommandCore core) {
        return registerCommand(new SpigotCommand(core));
    }

    @Override
    public boolean registerCommand(@NotNull Object command, String... aliases) {
        if (command instanceof Command) {
            if (aliases.length > 0) {
                ((Command) command).setAliases(Arrays.asList(aliases));
            }
            return COMMAND_MAP != null && COMMAND_MAP.register(id(), (Command) command);
        }
        return false;
    }

    @Override
    public boolean registerCommand(@NotNull Object command, @NotNull List<String> aliases) {
        if (command instanceof Command) {
            if (aliases.size() > 0) {
                ((Command) command).setAliases(aliases);
            }
            return COMMAND_MAP != null && COMMAND_MAP.register(id(), (Command) command);
        }
        return false;
    }

    @Override
    public String name() {
        return getName();
    }

    @Override
    public String bStatsId() {
        return id().equals(Violet.PLUGIN_ID) ? "2429" : "";
    }

    @Override
    public void runTask(@NotNull Runnable task) {
        Bukkit.getScheduler().runTask(this, task);
    }

    @Override
    public void runTaskAsync(@NotNull Runnable task) {
        Bukkit.getScheduler().runTaskAsynchronously(this, task);
    }

    @Override
    public void runTaskLater(@NotNull Runnable task, long delay) {
        Bukkit.getScheduler().runTaskLater(this, task, delay);
    }

    @Override
    public void runTaskLaterAsync(@NotNull Runnable task, long delay) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(this, task, delay);
    }

    @Override
    public void broadcastKey(@NotNull String key, Object... args) {
        Bukkit.broadcastMessage(core.trans(key, args));
    }

    @Override
    public void console(@NotNull String message) {
        Bukkit.getConsoleSender().sendMessage(core.getChatHead() + message);
    }

    @Override
    public void consoleKey(String key, Object... args) {
        console(trans(key, args));
    }

    @Override
    public void log(@NotNull String text) {
        core.log(text);
    }

    @Override
    public void logKey(@NotNull String key, Object... args) {
        log(trans(key, args));
    }

    @Override
    public void consoleLog(@NotNull String text) {
        console(text);
        log(text);
    }

    @Override
    public void consoleLogKey(@NotNull String key, Object... args) {
        consoleLog(trans(key, args));
    }

    @Override
    public void broadcast(@NotNull String message) {
        Bukkit.broadcastMessage(core.getChatHead() + message);
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
        String text = core.getChatHead() + message;
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.isOp()) {
                player.sendMessage(text);
            }
        }
    }

    @Override
    public void notifyOpsKey(@NotNull String key, Object... args) {
        String text = core.getChatHead() + core.trans(key, args);
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.isOp()) {
                player.sendMessage(text);
            }
        }
    }

    @Override
    public void notifyOps(@NotNull ChatType type, @NotNull String message) {
        String text = core.getChatHead() + message;
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.isOp()) {
                Wrapper.wrapper(player).sendMessage(type, text);
            }
        }
    }

    @Override
    public void notifyOpsKey(@NotNull ChatType type, @NotNull String key, Object... args) {
        String text = core.getChatHead() + core.trans(key, args);
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.isOp()) {
                Wrapper.wrapper(player).sendMessage(type, text);
            }
        }
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
}
