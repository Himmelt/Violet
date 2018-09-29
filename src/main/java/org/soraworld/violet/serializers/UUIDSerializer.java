package org.soraworld.violet.serializers;

import org.soraworld.hocon.node.Node;
import org.soraworld.hocon.node.NodeBase;
import org.soraworld.hocon.node.Options;
import org.soraworld.hocon.serializer.TypeSerializer;

import java.lang.reflect.Type;
import java.util.UUID;

/**
 * UUID 序列化器.
 */
public class UUIDSerializer implements TypeSerializer<UUID> {
    public UUID deserialize(Type type, Node node) {
        if (node instanceof NodeBase) {
            try {
                return UUID.fromString(((NodeBase) node).getString());
            } catch (Throwable ignored) {
            }
        }
        return null;
    }

    public Node serialize(Type type, UUID value, Options options) {
        return new NodeBase(options, value, false);
    }

    public Type getRegType() {
        return UUID.class;
    }
}
