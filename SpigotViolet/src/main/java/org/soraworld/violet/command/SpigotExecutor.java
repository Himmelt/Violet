package org.soraworld.violet.command;

import org.bukkit.command.CommandSender;
import org.soraworld.violet.manager.SpigotManager;

/**
 * Spigot 命令执行器 接口.
 */
public interface SpigotExecutor {
    /**
     * 执行.
     *
     * @param manager 管理器
     * @param sender  命令执行者
     * @param args    参数
     */
    void execute(SpigotManager manager, CommandSender sender, Paths args);
}
