package org.soraworld.violet.command;

import org.bukkit.command.CommandSender;
import org.soraworld.violet.manager.SpigotManager;

public interface SpigotExecutor {
    void execute(SpigotManager manager, CommandSender sender, CommandArgs args);
}
