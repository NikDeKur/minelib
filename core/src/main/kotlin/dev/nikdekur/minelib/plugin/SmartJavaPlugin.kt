//@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")
//
//package dev.nikdekur.minelib.plugin
//
//import com.google.common.base.Charsets
//import org.apache.commons.lang.Validate
//import org.bukkit.Server
//import org.bukkit.command.Command
//import org.bukkit.command.CommandSender
//import org.bukkit.command.PluginCommand
//import org.bukkit.configuration.file.FileConfiguration
//import org.bukkit.configuration.file.YamlConfiguration
//import org.bukkit.generator.ChunkGenerator
//import org.bukkit.plugin.PluginBase
//import org.bukkit.plugin.PluginDescriptionFile
//import org.bukkit.plugin.PluginLoader
//import org.bukkit.plugin.PluginLogger
//import org.bukkit.plugin.java.JavaPluginLoader
//import java.io.*
//import java.util.*
//import java.util.logging.Level
//import java.util.logging.Logger
//import kotlin.jvm.java
//import org.bukkit.plugin.java.PluginClassLoader;
//
///**
// * Represents a Java plugin
// */
//abstract class SmartJavaPlugin : PluginBase {
//
//    var isEnabled: Boolean = false
//        protected set
//
//    var loader: PluginLoader? = null
//        protected set
//
//    var server: Server? = null
//        protected set
//
//    var file: File? = null
//        protected set
//
//    var description: PluginDescriptionFile? = null
//        protected set
//
//    var dataFolder: File? = null
//        protected set
//
//    var classLoader: ClassLoader? = null
//        protected set
//
//    var naggable: Boolean = true
//        protected set
//
//    var newConfig: FileConfiguration? = null
//        protected set
//
//    var configFile: File? = null
//        protected set
//
//    var logger: PluginLogger? = null
//        protected set
//
//    constructor() {
//        val classLoader = this.javaClass.classLoader
//        if (classLoader !is PluginClassLoader) {
//            throw IllegalStateException("JavaPlugin requires ${PluginClassLoader::class.java.name}")
//        }
//        classLoader.initialize(this)
//    }
//
//    protected constructor(
//        loader: JavaPluginLoader,
//        description: PluginDescriptionFile,
//        dataFolder: File,
//        file: File
//    ) {
//        val classLoader = this.javaClass.classLoader
//        if (classLoader is PluginClassLoader) {
//            throw IllegalStateException("Cannot use initialization constructor at runtime")
//        }
//        init(loader, loader.server, description, dataFolder, file, classLoader)
//    }
//
//    /**
//     * Returns the folder that the plugin data's files are located in. The
//     * folder may not yet exist.
//     *
//     * @return The folder.
//     */
//    override fun getDataFolder(): File {
//        return dataFolder ?: throw IllegalStateException("Data folder not initialized")
//    }
//
//    /**
//     * Gets the associated PluginLoader responsible for this plugin
//     *
//     * @return PluginLoader that controls this plugin
//     */
//    override fun getPluginLoader(): PluginLoader {
//        return loader ?: throw IllegalStateException("Plugin loader not initialized")
//    }
//
//    /**
//     * Returns the Server instance currently running this plugin
//     *
//     * @return Server running this plugin
//     */
//    override fun getServer(): Server {
//        return server ?: throw IllegalStateException("Server not initialized")
//    }
//
//    /**
//     * Returns a value indicating whether or not this plugin is currently
//     * enabled
//     *
//     * @return true if this plugin is enabled, otherwise false
//     */
//    override fun isEnabled(): Boolean = isEnabled
//
//    /**
//     * Returns the file which contains this plugin
//     *
//     * @return File containing this plugin
//     */
//    fun getFile(): File {
//        return file ?: throw IllegalStateException("Plugin file not initialized")
//    }
//
//    /**
//     * Returns the plugin.yaml file containing the details for this plugin
//     *
//     * @return Contents of the plugin.yaml file
//     */
//    override fun getDescription(): PluginDescriptionFile {
//        return description ?: throw IllegalStateException("Plugin description not initialized")
//    }
//
//    override fun getConfig(): FileConfiguration {
//        if (newConfig == null) {
//            reloadConfig()
//        }
//        return newConfig ?: throw IllegalStateException("Config not initialized")
//    }
//
//    /**
//     * Provides a reader for a text file located inside the jar.
//     *
//     * The returned reader will read text with the UTF-8 charset.
//     *
//     * @param file the filename of the resource to load
//     * @return null if [getResource] returns null
//     * @throws IllegalArgumentException if file is null
//     * @see ClassLoader.getResourceAsStream
//     */
//    @Suppress("DEPRECATION")
//    protected fun getTextResource(file: String): Reader? {
//        val input = getResource(file)
//        return input?.let { InputStreamReader(it, Charsets.UTF_8) }
//    }
//
//    @Suppress("DEPRECATION")
//    override fun reloadConfig() {
//        val configFile = this.configFile ?: throw IllegalStateException("Config file not initialized")
//        newConfig = YamlConfiguration.loadConfiguration(configFile)
//
//        val defConfigStream = getResource("config.yml")
//        if (defConfigStream != null) {
//            newConfig?.setDefaults(
//                YamlConfiguration.loadConfiguration(
//                    InputStreamReader(defConfigStream, Charsets.UTF_8)
//                )
//            )
//        }
//    }
//
//    override fun saveConfig() {
//        val configFile = this.configFile ?: throw IllegalStateException("Config file not initialized")
//        val logger = this.logger ?: throw IllegalStateException("Logger not initialized")
//
//        try {
//            getConfig().save(configFile)
//        } catch (ex: IOException) {
//            logger.log(Level.SEVERE, "Could not save config to $configFile", ex)
//        }
//    }
//
//    override fun saveDefaultConfig() {
//        val configFile = this.configFile ?: throw IllegalStateException("Config file not initialized")
//        if (!configFile.exists()) {
//            saveResource("config.yml", false)
//        }
//    }
//
//    override fun saveResource(resourcePath: String, replace: Boolean) {
//        require(resourcePath.isNotEmpty()) { "ResourcePath cannot be null or empty" }
//
//        val normalizedPath = resourcePath.replace('\\', '/')
//        val input = getResource(normalizedPath)
//            ?: throw IllegalArgumentException("The embedded resource '$normalizedPath' cannot be found in $file")
//
//        val dataFolder = this.dataFolder ?: throw IllegalStateException("Data folder not initialized")
//        val outFile = File(dataFolder, normalizedPath)
//        val lastIndex = normalizedPath.lastIndexOf('/')
//        val outDir = File(dataFolder, normalizedPath.take(if (lastIndex >= 0) lastIndex else 0))
//
//        if (!outDir.exists()) {
//            outDir.mkdirs()
//        }
//
//        val logger = this.logger ?: throw IllegalStateException("Logger not initialized")
//
//        try {
//            if (!outFile.exists() || replace) {
//                FileOutputStream(outFile).use { out ->
//                    input.use { inp ->
//                        val buf = ByteArray(1024)
//                        var len: Int
//                        while (inp.read(buf).also { len = it } > 0) {
//                            out.write(buf, 0, len)
//                        }
//                    }
//                }
//            } else {
//                logger.log(
//                    Level.WARNING,
//                    "Could not save ${outFile.name} to $outFile because ${outFile.name} already exists."
//                )
//            }
//        } catch (ex: IOException) {
//            logger.log(Level.SEVERE, "Could not save ${outFile.name} to $outFile", ex)
//        }
//    }
//
//    override fun getResource(filename: String): InputStream? {
//        require(filename.isNotEmpty()) { "Filename cannot be null or empty" }
//
//        return try {
//            val classLoader = getClassLoader()
//            val url = classLoader.getResource(filename) ?: return null
//            val connection = url.openConnection()
//            connection.useCaches = false
//            connection.inputStream
//        } catch (ex: IOException) {
//            null
//        }
//    }
//
//    /**
//     * Returns the ClassLoader which holds this plugin
//     *
//     * @return ClassLoader holding this plugin
//     */
//    fun getClassLoader(): ClassLoader {
//        return classLoader ?: throw IllegalStateException("Class loader not initialized")
//    }
//
//    /**
//     * Sets the enabled state of this plugin
//     *
//     * @param enabled true if enabled, otherwise false
//     */
//    fun setEnabled(enabled: Boolean) {
//        if (isEnabled != enabled) {
//            isEnabled = enabled
//
//            if (isEnabled) {
//                onEnable()
//            } else {
//                onDisable()
//            }
//        }
//    }
//
//    fun init(
//        loader: PluginLoader,
//        server: Server,
//        description: PluginDescriptionFile,
//        dataFolder: File,
//        file: File,
//        classLoader: ClassLoader
//    ) {
//        this.loader = loader
//        this.server = server
//        this.file = file
//        this.description = description
//        this.dataFolder = dataFolder
//        this.classLoader = classLoader
//        this.configFile = File(dataFolder, "config.yml")
//        this.logger = PluginLogger(this)
//    }
//
//    /**
//     * {@inheritDoc}
//     */
//    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
//        return false
//    }
//
//    /**
//     * {@inheritDoc}
//     */
//    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<String>): List<String>? {
//        return null
//    }
//
//    /**
//     * Gets the command with the given name, specific to this plugin. Commands
//     * need to be registered in the [PluginDescriptionFile.getCommands]
//     * PluginDescriptionFile to exist at runtime.
//     *
//     * @param name name or alias of the command
//     * @return the plugin command if found, otherwise null
//     */
//    fun getCommand(name: String): PluginCommand? {
//        val server = this.server ?: return null
//        val description = this.description ?: return null
//
//        val alias = name.lowercase(Locale.ENGLISH)
//        var command = server.getPluginCommand(alias)
//
//        if (command == null || command.plugin != this) {
//            command = server.getPluginCommand("${description.name.lowercase(Locale.ENGLISH)}:$alias")
//        }
//
//        return if (command?.plugin == this) command else null
//    }
//
//    override fun onLoad() {}
//
//    override fun onDisable() {}
//
//    override fun onEnable() {}
//
//    override fun getDefaultWorldGenerator(worldName: String, id: String): ChunkGenerator? {
//        return null
//    }
//
//    override fun isNaggable(): Boolean = naggable
//
//    override fun setNaggable(canNag: Boolean) {
//        this.naggable = canNag
//    }
//
//    override fun getLogger(): Logger {
//        return logger ?: throw IllegalStateException("Logger not initialized")
//    }
//
//    override fun toString(): String {
//        return description?.fullName ?: "Unknown Plugin"
//    }
//
//    companion object {
//        /**
//         * This method provides fast access to the plugin that has [getProvidingPlugin]
//         * provided the given plugin class, which is usually the plugin that implemented it.
//         *
//         * An exception to this would be if plugin's jar that contained the class
//         * does not extend the class, where the intended plugin would have
//         * resided in a different jar / classloader.
//         *
//         * @param T a class that extends JavaPlugin
//         * @param clazz the class desired
//         * @return the plugin that provides and implements said class
//         * @throws IllegalArgumentException if clazz is null
//         * @throws IllegalArgumentException if clazz does not extend [SmartJavaPlugin]
//         * @throws IllegalStateException if clazz was not provided by a plugin,
//         *     for example, if called with `JavaPlugin.getPlugin(JavaPlugin::class.java)`
//         * @throws IllegalStateException if called from the static initializer for
//         *     given JavaPlugin
//         * @throws ClassCastException if plugin that provided the class does not
//         *     extend the class
//         */
//        @JvmStatic
//        fun <T : SmartJavaPlugin> getPlugin(clazz: Class<T>): T {
//            Validate.notNull(clazz, "Null class cannot have a plugin")
//            if (!SmartJavaPlugin::class.java.isAssignableFrom(clazz)) {
//                throw IllegalArgumentException("$clazz does not extend ${SmartJavaPlugin::class.java}")
//            }
//            val cl = clazz.classLoader
//            if (cl !is PluginClassLoader) {
//                throw IllegalArgumentException("$clazz is not initialized by ${PluginClassLoader::class.java}")
//            }
//            val plugin = cl.plugin
//                ?: throw IllegalStateException("Cannot get plugin for $clazz from a static initializer")
//            return clazz.cast(plugin)
//        }
//
//        /**
//         * This method provides fast access to the plugin that has provided the
//         * given class.
//         *
//         * @param clazz a class belonging to a plugin
//         * @return the plugin that provided the class
//         * @throws IllegalArgumentException if the class is not provided by a
//         *     JavaPlugin
//         * @throws IllegalArgumentException if class is null
//         * @throws IllegalStateException if called from the static initializer for
//         *     given JavaPlugin
//         */
//        @JvmStatic
//        fun getProvidingPlugin(clazz: Class<*>): SmartJavaPlugin {
//            Validate.notNull(clazz, "Null class cannot have a plugin")
//            val cl = clazz.classLoader
//            if (cl !is PluginClassLoader) {
//                throw IllegalArgumentException("$clazz is not provided by ${PluginClassLoader::class.java}")
//            }
//            PluginDi
//            return cl.plugin
//                ?: throw IllegalStateException("Cannot get plugin for $clazz from a static initializer")
//        }
//    }
//}