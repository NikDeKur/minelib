package dev.nikdekur.minelib.service

import dev.nikdekur.minelib.plugin.ServerPlugin
import dev.nikdekur.ndkore.service.ServicesComponent
import dev.nikdekur.ndkore.service.ServicesManager

interface PluginComponent : ServicesComponent {
    val app: ServerPlugin

    override val manager: ServicesManager
        get() = app.servicesManager
}