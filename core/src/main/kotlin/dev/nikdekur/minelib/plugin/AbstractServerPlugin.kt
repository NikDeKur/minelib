package dev.nikdekur.minelib.plugin

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import dev.nikdekur.minelib.command.api.ServerCommand
import dev.nikdekur.minelib.ext.nanosToMs
import dev.nikdekur.minelib.scheduler.Scheduler
import dev.nikdekur.minelib.service.PluginComponent
import dev.nikdekur.minelib.service.PluginService
import dev.nikdekur.minelib.utils.ClassUtils
import dev.nikdekur.ndkore.ext.forEachSafe
import dev.nikdekur.ndkore.ext.format
import dev.nikdekur.ndkore.ext.loadConfig
import dev.nikdekur.ndkore.ext.tryEverything
import dev.nikdekur.ndkore.ext.warning
import dev.nikdekur.ndkore.koin.SimpleKoinContext
import dev.nikdekur.ndkore.reflect.Reflect
import dev.nikdekur.ndkore.service.ServicesManager
import dev.nikdekur.ndkore.service.manager.KoinServicesManager
import kotlinx.serialization.encodeToString
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import org.koin.environmentProperties
import java.io.File
import java.io.IOException
import java.util.logging.Logger
import kotlin.properties.Delegates
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds


/**
 * # ServerPlugin
 *
 * Abstract class that represents a plugin for the server (Should be used instead of [JavaPlugin]).
 * Provides a lot of useful features for the plugin.
 *
 * ### Functionality:
 * - [Scheduler] for the plugin that doesn't require to pass the plugin instance
 * - Components system for registering listeners, commands and services (from ndkore)
 * - Loading files/directories from the plugin directory
 * - Loading and saving configuration files
 * - Reloading the plugin and all components
 * - Loading all classes from the plugin jar to avoid class-loading issues on reloading
 * - Setting static fields of the plugin for your subclass (not recommended, use services instead)
 *
 * ### Example Usage:
 * ```kotlin
 * class MyPlugin : ServerPlugin() {
 *    override val components by lazy {
 *        listOf(
 *            // Services
 *            MyService(this),
 *
 *            // Commands
 *            MyCommand(this),
 *
 *            // Listeners
 *            MyListener(this)
 *         )
 *     }
 *
 *     override fun whenLoad() {
 *         // Do something when the plugin is loaded
 *     }
 *
 *     override fun whenEnabled() {
 *         // Do something when the plugin is enabled
 *     }
 * }
 * ```
 */
abstract class AbstractServerPlugin : JavaPlugin(), ServerPlugin, PluginComponent {

    override val onlinePlayers: Collection<Player>
        get() = server.onlinePlayers

    override var scheduler: Scheduler by Delegates.notNull()

    override lateinit var servicesManager: ServicesManager

    override val app
        get() = this

    /**
     * Private set of listeners provided by the plugin.
     *
     * The field is used
     * to register all listeners when the plugin is loaded and unregister all listeners when the plugin is unloaded.
     */
    private val _listeners: MutableSet<Listener> = HashSet()
    override val listeners: Set<Listener>
        get() = _listeners

    var startTime: Long = 0
        private set

    override val uptime: Duration
        get() = (System.currentTimeMillis() - startTime).milliseconds

    override val clazzLoader
        get() = classLoader

    override fun onEnable() {
        registerComponents()

        stopReload()
    }

    override fun onDisable() {
        startReload()
    }

    /**
     * Internal function called before the plugin is reloaded.
     *
     * Executes [beforeReload] and executes all necessary actions to prepare the plugin for reloading.
     */
    open fun startReload() {
        beforeReload()
        this.scheduler.cancelTasks()

        // Unregister all listeners that are linked to the plugin
        HandlerList.unregisterAll(this)

        try {
            servicesManager.disable()
        } catch (_: IllegalStateException) {
            // May occur if the plugin if error occurred while enabling the plugin
            // User would have already been notified about the enabling error
            // So don't spam also with onDisable error
        }
    }

    /**
     * Internal function called after the plugin is reloaded.
     *
     * Executes [afterReload] and executes all necessary actions to prepare the plugin after reloading.
     */
    open fun stopReload() {
        startTime = System.currentTimeMillis()
        servicesManager.enable()

        // Register all listeners
        _listeners.forEach(::registerListener)

        afterReload()
    }


    /**
    * Internal function called once the plugin is enabled to register all components.
    *
    * Function go through [components] and [defaultComponents] and register them via [registerComponent] function.
    */
    protected open fun registerComponents() {
        val components = try {
            components
        } catch (e: LinkageError) {
            logger.severe(
                "Failed to register plugin components. " +
                        "Make sure to create all instances in lazy or getter format. " +
                        "Storing instances in fields can cause bukkit class-loading issues."
            )

            logger.severe(
                "Also, error may occur if dependency collision is present. " +
                        "Make sure for bukkit not to see similar classes (at same package) in different plugins."
            )
            e.printStackTrace()
            return
        }

        components.forEachSafe(
            { e, el -> logger.warning(e) { "Failed to register component $el" } },
            ::registerComponent
        )
    }


