package org.soraworld.violet.command;

import org.spongepowered.api.command.CommandSource;

import java.util.List;

public interface TabExecutor<S extends CommandSource> {
    List<String> complete(VCommand cmd, S sender, Args args);
}
