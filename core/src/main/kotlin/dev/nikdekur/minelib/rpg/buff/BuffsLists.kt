@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.minelib.rpg.buff

import dev.nikdekur.minelib.rpg.stat.RPGStat
import java.util.*

interface BuffsList : Iterable<RPGBuff<*>> {

    fun <T : Comparable<T>> getBuffs(type: RPGStat<T>): Collection<RPGBuff<T>>
    fun <T : Comparable<T>> getBuff(type: RPGStat<T>, id: UUID): RPGBuff<*>?

    fun <T : Comparable<T>> getBuffValueOrNull(type: RPGStat<T>): T?
    fun <T : Comparable<T>> getBuffValue(type: RPGStat<T>): T

    fun hasBuff(type: RPGBuff<*>): Boolean

    fun forEachStat(action: (RPGStat<*>) -> Unit)

    object Empty : BuffsList {
        override fun <T : Comparable<T>> getBuffs(type: RPGStat<T>): Collection<RPGBuff<T>> = emptyList()
        override fun <T : Comparable<T>> getBuff(type: RPGStat<T>, id: UUID): RPGBuff<T>? = null
        override fun <T : Comparable<T>> getBuffValueOrNull(type: RPGStat<T>): T? = null
        override fun <T : Comparable<T>> getBuffValue(type: RPGStat<T>): T = type.defaultValue
        override fun hasBuff(type: RPGBuff<*>) = false
        override fun forEachStat(action: (RPGStat<*>) -> Unit) {}
        override fun iterator() = emptyList<RPGBuff<*>>().iterator()
    }
}



