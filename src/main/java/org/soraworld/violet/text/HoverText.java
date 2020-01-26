package org.soraworld.violet.text;

/**
 * The type Hover text.
 *
 * @author Himmelt
 */
public class HoverText {

    /**
     * The Value.
     */
    protected final String value;
    /**
     * The Action.
     */
    protected final Action action;

    /**
     * Instantiates a new Hover text.
     *
     * @param value the value
     */
    public HoverText(String value) {
        this.value = value == null ? "" : value;
        this.action = Action.SHOW_TEXT;
    }

    /**
     * Instantiates a new Hover text.
     *
     * @param value  the value
     * @param action the action
     */
    public HoverText(String value, Action action) {
        this.value = value == null ? "" : value;
        this.action = action == null ? Action.SHOW_TEXT : action;
    }

    @Override
    public String toString() {
        return "\"hoverEvent\":{"
                + "\"action\":\"" + action + "\","
                + "\"value\":\"" + value
                + "\"}";
    }

    /**
     * The enum Action.
     */
    public enum Action {
        /**
         * Show text action.
         */
        SHOW_TEXT("show_text"),
        /**
         * Show item action.
         */
        SHOW_ITEM("show_item"),
        /**
         * Show entity action.
         */
        SHOW_ENTITY("show_entity"),
        /**
         * Show achievement action.
         */
        SHOW_ACHIEVEMENT("show_achievement");

        private final String name;

        Action(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
