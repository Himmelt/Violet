package org.soraworld.violet.command;

import org.bukkit.command.CommandSender;

public interface SubExecutor<S extends CommandSender> {
    void execute(VCommand cmd, S sender, Args args);
}
