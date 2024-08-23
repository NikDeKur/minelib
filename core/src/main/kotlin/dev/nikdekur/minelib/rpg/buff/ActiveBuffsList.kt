@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.minelib.rpg.buff

import dev.nikdekur.minelib.rpg.condition.ConditionType
import dev.nikdekur.minelib.rpg.stat.RPGStat

interface ActiveBuffsList : BuffsList {

    fun addBuff(buff: RPGBuff<*>)

    fun removeBuff(buff: RPGBuff<*>)

    fun updateConditional(type: ConditionType)

    fun clear()

    fun <T : Comparable<T>> afterAddBuff(buff: RPGBuff<T>) {
        // Do nothing
    }
    fun <T : Comparable<T>> afterRemoveBuff(buff: RPGBuff<T>) {
        // Do nothing
    }
}

inline fun ActiveBuffsList.addBuffs(buffs: Iterable<RPGBuff<*>>) = buffs.forEach(::addBuff)
inline fun ActiveBuffsList.addBuffs(buffs: BuffsList) = buffs.forEach(::addBuff)

inline fun <T : Comparable<T>> ActiveBuffsList.removeBuffs(stat: RPGStat<T>): Collection<RPGBuff<T>> {
    return getBuffs(stat).also {
        it.forEach(this::removeBuff)
    }
}