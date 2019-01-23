package org.soraworld.violet.api;

import org.jetbrains.annotations.Nullable;

public interface ISender<T> {
    boolean hasPermission(@Nullable String permission);

    T getSender();
}
