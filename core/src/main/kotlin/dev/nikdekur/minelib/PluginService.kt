package dev.nikdekur.minelib

import dev.nikdekur.minelib.koin.MineLibKoinComponent
import dev.nikdekur.minelib.plugin.ServerPlugin
import dev.nikdekur.ndkore.service.Service
import dev.nikdekur.ndkore.service.ServicesManager
import kotlin.reflect.KClass

interface PluginService : Service<ServerPlugin>, MineLibKoinComponent {

    override val manager: ServicesManager<ServerPlugin>
        get() = app.servicesManager

    val bindClass: KClass<*>
        get() = this::class
}