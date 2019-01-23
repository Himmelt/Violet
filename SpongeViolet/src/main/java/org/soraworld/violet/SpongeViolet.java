package org.soraworld.violet;

import org.soraworld.violet.manager.FSManager;
import org.soraworld.violet.plugin.SpongePlugin;
import org.spongepowered.api.plugin.Plugin;

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
public class SpongeViolet extends SpongePlugin<FSManager> {

    private static SpongeViolet instance;

    {
        instance = this;
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
    public static SpongeViolet getViolet() {
        return instance;
    }
}
