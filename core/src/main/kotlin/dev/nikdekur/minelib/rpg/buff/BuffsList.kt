@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.minelib.rpg.buff

import dev.nikdekur.minelib.rpg.stat.RPGStat
import java.util.*

interface BuffsList : Iterable<RPGBuffData<*>> {

    fun <T : Comparable<T>> getBuffs(type: RPGStat<T>): Collection<RPGBuffData<T>>
    fun <T : Comparable<T>> getBuff(type: RPGStat<T>, id: UUID): RPGBuffData<T>?

    fun <T : Comparable<T>> getBuffValueOrNull(type: RPGStat<T>): T?
    fun <T : Comparable<T>> getBuffValue(type: RPGStat<T>): T

    fun hasBuff(type: RPGBuffData<*>): Boolean

    fun forEachStat(action: (RPGStat<*>) -> Unit)

    object Empty : BuffsList {
        override fun <T : Comparable<T>> getBuffs(type: RPGStat<T>): Collection<RPGBuffData<T>> = emptyList()
        override fun <T : Comparable<T>> getBuff(type: RPGStat<T>, id: UUID): RPGBuffData<T>? = null
        override fun <T : Comparable<T>> getBuffValueOrNull(type: RPGStat<T>): T? = null
        override fun <T : Comparable<T>> getBuffValue(type: RPGStat<T>): T = type.defaultValue
        override fun hasBuff(type: RPGBuffData<*>) = false
        override fun forEachStat(action: (RPGStat<*>) -> Unit) { /* Do nothing */ }
        override fun iterator() = emptyList<RPGBuffData<*>>().iterator()

        override fun toString() = "EmptyBuffsList"
    }
}



