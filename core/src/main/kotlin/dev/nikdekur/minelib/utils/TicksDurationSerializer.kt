package dev.nikdekur.minelib.utils

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.serializer
import kotlin.time.Duration

class TicksDurationSerializer : KSerializer<Duration> {

    val delegate = serializer<Duration>()

    override val descriptor: SerialDescriptor
        get() = delegate.descriptor

    override fun serialize(encoder: Encoder, value: Duration) {
        delegate.serialize(encoder, value)
    }

    override fun deserialize(decoder: Decoder): Duration {
        val result = delegate.deserialize(decoder)

        val inWholeMilliseconds = result.inWholeMilliseconds
        require(inWholeMilliseconds % 50 == 0L) { "Duration must be a multiple of 50 milliseconds to represent ticks." }

        return result
    }
}