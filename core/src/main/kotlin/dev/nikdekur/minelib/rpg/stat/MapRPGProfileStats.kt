@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.minelib.rpg.stat

import dev.nikdekur.minelib.ext.call
import dev.nikdekur.minelib.rpg.event.RPGStatChangeEvent
import dev.nikdekur.minelib.rpg.profile.RPGProfile
import dev.nikdekur.ndkore.ext.forEachSafe
import dev.nikdekur.ndkore.map.MutableListsMap
import java.util.LinkedList

class MapRPGProfileStats(val profile: RPGProfile) : RPGProfileStats {

    val beforeStatChangeProcessors: MutableListsMap<RPGStat<*>, StatChangeProcessor<*>> = LinkedHashMap()
    override fun <T : Comparable<T>> beforeStatChange(stat: RPGStat<T>, action: StatChangeProcessor<T>) {
        @Suppress("UNCHECKED_CAST")
        (beforeStatChangeProcessors.getOrPut(stat, ::LinkedList) as MutableList<StatChangeProcessor<T>>).add(action)
    }

    val afterStatChangeProcessors: MutableListsMap<RPGStat<*>, StatChangeProcessor<*>> = LinkedHashMap()
    override fun <T : Comparable<T>> afterStatChange(stat: RPGStat<T>, action: StatChangeProcessor<T>) {
        @Suppress("UNCHECKED_CAST")
        (afterStatChangeProcessors.getOrPut(stat, ::LinkedList) as MutableList<StatChangeProcessor<T>>).add(action)
    }


    private fun <T : Comparable<T>> processStatChange(before: Boolean, event: RPGStatChangeEvent<T>) {
        val stat = event.stat
        val processors = if (before)
            beforeStatChangeProcessors[stat]
        else
            afterStatChangeProcessors[stat]
        processors?.forEachSafe({e, _ -> e.printStackTrace()}) {
            @Suppress("UNCHECKED_CAST")
            val processor = it as StatChangeProcessor<T>
            processor.invoke(event)
        }
    }


    val map = StatsMap()

    override fun getRaw(stat: RPGStat<*>): Any? {
        return map.getRaw(stat)
    }

    override fun <T : Comparable<T>> getOrNull(stat: RPGStat<T>): T? {
        return map.getOrNull(stat)
    }

    override operator fun <T : Comparable<T>> get(stat: RPGStat<T>): T {
        return map[stat]
    }

    override operator fun <T : Comparable<T>> set(stat: RPGStat<T>, newValue: T): Boolean {
        val oldValue = map[stat]
        if (oldValue == newValue) return false

        val event = RPGStatChangeEvent(profile, stat, oldValue, newValue)
        processStatChange(true, event)
        if (!event.isCancelled) event.call()
        else return false

        val value = event.newValue
        map[stat] = value

        processStatChange(false, event)

        return true
    }

    override fun clear() {
        map.clear()
    }


    override fun toString(): String {
        return map.toString()
    }
}