package org.soraworld.violet.command;

import org.soraworld.violet.api.VioletSender;

public final class InvalidSender implements VioletSender {
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
