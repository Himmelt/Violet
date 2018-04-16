package org.soraworld.violet.yaml

import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.configuration.file.YamlConstructor
import org.bukkit.configuration.file.YamlRepresenter
import org.yaml.snakeyaml.DumperOptions
import java.io.*

class IYamlConfiguration : YamlConfiguration() {

    private val dumperOptions = DumperOptions()
    private val yamlRepresent = YamlRepresenter()
    private val yaml: IYaml = IYaml(YamlConstructor(), yamlRepresent, dumperOptions)

    override fun saveToString(): String {
        dumperOptions.indent = options().indent()
        dumperOptions.defaultFlowStyle = DumperOptions.FlowStyle.BLOCK
        dumperOptions.isAllowUnicode = true
        yamlRepresent.defaultFlowStyle = DumperOptions.FlowStyle.BLOCK
        val header = buildHeader()
        var dump = yaml.dump(getValues(false))
        if (dump == BLANK_CONFIG) dump = ""
        return header + dump
    }

    @Throws(Exception::class)
    override fun load(file: File) = load(InputStreamReader(FileInputStream(file), Charsets.UTF_8))

    @Throws(Exception::class)
    override fun load(stream: InputStream) = load(InputStreamReader(stream, Charsets.UTF_8))

    @Throws(Exception::class)
    override fun load(reader: Reader) {
        val builder = StringBuilder()
        (reader as? BufferedReader ?: BufferedReader(reader)).use { input ->
            var line: String? = input.readLine()
            while (line != null) {
                builder.append(line)
                builder.append('\n')
                line = input.readLine()
            }
        }
        loadFromString(builder.toString())
    }

    @Throws(Exception::class)
    override fun save(file: File) {
        val parent = file.canonicalFile.parentFile ?: return
        parent.mkdirs()
        if (!parent.isDirectory) throw IOException("Unable to create parent directories of $file")
        val data = saveToString()
        OutputStreamWriter(FileOutputStream(file), Charsets.UTF_8).use { writer -> writer.write(data) }
    }

    fun clear() {
        map.clear()
    }

}
