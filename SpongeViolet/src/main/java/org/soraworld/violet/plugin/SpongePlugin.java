package org.soraworld.violet.plugin;

import org.soraworld.hocon.node.Paths;
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

import javax.inject.Inject;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;

/**
 * Sponge 插件.
 */
public abstract class SpongePlugin implements IPlugin {

    /**
     * 主管理器.
     */
    protected SpongeManager manager;
    /**
     * 插件命令.
     */
    protected HashSet<SpongeCommand> commands = new HashSet<>();

    @Inject
    @ConfigDir(sharedRoot = false)
    protected Path path;

    @Inject
    protected PluginContainer container;

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
        disableCommands();
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
            if (manager.canSaveOnDisable()) {
                manager.consoleKey(manager.save() ? "configSaved" : "configSaveFailed");
            }
        }
    }

    /**
     * 注册 Sponge 管理器.
     *
     * @param path 配置文件路径
     * @return 管理器
     */
    protected abstract SpongeManager registerManager(Path path);

    /**
     * 注册监听器.
     *
     * @return 监听器列表
     */
    protected abstract List<Object> registerListeners();

    /**
     * 注册 Sponge 命令.<br>
     * 使用 {@link SpongePlugin#register} 注册<br>
     * 默认注册了6个子命令 save|reload|lang|debug|help|rextract<br>
     * 可以通过 override 此方法修改注册的内容.<br>
     * 建议保留这6个基础子命令 !!<br>
     * 特别建议:<br>
     * 重写此方法时要参考此模板的写法, 由于综合限制, 暂时没想到自动化解决方案.
     */
    protected void registerCommands() {
        SpongeCommand command = new SpongeCommand(getId(), manager.defAdminPerm(), false, manager);
        command.extractSub(SpongeBaseSubs.class);
        register(this, command);
    }

    public String getId() {
        return container.getId().toLowerCase().replace(' ', '_');
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

    private void disableCommands() {
        for (SpongeCommand command : commands) {
            for (String name : manager.getDisableCmds(command.name)) {
                command.removeSub(new Paths(name));
            }
        }
    }

    /**
     * 向服务器注册命令.
     *
     * @param plugin  命令注册到的插件
     * @param command 命令
     */
    public void register(SpongePlugin plugin, SpongeCommand command) {
        Sponge.getCommandManager().register(plugin, command, command.getAliases());
        commands.add(command);
    }
}
