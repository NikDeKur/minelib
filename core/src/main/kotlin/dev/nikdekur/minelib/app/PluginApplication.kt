package dev.nikdekur.minelib.app

import dev.nikdekur.minelib.command.api.ServerCommand
import dev.nikdekur.minelib.plugin.ServerPlugin
import dev.nikdekur.ndkore.scheduler.Scheduler
import dev.nikdekur.ornament.Application
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import java.io.File

interface PluginApplication : Application, ServerPlugin {
    val scheduler: Scheduler

    val onlinePlayers: Collection<Player>

    val components: Collection<Any>

    /**
     * Register a new plugin component.
     *
     * Function could register [Listener], [ServerCommand]
     *
     * @param component Component to register
     * @return has the component been registered?
     */
    fun registerComponent(component: Any): Boolean


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


    fun whenLoad() {
        // Override this method to do something when the plugin is loaded
    }

    fun whenEnabled() {
        // Override this method to do something when the plugin is enabled
    }
    fun whenDisabled() {
        // Override this method to do something when the plugin is disabled
    }

    fun whenStartReload() {
        // Override this method to do something when the plugin is disabled
    }

    fun whenFinishReload() {
        // Override this method to do something when the plugin is disabled
    }



//    /**
//     * Load a configuration file from the specified folder.
//     *
//     * Method ensures that the file and root folder exist.
//     *
//     * If [requireFilled] is false, the method will try to create a new configuration and save it to the file.
//     *
//     * @param configName Name of the configuration file to load
//     * @param clazz Class of the configuration
//     * @param requireFilled If true, the method will throw an exception if the configuration is empty
//     * @param folder Folder to load the configuration from. If null, the default plugin folder will be used.
//     * @return Loaded configuration
//     * @throws IllegalArgumentException If the configuration is empty and requireFilled is true
//     */
//    fun <T : Any> loadConfig(
//        configName: String,
//        clazz: Class<T>,
//        requireFilled: Boolean = false,
//        folder: File? = null
//    ): T
//
//    fun saveConfig(configName: String, config: Any, folder: File? = null)


}