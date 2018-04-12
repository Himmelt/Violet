package org.soraworld.violet.chat;

import org.soraworld.violet.config.IIConfig;
import org.soraworld.violet.util.FileUtil;
import org.soraworld.violet.yaml.IYamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;

public class VLang {

    private final File path;
    private final IIConfig config;
    private final HashMap<String, File> files = new HashMap<>();
    private final HashMap<String, IYamlConfiguration> yamls = new HashMap<>();

    public VLang(File path, IIConfig config) {
        this.path = path;
        this.config = config;
    }

    private File getLangFile(String lang) {
        File file = files.get(lang);
        if (file == null) {
            file = new File(path, lang + ".yml");
            files.put(lang, file);
        }
        return file;
    }

    private IYamlConfiguration getLangYaml(String lang) {
        IYamlConfiguration yaml = yamls.get(lang);
        if (yaml == null) {
            yaml = new IYamlConfiguration();
            yamls.put(lang, yaml);
            load(lang);
        }
        return yaml;
    }

    private void load(String lang) {
        File lang_file = getLangFile(lang);
        if (!lang_file.exists()) {
            try {
                path.mkdirs();
                InputStream input = this.getClass().getResourceAsStream("/lang/" + lang + ".yml");
                FileUtil.copyInputStreamToFile(input, lang_file);
            } catch (Throwable e) {
                if (config.debug()) e.printStackTrace();
                config.println("&cLang file " + lang + " extract exception !!!");
            }
        }
        try {
            getLangYaml(lang).load(lang_file);
        } catch (Throwable e) {
            if (config.debug()) e.printStackTrace();
            config.println("&cLang file " + lang + " load exception !!!");
        }
    }

    public String format(String lang, String key, Object... args) {
        String value = getLangYaml(lang).getString(key);
        if (value == null || value.isEmpty()) {
            if ("en_us".equals(lang)) return key;
            return format("en_us", key, args);
        } else {
            return String.format(value, args);
        }
    }

}
