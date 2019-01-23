package org.soraworld.violet.command;

import org.soraworld.violet.Violet;
import org.soraworld.violet.inject.Command;
import org.soraworld.violet.inject.Inject;
import org.soraworld.violet.manager.FBManager;

@Command(name = Violet.PLUGIN_ID)
public final class SpigotBaseCmds {

    @Inject
    private FBManager manager;

    @Sub(parent = Violet.PLUGIN_ID, perm = "admin")
    public final SpigotSub plugins = (cmd, sender, args) -> {
        manager.listPlugins(sender);
    };
}
