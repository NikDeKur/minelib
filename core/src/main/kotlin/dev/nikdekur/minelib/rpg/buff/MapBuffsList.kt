package dev.nikdekur.minelib.rpg.buff

import dev.nikdekur.minelib.rpg.stat.RPGStat
import dev.nikdekur.ndkore.map.MutableMultiMap
import dev.nikdekur.ndkore.map.contains
import dev.nikdekur.ndkore.map.get
import java.util.UUID

open class MapBuffsList : BuffsList {
    val map: MutableMultiMap<RPGStat<*>, UUID, RPGBuffData<*>> = HashMap()

    override fun iterator(): Iterator<RPGBuffData<*>> {
        return map.values.flatMap { it.values }.iterator()
    }


    override fun <T : Comparable<T>> getBuffs(type: RPGStat<T>): Collection<RPGBuffData<T>> {
        val res = map[type] ?: return emptyList()
        @Suppress("UNCHECKED_CAST")
        return res.values as? Collection<RPGBuffData<T>> ?: emptyList()
    }

    override fun <T : Comparable<T>> getBuff(type: RPGStat<T>, id: UUID): RPGBuffData<T>? {
        @Suppress("UNCHECKED_CAST")
        return map[type, id] as? RPGBuffData<T>
    }



    override fun <T : Comparable<T>> getBuffValueOrNull(type: RPGStat<T>): T? {
        return getBuffs(type).fold(null) { acc, data ->
            if (acc == null)
                data.buff.value
            else
                type.plus(acc, data.buff.value)
        }
    }

    override fun <T : Comparable<T>> getBuffValue(type: RPGStat<T>): T {
        return getBuffValueOrNull(type) ?: type.defaultValue
    }

    override fun forEachStat(action: (RPGStat<*>) -> Unit) {
        map.keys.forEach(action)
    }

    override fun hasBuff(data: RPGBuffData<*>): Boolean {
        return map.contains(data.buff.stat, data.id)
    }


    override fun toString(): String {
        return "MapBuffsList(buffs=${toList()})"
    }
}