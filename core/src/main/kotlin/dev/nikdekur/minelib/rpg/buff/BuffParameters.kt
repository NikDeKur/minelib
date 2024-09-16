@file:OptIn(ExperimentalSerializationApi::class)

package dev.nikdekur.minelib.rpg.buff

import dev.nikdekur.minelib.rpg.condition.Condition
import dev.nikdekur.minelib.rpg.condition.ConditionType
import dev.nikdekur.ndkore.ext.CompAny
import dev.nikdekur.ndkore.map.MutableSetsMap
import dev.nikdekur.ndkore.map.SetsMap
import dev.nikdekur.ndkore.map.add
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.LinkedList

@Serializable
private data class BuffParametersSurrogate(
    val duration: Long? = null,
    val conditions: List<Condition<CompAny>> = emptyList()
)

/**
 * Parameters for a buff.
 *
 * @param duration The duration of the buff in minecraft ticks (20 ticks = 1 second).
 * @param condition The condition that must be met for the buff to be active.
 */
@Serializable(BuffParameters.Serializer::class)
data class BuffParameters(
    val duration: Long? = null,
    val conditions: SetsMap<ConditionType<*>, Condition<*>> = emptyMap()
) {

    object Serializer : KSerializer<BuffParameters> {
        override val descriptor = BuffParametersSurrogate.serializer().descriptor

        override fun serialize(
            encoder: Encoder,
            value: BuffParameters
        ) {
            @Suppress("kotlin:S6530", "UNCHECKED_CAST")
            val list = value.conditions.values.flatten() as List<Condition<CompAny>>
            val surrogate = BuffParametersSurrogate(value.duration, list)
            encoder.encodeSerializableValue(BuffParametersSurrogate.serializer(), surrogate)
        }

        override fun deserialize(decoder: Decoder): BuffParameters {
            val surrogate = decoder.decodeSerializableValue(BuffParametersSurrogate.serializer())

            val map: MutableSetsMap<ConditionType<*>, Condition<*>> = HashMap()
            surrogate.conditions.forEach {
                map.add(it.cType, it)
            }

            return BuffParameters(surrogate.duration, map)
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
