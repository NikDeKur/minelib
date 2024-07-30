package dev.nikdekur.minelib

import dev.nikdekur.minelib.plugin.ServerPlugin
import dev.nikdekur.ndkore.module.Module

typealias PluginModule = Module<ServerPlugin>

interface MineLibModule : PluginModule {

    override val app: ServerPlugin
        get() = MineLib.instance
}