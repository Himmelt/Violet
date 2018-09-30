package org.soraworld.violet;

import org.bukkit.event.Listener;
import org.soraworld.hocon.node.Paths;
import org.soraworld.violet.command.SpigotBaseSubs;
import org.soraworld.violet.command.SpigotCommand;
import org.soraworld.violet.manager.SpigotManager;
import org.soraworld.violet.plugin.SpigotPlugin;

import java.nio.file.Path;
import java.util.List;

/**
 * SpigotViolet 插件.
 */
public class SpigotViolet extends SpigotPlugin {

    public SpigotManager registerManager(Path path) {
        return new SpigotManager.Manager(this, path);
    }

    public List<Listener> registerListeners() {
        return null;
    }

    public void registerCommands() {
        SpigotCommand command = new SpigotCommand(getId(), manager.defAdminPerm(), false, manager);
        command.extractSub(SpigotBaseSubs.class);
        command.setUsage("/violet lang|debug|save|reload|rextract");
        manager.getDisableCmds().forEach(s -> command.removeSub(new Paths(s)));
        register(this, command);
    }

    public String assetsId() {
        return Violet.ASSETS_ID;
    }
}
