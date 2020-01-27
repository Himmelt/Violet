package org.soraworld.violet.command;

import org.soraworld.violet.api.ICommandSender;

import java.util.List;

/**
 * The interface Tab executor.
 *
 * @param <S> the type parameter
 * @author Himmelt
 */
public interface TabExecutor<S extends ICommandSender> {
    /**
     * Complete list.
     *
     * @param cmd    the cmd
     * @param sender the sender
     * @param args   the args
     * @return the list
     */
    List<String> complete(CommandCore cmd, S sender, Args args);
}
