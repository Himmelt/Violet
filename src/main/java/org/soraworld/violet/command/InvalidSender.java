package org.soraworld.violet.command;

import org.soraworld.violet.api.command.ICommandSender;

public final class InvalidSender implements ICommandSender {
    public String getName() {
        return "invalid";
    }

    public boolean hasPermission(String perm) {
        return false;
    }

    public void sendMessage(String msg) {
        System.out.println("Invalid sender send: " + msg);
    }

}
