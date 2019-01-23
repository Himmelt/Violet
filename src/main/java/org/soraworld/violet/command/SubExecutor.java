package org.soraworld.violet.command;

import org.soraworld.violet.api.ICommand;
import org.soraworld.violet.api.ISender;

public interface SubExecutor<C extends ICommand, S extends ISender> {
    void execute(C cmd, S sender, Args args);
}
