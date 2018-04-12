package org.soraworld.violet.chat;

import org.soraworld.violet.config.IIConfig;
import org.soraworld.violet.util.FileUtil;
import org.soraworld.violet.yaml.IYamlConfiguration;

import java.io.File;
import java.io.InputStream;

public class IILang {

    private String lang;
    private File lang_file;
    private final File path;
    private final IIConfig config;
    private final IYamlConfiguration lang_yaml = new IYamlConfiguration();

    public IILang(File path, IIConfig config) {
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
                config.println("&cLang file " + lang + " extract exception !!!");
                if (!"en_us".equals(lang)) {
                    setLang("en_us");
                    config.println("&cLang fall back to en_us .");
                }
            }
        }
        try {
            lang_yaml.load(lang_file);
        } catch (Throwable e) {
            if (config.debug()) e.printStackTrace();
            config.println("&cLang file " + lang + " load exception !!!");
            if (!"en_us".equals(lang)) {
                setLang("en_us");
                config.println("&cLang fall back to en_us .");
            }
        }
    }

    public boolean hasKey(String key) {
        return lang_yaml.getKeys(false).contains(key);
    }

    public String format(String key, Object... args) {
        String value = lang_yaml.getString(key);
        return value == null ? key : String.format(value, args);
    }

}