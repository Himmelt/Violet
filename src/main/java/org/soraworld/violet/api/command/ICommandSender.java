package org.soraworld.violet.api.command;

public interface ICommandSender {

    String getName();

    boolean hasPermission(String perm);

    void sendMessage(String msg);

}