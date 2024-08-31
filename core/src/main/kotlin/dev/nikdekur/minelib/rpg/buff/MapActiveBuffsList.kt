package dev.nikdekur.minelib.rpg.buff

import dev.nikdekur.minelib.rpg.condition.Condition
import dev.nikdekur.minelib.rpg.condition.ConditionType
import dev.nikdekur.minelib.rpg.stat.RPGStat
import dev.nikdekur.ndkore.map.MutableSetsMap
import dev.nikdekur.ndkore.map.add
import dev.nikdekur.ndkore.map.put
import dev.nikdekur.ndkore.map.remove
import java.util.UUID

open class MapActiveBuffsList : MapBuffsList(), ActiveBuffsList {

    val conditionToBuffsMap: MutableSetsMap<ConditionType<*>, RPGBuffData> = HashMap()

    protected fun <T : Comparable<T>> buildData(stat: RPGStat<T>, value: T, parameters: BuffParameters): RPGBuffData {
        return RPGBuffData(
            id = UUID.randomUUID(),
            buff = RPGBuff(stat, value),
            parameters = parameters,
            System.currentTimeMillis()
        )
    }

    protected open fun addBuffData(data: RPGBuffData) {
        map.put(data.buff.stat, data.id, data)

        data.parameters.conditions.forEach {
            val type = it.key
            it.value.forEach {
                conditionToBuffsMap.add(type, data)
            }
        }
    }

    override fun <T : Comparable<T>> addBuff(stat: RPGStat<T>, value: T, parameters: BuffParameters): RPGBuffData {
        val data = buildData(stat, value, parameters)
        addBuffData(data)
        return data
    }

    override fun removeBuff(data: RPGBuffData) {
        map.remove(data.buff.stat, data.id)
        data.parameters.conditions.forEach { entry ->
            val type = entry.key
            val conditions = entry.value
            conditions.forEach {
                conditionToBuffsMap.remove(type, data)
            }
        }
    }


    override fun <C> updateConditional(type: ConditionType<C>, context: C) {
        conditionToBuffsMap[type]?.forEach { data ->
            val conditions = data.parameters.conditions

            val states = data.conditionsState

            conditions.forEach { entry ->
                val type = entry.key
                val conditions = entry.value

                if (!type.contextClass.isInstance(context)) return

                conditions.forEach { condition ->
                    @Suppress("UNCHECKED_CAST")
                    condition as Condition<C>
                    if (condition.isSatisfied(context))
                        states.doMet(condition)
                    else
                        states.doNotMet(condition)
                }
            }

            if (states.allMet())
                addBuffData(data)
            else
                removeBuff(data)
        }
    }


    override fun clear() {
        map.clear()
        conditionToBuffsMap.clear()
    }
}
