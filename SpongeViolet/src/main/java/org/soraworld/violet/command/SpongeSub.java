package org.soraworld.violet.command;

import org.soraworld.violet.api.ISender;

/**
 * Spigot 命令执行器 接口.
 */
public interface SpongeSub extends SubExecutor<SpongeCommand, ISender> {
    /**
     * 执行.
     *
     * @param cmd    封装命令
     * @param sender 命令执行者
     * @param args   参数
     */
    void execute(SpongeCommand cmd, ISender sender, Args args);
}
