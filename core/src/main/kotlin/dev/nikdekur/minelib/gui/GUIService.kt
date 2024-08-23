package dev.nikdekur.minelib.gui

import dev.nikdekur.minelib.service.PluginService

interface GUIService : PluginService {
    fun registerGUI(gui: GUI)
    fun unregisterGUI(gui: GUI)
}