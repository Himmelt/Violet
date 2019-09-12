package org.soraworld.violet.text;

/**
 * @author Himmelt
 */
public class ClickText {

    private final String value;
    private final Action action;

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

    public enum Action {
        OPEN_URL("open_url"),
        OPEN_FILE("open_file"),
        RUN_COMMAND("run_command"),
        SUGGEST_COMMAND("suggest_command"),
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
