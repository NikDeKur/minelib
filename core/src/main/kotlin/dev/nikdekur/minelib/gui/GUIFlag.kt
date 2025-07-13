package dev.nikdekur.minelib.gui

interface GUIFlag {
    companion object {
        val CANNOT_TAKE = object : GUIFlag {}
        val CANNOT_PUT = object : GUIFlag {}
        val RETURN_ITEMS = object : GUIFlag {}
    }
}