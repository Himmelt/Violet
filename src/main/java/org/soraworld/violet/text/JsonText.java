package org.soraworld.violet.text;

public class JsonText {

    private String text;
    private ClickText click;
    private HoverText hover;

    public JsonText(String text) {
        this.text = text;
    }

    public JsonText(String text, ClickText click) {
        this.text = text;
        this.click = click;
    }

    public JsonText(String text, HoverText hover) {
        this.text = text;
        this.hover = hover;
    }

    public JsonText(String text, ClickText click, HoverText hover) {
        this.text = text;
        this.click = click;
        this.hover = hover;
    }
}
