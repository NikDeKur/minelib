package dev.nikdekur.minelib.plugin

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import dev.nikdekur.minelib.PluginModule
import dev.nikdekur.minelib.command.ServerCommand
import dev.nikdekur.minelib.ext.nanosToMs
import dev.nikdekur.minelib.ext.warning
import dev.nikdekur.minelib.nms.VersionAdapter
import dev.nikdekur.minelib.scheduler.Scheduler
import dev.nikdekur.minelib.utils.ClassUtils
import dev.nikdekur.minelib.utils.ClassUtils.logger
import dev.nikdekur.ndkore.ext.forEachSafe
import dev.nikdekur.ndkore.ext.format
import dev.nikdekur.ndkore.ext.loadConfig
import dev.nikdekur.ndkore.ext.tryEverything
import dev.nikdekur.ndkore.extra.Tools.jarFile
import dev.nikdekur.ndkore.module.Module
import dev.nikdekur.ndkore.module.ModulesManager
import dev.nikdekur.ndkore.reflect.ClassFinder
import dev.nikdekur.ndkore.reflect.Reflect
import kotlinx.serialization.encodeToString
import org.bukkit.Bukkit
import org.bukkit.command.PluginCommand
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.IOException
import java.util.function.Predicate
import java.util.logging.Level
import java.util.logging.Logger
import kotlin.properties.Delegates


@Suppress("unused")
abstract class ServerPlugin : JavaPlugin() {



    /**
     * Returns scheduler wrapper for this plugin
     *
     * It's not a global scheduler, it's a scheduler for this plugin
     * that uses global scheduler with this plugin instance.
     *
     * All plugin tasks would be automatically cancelled straight after [beforeReload].
     *
     * @return [Scheduler]
     */
    var scheduler: Scheduler by Delegates.notNull()
        private set

    /**
     * Returns [ModulesManager] for this plugin.
     *
     * It's a manager for all [PluginModule].
     * It can be used for getting modules / registering modules tasks.
     *
     * Modules must be registered in [components] property.
     */
    lateinit var modulesManager: ModulesManager<ServerPlugin>

    /**
     * Private set of listeners provided by the plugin.
     *
     * The field is used
     * to register all listeners when the plugin is loaded and unregister all listeners when the plugin is unloaded.
     */
    private val listeners: MutableSet<Listener> = HashSet()

    val loader: ClassLoader = classLoader

    override fun onEnable() {
        modulesManager = ModulesManager()

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
        this@ServerPlugin.scheduler.cancelTasks()

        // Unregister absolutely all listeners
        HandlerList.unregisterAll(this)

        modulesManager.unloadAll()
        modulesManager.tasks.clear()
    }

    /**
     * Internal function called after the plugin is reloaded.
     *
     * Executes [afterReload] and executes all necessary actions to prepare the plugin after reloading.
     */
    open fun stopReload() {
        modulesManager.loadAll()

        // Register all listeners
        listeners.forEach(::registerListener)

        afterReload()
    }




            /**
     * Internal function called once the plugin is enabled to register all components.
     *
     * Function go through [components] and [defaultComponents] and register them via [registerComponent] function.
     */
    private fun registerComponents() {
        defaultComponents.forEachSafe(
            {e, el -> logger.warning("Failed to register default component $el", e) },
            ::registerComponent
        )

        components.forEachSafe(
            {e, el -> logger.warning("Failed to register component $el", e) },
            ::registerComponent
        )
    }


    /**
     * Register a new plugin component.
     *
     * Function could register [Listener], [ServerCommand] or [PluginModule].
     *
     * @param component Component to register
     * @return has the component been registered?
     */
    open fun registerComponent(component: Any): Boolean {
        var registered = false

        if (component is Listener && !listeners.contains(component)) {
            addListener(component)
            registered = true
        }

        if (component is ServerCommand) {
            component.register(this)
            registered = true
        }

        if (component is Module<*>) {
            component.app as? ServerPlugin ?: return false
            @Suppress("UNCHECKED_CAST")
            modulesManager.addModule(component as PluginModule)
            registered = true
        }

        return registered
    }