    /**
     * Register a new plugin component.
     *
     * Function could register [Listener], [ServerCommand] or [PluginService].
     *
     * @param component Component to register
     * @return has the component been registered?
     */
    override fun registerComponent(component: Any): Boolean {
        var registered = false

        if (component is Listener && !_listeners.contains(component)) {
            addListener(component)
            registered = true
        }

        if (component is ServerCommand) {
            component.register(this)
            registered = true
        }

        if (component is PluginService) {
            servicesManager.registerService(component, component.bindClass)
            registered = true
        }

        return registered
    }

    open fun buildServicesManager() = KoinServicesManager(SHARED_CONTEXT)

    override fun onLoad() {
        loadAllPluginClasses()
        servicesManager = buildServicesManager()

        scheduler = Scheduler(this)
        setupStaticFields()
        whenLoad()
    }

    /**
     * Try to set the static fields of the plugin.
     *
     * Function is called in [onEnable] method.
     * If you wish to override it, be aware of this.
     *
     * It tries to set the following fields:
     * - instance [ServerPlugin]
     * - logger [Logger]
     * - scheduler [Scheduler]
     *
     * If any of the fields are not found, it will be ignored.
     */
    protected open fun setupStaticFields() {
        tryEverything(
            { Reflect.setFieldValue(javaClass, null, "instance", this) },
            { Reflect.setFieldValue(javaClass, null, "logger", logger) },
            { Reflect.setFieldValue(javaClass, null, "scheduler", scheduler) }
        )
    }

    open fun whenLoad() {
        // Override this method to do something when the plugin is loaded
    }

    open fun whenEnabled() {
        // Override this method to do something when the plugin is enabled
    }
    open fun whenDisable() {
        // Override this method to do something when the plugin is disabled
    }

    open fun beforeReload() {
        // Override this method to do something when the plugin is disabled
    }
    open fun afterReload() {
        // Override this method to do something when the plugin is disabled
    }


    var isReloading: Boolean = false
    override fun reload() {
        isReloading = true
        startReload()
        stopReload()
        isReloading = false
    }


    open val preLoadingClassesWL: Set<String> = emptySet()
    open val preLoadingClassesBL: Set<String> = emptySet()
    protected fun loadAllPluginClasses() {
        val startNanos = System.nanoTime()
        val success: Boolean = try {
            ClassUtils.loadAllClassesFromJar(classLoader, file) { className ->
                return@loadAllClassesFromJar when {
                    preLoadingClassesBL.contains(className) -> false
                    className.contains("v1_") -> false
                    className.startsWith("dev.nikdekur.") -> true
                    preLoadingClassesWL.contains(className) -> true
                    else -> false
                }
            }
            true
        } catch (e: IOException) {
            logger.warning(e) { "Could not load classes from jar file" }
            false
        }

        if (success) {
            val time = (System.nanoTime() - startNanos).nanosToMs().format(2)
            logger.info("Loaded all plugin classes in $time ms")
        }
    }

    override fun addListener(listener: Listener) {
        _listeners.add(listener)
    }

    fun registerCommand(command: ServerCommand) {
        val name = command.name
        try {
            val bCommand = getCommand(name) ?: run {
                logger.severe("Command '$name' not found. Maybe you forgot to register it?")
                return
            }
            bCommand.executor = command
            bCommand.tabCompleter = command
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun registerListener(listener: Listener) {
        server.pluginManager.registerEvents(listener, this)
    }


    override fun loadDirectory(name: String): File {
        val dir = File(dataFolder, name)
        if (!dir.exists())
            dir.mkdirs()
        return dir
    }


    override fun loadFile(fileName: String, folder: File?): File {
        val folder = folder ?: dataFolder
        folder.mkdirs()
        return File(folder, fileName).also {
            if (!it.exists()) it.createNewFile()
        }
    }

    val yaml by lazy {
        Yaml(
            configuration = YamlConfiguration(
                strictMode = false
            )
        )
    }

    override fun <T : Any> loadConfig(
        configName: String,
        clazz: Class<T>,
        requireFilled: Boolean,
        folder: File?
    ): T {
        val configName = if (!configName.endsWith(".yml")) "$configName.yml" else configName
        val file = loadFile(configName, folder)
        // Check if the file is empty
        // loadFile ensures that the file exists
        if (file.length() == 0L) {
            check(!requireFilled) {
                "Config file `$configName` is empty while it should be filled."
            }

            val config = try {
                clazz.newInstance()
            } catch (_: NoSuchMethodException) {
                throw IllegalArgumentException(
                    "Cannot create a default instance of ${clazz.simpleName}. " +
                            "Make sure the class has a no-args constructor or enable `requireFilled`."
                )
            }
            saveConfig(configName, config, folder)
            return config
        }

        return yaml.loadConfig(file.readText(), clazz)
    }

    override fun saveConfig(configName: String, config: Any, folder: File?) {
        val name = if (!configName.endsWith(".yml")) "$configName.yml" else configName
        val file = loadFile(name, folder)
        file.writeText(yaml.encodeToString(config))
    }



    override val components: Collection<Any>
        get() = emptyList()


    companion object {
        @JvmStatic
        val SHARED_CONTEXT by lazy {
            SimpleKoinContext().apply {
                startKoin { environmentProperties() }
            }
        }
    }
}
