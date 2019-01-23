package org.soraworld.violet.command;

import org.soraworld.violet.api.ICommand;
import org.soraworld.violet.api.ISender;

import java.util.List;

public interface TabExecutor<C extends ICommand, S extends ISender> {
    List<String> complete(C cmd, S sender, Args args);
}
