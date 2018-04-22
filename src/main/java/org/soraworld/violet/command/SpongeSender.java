package org.soraworld.violet.command;

import org.soraworld.violet.api.VioletSender;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;

public class SpongeSender implements VioletSender {

    private CommandSource sponge = null;

    public SpongeSender(Object source) {
        try {
            if (source instanceof CommandSource) sponge = (CommandSource) source;
        } catch (Throwable ignored) {
        }
    }

    public String getName() {
        return sponge == null ? "null" : sponge.getName();
    }

    public boolean hasPermission(String perm) {
        return sponge != null && sponge.hasPermission(perm);
    }

    public void sendMessage(String msg) {
        if (sponge != null) sponge.sendMessage(Text.of(msg));
    }

}
