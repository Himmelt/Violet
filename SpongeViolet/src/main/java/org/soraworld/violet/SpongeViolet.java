package org.soraworld.violet;

import org.soraworld.violet.command.SpongeBaseSubs;
import org.soraworld.violet.command.SpongeCommand;
import org.soraworld.violet.manager.SpongeManager;
import org.soraworld.violet.plugin.SpongePlugin;
import org.spongepowered.api.plugin.Plugin;

import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

/**
 * SpongeViolet 插件.
 */
@Plugin(
        id = Violet.PLUGIN_ID,
        name = Violet.PLUGIN_NAME,
        version = Violet.PLUGIN_VERSION,
        authors = {"Himmelt"},
        url = "https://github.com/Himmelt/Violet",
        description = "Violet Plugin Library."
)
public class SpongeViolet extends SpongePlugin {

    private static SpongeViolet instance;

    {
        instance = this;
    }

    public SpongeManager registerManager(Path path) {
        return new SpongeManager.Manager(this, path);
    }

    public List<Object> registerListeners() {
        return null;
    }

    public void registerCommands() {
        SpongeCommand command = new SpongeCommand(getId(), null, false, manager);
        command.extractSub(SpongeBaseSubs.class);
        command.extractSub(SpongeBaseSubs.VioletBaseSubs.class);
        command.setUsage("/violet lang|debug|save|reload|rextract");
        register(this, command);
    }

    /**
     * 获取 Violet 插件运行 uuid
     *
     * @return the uuid
     */
    public UUID getUUID() {
        return ((SpongeManager.Manager) manager).getUUID();
    }

    /**
     * 获取 紫罗兰(id:violet) 插件.
     *
     * @return 紫罗兰插件本体
     */
    public static SpongeViolet getViolet() {
        return instance;
    }
}
