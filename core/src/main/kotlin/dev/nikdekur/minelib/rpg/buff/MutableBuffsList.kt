@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.minelib.rpg.buff

import dev.nikdekur.minelib.rpg.condition.ConditionType
import dev.nikdekur.minelib.rpg.stat.RPGStat

interface MutableBuffsList : BuffsList {

    fun addBuff(data: RPGBuffData<*>)
    fun <T : Comparable<T>> addBuff(buff: RPGBuff<T>, parameters: BuffParameters = BuffParameters()): RPGBuffData<T>

    fun removeBuff(buff: RPGBuffData<*>)

    fun <C> updateConditional(type: ConditionType<C>, context: C)

    fun clear()

    fun onBuffAdd(buff: RPGBuffData<*>) {
        // Do nothing
    }
    fun onBuffRemove(buff: RPGBuffData<*>) {
        // Do nothing
    }
}


inline fun <T : Comparable<T>> MutableBuffsList.addBuff(stat: RPGStat<T>, value: T, parameters: BuffParameters = BuffParameters()) =
    addBuff(RPGBuff(stat, value), parameters)