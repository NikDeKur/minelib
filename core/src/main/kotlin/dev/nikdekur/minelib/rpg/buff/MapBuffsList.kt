package dev.nikdekur.minelib.rpg.buff

import dev.nikdekur.minelib.rpg.stat.RPGStat
import dev.nikdekur.ndkore.map.MutableMultiMap
import dev.nikdekur.ndkore.map.contains
import dev.nikdekur.ndkore.map.get
import java.util.UUID

open class MapBuffsList : BuffsList {
    val map: MutableMultiMap<RPGStat<*>, UUID, RPGBuffData> = HashMap()

    override fun iterator(): Iterator<RPGBuffData> {
        return map.values.flatMap { it.values }.iterator()
    }


    override fun <T : Comparable<T>> getBuffs(type: RPGStat<T>): Collection<RPGBuffData> {
        val res = map[type] ?: return emptyList()
        @Suppress("UNCHECKED_CAST")
        return res.values as? Collection<RPGBuffData> ?: emptyList()
    }

    override fun <T : Comparable<T>> getBuff(type: RPGStat<T>, id: UUID): RPGBuffData? {
        return map[type, id]
    }



    override fun <T : Comparable<T>> getBuffValueOrNull(type: RPGStat<T>): T? {
        var value: T? = null
        val buffsData = getBuffs(type)
        for (data in buffsData) {
            value = if (value == null) {
                data.buff.value
            } else {
                type.plus(value, data.buff.value as T)
            } as T
        }
        return value
    }

    override fun <T : Comparable<T>> getBuffValue(type: RPGStat<T>): T {
        return getBuffValueOrNull(type) ?: type.defaultValue
    }

    override fun forEachStat(action: (RPGStat<*>) -> Unit) {
        map.keys.forEach(action)
    }

    override fun hasBuff(data: RPGBuffData): Boolean {
        return map.contains(data.buff.stat, data.id)
    }

}