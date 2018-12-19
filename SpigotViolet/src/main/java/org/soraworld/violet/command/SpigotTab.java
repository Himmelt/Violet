package org.soraworld.violet.command;

import org.bukkit.command.CommandSender;
import org.soraworld.violet.manager.SpigotManager;

import java.util.List;

public interface SpigotTab<T extends SpigotManager> extends TabExecutor<SpigotCommand, T, CommandSender> {
    List<String> complete(SpigotCommand cmd, T manager, CommandSender sender, Args args);
}
