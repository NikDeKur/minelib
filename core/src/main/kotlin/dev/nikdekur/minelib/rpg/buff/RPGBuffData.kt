package dev.nikdekur.minelib.rpg.buff

import dev.nikdekur.minelib.rpg.condition.ConditionsState
import dev.nikdekur.minelib.rpg.condition.MapConditionsState
import dev.nikdekur.ndkore.`interface`.Snowflake
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.UUID

@Serializable
data class RPGRawBuffData(
    val buff: RPGBuff<*>,
    val firstGivenAt: Long,
    val parameters: BuffParameters = BuffParameters(),
    val used: Long = 0
)

data class RPGBuffData<T : Comparable<T>>(
    override val id: UUID = UUID.randomUUID(),
    val buff: RPGBuff<T>,
    val firstGivenAt: Long = System.currentTimeMillis(),
    val parameters: BuffParameters = BuffParameters(),
    val used: Long = 0
) : Snowflake<UUID> {


    @Transient
    val conditionsState: ConditionsState = MapConditionsState()

    fun toRaw(): RPGRawBuffData {
        return RPGRawBuffData(buff, firstGivenAt, parameters, used)
    }
}

object UUIDSerializer : KSerializer<UUID> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("UUID1", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: UUID) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): UUID {
        return UUID.fromString(decoder.decodeString())
    }
}