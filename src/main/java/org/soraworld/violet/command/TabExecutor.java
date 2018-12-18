package org.soraworld.violet.command;

import java.util.List;

public interface TabExecutor<C, M, S> {
    List<String> complete(C cmd, M manager, S sender, Args args);
}
