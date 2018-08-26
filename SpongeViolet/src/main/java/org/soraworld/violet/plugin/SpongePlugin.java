package org.soraworld.violet.plugin;

import org.soraworld.violet.Violet;
import org.soraworld.violet.api.IPlugin;
import org.soraworld.violet.manager.SpongeManager;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandManager;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartingServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.PluginContainer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Sponge 插件.
 */
public abstract class SpongePlugin implements IPlugin {

    /**
     * 主管理器.
     */
    protected SpongeManager manager;

    @Inject
    @ConfigDir(sharedRoot = false)
    private Path path;

    @Inject
    private PluginContainer container;

    @Listener
    public void onPreInit(GamePreInitializationEvent event) {
        if (path == null) path = new File("config", getId()).toPath();
        if (Files.notExists(path)) {
            try {
                Files.createDirectories(path);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        manager = registerManager(path);
    }

    /**
     * 插件启用时(游戏初始化事件监听).
     *
     * @param event {@link GameInitializationEvent}
     */
    @Listener
    public void onInit(GameInitializationEvent event) {
        manager.beforeLoad();
        manager.load();
        manager.afterLoad();
        List<Object> listeners = registerListeners();
        if (listeners != null && !listeners.isEmpty()) {
            for (Object listener : listeners) {
                Sponge.getEventManager().registerListeners(this, listener);
            }
        }
    }

    @Listener
    public void onStarting(GameStartingServerEvent event) {
        registerCommands();
        manager.consoleKey(Violet.KEY_PLUGIN_ENABLED, getId());
        afterEnable();
    }

    /**
     * 插件禁用时(服务器停止中事件监听).
     *
     * @param event {@link GameStoppingServerEvent}
     */
    @Listener
    public void onDisable(GameStoppingServerEvent event) {
        beforeDisable();
        if (manager != null) {
            manager.consoleKey(Violet.KEY_PLUGIN_DISABLED, getId());
            manager.save();
        }
    }

    /**
     * 注册 Sponge 管理器.
     *
     * @param path 配置文件路径
     * @return 管理器
     */
    @Nonnull
    protected abstract SpongeManager registerManager(Path path);

    /**
     * 注册监听器.
     *
     * @return 监听器列表
     */
    @Nullable
    protected abstract List<Object> registerListeners();

    /**
     * 注册 Sponge 命令.
     * 使用 {@link CommandManager#register} 注册.
     */
    protected abstract void registerCommands();

    @Nonnull
    public String getId() {
        return container.getId().toLowerCase().replace(' ', '_');
    }

    @Nonnull
    public String getName() {
        return container.getName();
    }

    @Nonnull
    public String getVersion() {
        return container.getVersion().orElse("x.y.z");
    }

    public boolean isEnabled() {
        return Sponge.getPluginManager().isLoaded(container.getId());
    }
}
