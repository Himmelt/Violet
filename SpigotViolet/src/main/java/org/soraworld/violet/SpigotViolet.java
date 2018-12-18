package org.soraworld.violet;

import org.bukkit.event.Listener;
import org.soraworld.violet.command.SpigotBaseSubs;
import org.soraworld.violet.command.SpigotCommand;
import org.soraworld.violet.manager.FBManager;
import org.soraworld.violet.plugin.SpigotPlugin;

import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

/**
 * SpigotViolet 插件.
 */
public class SpigotViolet extends SpigotPlugin<FBManager> {

    private static SpigotViolet instance;

    {
        instance = this;
    }

    public FBManager registerManager(Path path) {
        return new FBManager(this, path);
    }

    public List<Listener> registerListeners() {
        return null;
    }

    public void registerCommands() {
        SpigotCommand command = new SpigotCommand(getId(), null, false, manager);
        command.extractSub(SpigotBaseSubs.class);
        //command.extractSub(SpigotBaseSubs.VioletBaseSubs.class);
        command.setUsage("/violet lang|debug|save|reload|rextract");
        register(this, command);
    }

    public FBManager getManager() {
        return manager;
    }

    /**
     * 获取 Violet 插件运行 uuid
     *
     * @return the uuid
     */
    public UUID getUUID() {
        return manager.getUUID();
    }

    /**
     * 获取 紫罗兰(id:violet) 插件.
     *
     * @return 紫罗兰插件本体
     */
    public static SpigotViolet getViolet() {
        return instance;
    }
}
