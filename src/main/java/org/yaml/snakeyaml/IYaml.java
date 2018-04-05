package org.yaml.snakeyaml;

import org.bukkit.configuration.file.YamlConstructor;
import org.yaml.snakeyaml.emitter.IEmitter;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;
import org.yaml.snakeyaml.serializer.Serializer;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class IYaml extends Yaml {

    private IDumperOptions yamlOptions;

    public IYaml(YamlConstructor constructor, Representer represent, IDumperOptions yamlOptions) {
        super(constructor, represent, yamlOptions);
        this.yamlOptions = yamlOptions;
    }

    public void dumpAll(Iterator<?> data, Writer output) {
        dumpAll(data, output, yamlOptions.getExplicitRoot());
    }

    private void dumpAll(Iterator<?> data, Writer output, Tag rootTag) {
        Serializer serializer = new Serializer(new IEmitter(output, yamlOptions), resolver, yamlOptions, rootTag);
        try {
            serializer.open();
            while (data.hasNext()) {
                Node node = representer.represent(data.next());
                serializer.serialize(node);
            }
            serializer.close();
        } catch (IOException e) {
            throw new YAMLException(e);
        }
    }

    public String dumpAs(Object data, Tag rootTag, DumperOptions.FlowStyle flowStyle) {
        DumperOptions.FlowStyle oldStyle = representer.getDefaultFlowStyle();
        if (flowStyle != null) {
            representer.setDefaultFlowStyle(flowStyle);
        }
        List<Object> list = new ArrayList<>(1);
        list.add(data);
        StringWriter buffer = new StringWriter();
        dumpAll(list.iterator(), buffer, rootTag);
        representer.setDefaultFlowStyle(oldStyle);
        return buffer.toString();
    }

}
