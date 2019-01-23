package org.soraworld.violet.command;

import org.soraworld.violet.api.ISender;

import java.util.List;

public interface SpigotTab extends TabExecutor<SpigotCommand, ISender> {
    List<String> complete(SpigotCommand cmd, ISender sender, Args args);
}
