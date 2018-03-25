package org.soraworld.violet.config;

import org.bukkit.configuration.file.YamlConfiguration;
import org.soraworld.violet.util.FileUtil;

import java.io.File;
import java.io.InputStream;

public class IILang {

    private File file;
    private String lang;
    private final File path;
    private final IIConfig config;
    private final YamlConfiguration yaml = new YamlConfiguration();

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
        this.file = new File(path, lang + ".yml");
        load();
    }

    public String getLang() {
        return lang;
    }

    private void load() {
        if (!file.exists()) {
            try {
                path.mkdirs();
                InputStream input = this.getClass().getResourceAsStream("/lang/" + lang + ".yml");
                FileUtil.copyInputStreamToFile(input, file);
            } catch (Throwable e) {
                if (config.debug()) e.printStackTrace();
                config.iiChat.console("&cLang file load exception !!!");
            }
        }
        try {
            yaml.load(file);
        } catch (Throwable e) {
            if (config.debug()) e.printStackTrace();
            config.iiChat.console("&cLang file load exception !!!");
        }
    }

    public String format(String key, Object... args) {
        String value = yaml.getString(key);
        return String.format(value == null ? key : value, args);
    }

}
