package dev.nikdekur.minelib.rpg.buff

import dev.nikdekur.minelib.rpg.condition.Condition
import dev.nikdekur.minelib.rpg.condition.ConditionType
import dev.nikdekur.ndkore.map.MutableSetsMap
import dev.nikdekur.ndkore.map.add
import dev.nikdekur.ndkore.map.put
import dev.nikdekur.ndkore.map.remove
import java.util.UUID

open class MapMutableBuffsList : MapBuffsList(), MutableBuffsList {

    val conditionToBuffsMap: MutableSetsMap<ConditionType<*>, RPGBuffData<*>> = HashMap()

    protected fun <T : Comparable<T>> buildData(buff: RPGBuff<T>, parameters: BuffParameters): RPGBuffData<T> {
        return RPGBuffData(
            id = UUID.randomUUID(),
            buff = buff,
            System.currentTimeMillis(),
            parameters = parameters,
        )
    }

    fun apply(data: RPGBuffData<*>) {
        map.put(data.buff.stat, data.id, data)

        data.parameters.conditions.forEach {
            val type = it.key
            it.value.forEach {
                conditionToBuffsMap.add(type, data)
            }
        }
    }

    override fun addBuff(data: RPGBuffData<*>) {
        apply(data)
        onBuffAdd(data)
    }


    override fun <T : Comparable<T>> addBuff(buff: RPGBuff<T>, parameters: BuffParameters): RPGBuffData<T> {
        val data = buildData(buff, parameters)
        addBuff(data)
        return data
    }

     fun deApply(data: RPGBuffData<*>) {
        map.remove(data.buff.stat, data.id)
        data.parameters.conditions.forEach { entry ->
            val type = entry.key
            val conditions = entry.value
            conditions.forEach {
                conditionToBuffsMap.remove(type, data)
            }
        }
    }

    override fun removeBuff(buff: RPGBuffData<*>) {
        onBuffRemove(buff)
        deApply(buff)
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

            val met = states.allMet()
            if (met) apply(data)
            else deApply(data)
        }
    }


    override fun clear() {
        map.clear()
        conditionToBuffsMap.clear()
    }

    override fun toString(): String {
        return "MapMutableBuffsList(buffs=${toList()})"
    }
}
