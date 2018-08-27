package org.soraworld.violet.serializers;

import org.soraworld.hocon.node.Node;
import org.soraworld.hocon.node.NodeBase;
import org.soraworld.hocon.node.Options;
import org.soraworld.hocon.serializer.TypeSerializer;

import javax.annotation.Nonnull;
import java.lang.reflect.Type;
import java.util.UUID;

/**
 * UUID 序列化器.
 */
public class UUIDSerializer implements TypeSerializer<UUID> {
    public UUID deserialize(@Nonnull Type type, @Nonnull Node node) {
        if (node instanceof NodeBase) {
            try {
                return UUID.fromString(((NodeBase) node).getString());
            } catch (Throwable ignored) {
            }
        }
        return null;
    }

    public Node serialize(@Nonnull Type type, UUID value, @Nonnull Options options) {
        return new NodeBase(options, value, false);
    }

    @Nonnull
    public Type getRegType() {
        return UUID.class;
    }
}
