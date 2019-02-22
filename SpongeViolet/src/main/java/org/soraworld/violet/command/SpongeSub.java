package org.soraworld.violet.command;

import org.soraworld.violet.api.ISender;

/**
 * Spigot 命令执行器 接口.
 */
public interface SpongeSub extends SubExecutor<VioletCommand, ISender> {
    /**
     * 执行.
     *
     * @param cmd    封装命令
     * @param sender 命令执行者
     * @param args   参数
     */
    void execute(VioletCommand cmd, ISender sender, Args args);
}
