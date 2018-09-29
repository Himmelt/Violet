package org.soraworld.violet.command;

import org.bukkit.command.CommandSender;

/**
 * Spigot 命令执行器 接口.
 */
public interface SpigotExecutor {
    /**
     * 执行.
     *
     * @param self   封装命令
     * @param sender 命令执行者
     * @param args   参数
     */
    void execute(SpigotCommand self, CommandSender sender, Args args);
}
