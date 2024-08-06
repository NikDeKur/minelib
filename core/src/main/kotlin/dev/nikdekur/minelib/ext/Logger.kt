@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.minelib.ext

import java.util.logging.Level
import java.util.logging.Logger

inline fun Logger.warning(message: String, e: Throwable) {
    this.log(Level.WARNING, message, e)
}

inline fun Logger.severe(message: String, e: Throwable) {
    this.log(Level.SEVERE, message, e)
}