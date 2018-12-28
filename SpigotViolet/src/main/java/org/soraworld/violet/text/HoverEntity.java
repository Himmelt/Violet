package org.soraworld.violet.text;

import org.bukkit.entity.Entity;

public class HoverEntity extends HoverText {
    public HoverEntity(Entity entity) {
        super(toJson(entity), Action.SHOW_ENTITY);
    }

    public static String toJson(Entity entity) {
        return "";
    }
}
