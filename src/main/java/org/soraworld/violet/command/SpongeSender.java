package org.soraworld.violet.command;

import org.soraworld.violet.api.command.ICommandSender;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;

public class SpongeSender<T extends CommandSource> implements ICommandSender {

    protected final T sponge;

    public SpongeSender(T source) {
        sponge = source;
    }

    public String getName() {
        return sponge.getName();
    }

    public boolean hasPermission(String perm) {
        return sponge.hasPermission(perm);
    }

    public void sendMessage(String msg) {
        sponge.sendMessage(Text.of(msg));
    }

}
