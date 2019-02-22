package org.soraworld.violet.command;

import java.util.List;

public interface TabExecutor<C extends ICommand, S> {
    List<String> complete(C cmd, S sender, Args args);
}
