package org.soraworld.violet.command;

import org.soraworld.violet.api.ISender;

import java.util.List;

public interface SpongeTab extends TabExecutor<SpongeCommand, ISender> {
    List<String> complete(SpongeCommand cmd, ISender sender, Args args);
}
