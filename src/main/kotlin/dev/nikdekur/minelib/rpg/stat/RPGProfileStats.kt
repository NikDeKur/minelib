@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.minelib.rpg.stat

import dev.nikdekur.minelib.ext.call
import dev.nikdekur.minelib.rpg.event.RPGStatChangeEvent
import dev.nikdekur.minelib.rpg.profile.RPGProfile
import dev.nikdekur.ndkore.ext.forEachSafe
import dev.nikdekur.ndkore.map.list.ListsHashMap
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class RPGProfileStats(val profile: RPGProfile) {

    val beforeStatChangeProcessors = ListsHashMap<RPGStat<*>, StatChangeProcessor<*>>()
    fun <T : Comparable<T>> beforeStatChange(stat: RPGStat<T>, action: StatChangeProcessor<T>) {
        @Suppress("UNCHECKED_CAST")
        (beforeStatChangeProcessors[stat] as MutableList<StatChangeProcessor<T>>).add(action)
    }

    val afterStatChangeProcessors = ListsHashMap<RPGStat<*>, StatChangeProcessor<*>>()
    fun <T : Comparable<T>> afterStatChange(stat: RPGStat<T>, action: StatChangeProcessor<T>) {
        @Suppress("UNCHECKED_CAST")
        (afterStatChangeProcessors[stat] as MutableList<StatChangeProcessor<T>>).add(action)
    }


    private fun <T : Comparable<T>> processStatChange(before: Boolean, event: RPGStatChangeEvent<T>) {
        val stat = event.stat
        val processors = if (before)
            beforeStatChangeProcessors[stat]
        else
            afterStatChangeProcessors[stat]
        processors.forEachSafe({e, _ -> e.printStackTrace()}) {
            @Suppress("UNCHECKED_CAST")
            val processor = it as StatChangeProcessor<T>
            processor.invoke(event)
        }
    }


    val map = StatsMap()

    inline fun getRaw(stat: RPGStat<*>): Any? {
        return map.getRaw(stat)
    }

    inline fun <T : Comparable<T>> getOrNull(stat: RPGStat<T>): T? {
        return map.getOrNull(stat)
    }

    inline operator fun <T : Comparable<T>> get(stat: RPGStat<T>): T {
        return map[stat]
    }

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
    operator fun <T : Comparable<T>> set(stat: RPGStat<T>, newValue: T): Boolean {
        val oldValue = map[stat]
        if (oldValue == newValue) return false

        val event = RPGStatChangeEvent(profile, stat, oldValue, newValue)
        processStatChange(true, event)
        if (!event.isCancelled) event.call()
        if (event.isCancelled) return false

        val value = event.newValue
        map[stat] = value

        processStatChange(false, event)

        return true
    }


    fun <T : Comparable<T>> add(stat: RPGStat<T>, value: T): Boolean {
        val old = this[stat]
        val new = stat.plus(old, value)
        return set(stat, new)
    }

    fun <T : Comparable<T>> take(stat: RPGStat<T>, value: T): Boolean {
        val old = this[stat]
        val new = stat.minus(old, value)
        return set(stat, new)
    }


    fun <T : Comparable<T>> boundVar(stat: RPGStat<T>): ReadWriteProperty<Any, T> {
        return object : ReadWriteProperty<Any, T> {
            override fun getValue(thisRef: Any, property: KProperty<*>) = get(stat)
            override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
                set(stat, value)
            }
        }
    }


    fun clear() {
        map.clear()
    }
}