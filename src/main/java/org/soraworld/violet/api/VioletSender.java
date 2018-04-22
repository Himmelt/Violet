package org.soraworld.violet.api;

public interface VioletSender {

    String getName();

    boolean hasPermission(String perm);

    void sendMessage(String msg);

}