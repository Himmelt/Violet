package org.soraworld.violet.serializers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.soraworld.hocon.node.Node;
import org.soraworld.hocon.node.NodeBase;
import org.soraworld.hocon.node.NodeMap;
import org.soraworld.hocon.node.Options;
import org.soraworld.hocon.serializer.TypeSerializer;

import javax.annotation.Nonnull;
import java.lang.reflect.Type;
import java.util.HashMap;

import static org.bukkit.util.NumberConversions.toDouble;
import static org.bukkit.util.NumberConversions.toFloat;

public class LocationSerializer implements TypeSerializer<Location> {
    @Override
    public Location deserialize(@Nonnull Type type, @Nonnull Node node) {
        if (node instanceof NodeMap) {
            HashMap<String, String> map = ((NodeMap) node).asStringMap();
            String name = map.get("world");
            if (name != null && !name.isEmpty()) {
                World world = Bukkit.getWorld(name);
                if (world != null) {
                    return new Location(world, toDouble(map.get("x")), toDouble(map.get("y")), toDouble(map.get("z")), toFloat(map.get("yaw")), toFloat(map.get("pitch")));
                }
            }
        }
        return null;
    }

    @Override
    public Node serialize(@Nonnull Type type, Location loc, @Nonnull Options options) {
        if (loc != null) {
            NodeMap map = new NodeMap(options);
            World world = loc.getWorld();
            map.setNode("world", world == null ? null : world.getName());
            map.setNode("x", loc.getX());
            map.setNode("y", loc.getY());
            map.setNode("z", loc.getZ());
            map.setNode("yaw", loc.getYaw());
            map.setNode("pitch", loc.getPitch());
            return map;
        } else return new NodeBase(options, null, false);
    }

    @Nonnull
    public Type getRegType() {
        return Location.class;
    }
}
