@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.minelib.rpg.stat

import dev.nikdekur.minelib.rpg.event.RPGStatChangeEvent
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

interface RPGProfileStats {

    fun <T : Comparable<T>> beforeStatChange(stat: RPGStat<T>, action: StatChangeProcessor<T>)
    fun <T : Comparable<T>> afterStatChange(stat: RPGStat<T>, action: StatChangeProcessor<T>)

    fun getRaw(stat: RPGStat<*>): Any?
    fun <T : Comparable<T>> getOrNull(stat: RPGStat<T>): T?
    operator fun <T : Comparable<T>> get(stat: RPGStat<T>): T


    /**
     * Set the stat value
     *
     * Stat change can be failed either:
     * - The new value is the same as the old value
     * - Default stat processors cancelled the event
     * - The event is cancelled
     *
     * @param stat The stat to set
     * @param newValue The new value
     * @return True if the value was changed
     * @see beforeStatChange
     * @see RPGStatChangeEvent
     */
    operator fun <T : Comparable<T>> set(stat: RPGStat<T>, newValue: T): Boolean


    fun clear()
}


inline fun <T : Comparable<T>> RPGProfileStats.add(stat: RPGStat<T>, value: T): Boolean {
    val old = getOrNull(stat)

    val new =
        if (old == null) value
        else stat.plus(old, value)

    return set(stat, new)
}

inline fun <T : Comparable<T>> RPGProfileStats.take(stat: RPGStat<T>, value: T): Boolean {
    val old = getOrNull(stat) ?: return false
    val new = stat.minus(old, value)
    return set(stat, new)
}


inline fun <T : Comparable<T>> RPGProfileStats.boundVar(stat: RPGStat<T>): ReadWriteProperty<Any, T> {
    return object : ReadWriteProperty<Any, T> {
        override fun getValue(thisRef: Any, property: KProperty<*>) = get(stat)
        override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
            set(stat, value)
        }
    }
}