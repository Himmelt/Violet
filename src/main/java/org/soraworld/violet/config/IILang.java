package org.soraworld.violet.config;

import org.bukkit.configuration.file.YamlConfiguration;
import org.soraworld.violet.util.FileUtil;

import java.io.File;
import java.io.InputStream;

public class IILang {

    private String lang;
    private File lang_file;
    private final File path;
    private final IIConfig config;
    private final YamlConfiguration lang_yaml = new YamlConfiguration();

    IILang(File path, IIConfig config) {
        this.path = path;
        this.config = config;
        setLang("en_us");
    }

    public void setLang(String lang) {
        if (lang == null || lang.isEmpty()) {
            lang = "en_us";
        }
        this.lang = lang;
        this.lang_file = new File(path, lang + ".yml");
        load();
    }

    public String getLang() {
        return lang;
    }

    private void load() {
        if (!lang_file.exists()) {
            try {
                path.mkdirs();
                InputStream input = config.getClass().getResourceAsStream("/lang/" + lang + ".yml");
                FileUtil.copyInputStreamToFile(input, lang_file);
            } catch (Throwable e) {
                if (config.debug()) e.printStackTrace();
                config.iiChat.console("&cLang file extract exception !!!");
                if (!"en_us".equals(lang)) {
                    setLang("en_us");
                    config.iiChat.console("&cLang fall back to en_us .");
                }
            }
        }
        try {
            lang_yaml.load(lang_file);
        } catch (Throwable e) {
            if (config.debug()) e.printStackTrace();
            config.iiChat.console("&cLang file load exception !!!");
            if (!"en_us".equals(lang)) {
                setLang("en_us");
                config.iiChat.console("&cLang fall back to en_us .");
            }
        }
    }

    public boolean hasKey(String key) {
        return lang_yaml != null && lang_yaml.getKeys(false).contains(key);
    }

    public String format(String key, Object... args) {
        String value = lang_yaml.getString(key);
        return value == null ? key : String.format(value, args);
    }

}
