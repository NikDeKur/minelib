package dev.nikdekur.minelib.plugin

import dev.nikdekur.minelib.command.api.ServerCommand
import dev.nikdekur.minelib.scheduler.Scheduler
import dev.nikdekur.minelib.service.PluginService
import dev.nikdekur.ndkore.service.ServicesManager
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin
import java.io.File
import kotlin.time.Duration

interface ServerPlugin : Plugin {

    val onlinePlayers: Collection<Player>

    val uptime: Duration

    /**
     * Returns [ServicesManager] for this plugin.
     *
     * It's a manager for all [PluginService].
     * It can be used for getting or registering services.
     */
    val servicesManager: ServicesManager

    val components: Collection<Any>

    /**
     * Register a new plugin component.
     *
     * Function could register [Listener], [ServerCommand] or [PluginService].
     *
     * @param component Component to register
     * @return has the component been registered?
     */
    fun registerComponent(component: Any): Boolean

    /**
     * Returns scheduler wrapper for this plugin
     *
     * It's not a global scheduler, it's a scheduler for this plugin
     * that uses global scheduler with this plugin instance.
     *
     * All plugin tasks would be automatically cancelled straight after reloading.
     *
     * @return [Scheduler]
     */
    val scheduler: Scheduler

    /**
     * Returns the class loader for this plugin.
     *
     * It's a class loader that loads classes from the plugin jar.
     */
    val clazzLoader: ClassLoader

    val listeners: Collection<Listener>

    /**
     * Add a new listener to the plugin.
     *
     * The listener will be registered when the plugin is finished reloading
     * and unregistered when the plugin is starting to reload.
     *
     * Modules have to use this function to add listeners.
     *
     * Note: This function does not register the listener, registering would be performed after reloading.
     * Usually, you don't need to worry about it. Add a listener and it will be registered automatically.
     *
     * @param listener Listener to add
     */
    fun addListener(listener: Listener)

    fun reload()


    fun loadDirectory(name: String): File

    /**
     * Load a file from the specified folder.
     *
     * Method ensures that the file and root folder exist.
     *
     * @param fileName Name of the file to load
     * @param folder Folder to load the file from. If null, the default plugin folder will be used.
     * @return Loaded file
     */
    fun loadFile(fileName: String, folder: File? = null): File


    /**
     * Load a configuration file from the specified folder.
     *
     * Method ensures that the file and root folder exist.
     *
     * If [requireFilled] is false, the method will try to create a new configuration and save it to the file.
     *
     * @param configName Name of the configuration file to load
     * @param clazz Class of the configuration
     * @param requireFilled If true, the method will throw an exception if the configuration is empty
     * @param folder Folder to load the configuration from. If null, the default plugin folder will be used.
     * @return Loaded configuration
     * @throws IllegalArgumentException If the configuration is empty and requireFilled is true
     */
    fun <T : Any> loadConfig(
        configName: String,
        clazz: Class<T>,
        requireFilled: Boolean = false,
        folder: File? = null
    ): T

    fun saveConfig(configName: String, config: Any, folder: File? = null)
}


/**
 * Load a configuration file from the specified folder.
 *
 * Method ensures that the file and root folder exist.
 *
 * If [requireFilled] is false, the method will try to create a new configuration and save it to the file.
 *
 * @param T Type of the configuration
 * @param configName Name of the configuration file to load
 * @param requireFilled If true, the method will throw an exception if the configuration is empty
 * @param folder Folder to load the configuration from. If null, the default plugin folder will be used.
 * @return Loaded configuration
 * @throws IllegalArgumentException If the configuration is empty and requireFilled is true
 */
inline fun <reified T : Any> ServerPlugin.loadConfig(
    configName: String,
    requireFilled: Boolean = false,
    folder: File? = null
): T = loadConfig(configName, T::class.java, requireFilled, folder)