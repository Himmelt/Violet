package org.soraworld.violet.serializers;

import org.jetbrains.annotations.NotNull;
import org.soraworld.hocon.exception.HoconException;
import org.soraworld.hocon.exception.SerializerException;
import org.soraworld.hocon.node.NodeBase;
import org.soraworld.hocon.node.Options;
import org.soraworld.hocon.serializer.TypeSerializer;

import java.lang.reflect.Type;
import java.util.UUID;

/**
 * UUID 序列化器.
 *
 * @author Himmelt
 */
public class UUIDSerializer extends TypeSerializer<UUID, NodeBase> {

    public UUIDSerializer() throws SerializerException {
    }

    @Override
    public @NotNull UUID deserialize(@NotNull Type fieldType, @NotNull NodeBase node) throws HoconException {
        try {
            return UUID.fromString(node.getString());
        } catch (Throwable e) {
            throw new SerializerException(e);
        }
    }


    @Override
    public @NotNull NodeBase serialize(@NotNull Type fieldType, @NotNull UUID value, @NotNull Options options) {
        return new NodeBase(options, value);
    }
}
