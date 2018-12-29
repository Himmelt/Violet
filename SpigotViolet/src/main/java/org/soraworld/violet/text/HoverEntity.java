package org.soraworld.violet.text;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

/**
 * since 1.8+
 * The type Hover entity.
 */
public class HoverEntity extends HoverText {

    private static final String entityJson = "{type:\\\"$type$\\\",id:\\\"7ba41798-f5e1-42e8-b090-b2caf6e04d7c\\\"}";//name:"$name$",
    //private static final String ENTITY_UUID = "7ba41798-f5e1-42e8-b090-b2caf6e04d7c";

    public HoverEntity(Entity entity) {
        super(toJson(entity), Action.SHOW_ENTITY);
    }

    public static String toJson(Entity entity) {
        if (entity == null) return "";
        EntityType type = entity.getType();
        return entityJson.replace("$type$", type.getName());
        //.replace("$name$", entity.getName())
        //.replace("$uuid$", ENTITY_UUID);
    }
}
