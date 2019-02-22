package org.soraworld.violet.command;

import org.soraworld.violet.api.ISender;

import java.util.List;

public interface SpongeTab extends TabExecutor<CommandAdaptor, ISender> {
    List<String> complete(CommandAdaptor cmd, ISender sender, Args args);
}
