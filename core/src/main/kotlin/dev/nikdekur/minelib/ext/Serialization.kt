@file:OptIn(ExperimentalSerializationApi::class)

package dev.nikdekur.minelib.ext

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.LongArraySerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.*

object StringUUIDSerializer : KSerializer<UUID> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("UUID", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: UUID) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): UUID {
        return UUID.fromString(decoder.decodeString())
    }
}

object BytesUUIDSerializer : KSerializer<UUID> {
    val serializer = LongArraySerializer()
    override val descriptor = SerialDescriptor("CustomType", serializer.descriptor)

    override fun serialize(encoder: Encoder, value: UUID) {
        val longs = longArrayOf(value.mostSignificantBits, value.leastSignificantBits)
        serializer.serialize(encoder, longs)
    }

    override fun deserialize(decoder: Decoder): UUID {
        val longs = serializer.deserialize(decoder)
        return UUID(longs[0], longs[1])
    }
}