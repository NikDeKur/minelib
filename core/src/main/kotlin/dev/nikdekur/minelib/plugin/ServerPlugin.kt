package dev.nikdekur.minelib.plugin

import dev.nikdekur.minelib.command.api.ServerCommand
import dev.nikdekur.minelib.scheduler.Scheduler
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin

interface ServerPlugin : Plugin {
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


    fun addCommand(command: ServerCommand)
}


