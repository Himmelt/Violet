package org.soraworld.violet.command;

import org.bukkit.command.CommandSender;

/**
 * The interface Sub executor.
 *
 * @param <S> the type parameter
 * @author Himmelt
 */
public interface SubExecutor<S extends CommandSender> {
    /**
     * Execute.
     *
     * @param cmd    the cmd
     * @param sender the sender
     * @param args   the args
     */
    void execute(VCommand cmd, S sender, Args args);
}
