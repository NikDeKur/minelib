package dev.nikdekur.minelib.config

import dev.nikdekur.ndkore.ext.r_CallMethod
import kotlinx.io.files.FileNotFoundException
import org.apache.commons.lang.Validate
import org.bukkit.Bukkit
import org.bukkit.configuration.Configuration
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.InvalidConfigurationException
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.FileConfigurationOptions
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.configuration.file.YamlConstructor
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.DumperOptions.FlowStyle
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.error.YAMLException
import org.yaml.snakeyaml.representer.Representer
import java.io.File
import java.io.IOException
import java.io.Reader
import java.util.logging.Level


open class SmartYamlConfiguration(
    val yamlOptions: DumperOptions = DumperOptions(),
    val yamlRepresenter: Representer = Representer()
) : FileConfiguration() {

    val yaml = Yaml(YamlConstructor(), yamlRepresenter, yamlOptions)

    override fun saveToString(): String {
        yamlOptions.indent = this.options().indent()
        yamlOptions.setDefaultFlowStyle(FlowStyle.BLOCK)
        yamlRepresenter.setDefaultFlowStyle(FlowStyle.BLOCK)
        val header = this.buildHeader()
        var dump: String = this.yaml.dump(this.getValues(false))
        if (dump == "{}\n") {
            dump = ""
        }

        return header + dump
    }

    @Throws(InvalidConfigurationException::class)
    override fun loadFromString(contents: String?) {
        Validate.notNull(contents, "Contents cannot be null")

        val input: MutableMap<*, *>?
        try {
            input = this.yaml.load(contents) as MutableMap<*, *>?
        } catch (e: YAMLException) {
            throw InvalidConfigurationException(e)
        } catch (_: ClassCastException) {
            throw InvalidConfigurationException("Top level is not a Map.")
        }

        val header = this.parseHeader(contents!!)
        if (header.isNotEmpty()) {
            this.options().header(header)
        }

        if (input != null) {
            this.convertMapsToSections(input, this)
        }
    }

    protected fun convertMapsToSections(input: MutableMap<*, *>, section: ConfigurationSection) {
        for (entry in input.entries) {
            val key: String? = entry.key.toString()
            val value: Any? = entry.value
            if (value is MutableMap<*, *>) {
                this.convertMapsToSections(value, section.createSection(key))
            } else {
                section[key] = value
            }
        }
    }

    protected fun parseHeader(input: String): String {
        val lines = input.split("\r?\n".toRegex()).toTypedArray()
        val result = StringBuilder()
        var readingHeader = true
        var foundHeader = false

        var i = 0
        while (i < lines.size && readingHeader) {
            val line = lines[i]
            if (line.startsWith("# ")) {
                if (i > 0) {
                    result.append("\n")
                }

                if (line.length > "# ".length) {
                    result.append(line.substring("# ".length))
                }

                foundHeader = true
            } else if (foundHeader && line.isEmpty()) {
                result.append("\n")
            } else if (foundHeader) {
                readingHeader = false
            }
            ++i
        }

        return result.toString()
    }

    override fun buildHeader(): String {
        val header = this.options().header()
        if (this.options().copyHeader()) {
            val def: Configuration? = this.getDefaults()
            if (def != null && def is FileConfiguration) {
                val fileDefaults = def
                val defaultsHeader = fileDefaults.r_CallMethod("buildHeader").value as String?
                if (!defaultsHeader.isNullOrEmpty()) {
                    return defaultsHeader
                }
            }
        }

        if (header == null) {
            return ""
        } else {
            val builder = StringBuilder()
            val lines = header.split("\r?\n".toRegex()).toTypedArray()
            var startedHeader = false

            for (i in lines.indices.reversed()) {
                builder.insert(0, "\n")
                if (startedHeader || lines[i].isNotEmpty()) {
                    builder.insert(0, lines[i])
                    builder.insert(0, "# ")
                    startedHeader = true
                }
            }

            return builder.toString()
        }
    }

    override fun options(): SmartYamlConfigurationOptions {
        if (this.options == null) {
            this.options = SmartYamlConfigurationOptions(this)
        }

        return this.options as SmartYamlConfigurationOptions
    }

    companion object {
        fun loadConfiguration(
            file: File?,
            yamlOptions: DumperOptions = DumperOptions(),
            yamlRepresenter: Representer = Representer()
        ): SmartYamlConfiguration {
            Validate.notNull(file, "File cannot be null")
            val config = SmartYamlConfiguration(
                yamlOptions,
                yamlRepresenter
            )

            try {
                config.load(file)
            } catch (_: FileNotFoundException) {
            } catch (ex: IOException) {
                Bukkit.getLogger().log(Level.SEVERE, "Cannot load $file", ex)
            } catch (ex: InvalidConfigurationException) {
                Bukkit.getLogger().log(Level.SEVERE, "Cannot load $file", ex)
            }

            return config
        }



        fun loadConfiguration(
            reader: Reader?,
            yamlOptions: DumperOptions = DumperOptions(),
            yamlRepresenter: Representer = Representer()
        ): SmartYamlConfiguration {
            Validate.notNull(reader, "Stream cannot be null")
            val config = SmartYamlConfiguration(
                yamlOptions,
                yamlRepresenter
            )

            try {
                config.load(reader)
            } catch (ex: IOException) {
                Bukkit.getLogger().log(Level.SEVERE, "Cannot load configuration from stream", ex)
            } catch (ex: InvalidConfigurationException) {
                Bukkit.getLogger().log(Level.SEVERE, "Cannot load configuration from stream", ex)
            }

            return config
        }
    }
}


/**
 * Various settings for controlling the input and output of a [ ]
 */
class SmartYamlConfigurationOptions(configuration: SmartYamlConfiguration) : FileConfigurationOptions(configuration) {
    private var indent = 2

    override fun configuration(): YamlConfiguration? {
        return super.configuration() as YamlConfiguration?
    }

    override fun copyDefaults(value: Boolean): SmartYamlConfigurationOptions {
        super.copyDefaults(value)
        return this
    }

    override fun pathSeparator(value: Char): SmartYamlConfigurationOptions {
        super.pathSeparator(value)
        return this
    }

    override fun header(value: String?): SmartYamlConfigurationOptions {
        super.header(value)
        return this
    }

    override fun copyHeader(value: Boolean): SmartYamlConfigurationOptions {
        super.copyHeader(value)
        return this
    }

    /**
     * Gets how much spaces should be used to indent each line.
     *
     *
     * The minimum value this maybe is 2, and the maximum is 9.
     *
     * @return How much to indent by
     */
    fun indent(): Int {
        return indent
    }

    /**
     * Sets how much spaces should be used to indent each line.
     *
     *
     * The minimum value this maybe is 2, and the maximum is 9.
     *
     * @param value New indent
     * @return This object, for chaining
     */
    fun indent(value: Int): SmartYamlConfigurationOptions {
        Validate.isTrue(value >= 2, "Indent must be at least 2 characters")
        Validate.isTrue(value <= 9, "Indent cannot be greater than 9 characters")

        this.indent = value
        return this
    }
}

