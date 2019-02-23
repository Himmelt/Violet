package org.soraworld.violet.command;

import org.bukkit.command.CommandSender;

import java.util.List;

public interface TabExecutor<S extends CommandSender> {
    List<String> complete(VCommand cmd, S sender, Args args);
}
