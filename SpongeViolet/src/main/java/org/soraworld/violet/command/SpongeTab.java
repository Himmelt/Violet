package org.soraworld.violet.command;

import org.soraworld.violet.api.ISender;

import java.util.List;

public interface SpongeTab extends TabExecutor<VioletCommand, ISender> {
    List<String> complete(VioletCommand cmd, ISender sender, Args args);
}
