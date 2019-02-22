package org.soraworld.violet.command;

public interface SubExecutor<C extends ICommand, S> {
    void execute(C cmd, S sender, Args args);
}
