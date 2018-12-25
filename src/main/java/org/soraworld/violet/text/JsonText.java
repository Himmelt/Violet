package org.soraworld.violet.text;

public class JsonText {

    private String text;
    private ClickText click;
    private HoverText hover;
    private String json;

    public JsonText(String text) {
        this.text = text;
        getJson();
    }

    public JsonText(String text, ClickText click) {
        this.text = text;
        this.click = click;
        getJson();
    }

    public JsonText(String text, HoverText hover) {
        this.text = text;
        this.hover = hover;
        getJson();
    }

    public JsonText(String text, ClickText click, HoverText hover) {
        this.text = text;
        this.click = click;
        this.hover = hover;
        getJson();
    }

    private void getJson() {
        if (json == null) {
            if (click != null || hover != null) {
                json = "{\"text\":\"" + text + "\",";
                if (click != null) {
                    json += click;
                    if (hover != null) json += ",";
                    else json += "}";
                }
                if (hover != null) json += hover + "}";
            } else json = "\"" + text + "\"";
        }
    }

    public String toString() {
        return json == null ? "" : json;
    }

    public static String toJson(JsonText... texts) {
        if (texts == null) return null;
        if (texts.length == 1) return texts[0].toString();
        StringBuilder builder = new StringBuilder("[");
        for (JsonText text : texts) builder.append(text).append(",");
        builder.replace(builder.length() - 1, builder.length(), "]");
        return builder.toString();
    }
}
