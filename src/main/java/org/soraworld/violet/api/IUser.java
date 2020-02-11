package org.soraworld.violet.api;

import java.util.UUID;

/**
 * @author Himmelt
 */
public interface IUser {
    UUID uuid();

    String getName();

    Object getHandle();

    <C> C getHandle(Class<C> clazz);
}
