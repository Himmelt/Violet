package org.soraworld.violet.text;

public class ClickText {

    private String value;
    private Action action;

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
    }
}