    override fun onLoad() {
        loadAllPluginClasses()
        this@ServerPlugin.scheduler = Scheduler(this)
        setupStaticFields()
        whenLoad()
    }

    /**
     * Try to set the static fields of the plugin.
     *
     * Function is called in [onLoad] method.
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
    fun reload() {
        isReloading = true
        startReload()
        stopReload()
        isReloading = false
    }


    open val preLoadingClassesWL: Set<String> = emptySet()
    open val preLoadingClassesBL: Set<String> = emptySet()
    private fun loadAllPluginClasses() {
        val startNanos = System.nanoTime()
        val success: Boolean = try {
            ClassUtils.loadAllClassesFromJar(classLoader, file) { className ->
                return@loadAllClassesFromJar when {
                    preLoadingClassesBL.contains(className) -> false
                    className.startsWith("dev.nikdekur.") -> true
                    preLoadingClassesWL.contains(className) -> true
                    else -> false
                }
            }
            true
        } catch (e: IOException) {
            logger.log(
                Level.WARNING, "Could not load classes from jar file '" + jarFile.path
                        + "'.", e
            )
            false
        }

        if (success) {
            val time = (System.nanoTime() - startNanos).nanosToMs().format(2)
            logger.info("Loaded all plugin classes in $time ms")
        }
    }

    private fun getNDKCommand(name: String): PluginCommand? {
        return super.getCommand(name) ?: run {
            logger.warning("Command '$name' not found. Maybe you forgot to register it?")
            return null
        }
    }

    protected fun registerCommand(name: String, executor: ServerCommand) {
        try {
            val command = getNDKCommand(name) ?: return
            command.executor = executor
            command.tabCompleter = executor
        } catch (e: NoSuchElementException) {
            e.printStackTrace()
        }
    }

    /**
     * Add a new listener to the plugin.
     *
     * The listener will be registered when the plugin is finished reloading
     * and unregistered when the plugin is starting to reload.
     *
     * Modules have to use this function to add listeners.
     *
     * Note: This function does not register the listener, registering would be performed in [stopReload].
     * Usually, you don't need to worry about it. Add a listener and it will be registered automatically.
     *
     * @param listener Listener to add
     */
    fun addListener(listener: Listener) {
        listeners.add(listener)
    }


    private fun registerListener(listener: Listener) {
        server.pluginManager.registerEvents(listener, this)
    }



    fun loadFile(fileName: String, folder: File = dataFolder): File {
        return try {
            folder.mkdirs()
            val file = File(folder, fileName)
            if (!file.exists()) file.createNewFile()
            file
        } catch (e: Throwable) {
            throw RuntimeException(e)
        }
    }

    val yaml by lazy {
        Yaml(
            configuration = YamlConfiguration(
                strictMode = false
            )
        )
    }

    inline fun <reified T> loadConfig(configName: String, folder: File = dataFolder): T {
        val file = if (!configName.endsWith(".yml"))
            loadFile("$configName.yml", folder)
        else
            loadFile(configName, folder)

        //Check if the file is empty
        if (file.length() == 0L) {
            val config = T::class.java.newInstance()
            saveConfig(configName, config, folder)
            return config
        }

       return yaml.loadConfig<T>(file)
    }

    inline fun <reified T> saveConfig(configName: String, config: T, folder: File = dataFolder) {
        val file = if (!configName.endsWith(".yml"))
            loadFile("$configName.yml", folder)
        else
            loadFile(configName, folder)

        file.writeText(yaml.encodeToString(config))
    }

    fun loadDir(name: String): File {
        val dir = File(dataFolder, name)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

    open val components: Collection<Any> = emptyList()




    fun newClassFinder(packageName: String, filter: Predicate<Class<*>>): ClassFinder {
        return ClassFinder.new(packageName, classLoader, filter)
    }

    private val defaultComponents by lazy {
        listOf<PluginModule>()
    }


    companion object {


        val versionAdapter by lazy {
            val packageName = Bukkit.getServer().javaClass.`package`.name
            val versionStr = packageName.substring(packageName.lastIndexOf('.') + 1)
            logger.log(Level.INFO, "Version: $versionStr");
            VersionAdapter.getForVersion(versionStr)
        }

    }
}
