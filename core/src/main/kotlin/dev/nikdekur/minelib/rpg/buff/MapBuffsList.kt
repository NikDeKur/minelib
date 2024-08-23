package dev.nikdekur.minelib.rpg.buff

import dev.nikdekur.minelib.rpg.stat.RPGStat
import dev.nikdekur.ndkore.map.MutableMultiMap
import dev.nikdekur.ndkore.map.contains
import dev.nikdekur.ndkore.map.get
import java.util.UUID

open class MapBuffsList : BuffsList {
    val map: MutableMultiMap<RPGStat<*>, UUID, RPGBuff<*>> = HashMap()

    override fun iterator(): Iterator<RPGBuff<*>> {
        return map.values.flatMap { it.values }.iterator()
    }


    override fun <T : Comparable<T>> getBuffs(type: RPGStat<T>): Collection<RPGBuff<T>> {
        val res = map[type] ?: return emptyList()
        @Suppress("UNCHECKED_CAST")
        return res.values as? Collection<RPGBuff<T>> ?: emptyList()
    }

    override fun <T : Comparable<T>> getBuff(type: RPGStat<T>, id: UUID): RPGBuff<T>? {
        @Suppress("UNCHECKED_CAST")
        return map[type, id] as? RPGBuff<T>
    }



    override fun <T : Comparable<T>> getBuffValueOrNull(type: RPGStat<T>): T? {
        var value: T? = null
        val buffs = getBuffs(type)
        for (buff in buffs) {
            value = if (value == null) {
                buff.value
            } else {
                type.plus(value, buff.value)
            }
        }
        return value
    }

    override fun <T : Comparable<T>> getBuffValue(type: RPGStat<T>): T {
        return getBuffValueOrNull(type) ?: type.defaultValue
    }

    override fun forEachStat(action: (RPGStat<*>) -> Unit) {
        map.keys.forEach(action)
    }

    override fun hasBuff(type: RPGBuff<*>): Boolean {
        return map.contains(type.stat, type.id)
    }

}