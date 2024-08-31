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


/**
 * Represents the data of a buff.
 *
 * @param T The type of the buff stat value.
 * @property buff The buff.
 * @property parameters The parameters of the buff.
 * @property firstGivenAt The time the buff was first given in milliseconds.
 * @property used The amount of time buff was used in milliseconds.
 */
@Serializable
open class RPGBuffData(
    @Serializable(UUIDSerializer::class)
    override val id: UUID,
    val buff: RPGBuff<*>,
    val parameters: BuffParameters,
    val firstGivenAt: Long,
    val used: Long = 0
) : Snowflake<UUID> {


    @Transient
    val conditionsState: ConditionsState = MapConditionsState()

    override fun toString(): String {
        return "RPGBuffData(id=$id, buff=$buff, parameters=$parameters, firstGivenAt=$firstGivenAt, used=$used)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is RPGBuffData) return false

        if (id != other.id) return false
        if (buff != other.buff) return false
        if (parameters != other.parameters) return false
        if (firstGivenAt != other.firstGivenAt) return false
        if (used != other.used) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + buff.hashCode()
        result = 31 * result + parameters.hashCode()
        result = 31 * result + firstGivenAt.hashCode()
        result = 31 * result + used.hashCode()
        return result
    }
}

object UUIDSerializer : KSerializer<UUID> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("UUID", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: UUID) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): UUID {
        return UUID.fromString(decoder.decodeString())
    }
}