package org.soraworld.violet.command;

import org.soraworld.violet.api.ICommandSender;

/**
 * The interface Sub executor.
 *
 * @param <S> the type parameter
 * @author Himmelt
 */
public interface SubExecutor<S extends ICommandSender> {
    /**
     * Execute.
     *
     * @param cmd    the cmd
     * @param sender the sender
     * @param args   the args
     */
    void execute(CommandCore cmd, S sender, Args args);
}
