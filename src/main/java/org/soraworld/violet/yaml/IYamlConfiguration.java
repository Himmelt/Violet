package org.soraworld.violet.yaml;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.file.YamlConstructor;
import org.bukkit.configuration.file.YamlRepresenter;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.representer.Representer;

import java.io.*;

public class IYamlConfiguration extends YamlConfiguration {

    private final DumperOptions dumperOptions = new DumperOptions();
    private final Representer yamlRepresent = new YamlRepresenter();
    private final IYaml yaml = new IYaml(new YamlConstructor(), yamlRepresent, dumperOptions);

    @Override
    public String saveToString() {
        dumperOptions.setIndent(options().indent());
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        dumperOptions.setAllowUnicode(true);
        yamlRepresent.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

        String header = buildHeader();
        String dump = yaml.dump(getValues(false));

        if (dump.equals(BLANK_CONFIG)) {
            dump = "";
        }

        return header + dump;
    }

    public void load(File file) throws IOException, InvalidConfigurationException {
        Validate.notNull(file, "File cannot be null");
        final FileInputStream stream = new FileInputStream(file);
        load(new InputStreamReader(stream, Charsets.UTF_8));
    }

    public void load(InputStream stream) throws IOException, InvalidConfigurationException {
        Validate.notNull(stream, "Stream cannot be null");
        load(new InputStreamReader(stream, Charsets.UTF_8));
    }

    public void load(Reader reader) throws IOException, InvalidConfigurationException {

        StringBuilder builder = new StringBuilder();

        try (BufferedReader input = reader instanceof BufferedReader ? (BufferedReader) reader : new BufferedReader(reader)) {
            String line;
            while ((line = input.readLine()) != null) {
                builder.append(line);
                builder.append('\n');
            }
        }

        loadFromString(builder.toString());
    }

    public void save(File file) throws IOException {
        Validate.notNull(file, "File cannot be null");
        Files.createParentDirs(file);
        String data = saveToString();
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), Charsets.UTF_8)) {
            writer.write(data);
        }
    }

    public void clear() {
        map.clear();
    }

}
