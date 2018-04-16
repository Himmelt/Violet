package org.soraworld.violet.yaml

import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.BaseConstructor
import org.yaml.snakeyaml.error.YAMLException
import org.yaml.snakeyaml.nodes.Tag
import org.yaml.snakeyaml.representer.Representer
import org.yaml.snakeyaml.serializer.Serializer
import java.io.IOException
import java.io.StringWriter
import java.io.Writer
import java.util.*

class IYaml(constructor: BaseConstructor?, represent: Representer?, dumperOptions: DumperOptions?) : Yaml(constructor, represent, dumperOptions) {

    override fun dumpAll(data: Iterator<*>, output: Writer) = dumpAll(data, output, null)

    override fun dumpAs(data: Any, rootTag: Tag, flowStyle: DumperOptions.FlowStyle?): String {
        val oldStyle = representer.defaultFlowStyle
        if (flowStyle != null) representer.defaultFlowStyle = flowStyle
        val list = ArrayList<Any>(1)
        list.add(data)
        val buffer = StringWriter()
        dumpAll(list.iterator(), buffer, rootTag)
        representer.defaultFlowStyle = oldStyle
        return buffer.toString()
    }

    private fun dumpAll(data: Iterator<*>, output: Writer, rootTag: Tag?) {
        val serializer = Serializer(IEmitter(output, dumperOptions), resolver, dumperOptions, rootTag)
        try {
            serializer.open()
            while (data.hasNext()) {
                val node = representer.represent(data.next())
                serializer.serialize(node)
            }
            serializer.close()
        } catch (e: IOException) {
            throw YAMLException(e)
        }
    }

}
