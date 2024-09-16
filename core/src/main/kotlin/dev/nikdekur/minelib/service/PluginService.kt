package dev.nikdekur.minelib.service

import dev.nikdekur.ndkore.service.Service
import kotlin.reflect.KClass

abstract class PluginService : Service(), PluginComponent {

    abstract val bindClass: KClass<out Any>
}