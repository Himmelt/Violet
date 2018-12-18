package org.soraworld.violet.command;

import org.bukkit.command.CommandSender;
import org.soraworld.violet.manager.SpigotManager;

/**
 * Spigot 命令执行器 接口.
 */
public interface SpigotExecutor<T extends SpigotManager> extends SubExecutor<SpigotCommand, T, CommandSender> {
    /**
     * 执行.
     *
     * @param cmd    封装命令
     * @param sender 命令执行者
     * @param args   参数
     */
    void execute(SpigotCommand cmd, T manager, CommandSender sender, Args args);
}
