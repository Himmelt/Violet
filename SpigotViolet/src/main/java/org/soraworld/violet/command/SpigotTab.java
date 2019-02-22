package org.soraworld.violet.command;

import org.soraworld.violet.api.ISender;

import java.util.List;

public interface SpigotTab extends TabExecutor<ICommand, ISender> {
    List<String> complete(ICommand cmd, ISender sender, Args args);
}
