package org.soraworld.violet.command;

import org.bukkit.command.CommandSender;

public interface Executor {
    void execute(CommandSender sender, CommandArgs args);
}
