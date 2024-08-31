package dev.nikdekur.minelib.rpg.buff

import dev.nikdekur.minelib.rpg.condition.Condition
import dev.nikdekur.minelib.rpg.condition.ConditionType
import dev.nikdekur.ndkore.map.MutableSetsMap
import dev.nikdekur.ndkore.map.SetsMap
import dev.nikdekur.ndkore.map.add
import kotlinx.serialization.Serializable
import java.util.LinkedList

/**
 * Parameters for a buff.
 *
 * @param duration The duration of the buff in minecraft ticks (20 ticks = 1 second).
 * @param condition The condition that must be met for the buff to be active.
 */
@Serializable
data class BuffParameters(
    val duration: Long? = null,
    val conditions: SetsMap<ConditionType<*>, Condition<*>> = emptyMap()
) {
    companion object {
        val Empty by lazy {
            BuffParameters()
        }
    }
}

class BuffParametersBuilder {
    var duration: Long? = null
    val conditions by lazy { LinkedList<Condition<*>>() }

    fun condition(condition: Condition<*>) {
        conditions.add(condition)
    }

    fun build(): BuffParameters {
        val map: MutableSetsMap<ConditionType<*>, Condition<*>> = HashMap()
        conditions.forEach {
            map.add(it.cType, it)
        }
        return BuffParameters(duration, map)
    }
}

inline fun buffParameters(block: BuffParametersBuilder.() -> Unit): BuffParameters {
    return BuffParametersBuilder().apply(block).build()
}
