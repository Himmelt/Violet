package org.soraworld.violet.plugin;

import org.jetbrains.annotations.NotNull;
import org.soraworld.violet.Violet;
import org.soraworld.violet.api.IPlugin;
import org.soraworld.violet.command.CommandCore;
import org.soraworld.violet.command.SpongeCommand;
import org.soraworld.violet.core.PluginCore;
import org.soraworld.violet.text.ChatType;
import org.soraworld.violet.util.FileUtils;
import org.soraworld.violet.util.Helper;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.chat.ChatTypes;

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
        if (!Violet.VIOLET_VERSION.match(violetVersion())) {
            throw new RuntimeException("Plugin " + id() + " incompatible with server violet@" + Violet.VIOLET_VERSION + " , need violet@" + violetVersion());
        }
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
        return container.getVersion().orElse("0.0.0");
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

    public boolean registerCommand(@NotNull CommandCallable command, String... aliases) {
        return Sponge.getCommandManager().register(this, command, aliases).isPresent();
    }

    public boolean registerCommand(@NotNull CommandCallable command, @NotNull List<String> aliases) {
        return Sponge.getCommandManager().register(this, command, aliases).isPresent();
    }

    @Override
    public void console(@NotNull String message) {
        Sponge.getServer().getConsole().sendMessage(Text.of(core.getChatHead() + message));
    }

    @Override
    public void broadcast(@NotNull String message) {
        Sponge.getServer().getBroadcastChannel().send(Text.of(core.getChatHead() + message));
    }

    @Override
    public void notifyOps(@NotNull String message) {
        Text text = Text.of(core.getChatHead() + message);
        for (Player player : Sponge.getServer().getOnlinePlayers()) {
            if (Helper.isOp(player)) {
                player.sendMessage(text);
            }
        }
    }

    @Override
    public void notifyOps(@NotNull ChatType type, @NotNull String message) {
        org.spongepowered.api.text.chat.ChatType chatType = type == ChatType.ACTION_BAR ? ChatTypes.ACTION_BAR : type == ChatType.SYSTEM ? ChatTypes.SYSTEM : ChatTypes.CHAT;
        Text text = chatType == ChatTypes.ACTION_BAR ? Text.of(message) : Text.of(core.getChatHead() + message);
        for (Player player : Sponge.getServer().getOnlinePlayers()) {
            if (Helper.isOp(player)) {
                player.sendMessage(chatType, text);
            }
        }
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

    public void sendChat(@NotNull CommandSource sender, @NotNull String message) {
        sender.sendMessage(Text.of(message));
    }

    public void sendChatKey(@NotNull CommandSource sender, @NotNull String key, Object... args) {
        sender.sendMessage(Text.of(core.trans(key, args)));
    }

    public void sendMessage(@NotNull CommandSource sender, @NotNull String message) {
        sender.sendMessage(Text.of(core.getChatHead() + message));
    }

    public void sendMessageKey(@NotNull CommandSource sender, @NotNull String key, Object... args) {
        sender.sendMessage(Text.of(core.getChatHead() + core.trans(key, args)));
    }

    public void sendChat(@NotNull Player player, @NotNull ChatType type, @NotNull String message) {
        Helper.sendChatType(player, type, message);
    }

    public void sendChatKey(@NotNull Player player, @NotNull ChatType type, @NotNull String key, Object... args) {
        Helper.sendChatType(player, type, core.trans(key, args));
    }

    public void sendMessage(@NotNull Player player, @NotNull ChatType type, String message) {
        Helper.sendChatType(player, type, core.getChatHead() + message);
    }

    public void sendMessageKey(@NotNull Player player, @NotNull ChatType type, @NotNull String key, Object... args) {
        Helper.sendChatType(player, type, core.getChatHead() + core.trans(key, args));
    }
}
