package org.soraworld.violet.wrapper;

import org.bukkit.command.CommandSender;
import org.soraworld.violet.api.ICommandSender;

/**
 * @author Himmelt
 */
public class WrapperCommandSender<T extends CommandSender> implements ICommandSender {
    private final T sender;

    public WrapperCommandSender(T sender) {
        this.sender = sender;
    }
}
