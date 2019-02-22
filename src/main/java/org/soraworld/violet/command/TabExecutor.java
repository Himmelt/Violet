package org.soraworld.violet.command;

import java.util.List;

public interface TabExecutor<C, S> {
    List<String> complete(C cmd, S sender, Args args);
}
