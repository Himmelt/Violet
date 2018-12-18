package org.soraworld.violet.command;

import org.soraworld.violet.manager.SpongeManager;
import org.spongepowered.api.command.CommandSource;

/**
 * Sponge 命令执行器 接口.
 */
public interface SpongeExecutor<T extends SpongeManager> extends SubExecutor<SpongeCommand, T, CommandSource> {
    void execute(SpongeCommand cmd, T manager, CommandSource sender, Args args);
}
