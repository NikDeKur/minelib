@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.minelib.rpg.buff

import dev.nikdekur.minelib.rpg.condition.ConditionType
import dev.nikdekur.minelib.rpg.stat.RPGStat

interface ActiveBuffsList : BuffsList {

    fun <T : Comparable<T>> addBuff(stat: RPGStat<T>, value: T, parameters: BuffParameters = BuffParameters()): RPGBuffData

    fun removeBuff(buff: RPGBuffData)

    fun <C> updateConditional(type: ConditionType<C>, context: C)

    fun clear()

    fun afterAddBuff(buff: RPGBuffData) {
        // Do nothing
    }
    fun afterRemoveBuff(buff: RPGBuffData) {
        // Do nothing
    }
}