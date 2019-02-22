package org.soraworld.violet.command;

public interface SubExecutor<C, S> {
    void execute(C cmd, S sender, Args args);
}
