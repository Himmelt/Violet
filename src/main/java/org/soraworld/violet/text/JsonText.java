package org.soraworld.violet.text;

/**
 * @author Himmelt
 */
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

    public JsonText(String text, ClickText.Action action, String value) {
        this.text = text;
        if (action != null && value != null) {
            this.click = new ClickText(value, action);
        }
        getJson();
    }

    public JsonText(String text, HoverText.Action action, String value) {
        this.text = text;
        if (action != null && value != null) {
            this.hover = new HoverText(value, action);
        }
        getJson();
    }

    public JsonText(String text, ClickText.Action clickAction, String clickValue, HoverText.Action hoverAction, String hoverValue) {
        this.text = text;
        if (clickAction != null && clickValue != null) {
            this.click = new ClickText(clickValue, clickAction);
        }
        if (hoverAction != null && hoverValue != null) {
            this.hover = new HoverText(hoverValue, hoverAction);
        }
        getJson();
    }

    private void getJson() {
        if (json == null) {
            if (click != null || hover != null) {
                json = "{\"text\":\"" + text + "\",";
                if (click != null) {
                    json += click;
                    if (hover != null) {
                        json += ",";
                    } else {
                        json += "}";
                    }
                }
                if (hover != null) {
                    json += hover + "}";
                }
            } else {
                json = "\"" + text + "\"";
            }
        }
    }

    @Override
    public String toString() {
        return json == null ? "" : json;
    }

    public static String toJson(JsonText... texts) {
        if (texts == null) {
            return null;
        }
        if (texts.length == 1) {
            return texts[0] == null ? "" : texts[0].toString();
        }
        StringBuilder builder = new StringBuilder("[");
        for (JsonText text : texts) {
            builder.append(text == null ? "\"\"" : text.toString()).append(",");
        }
        builder.replace(builder.length() - 1, builder.length(), "]");
        return builder.toString();
    }

    public static String toJson(JsonText head, JsonText... texts) {
        if (texts == null) {
            return toJson(new JsonText[]{head});
        }
        JsonText[] array = new JsonText[texts.length + 1];
        array[0] = head;
        System.arraycopy(texts, 0, array, 1, texts.length);
        return toJson(array);
    }
}
