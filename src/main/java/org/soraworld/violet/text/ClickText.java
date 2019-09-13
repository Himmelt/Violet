package org.soraworld.violet.text;

/**
 * The type Click text.
 *
 * @author Himmelt
 */
public class ClickText {

    private final String value;
    private final Action action;

    /**
     * Instantiates a new Click text.
     *
     * @param value  the value
     * @param action the action
     */
    public ClickText(String value, Action action) {
        this.value = value;
        this.action = action;
    }

    @Override
    public String toString() {
        return "\"clickEvent\":{"
                + "\"action\":\"" + action + "\","
                + "\"value\":\"" + value + "\""
                + "}";
    }

    /**
     * The enum Action.
     */
    public enum Action {
        /**
         * Open url action.
         */
        OPEN_URL("open_url"),
        /**
         * Open file action.
         */
        OPEN_FILE("open_file"),
        /**
         * Run command action.
         */
        RUN_COMMAND("run_command"),
        /**
         * Suggest command action.
         */
        SUGGEST_COMMAND("suggest_command"),
        /**
         * Change page action.
         */
        CHANGE_PAGE("change_page");

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
