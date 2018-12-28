package org.soraworld.violet.text;

public class HoverText {

    private final String value;
    private final Action action;

    public HoverText(String value) {
        this.value = value == null ? "" : value;
        this.action = Action.SHOW_TEXT;
    }

    public HoverText(String value, Action action) {
        this.value = value == null ? "" : value;
        this.action = action == null ? Action.SHOW_TEXT : action;
    }

    public String toString() {
        return "\"hoverEvent\":{"
                + "\"action\":\"" + action + "\","
                + "\"value\":{\"text\":\"" + value + "\"}"
                + "}";
    }

    public enum Action {
        SHOW_TEXT("show_text"),
        SHOW_ITEM("show_item"),
        SHOW_ENTITY("show_entity"),
        SHOW_ACHIEVEMENT("show_achievement");

        private final String name;

        Action(String name) {
            this.name = name;
        }

        public String toString() {
            return name;
        }
    }
}
