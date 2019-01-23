package org.soraworld.violet.command;

import org.soraworld.violet.Violet;
import org.soraworld.violet.inject.Inject;
import org.soraworld.violet.manager.FSManager;

public final class SpongeBaseSubs {

    @Inject
    private FSManager manager;

    @Sub(parent = Violet.PLUGIN_ID, perm = "admin")
    public final SpongeSub plugins = (cmd, sender, args) -> {
        manager.listPlugins(sender);
    };
}
