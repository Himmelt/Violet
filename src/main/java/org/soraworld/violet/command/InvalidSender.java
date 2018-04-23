package org.soraworld.violet.command;

import org.soraworld.rikka.command.CommandSource;
import org.soraworld.rikka.text.Text;
import org.soraworld.rikka.text.channel.MessageChannel;

public final class InvalidSender implements CommandSource {

    public String getName() {
        return "invalid";
    }

    public boolean hasPermission(String perm) {
        return false;
    }

    public void sendMessage(String msg) {
        System.out.println("Invalid sender send: " + msg);
    }

    public void sendMessage(Text message) {

    }

    public MessageChannel getMessageChannel() {
        return null;
    }

    public void setMessageChannel(MessageChannel channel) {

    }
}
