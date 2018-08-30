package org.soraworld.violet.command;

import org.spongepowered.api.command.CommandSource;

/**
 * Sponge 命令执行器 接口.
 */
public interface SpongeExecutor {
    /**
     * 执行.
     *
     * @param self   封装命令
     * @param sender 命令执行者
     * @param args   参数
     */
    void execute(SpongeCommand self, CommandSource sender, Paths args);
}
