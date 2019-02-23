package org.soraworld.violet.command;

import org.spongepowered.api.command.CommandSource;

public interface SubExecutor<S extends CommandSource> {
    void execute(VCommand cmd, S sender, Args args);
}
