package org.soraworld.violet.text;

import org.bukkit.inventory.ItemStack;

public class HoverItem extends HoverText {
    public HoverItem(ItemStack stack) {
        super(toJson(stack), Action.SHOW_ITEM);
    }

    public static String toJson(ItemStack stack) {
        return "";
    }
}
