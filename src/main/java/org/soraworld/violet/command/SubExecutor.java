package org.soraworld.violet.command;

public interface SubExecutor<C, M, S> {
    void execute(C cmd, M manager, S sender, Args args);
}
