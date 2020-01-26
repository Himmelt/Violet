package org.soraworld.violet.command;

import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * The interface Tab executor.
 *
 * @param <S> the type parameter
 * @author Himmelt
 */
public interface TabExecutor<S extends CommandSender> {
    /**
     * Complete list.
     *
     * @param cmd    the cmd
     * @param sender the sender
     * @param args   the args
     * @return the list
     */
    List<String> complete(VCommand cmd, S sender, Args args);
}
