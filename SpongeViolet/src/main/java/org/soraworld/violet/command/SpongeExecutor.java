package org.soraworld.violet.command;

import org.soraworld.violet.manager.SpongeManager;
import org.spongepowered.api.command.CommandSource;

/**
 * Sponge 命令执行器 接口.
 */
public interface SpongeExecutor {
    /**
     * 执行.
     *
     * @param manager 管理器
     * @param sender  命令执行者
     * @param args    参数
     */
    void execute(SpongeManager manager, CommandSource sender, CommandArgs args);
}
