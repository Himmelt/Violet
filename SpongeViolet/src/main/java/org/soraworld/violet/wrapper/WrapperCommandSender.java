package org.soraworld.violet.wrapper;

import org.soraworld.violet.api.ICommandSender;
import org.spongepowered.api.command.CommandSource;

/**
 * @author Himmelt
 */
public class WrapperCommandSender<T extends CommandSource> implements ICommandSender {
    private final T source;

    public WrapperCommandSender(T source) {
        this.source = source;
    }
}
