package org.soraworld.violet.core;

import org.jetbrains.annotations.NotNull;
import org.soraworld.hocon.node.FileNode;
import org.soraworld.hocon.node.Setting;
import org.soraworld.violet.Violet;
import org.soraworld.violet.text.JsonText;
import org.soraworld.violet.util.ChatColor;
import org.soraworld.violet.util.FileUtils;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author Himmelt
 */
public final class I18n {
    @Setting
    private String lang = Locale.CHINA.equals(Locale.getDefault()) ? "zh_cn" : "en_us";
    @Setting
    private boolean autoUpdate = true;
    private HashMap<String, String> langMap = new HashMap<>();
    private static HashMap<String, HashMap<String, String>> langMaps = new HashMap<>();


    public boolean reExtract() {
        if (FileUtils.deletePath(path.resolve("lang").toFile(), debug)) {
            return setLang(lang);
        }
        if (debug) {
            console(ChatColor.RED + "deletePath " + path.resolve("lang") + " failed !!");
        }
        return false;
    }

    public String trans(@NotNull String key, Object... args) {
        String text = langMap.get(key);
        if ((text == null || text.isEmpty()) && !plugin.getId().equalsIgnoreCase(Violet.PLUGIN_ID) && translator != null) {
            text = translator.trans(lang, key, args);
        }
        if (text == null || text.isEmpty()) {
            return key;
        }
        if (args.length > 0) {
            try {
                return String.format(text, args);
            } catch (Throwable e) {
                console(ChatColor.RED + "Translation " + key + " -> " + text + " format failed !");
            }
        }
        return text;
    }
    public String getLang() {
        return lang;
    }

    public boolean setLang(String lang) {
        lang = lang.toLowerCase();
        HashMap<String, String> temp = loadLangMap(lang);
        if (!temp.isEmpty()) {
            this.lang = lang;
            langMap = temp;
            String head = langMap.get("chatHead");
            if (head != null && !head.isEmpty()) {
                setHead(head);
            }
            return true;
        } else {
            consoleKey("emptyLangMap");
            return false;
        }
    }

    final HashMap<String, String> loadLangMap(String lang) {
        Path langFile = path.resolve("lang").resolve(lang + ".lang");
        boolean extract = false;
        URL url = plugin.getAssetUrl("lang/" + lang + ".lang");
        try {
            if (Files.notExists(langFile)) {
                Files.createDirectories(langFile.getParent());
                Files.copy(url.openStream(), langFile);
            }
            extract = true;
            FileNode langNode = new FileNode(langFile.toFile(), options);
            langNode.load(true);
            HashMap<String, String> map = langNode.asStringMap();
            for (Map.Entry<String, String> entry : map.entrySet()) {
                entry.setValue(ChatColor.colorize(entry.getValue()));
            }
            return map;
        } catch (Throwable e) {
            if (extract) {
                console(ChatColor.RED + "Lang file " + langFile + " load exception !!!");
            } else {
                console(ChatColor.RED + "Lang file " + url + " extract exception !!!");
            }
            debug(e);
            return new HashMap<>();
        }
    }

}
