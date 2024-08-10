package dev.nikdekur.minelib.gui

interface GUIService {
    fun registerGUI(gui: GUI)
    fun unregisterGUI(gui: GUI)
}