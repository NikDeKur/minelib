package dev.nikdekur.minelib.service

import dev.nikdekur.minelib.plugin.ServerPlugin
import dev.nikdekur.ndkore.service.Service
import kotlin.reflect.KClass

interface PluginService : Service<ServerPlugin>, PluginComponent {

    val bindClass: KClass<out Service<*>>
}