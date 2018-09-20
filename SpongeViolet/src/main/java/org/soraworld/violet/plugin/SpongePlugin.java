package org.soraworld.violet.plugin;

import org.soraworld.violet.api.IPlugin;
import org.soraworld.violet.command.SpongeBaseSubs;
import org.soraworld.violet.command.SpongeCommand;
import org.soraworld.violet.manager.SpongeManager;
import org.spongepowered.api.Sponge;
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

    /**
     * 游戏预初始化.
     *
     * @param event 事件
     */
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
     * 游戏初始化.
     *
     * @param event 事件
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

    /**
     * 服务器启动中事件.
     *
     * @param event 事件
     */
    @Listener
    public void onStarting(GameStartingServerEvent event) {
        registerCommands();
        manager.consoleKey("pluginEnabled", getId());
        afterEnable();
    }

    /**
     * 服务器停止中事件.
     *
     * @param event 事件
     */
    @Listener
    public void onDisable(GameStoppingServerEvent event) {
        beforeDisable();
        if (manager != null) {
            manager.consoleKey("pluginDisabled", getId());
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
     * 使用 {@link SpongePlugin#register} 注册
     * 默认注册了4个子命令 save|reload|lang|debug ，可以通过 override 此方法修改注册的内容。
     * 建议保留这4个基础子命令 !!
     */
    protected void registerCommands() {
        SpongeCommand command = new SpongeCommand(getId(), manager.defAdminPerm(), false, manager);
        command.extractSub(SpongeBaseSubs.class, "lang");
        command.extractSub(SpongeBaseSubs.class, "debug");
        command.extractSub(SpongeBaseSubs.class, "save");
        command.extractSub(SpongeBaseSubs.class, "reload");
        command.extractSub(SpongeBaseSubs.class, "help");
        command.extractSub(SpongeBaseSubs.class, "rextract");
        register(this, command);
    }

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

    /**
     * 向服务器注册命令.
     *
     * @param plugin  命令注册到的插件
     * @param command 命令
     */
    public static void register(SpongePlugin plugin, SpongeCommand command) {
        Sponge.getCommandManager().register(plugin, command, command.getAliases());
    }
}
