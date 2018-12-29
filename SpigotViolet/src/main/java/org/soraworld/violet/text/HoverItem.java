package org.soraworld.violet.text;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class HoverItem extends HoverText {

    private static final String itemJson = "{id:$id$,Damage:$damage$,Count:$count$}";

    public HoverItem(ItemStack stack) {
        super(toJson(stack), Action.SHOW_ITEM);
    }

    public static String toJson(ItemStack stack) {
        if (stack == null) return "";
        Material item = stack.getType();
        return itemJson
                .replace("$id$", String.valueOf(item.getId()))
                .replace("$damage$", String.valueOf(stack.getDurability()))
                .replace("$count$", String.valueOf(stack.getAmount()));
    }
}
