@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.minelib.plugin

import dev.nikdekur.minelib.command.api.ServerCommand
import dev.nikdekur.minelib.scheduler.DelegatePluginScheduler
import dev.nikdekur.minelib.scheduler.PluginScheduler
import dev.nikdekur.minelib.utils.ClassUtils
import dev.nikdekur.ndkore.ext.asKLogger
import dev.nikdekur.ndkore.ext.format
import dev.nikdekur.ndkore.scheduler.Scheduler
import io.github.oshai.kotlinlogging.KLogger
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import java.io.IOException
import kotlin.properties.Delegates
import kotlin.time.TimeSource
import kotlin.time.measureTimedValue


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
open class BaseServerPlugin : JavaPlugin(), ServerPlugin {

    override val clazzLoader: ClassLoader
        get() = classLoader

    override var bukkitScheduler: PluginScheduler by Delegates.notNull()

    /**
     * Private set of listeners provided by the plugin.
     *
     * The field is used
     * to register all listeners when the plugin is loaded and unregister all listeners when the plugin is unloaded.
     */
    private val _listeners: MutableSet<Listener> = HashSet()
    override val listeners: Set<Listener>
        get() = _listeners


    val kLogger: KLogger by lazy { logger.asKLogger() }


    override fun onEnable() {
        saveDefaultConfig()

        finishReload()

        whenEnabled()
    }



    override fun onDisable() {
        startReload()

        whenDisabled()
    }



    override fun addListener(listener: Listener) {
        if (listener in _listeners) return
        _listeners.add(listener)
    }

    override fun addCommand(command: ServerCommand) {
        val name = command.name
        try {
            val bCommand = getCommand(name) ?: run {
                kLogger.error { "Command '$name' not found. Maybe you forgot to register it?" }
                return
            }
            bCommand.executor = command
            bCommand.tabCompleter = command
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }



    /**
     * Internal function called before the plugin is reloaded.
     *
     * Executes [whenStartReload] and executes all necessary actions to prepare the plugin for reloading.
     */
    open fun startReload() {
        whenStartReload()

        // Unregister all listeners that are linked to the plugin
        HandlerList.unregisterAll(this)
    }

    /**
     * Internal function called after the plugin is reloaded.
     *
     * Executes [whenFinishReload] and executes all necessary actions to prepare the plugin after reloading.
     */
    open fun finishReload() {
        whenFinishReload()

        // Register all listeners
        _listeners.forEach(::registerListener)
    }

    private fun registerListener(listener: Listener) {
        server.pluginManager.registerEvents(listener, this)
    }






    override fun onLoad() {
        loadAllPluginClasses()

        bukkitScheduler = DelegatePluginScheduler(this)
        whenLoad()
    }

    open fun whenLoad() {
        // Override this method to do something when the plugin is loaded
    }

    open fun whenEnabled() {
        // Override this method to do something when the plugin is enabled
    }
    open fun whenDisabled() {
        // Override this method to do something when the plugin is disabled
    }

    open fun whenStartReload() {
        // Override this method to do something when the plugin is disabled
    }

    open fun whenFinishReload() {
        // Override this method to do something when the plugin is disabled
    }


    open val preLoadingClassesWL: Set<String> = emptySet()
    open val preLoadingClassesBL: Set<String> = emptySet()
    protected fun loadAllPluginClasses() {
        val data = TimeSource.Monotonic.measureTimedValue {
            try {
                ClassUtils.loadAllClassesFromJar(classLoader, file) { className ->
                    return@loadAllClassesFromJar when {
                        preLoadingClassesBL.contains(className) -> false

                        // Exclude NMS classes
                        className.contains("v1_") -> false

                        // Allow loading any nikdekur's classes
                        className.startsWith("dev.nikdekur.") -> true

                        preLoadingClassesWL.contains(className) -> true
                        else -> false
                    }
                }
                true
            } catch (e: IOException) {
                kLogger.warn(e) { "Could not load classes from jar file" }
                false
            }
        }


        if (data.value) {
            val time = data.duration.inWholeMilliseconds.format(2)
            kLogger.info { "Loaded all plugin classes in $time ms" }
        }
    }







}
